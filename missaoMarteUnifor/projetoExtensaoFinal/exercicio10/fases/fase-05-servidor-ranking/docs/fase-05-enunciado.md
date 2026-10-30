# Enunciado - Servidor e ranking global do Missão Marte

## 1. Contexto

Você recebeu o jogo **Missão Marte** da Fase 4: um projeto Gradle multi-módulo (`core` e `lwjgl3`) em libGDX, com sprites, animações, sons e HUD. O ranking é **local**: fica em um arquivo `ranking.json` na máquina do jogador, qualquer um pode editá-lo e cada computador tem o seu. Não existe o conceito de usuário. Há 66 testes JUnit que passam.

A equipe quer um **ranking compartilhado**. Sua tarefa é **criar um servidor independente** (Spring Boot) com cadastro, login, histórico de partidas e ranking global, e **evoluir o cliente libGDX** para usá-lo por uma API REST, preservando a arquitetura em camadas e **documentando o que foi feito e por quê**.

Decisão de produto desta fase: o jogo continua jogável **sem login** (modo convidado, com o ranking local da Fase 4). Quem entra na conta usa o ranking global.

## 2. Objetivos de aprendizagem

- Explicar o que é uma API REST, os métodos HTTP e os códigos de status usados.
- Montar um servidor em camadas (controller, serviço, repositório) com Spring Boot e persistência com JPA.
- Guardar senhas com hash e sal, e explicar por que isso difere de criptografar.
- Autenticar por token (JWT) em uma API sem estado e proteger rotas.
- Validar a entrada e devolver erros no formato `ProblemDetail` sem expor detalhes internos.
- Chamar uma API a partir do libGDX sem travar o desenho, devolvendo o resultado na thread principal.
- Isolar a rede atrás de uma porta (Port and Adapter) e testar a conversão de JSON sem janela.
- Explicar o que o servidor **não** consegue garantir quando não executa o jogo.

## 3. Entrada

Use como ponto de partida a pasta `fase-04-interface-grafica`.

Regras:

- **Não altere** `fase-04-interface-grafica`, as fases anteriores nem `exercicio10/src`. Trabalhe em uma pasta nova (por exemplo, `fase-05-servidor-ranking`).
- O **servidor** e o **cliente** devem ser **subprojetos dentro dessa pasta** e aplicativos independentes: o servidor não depende do código do cliente (duplique o que precisar, como o enum de dificuldade).
- **Não altere as regras do jogo.** `domain` deve ficar idêntico ao da Fase 4 (verifique por hash). Nenhuma mudança de regra é necessária.
- O servidor **não executa a lógica do jogo**: ele recebe o resultado enviado pelo cliente. Registre isso como limitação (seção 8.3).

## 4. Ambiente

| Item | Requisito |
|---|---|
| JDK | 17 (`JAVA_HOME` **e** `PATH`: confira que o primeiro `java` do `PATH` é o 17) |
| Servidor | Spring Boot 4.1.2 e Gradle com wrapper 9.8.2 (o Boot 4.1 exige Gradle 8.14+ ou 9.x) |
| Cliente | Gradle com wrapper 8.7 e libGDX 1.12.1, como na Fase 4 |
| Banco | H2 em arquivo (nenhuma instalação) |
| Vídeo e áudio | Como na Fase 4 |
| Rede | Acesso ao Maven Central e ao `services.gradle.org` |

Os dois projetos são **builds Gradle independentes**, cada um com o seu wrapper. Explique na documentação por que não é um único build.

## 5. O que deve ser feito

### 5.1 Estrutura do projeto

```text
fase-05-servidor-ranking/
  cliente/     a Fase 4 evoluída (core, lwjgl3, wrapper 8.7)
  servidor/    projeto Spring Boot novo (wrapper 9.8.2)
  docs/
  README.md
```

Cada subprojeto tem o seu `.gitignore` (`build`, `.gradle`, `ranking.json` no cliente; `dados` no servidor).

### 5.2 Servidor: modelo e persistência

Duas entidades JPA:

