# Fase 05 - Tutorial de Servidor e Ranking Global

## 1. Introdução

Até a Fase 4 o Missão Marte era um programa isolado: o ranking ficava num arquivo da própria máquina e qualquer pessoa podia editá-lo. Nesta fase o jogo passa a conversar com um **servidor**: os jogadores têm conta, o resultado de cada partida vai para um banco de dados e o ranking é compartilhado por todos.

O que muda para quem joga:

- Sem entrar, o jogo é o da Fase 4 (modo **convidado**, ranking local).
- Entrando, o resultado de cada partida é enviado ao servidor; o menu ganha o **ranking global** e o **histórico** do jogador.

O que muda no código: surge um segundo aplicativo (o servidor, em Spring Boot) e o cliente ganha uma camada de rede. As regras do jogo (`domain`) não mudam.

## 2. Objetivos de aprendizagem

Ao final, você deverá saber:

1. Explicar o que é uma API REST, os verbos HTTP e os códigos de status usados aqui.
2. Montar um servidor com Spring Boot em camadas (controller, serviço, repositório) com persistência JPA.
3. Guardar senhas com hash e explicar por que isso é diferente de criptografar.
4. Explicar a autenticação por token (JWT) e o que significa uma API sem estado.
5. Validar dados de entrada e devolver erros no formato padrão (`ProblemDetail`).
6. Chamar uma API a partir do libGDX sem travar o desenho da tela.
7. Separar o cliente do servidor por uma porta (Port and Adapter) e testar a conversão de JSON sem abrir janela.
8. Dizer o que o servidor **não** consegue garantir (a pontuação enviada pelo cliente).

## 3. Conceitos teóricos

> Esta fase **não** usa o `tutorial-api-gradle-libgdx.md` como base (o `PROMPT_MESTRE` o exclui das Fases 5 a 7). As fontes estão em 3.10.

### 3.1 Cliente e servidor

O **servidor** é um programa que fica esperando pedidos; o **cliente** (o jogo) faz os pedidos. Eles se falam por **HTTP**: o cliente envia uma requisição (método, endereço, cabeçalhos e, às vezes, um corpo) e o servidor devolve uma resposta (código de status, cabeçalhos e corpo). Os dois são programas independentes: podem estar na mesma máquina, como aqui, ou em máquinas diferentes.

### 3.2 API REST

Uma **API** é o conjunto de operações que um programa oferece a outros. Em uma API **REST** cada recurso (usuário, partida, ranking) tem um endereço e o método HTTP diz o que fazer com ele:

| Método | Uso nesta fase |
| --- | --- |
| `GET` | Ler (histórico, ranking). Não altera nada |
| `POST` | Criar (usuário, partida) e entrar (login) |

Os dados trafegam em **JSON**, um texto com objetos `{ "nome": "piloto1" }` e listas `[ ... ]`.

**Códigos de status usados:**

| Código | Significado aqui |
| --- | --- |
| 200 | Deu certo (login, consultas) |
| 201 | Criado (usuário, partida) |
| 400 | Dados inválidos (nome curto, pontuação fora da faixa) |
| 401 | Sem token, token inválido ou expirado, ou usuário e senha errados |
| 409 | Conflito (nome de usuário já existe) |

### 3.3 Spring Boot em camadas

O **Spring Boot** monta uma aplicação web a partir de convenções: ao ver as dependências no `build.gradle`, ele configura o servidor embutido (Tomcat), o JSON, o banco e a segurança. O código fica em camadas:

```text
Controller  ->  Service  ->  Repository  ->  Banco
(HTTP)         (regra)       (JPA)
```

- **Controller:** recebe a requisição, valida e devolve a resposta. Não tem regra de negócio.
- **Service:** aplica as regras (nome único, hash da senha, ranking).
- **Repository:** lê e grava no banco. Com Spring Data basta declarar uma interface.
- **DTO** (objeto de transferência): o formato que viaja pela rede. Aqui são `record`s. A entidade `Usuario`, que guarda o hash da senha, nunca é devolvida.

### 3.4 Persistência com JPA

**JPA** mapeia classes Java para tabelas. `Usuario` e `Partida` são `@Entity`; cada campo vira uma coluna e `@ManyToOne` liga a partida ao usuário. O **Hibernate** executa o SQL. O banco é o **H2**, gravado em arquivo (`servidor/dados`), então os dados sobrevivem a reinícios sem instalar nada.