| Entidade | Campos mínimos |
|---|---|
| `Usuario` | id, nome, nome normalizado (minúsculas, **único**), hash da senha, data de criação |
| `Partida` | id, usuário, dificuldade, resultado (`VITORIA` ou `DERROTA`), pontuação, duração em segundos, movimentos, data e hora |

Regras:

1. O banco guarda **só o hash** da senha, nunca a senha.
2. O nome é único **sem diferenciar maiúsculas de minúsculas** (garanta pelo banco, e não só pelo código: dois cadastros simultâneos iguais não podem passar).
3. Configure o H2 em arquivo, com a pasta trocável por variável de ambiente, e o Hibernate criando as tabelas (`ddl-auto=update`; explique por que isso não serve para produção).
4. Entidades nunca saem pela API: use DTOs (`record`).

### 5.3 Servidor: API

| Método e rota | Autenticação | Resposta |
|---|---|---|
| `POST /api/usuarios` | não | 201 com `id` e `nome`; 409 se o nome existe; 400 se inválido |
| `POST /api/auth/login` | não | 200 com o token, o tipo (`Bearer`) e a expiração; 401 se usuário ou senha estiverem errados |
| `POST /api/partidas` | sim | 201 com a partida registrada |
| `GET /api/partidas/minhas` | sim | Lista do usuário do token, da mais recente para a mais antiga, com `pagina` e `tamanho` (máximo 50) |
| `GET /api/ranking` | sim | Top 10 (ver 5.4) |

Regras:

1. **Tudo exige token, exceto cadastro e login.** Configure "tudo protegido por padrão, liberando o que for público", e não o contrário.
2. O usuário de uma partida vem **do token**, nunca do corpo. Se o corpo trouxer um campo `usuario`, ele é ignorado.
3. Abortar uma partida **não** é enviado ao servidor; o enum de resultado só tem `VITORIA` e `DERROTA`.
4. Responda os erros no formato `ProblemDetail` (RFC 9457). Em erro de validação, inclua os campos com problema.
5. Nenhuma resposta traz senha ou hash.

### 5.4 Servidor: ranking

- Uma linha **por jogador**: a **melhor vitória** dele.
- Só entram vitórias com **pontuação maior que zero**.
- Ordem: maior pontuação; no empate, **menor duração**; persistindo o empate, a partida mais antiga.
- No máximo 10 linhas.

Dica: a escolha da melhor partida de cada jogador é uma subconsulta; teste os empates.

### 5.5 Servidor: validação

| Campo | Regra |
|---|---|
| Nome | 3 a 20 caracteres: letras, números, `_` e `-` |
| Senha | Mínimo de 8 caracteres e **no máximo 72 bytes** (limite do BCrypt, que corta o resto em silêncio) |
| Dificuldade e resultado | Valores do enum; qualquer outro é 400 |
| Pontuação | Entre -100000 e 100000 |
| Duração | De 0 a 86400 segundos |
| Movimentos | De 0 a 100000 |
| Campos ausentes | 400 (use tipos que aceitem `null`, para que um campo faltando não vire zero) |

Esses limites só barram valores absurdos: o servidor **não consegue** provar que a pontuação é verdadeira.

### 5.6 Servidor: segurança

1. **Hash:** BCrypt com custo mínimo 10 (ou Argon2id, se justificar). Duas contas com a mesma senha devem ter hashes diferentes.
2. **Token:** JWT assinado, com id do usuário em `sub` e expiração de 2 horas. A senha **nunca** entra no token.
3. **Segredo do token:** vem de variável de ambiente, tem pelo menos 32 bytes e **não vai para o código nem para o repositório**. Sem a variável, o servidor pode gerar um segredo aleatório e avisar no log.
4. **API sem estado:** sem sessão e sem cookie; explique por que, nesse caso, o CSRF pode ficar desligado.
5. **Mesma resposta** para usuário inexistente e para senha errada. Para o **tempo** de resposta também não revelar quais nomes existem, confira a senha contra um hash falso quando o nome não existir.
6. **Sem senha em log:** os DTOs de login e cadastro não podem imprimir a senha em `toString`.
7. O servidor escuta **só em `127.0.0.1`** por padrão.

### 5.7 Servidor: testes

Escreva testes de API com `MockMvc` e H2 em memória, desfazendo cada teste ao final. No mínimo:

| Assunto | O que verificar |
|---|---|
| Cadastro | 201 sem senha na resposta; hash BCrypt no banco, diferente da senha; sal diferente; nome repetido com outra caixa (409); nome e senha inválidos (400); limite de 72 bytes |
| Login | Sucesso; mesma resposta para senha errada e usuário inexistente; senha ausente da resposta |
| Proteção | Sem token (401), token inválido (401), **token expirado** (401), token válido (200) |
| Partidas | Registro; usuário do corpo ignorado; enum inválido e valor fora da faixa (400); histórico só do dono, na ordem certa e paginado |
| Ranking | Vazio; uma linha por jogador com a melhor pontuação; derrotas e pontuação não positiva fora; desempate por duração e depois por antiguidade; limite de 10; sem dados sensíveis |

Os testes de API **não podem** usar o arquivo de banco real nem um segredo gerado aleatoriamente: use um arquivo de propriedades de teste.

### 5.8 Cliente: porta e adaptador

1. Em `application`, crie uma **porta** (sugestão: `ServidorDoJogo`) com as operações: cadastrar, entrar, enviar partida, carregar ranking e carregar histórico. Todas **assíncronas**, com callback.
2. Crie os tipos de apoio em `application` sem libGDX: resposta (valor **ou** mensagem de erro, com marca de "não autorizado"), callback, sessão (nome e token), partida concluída e partida registrada.
3. A sessão fica **só na memória** e o `toString` dela não mostra o token.
4. Em `gdx`, crie o **adaptador HTTP** com `Gdx.net.sendHttpRequest` e `HttpResponseListener`.
5. O libGDX chama o listener em outra thread: repasse **todo** resultado com `Gdx.app.postRunnable`.
6. Defina `Content-Type: application/json` nos `POST` e `Authorization: Bearer ...` nas rotas protegidas, e use um tempo limite.
7. Crie uma classe **só para converter JSON** (objeto para texto e texto para objeto) com `JsonWriter` e `JsonReader` da libGDX. Ela não pode usar rede nem OpenGL.
8. O JSON enviado deve ser montado por `JsonWriter` (que escapa aspas e barras), **nunca por concatenação de texto**: uma senha como `a"b\c` não pode quebrar a estrutura.
9. A mensagem de erro mostrada ao jogador sai do `ProblemDetail` (primeiro erro de campo ou `detail`), ou de um texto padrão por código HTTP. **Nunca** mostre o corpo bruto da resposta.
10. `domain`, `application` e `infrastructure` **continuam sem importar** `com.badlogic`.
11. O endereço do servidor é configurável (sugestão: `-Dmissaomarte.servidor=http://localhost:8080`, valor padrão `http://localhost:8080`) e é montado no launcher.

### 5.9 Cliente: modos e telas

O jogo passa a ter **dois modos**, decididos por existir ou não uma sessão:

| Tela | Convidado | Logado |
|---|---|---|
| Menu | 1 Jogar, 2 Ranking local, 3 Limpar ranking local, 4 Entrar ou criar conta, 5 Sair | 1 Jogar, 2 Ranking global, 3 Meu histórico, 4 Sair da conta, 5 Sair |
| Nova partida | Digita o nome | Usa o nome da conta (só escolhe a dificuldade) |
| Fim de partida | Grava no ranking local (só vitória) | Envia vitória **e derrota** ao servidor |
| Ranking | Local, síncrono | Global, assíncrono |

Telas novas:

1. **Entrar e criar conta:** dois campos (nome e senha). A senha aparece **mascarada**. `Tab` ou setas trocam de campo, `F2` alterna entre entrar e criar conta, `Enter` confirma, `Esc` volta. Ao criar a conta, o jogo entra automaticamente. A letra `M` faz parte do texto: esta tela **não** usa o atalho de silenciar.
2. **Meu histórico:** últimas 10 partidas (data e hora, resultado, dificuldade, pontuação, tempo e movimentos).

Regras:

1. O formulário (campos, mascaramento, validação e caracteres aceitos) fica em uma classe **sem libGDX**, testável. Valide no cliente o que o servidor valida, mas **só o servidor é confiável**.
2. Ranking, histórico e envio têm **estados** visíveis: carregando, pronto e erro. Nunca deixe a tela em branco.
3. No fim da partida, enquanto o envio está em andamento, o `Enter` é ignorado. Se o envio falhar, mostre o motivo e permita tentar de novo com `R`.
4. Resposta 401 em chamada protegida significa sessão expirada: descarte a sessão, avise e volte ao modo convidado.
5. Servidor desligado **não pode** derrubar o jogo: mostre "não foi possível conectar" e o modo convidado continua disponível.
6. Depois de uma vitória logada, o ranking global indica se o jogador está no Top 10.

### 5.10 Cliente: testes

Mantenha os 66 testes da Fase 4 passando e acrescente testes para o que criou:

| O que testar | O que verificar (no mínimo) |
|---|---|
| Conversão de JSON | Credenciais e partida viram JSON válido; aspas e barras na senha não quebram a estrutura; números sem aspas, inclusive negativos; leitura do token (e erro se faltar); leitura do ranking e do histórico, incluindo lista vazia |
| Mensagens de erro | Erro de campo, `detail`, textos por código (401, 5xx, outros) e corpo que não é JSON sem vazar |
| Formulário | Caracteres aceitos e rejeitados em cada campo, limites, mascaramento, apagar, troca de campo e de modo, validação de entrar e de criar conta |
| Dados do servidor | Partida concluída a partir da sessão (vitória) e erro se a partida não terminou; sessão sem token no `toString`; os três tipos de resposta |
| Apresentação | Formatação da data do histórico |

Os testes **não podem** abrir janela, usar OpenGL nem acessar a rede.

### 5.11 Comandos que devem funcionar

```powershell
# servidor
cd servidor
.\gradlew --version                     # Gradle 9.8.2 e JVM 17
.\gradlew test
.\gradlew bootRun --console=plain       # http://localhost:8080

# cliente (outro terminal)
cd cliente
.\gradlew --version                     # Gradle 8.7 e JVM 17
.\gradlew clean build
.\gradlew :lwjgl3:run --console=plain
.\gradlew :lwjgl3:run -Dmissaomarte.servidor=http://localhost:8080 --console=plain
```

Inclua no servidor um arquivo de chamadas de exemplo (por exemplo, `requests.http`) para testar a API sem o jogo. Opcionalmente, exponha a documentação interativa (Swagger UI) com a biblioteca springdoc-openapi: nesse caso, libere **só a leitura** da página e da descrição OpenAPI, declare o esquema `bearer` para o botão Authorize e confira por teste que as rotas da API continuam exigindo o token.

## 6. Pontos de atenção

Erros comuns que costumam consumir tempo:

- **Spring Boot 4 mudou os starters.** Use `spring-boot-starter-webmvc` (e não `-web`), `-data-jpa`, `-security`, `-security-oauth2-resource-server` e `-validation`; nos testes, os starters `-webmvc-test`, `-data-jpa-test` e `-security-test`. O Jackson agora é o 3 (`tools.jackson`). Consulte o guia de migração 4.0.
- Em Boot 4, `@SpringBootTest` **não** configura o `MockMvc`: acrescente `@AutoConfigureMockMvc`.
- O `java` que o terminal usa vem do `PATH`, e não de `JAVA_HOME`; um JDK antigo na frente causa erros de "class file version".
- O BCrypt só lê os **72 primeiros bytes**; sem validação, senhas longas são cortadas sem aviso.
- Por padrão o resource server só confia em tokens `RS256`. Para um segredo simétrico (HS256), declare o `JwtDecoder` com `withSecretKey` e o algoritmo, e o `JwtEncoder` com a mesma chave.
- Sem o `/error` liberado, uma resposta de erro pode virar 401.
- Um `record` com campos primitivos transforma "campo ausente" em `0`; use `Integer` e `@NotNull`.
- O mesmo nome com outra caixa **não** é conflito para o banco; por isso o campo normalizado com índice único.
- Chamadas de rede no libGDX **não bloqueiam**: o código depois de `sendHttpRequest` roda antes da resposta. Não leia o resultado logo em seguida.
- O listener roda fora da thread de desenho: sem `postRunnable`, o estado da tela é alterado em outra thread.
- Defina o `Content-Type` dos `POST`: nem todo backend assume o mesmo valor padrão.
- Não guarde a senha nem o token em arquivo, em log ou em mensagem na tela.
- Dois servidores no mesmo arquivo de banco travam um ao outro (arquivo bloqueado). Pare o servidor antes de subir outro.
- Com a semente fixa do cliente, só a **primeira** partida da execução é reproduzível: cada nova partida consome o mesmo `Random`.