O ranking é uma consulta que devolve, para cada jogador, só a melhor vitória:

```java
select p from Partida p join fetch p.usuario
where p.resultado = ...Resultado.VITORIA and p.pontuacao > 0
  and p.id = (select p2.id from Partida p2
              where p2.usuario = p.usuario and p2.resultado = ...Resultado.VITORIA and p2.pontuacao > 0
              order by p2.pontuacao desc, p2.duracaoSegundos asc, p2.id asc limit 1)
order by p.pontuacao desc, p.duracaoSegundos asc, p.id asc
```

A subconsulta escolhe a melhor partida de cada jogador; a consulta externa ordena e o `Pageable` limita a 10. Empate de pontuação: vence quem levou menos tempo; persistindo o empate, quem chegou antes.

### 3.5 Senhas: hash, sal e BCrypt

Uma senha **nunca** deve ser guardada em texto nem criptografada (criptografia se desfaz). Guarda-se o **hash**: uma função de mão única, lenta de propósito. Cada hash leva um **sal** aleatório, então duas contas com a mesma senha têm hashes diferentes e tabelas pré-calculadas não servem.

Este projeto usa o **BCrypt** (`BCryptPasswordEncoder`, fator de custo 10):

```text
$2a$10$N9qo8uLOickgx2ZMRZoMye...   <- versão, custo, sal e hash no mesmo texto
```

Dois cuidados:

- O BCrypt só lê os **72 primeiros bytes** da senha. Por isso o servidor recusa senhas maiores (`Senhas.cabeNoBcrypt`), em vez de cortá-las em silêncio.
- A OWASP recomenda **Argon2id** para sistemas novos e descreve o BCrypt como adequado a sistemas legados, com custo mínimo 10. Aqui o BCrypt foi escolhido por vir pronto no Spring Security; trocar por Argon2id é o exercício 6.

### 3.6 Autenticação por token (JWT)

Depois de conferir a senha, o servidor emite um **token**: um texto assinado que diz "este é o usuário 7 e vale até as 20h". O cliente o envia em toda chamada protegida:

```text
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

O **JWT** tem três partes (cabeçalho, conteúdo e assinatura). Neste projeto:

- Assinado com **HS256**: uma chave secreta que só o servidor conhece, usada para assinar e para verificar.
- Conteúdo (claims): `sub` (id do usuário), `iat`/`exp` (emissão e expiração, 2 horas) e `nome`. A senha nunca entra no token.
- O servidor **não guarda sessão**: cada requisição se prova sozinha. Isso se chama API **sem estado** (stateless) e é por isso que o CSRF, um ataque que explora cookies de sessão, pode ficar desligado.

O Spring Security valida a assinatura e a expiração antes de o controller ser chamado. O `@AuthenticationPrincipal Jwt` entrega o token já validado.

Limite: se o token vazar, vale até expirar. Não há renovação nem revogação (exercício 7).

### 3.7 Validação e erros

Os DTOs de entrada têm regras declaradas por anotações (`@Pattern`, `@Size`, `@Min`, `@NotNull`). Se uma regra falha, o Spring devolve 400 e o `TratadorDeErros` monta a resposta no formato **ProblemDetail** (RFC 9457):

```json
{ "status": 400, "detail": "Dados inválidos.", "erros": { "senha": "A senha deve ter pelo menos 8 caracteres." } }
```

Regra de ouro: a resposta explica o problema **do dado**, mas não revela detalhes internos. Por isso usuário inexistente e senha errada têm a mesma resposta ("Usuário ou senha inválidos.") e, para o tempo de resposta também não denunciar, o servidor calcula o BCrypt contra um hash falso quando o nome não existe.

### 3.8 Rede no libGDX: assíncrona e fora da thread de desenho

O jogo desenha 60 vezes por segundo. Se uma chamada de rede travasse essa thread, a janela congelaria. O `Gdx.net.sendHttpRequest` roda a chamada em outra thread e avisa por um **listener** (`handleHttpResponse`, `failed`, `cancelled`). Como as telas só podem ser tocadas na thread principal, o adaptador repassa o resultado com `Gdx.app.postRunnable(...)`.

Da tela, o uso é um **callback**:

```java
jogo.servidor().carregarRanking(jogo.sessao(), this::aoCarregar);   // retorna na hora
// ... a tela mostra "Carregando..." até aoCarregar ser chamado
```

Notas da documentação do libGDX: defina o `Content-Type` nos `POST`; no Android é preciso a permissão `INTERNET` e não se pode usar a rede na thread principal (Fase 6).

### 3.9 Port and Adapter

A tela não sabe que existe HTTP. Ela depende da interface `ServidorDoJogo` (a **porta**), que fala em objetos do jogo (`Sessao`, `PartidaConcluida`, `RankingEntry`). `ServidorHttp` é o **adaptador** que traduz isso para HTTP e JSON. Benefícios: a regra de dependência da Fase 3 continua valendo (`domain`, `application` e `infrastructure` não importam `com.badlogic`) e o adaptador pode ser trocado, por exemplo por um falso nos testes.

### 3.10 Fontes

- Spring Boot, requisitos do sistema: <https://docs.spring.io/spring-boot/system-requirements.html>
- Spring Boot 4.0 Migration Guide (starters, Jackson 3, testes): <https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide>
- Spring Boot, versões e suporte: <https://spring.io/projects/spring-boot#support>
- Spring Security, resource server com JWT: <https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html>
- OWASP Password Storage Cheat Sheet: <https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html>
- libGDX, Networking: <https://libgdx.com/wiki/networking>

## 4. Problemas do código de origem (Fase 4)

| Problema | Consequência |
| --- | --- |
| Ranking em `ranking.json` | Qualquer um edita o arquivo; cada máquina tem o seu ranking |
| Sem usuários | Não há dono para as pontuações nem histórico por jogador |
| Nome digitado a cada partida | O mesmo jogador aparece com nomes diferentes |
| "Limpar ranking" no menu | Não faz sentido para um ranking compartilhado |
| Regra do ranking presa a um arquivo | Para vários jogadores a regra precisa ficar num servidor |

## 5. Arquitetura antes da transformação

```text
cliente (Fase 4)
  gdx  ->  application (RankingService, GameSession...)  ->  domain
              |
              v
        RankingRepository (porta)  <-  FileRankingRepository (ranking.json)
```

## 6. Passo a passo da implementação

1. **Copiar** a Fase 4 para `cliente/` sem mudar nada e conferir que o build passa.
2. **Criar** `servidor/` com `settings.gradle`, `build.gradle` (Spring Boot 4.1.2) e o wrapper do Gradle 9.8.2 (`gradle wrapper --gradle-version 9.8.2`).
3. **Persistência:** `Usuario`, `Partida` e os repositórios.
4. **Cadastro:** `CadastroRequisicao` com validação, `UsuarioService` (nome único, BCrypt) e `UsuarioController`.
5. **Login:** `AutenticacaoService`, `TokenService` (emite o JWT) e `SegurancaConfig` (quem pode chamar o quê).
6. **Partidas e ranking:** `PartidaController`, `PartidaService`, `RankingService` e a consulta do Top 10.
7. **Erros:** `TratadorDeErros`.
8. **Testes** do servidor a cada etapa (seção 10).
9. **Cliente, porta:** `ServidorDoJogo`, `Resposta`, `Retorno`, `Sessao`, `PartidaConcluida`, `PartidaRegistrada`.
10. **Cliente, adaptador:** `JsonDoServidor` (com testes) e `ServidorHttp`.
11. **Cliente, telas:** `FormularioDeLogin`, `LoginScreen`, `HistoricoScreen`; alterar `MenuScreen`, `NovaPartidaScreen`, `GameScreen`, `RankingScreen` e `MissaoMarteGame`.
12. **Montar** tudo no `Lwjgl3Launcher`.
13. **Validar** servidor e cliente juntos.

## 7. Código antes e depois

### 7.1 Quem pode chamar o quê

```java
http
    .csrf(AbstractHttpConfigurer::disable)                       // API sem sessão nem cookie
    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(a -> a
        .requestMatchers(HttpMethod.POST, "/api/usuarios", "/api/auth/login").permitAll()
        .requestMatchers("/error").permitAll()
        .anyRequest().authenticated())                           // tudo o mais exige token
    .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()));
```

A regra é "tudo protegido, exceto o que for liberado": esquecer de proteger uma rota nova é impossível.

### 7.2 O usuário vem do token, não do corpo

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public PartidaResposta registrar(@AuthenticationPrincipal Jwt token, @Valid @RequestBody PartidaRequisicao requisicao) {
    return PartidaResposta.de(servico.registrar(Long.valueOf(token.getSubject()), requisicao));
}
```