## 7. Preservar o comportamento

Prove que as regras não mudaram:

- Os arquivos de `domain` devem ser **idênticos** aos da Fase 4 (compare por hash). Liste os arquivos de `application`, `infrastructure` e `gdx` que diferem ou são novos e justifique cada um.
- O modo convidado se comporta como na Fase 4 (ranking local, nome digitado, "Limpar ranking").
- Pontuação, dificuldade, embarque, entrega, colisão e vitória se comportam como antes.
- Fases anteriores e `exercicio10/src` permanecem intactos (`git status`).

## 8. Documentação obrigatória

Entregue um arquivo `docs/tutorial.md` (ou nome equivalente) com as seções abaixo, e um `README.md` na pasta da fase.

### 8.1 Estado de origem e destino

- Como era o ranking (arquivo local, sem usuários) e como ficou.
- Tabela "item da Fase 4 -> equivalente na Fase 5" (por exemplo, `ranking.json` -> banco do servidor; nome digitado -> nome da conta; ranking síncrono -> chamada assíncrona).
- Diagrama da arquitetura **antes** e **depois** (Mermaid ou imagem), mostrando o cliente, o servidor e a regra de dependência.

### 8.2 Conceitos aplicados

Explique, com exemplos do seu próprio código:

- O que é uma API REST e o significado dos códigos 200, 201, 400, 401 e 409.
- As camadas do servidor e o que é um DTO.
- O que o JPA faz e como a consulta do ranking escolhe a melhor partida de cada jogador.
- Hash, sal e custo; por que não se criptografa senha; o limite de 72 bytes do BCrypt; por que a OWASP prefere Argon2id para sistemas novos.
- O que é um JWT, o que vai dentro dele, o que significa "sem estado" e o que acontece se o token vazar.
- Por que o servidor valida de novo o que o cliente já validou.
- Por que a rede no libGDX é assíncrona e o papel do `postRunnable`.
- O padrão Port and Adapter: o que é a porta e o que é o adaptador no seu código.

### 8.3 Decisões e limitações de segurança

- Quais ameaças você tratou e como (senha vazada do banco, enumeração de usuários, token falsificado ou expirado, gravação em nome de outro, JSON malicioso).
- O que **não** foi tratado: pontuação falsificada pelo cliente (por quê e que defesa exigiria), HTTPS, força bruta no login, renovação e revogação de token, fila de reenvio.
- Por que o segredo do token vem de variável de ambiente.

### 8.4 Decisões de projeto

- Por que dois builds independentes, com wrappers diferentes.
- Por que o servidor tem a sua própria cópia do enum de dificuldade.
- Por que o modo convidado foi mantido e o que ele custou na complexidade do cliente.
- Padrões usados e onde (por exemplo, Repository, DTO, Port and Adapter, Callback, State e injeção de dependência), distinguindo o que de fato segue o padrão do que apenas se parece com ele.

### 8.5 Passo a passo

Os comandos e as decisões na ordem em que foram feitos, incluindo problemas reais encontrados (por exemplo, erro de versão do Gradle, starter com nome antigo, `MockMvc` ausente nos testes, teste falhando por nome de usuário curto, tela travada esperando a rede) e como cada um foi resolvido.

### 8.6 Testes e evidências