`PartidaRequisicao` não tem campo de usuário: ninguém consegue gravar partida em nome de outro. Há um teste que envia `"usuario":"Trinity"` no corpo e confere que a partida ficou com o dono do token.

### 7.3 Do convidado ao logado, no fim da partida

Antes (Fase 4):

```java
} else if (estado == EstadoPartida.VITORIA) {
    pararRepeticao();
    novoRegistro = jogo.rankingService().registrarVitoria(session);
}
```

Depois (Fase 5):

```java
} else if (estado == EstadoPartida.VITORIA) {
    pararRepeticao();
    if (jogo.sessao() == null) {
        novoRegistro = jogo.rankingService().registrarVitoria(session);   // convidado: local
    } else {
        enviarResultado();                                                // logado: servidor
    }
} else if (estado == EstadoPartida.DERROTA) {
    pararRepeticao();
    if (jogo.sessao() != null) {
        enviarResultado();
    }
}
```

### 7.4 JSON sem montar texto à mão

```java
public static String corpoDeCredenciais(String nome, String senha) {
    return escrever(w -> w.object().set("nome", nome).set("senha", senha).pop());
}
```

O `JsonWriter` da libGDX escapa aspas e barras. Um teste usa a senha `a"b\c","nome":"hacker` e confere que o JSON continua com exatamente dois campos.

## 8. Explicação das alterações

### 8.1 Servidor: pacotes por funcionalidade

`usuario`, `auth`, `partida`, `ranking`, `seguranca` e `erro`. Cada pasta tem o que a funcionalidade precisa (controller, serviço, DTOs), o que facilita achar e alterar.

### 8.2 Segurança

- **Hash:** `BCryptPasswordEncoder`. O hash fica em `senha_hash`; `UsuarioResposta` e `RankingResposta` não têm esse campo.
- **Senha em log:** os `record`s de requisição sobrescrevem `toString` para `senha=***`.
- **Segredo do token:** vem de `MISSAOMARTE_JWT_SEGREDO`. Sem a variável, o servidor gera um segredo aleatório e avisa no log. Segredos não vão para o código nem para o repositório.
- **Mínimo de 32 bytes** para o HS256: segredo menor impede o servidor de subir.
- **Só `127.0.0.1`:** `server.address=127.0.0.1` evita abrir a API para a rede sem querer.
- **Nome único sem diferenciar caixa:** coluna `nome_normalizado` com índice único; o `saveAndFlush` faz o banco acusar dois cadastros simultâneos iguais (409).
- **Validação do cliente de novo no servidor:** o cliente também limita nomes e senhas, mas só o servidor é confiável.

### 8.3 O que o servidor não garante

O servidor **não executa a lógica do jogo**. Ele recebe "pontuação 150" e grava. Um cliente adulterado pode enviar 100000. Os limites de `PartidaRequisicao` (-100000 a 100000, duração até 24 h) só barram valores absurdos. Uma defesa real exigiria que o servidor refizesse a partida (receber a semente e as jogadas), o que está fora desta fase.

### 8.4 Cliente: sessão, convidado e logado

`MissaoMarteGame` guarda a `Sessao` (nome e token) **só na memória**. `jogo.sessao() == null` significa convidado. A sessão é descartada ao sair da conta, ao receber 401 e ao fechar o jogo.

| Tela | Convidado | Logado |
| --- | --- | --- |
| Menu | Ranking local, limpar ranking local, entrar | Ranking global, meu histórico, sair da conta |
| Nova partida | Digita o nome | Usa o nome da conta |
| Fim de partida | Grava em `ranking.json` | Envia ao servidor (`R` repete se falhar) |
| Ranking | Local, síncrono | Global, assíncrono ("Carregando...") |

### 8.5 Estados de envio e de carga

O envio da partida tem quatro estados (`NAO_ENVIA`, `ENVIANDO`, `ENVIADO`, `FALHOU`) e as telas de ranking e histórico têm três (`CARREGANDO`, `PRONTO`, `ERRO`). Durante `ENVIANDO` o `Enter` é ignorado para o jogador não sair antes de o servidor responder. Em `FALHOU`, `R` envia de novo.

### 8.6 Erros de rede

| Situação | O que o jogador vê |
| --- | --- |
| Servidor desligado | "Não foi possível conectar ao servidor (http://localhost:8080)." |
| Senha errada | "Usuário ou senha inválidos." |
| Nome repetido no cadastro | "Nome de usuário já cadastrado." |
| Dado inválido | A primeira mensagem de campo do servidor |
| Token expirado (401 em chamada autenticada) | "Sessão expirada. Entre novamente." e o jogo volta ao modo convidado |
| Resposta fora do padrão | "Resposta inesperada do servidor." |

`JsonDoServidor.mensagemDeErro` nunca devolve o corpo bruto: se o corpo não for JSON (por exemplo, uma página de erro), o jogador vê só "Erro no servidor (500)".

### 8.7 O que **não** mudou

- `domain` e `GameSession`: as regras do jogo são as mesmas.
- `RankingService` e `FileRankingRepository`: continuam sendo o ranking do convidado.
- Gráficos, sons, HUD e controles da partida.

### 8.8 Padrões usados

Camadas, Repository, DTO, Port and Adapter, injeção de dependência manual (`Lwjgl3Launcher`), Callback, State (telas e estados de envio) e Template Method (`TelaBase`).

### 8.9 Limitações conhecidas

- Sem HTTPS: a senha e o token trafegam em texto puro pela rede. Aceitável em `localhost`; em rede real é obrigatório usar TLS.
- Sem limite de tentativas de login (força bruta) e sem renovação ou revogação de token.
- Sem fila de reenvio: se o envio falhar e o jogador sair da tela, a partida se perde.
- O ranking global não separa por dificuldade e mostra só as 10 melhores vitórias.
- O histórico mostra as últimas 10 partidas, em horário UTC.
- A digitação da senha aceita só ASCII imprimível (limite do teclado desta interface); o servidor aceita mais.
- `ddl-auto=update` serve ao ensino; em produção use migrações versionadas (Flyway).

## 9. Arquitetura resultante

```mermaid
flowchart LR
    subgraph Cliente["cliente (libGDX)"]
        G[gdx: telas] --> A[application: ServidorDoJogo, GameSession, RankingService]
        A --> D[domain]
        R[gdx.rede: ServidorHttp + JsonDoServidor] -.implementa.-> A
        A --> F[infrastructure: FileRankingRepository]
    end
    subgraph Servidor["servidor (Spring Boot)"]
        C[Controllers] --> S[Services] --> P[Repositories] --> B[(H2)]
        SEC[Segurança: JWT + BCrypt] --> C
    end
    R -- "HTTP + JSON" --> SEC
```

```text
servidor  = API REST sem estado, banco H2, token JWT, senhas com BCrypt
cliente   = o jogo da Fase 4 + login, envio de resultados, ranking global e histórico
```

## 10. Como executar e testar

### 10.1 Comandos

```powershell
# Servidor
cd exercicio10\fases\fase-05-servidor-ranking\servidor
.\gradlew.bat test
.\gradlew.bat bootRun --console=plain

# Cliente (outro terminal)
cd exercicio10\fases\fase-05-servidor-ranking\cliente
.\gradlew.bat clean build
.\gradlew.bat :lwjgl3:run --console=plain
```

Para testar a API sem o jogo, use `servidor/requests.http` (extensão REST Client) ou o PowerShell:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/usuarios -ContentType application/json `
  -Body '{"nome":"piloto1","senha":"senha-segura-1"}'
```

### 10.2 Testes automatizados

**Servidor (31):** `@SpringBootTest` com `MockMvc` e H2 em memória; cada teste é desfeito ao final (`@Transactional`).

| Classe | Testes | Cobre |
| --- | --- | --- |
| `UsuarioApiTest` | 8 | Cadastro, resposta sem senha, hash BCrypt no banco, sal diferente, nome duplicado (outra caixa), nome e senha inválidos, limite de 72 bytes |
| `AuthApiTest` | 9 | Login, mesma resposta para senha errada e usuário inexistente, sem token, token inválido, token expirado, token válido |
| `PartidaApiTest` | 8 | Registro, usuário do corpo ignorado, enums e faixas inválidos, histórico só do dono, ordem e paginação |
| `RankingApiTest` | 6 | Uma linha por jogador, derrotas e pontuação não positiva fora, empates, limite de 10, sem dados sensíveis |