O que cada teste cobre, a saída de `gradlew test` (servidor) e de `gradlew clean build` (cliente) com a quantidade de testes, **capturas de tela** do cliente (entrada, cadastro, menu logado, fim de partida com envio, ranking global, histórico, senha errada e servidor desligado), as respostas das chamadas diretas à API e a comparação por hash dos fontes.

### 8.7 Limitações e próximos passos

Liste pelo menos três pontos que ainda podem melhorar e relacione cada um com a Fase 6 ou 7 (por exemplo, HTTP em texto claro e permissão `INTERNET` no Android, ranking por dificuldade, tela de carregamento, reenvio offline).

## 9. Roteiro mínimo de validação

Execute e registre o resultado de cada passo:

1. `gradlew --version` mostra a versão e o JVM esperados em cada subprojeto.
2. `gradlew test` (servidor) e `gradlew clean build` (cliente) terminam com `BUILD SUCCESSFUL` (informe a quantidade de testes de cada um).
3. O servidor sobe na porta 8080 e o log **não** mostra senhas nem tokens.
4. Pela API: cadastro (201), nome repetido (409), nome curto (400), login com senha errada (401), login correto (200), ranking sem token (401), registro de partida (201), histórico e ranking.
5. No cliente, modo convidado: jogue uma partida; o resultado vai para `ranking.json` e aparece no ranking local.
6. Menu `4`, `F2`: crie uma conta. A senha aparece como asteriscos e o jogo entra sozinho; o menu mostra o nome do piloto.
7. Nova partida logada: o nome não é digitado. Vença uma partida: "Resultado enviado" e o ranking global mostra o jogador.
8. Crie um segundo jogador e jogue: cada jogador aparece **uma vez**, com a melhor pontuação.
9. Perca uma partida logada: o histórico mostra a vitória e a derrota, e o ranking não muda.
10. Entre com senha errada: aparece "Usuário ou senha inválidos." sem diferenciar a causa.
11. Pare o servidor e termine uma partida logada: aparece a falha de envio com `R`; suba o servidor, pressione `R` e confira o envio.
12. Pare o servidor e tente entrar: mensagem de conexão, sem exceção, e o modo convidado continua funcionando.
13. Saia da conta: o menu volta ao modo convidado e o ranking local reaparece.
14. Confira no banco (por teste ou consulta) que a senha está em BCrypt e não em texto.
15. Verifique por busca que nenhum arquivo fora de `gdx` e `lwjgl3` importa `com.badlogic`.
16. Verifique por hash e por `git status` que `domain`, as fases anteriores e `exercicio10/src` não mudaram.

## 10. Entregáveis

1. Pasta `fase-05-servidor-ranking` com `cliente/` e `servidor/` (cada um com seu wrapper e `.gitignore`).
2. Testes JUnit 5 que passam nos dois subprojetos.
3. `README.md` da fase com instruções de execução, controles, opções e a descrição da API.
4. Arquivo de chamadas de exemplo da API.
5. Documento `tutorial.md` com todas as seções da etapa 8.
6. Evidências do roteiro da etapa 9, com capturas de tela.

## 11. Critérios de avaliação

| Critério | Peso |
|---|---|
| API do servidor (rotas, códigos de status, `ProblemDetail`, DTOs sem dados sensíveis) | 15% |
| Persistência e regra do ranking (JPA, nome único pelo banco, melhor partida por jogador, desempates, limite) | 10% |
| Segurança (BCrypt, JWT, segredo fora do código, tudo protegido por padrão, usuário vindo do token, erro genérico no login) | 20% |
| Cliente: porta, adaptador e rede assíncrona (`postRunnable`, JSON por `JsonWriter`, mensagens de erro seguras) | 15% |
| Cliente: modos e telas (convidado e logado, entrada com senha mascarada, histórico, estados e falhas tratados) | 10% |
| Preservação das regras e das camadas (hash de `domain`, nenhum `com.badlogic` fora de `gdx`/`lwjgl3`, convidado como na Fase 4) | 10% |
| Testes JUnit 5 relevantes, isolados de rede, janela e banco real, e passando | 10% |
| Documentação, evidências e limitações (incluindo a pontuação não verificável) | 10% |