**Cliente (97, 31 novos):** `JsonDoServidorTest` (13), `FormularioDeLoginTest` (10), `DadosDoServidorTest` (6) e `HistoricoScreenTest` (2), além dos 66 da Fase 4.

### 10.3 Validação realizada

Servidor e cliente executados juntos, com o cliente controlado por teclas simuladas, semente fixa (`4` para vitória, `1` para derrota) e capturas de tela da janela:

1. Convidado: menu, ranking local vazio e vitória gravada em `ranking.json`.
2. Cadastro pela tela (`F2`), entrada automática, menu logado, partida vencida, "Resultado enviado ao servidor.", ranking global com o jogador no Top 10 e histórico.
3. Saída da conta e volta ao modo convidado.
4. Senha errada: "Usuário ou senha inválidos." Derrota enviada e histórico com vitória e derrota.
5. Servidor desligado: "Não foi possível conectar ao servidor".

Chamadas diretas à API confirmaram 401 sem token, 409 em nome repetido, 400 em dados inválidos, 201 ao registrar partida e o Top 10 ordenado.

### 10.4 O que não foi verificado

Áudio, macOS e Linux, HTTPS, Android (Fase 6) e carga (muitos jogadores ao mesmo tempo). O arquivo do banco não foi inspecionado à mão: o hash é conferido pelo teste automatizado.

### 10.5 Roteiro de validação manual

1. Suba o servidor e o cliente. No menu, `4`, `F2`, crie a conta `piloto1` com uma senha de 8 ou mais caracteres.
2. Jogue uma partida até o fim. Confira a mensagem de envio e o ranking global.
3. Crie uma segunda conta, jogue e compare o ranking (a melhor pontuação de cada jogador aparece uma vez).
4. `3` no menu: o histórico lista as partidas.
5. Pare o servidor com `Ctrl+C`, jogue outra partida e confira a falha de envio. Suba o servidor e pressione `R`.
6. `4`: sair da conta; confira que o menu volta ao modo convidado.

## 11. Exercícios para os alunos

1. **Ranking por dificuldade.** Adicione o parâmetro `dificuldade` em `GET /api/ranking` e uma tecla na tela para trocar a dificuldade exibida.
2. **Troca de senha.** Crie `PUT /api/usuarios/senha` (senha atual e nova) e uma tela no cliente.
3. **Limite de tentativas de login.** Bloqueie o login por alguns minutos depois de 5 erros seguidos do mesmo nome.
4. **Paginação do histórico.** Use `pagina` e `tamanho` na tela com as teclas seta para cima e para baixo.
5. **Fila de reenvio.** Guarde em arquivo as partidas que falharam e reenvie no próximo login.
6. **Argon2id.** Troque o `PasswordEncoder` por `Argon2PasswordEncoder` (parâmetros mínimos da OWASP) e explique o que muda para os usuários já cadastrados.
7. **Renovação de token.** Emita um segundo token de vida longa (refresh) e faça o cliente renovar a sessão sozinho.
8. **PostgreSQL.** Troque o H2 por PostgreSQL só com configuração e dependência, e adote o Flyway no lugar do `ddl-auto=update`.
9. **Defesa contra trapaça.** O cliente envia a semente e a lista de jogadas; o servidor refaz a partida e só aceita se a pontuação bater. Que partes do `domain` teriam de ser compartilhadas?
10. **Teste do adaptador.** Escreva um `ServidorDoJogo` falso (em memória) e use-o para testar uma tela sem rede.

## 12. Conclusão

O jogo deixou de ser um programa isolado: ganhou contas, histórico e um ranking compartilhado, com um servidor que guarda senhas com hash, autentica por token e valida tudo o que recebe. Do lado do cliente, a porta `ServidorDoJogo` manteve `domain`, `application` e `infrastructure` livres de libGDX e de HTTP, e a chamada assíncrona manteve o desenho fluido.

Três lições valem além deste projeto: (1) **nunca confie no cliente**: o servidor revalida os dados e, ainda assim, não consegue provar que uma pontuação é verdadeira sem refazer a partida; (2) **segredos ficam fora do código** (senha só como hash, segredo do token por variável de ambiente); (3) **mensagens de erro ajudam o usuário sem ajudar o atacante**.

A Fase 6 leva este cliente ao Android, onde a rede tem regras próprias (permissão `INTERNET`, HTTP em texto claro, thread principal), e a Fase 7 conclui o projeto.
