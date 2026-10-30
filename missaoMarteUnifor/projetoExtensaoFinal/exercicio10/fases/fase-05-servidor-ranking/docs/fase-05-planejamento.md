# Fase 05 - Planejamento

Plano aprovado em 2026-10-30 ("aprovo as recomendações"), com uma alteração do autor na decisão 6 (ver seção 10).

## 1. Diagnóstico do estado de origem (Fase 4)

- Build Gradle multi-módulo (`core` e `lwjgl3`), libGDX 1.12.1, Gradle 8.7, 66 testes.
- Camadas: `domain`, `application`, `infrastructure.ranking` e `gdx`. Só `gdx` e `lwjgl3` importam `com.badlogic`.
- O ranking era local: `RankingRepository` (porta síncrona), `RankingService` (regra do Top 10) e `FileRankingRepository` (`ranking.json`).
- Não havia conceito de usuário: o nome do piloto era digitado a cada partida.
- A `GameSession` já expõe o que o servidor precisa guardar: piloto, dificuldade, pontuação, duração, movimentos e estado.

## 2. Escopo

**Servidor (aplicativo independente, Spring Boot):** cadastro, login e autenticação, API REST, persistência JPA, histórico de partidas, ranking global (Top 10 com a maior pontuação de cada jogador) e senhas com hash seguro.

**Cliente (libGDX):** tela de entrada e cadastro, envio do resultado de cada partida, ranking global e tela "Meu histórico". Sem login o jogo continua como na Fase 4 (modo convidado, ranking local).

**Fora do escopo:** Android e toque (Fase 6); HTTPS em produção, limite de tentativas de login, renovação de token, papéis e administrador; verificação anti-trapaça (o servidor não executa o jogo); ranking por dificuldade.

## 3. Pastas e classes afetadas

Tudo em `exercicio10/fases/fase-05-servidor-ranking/`. Fases anteriores e `exercicio10/src` ficam intactos.

- `cliente/`: cópia da Fase 4 (sem `build`, `.gradle`, `ranking.json` e `docs`) com as alterações abaixo.
- `servidor/`: projeto novo.

**Servidor** (`br.edu.unifor.missaomarte.servidor`):

| Camada | Classes |
| --- | --- |
| Entidades JPA | `Usuario`, `Partida` (+ enums `Dificuldade`, `Resultado`) |
| Repositórios | `UsuarioRepository`, `PartidaRepository` (inclui a consulta do ranking) |
| Serviços | `UsuarioService`, `AutenticacaoService`, `PartidaService`, `RankingService` |
| REST | `UsuarioController`, `AuthController`, `PartidaController`, `RankingController` |
| DTOs (records) | `CadastroRequisicao`, `LoginRequisicao`, `TokenResposta`, `UsuarioResposta`, `PartidaRequisicao`, `PartidaResposta`, `RankingResposta` |
| Segurança | `SegurancaConfig`, `TokenService`, `PropriedadesDeSeguranca`, `Senhas` |
| Erros | `TratadorDeErros`, `NomeJaExisteException`, `CredenciaisInvalidasException` |

**Cliente:**

| Local | Mudança |
| --- | --- |
| `application/servidor` (novo) | Porta `ServidorDoJogo` e os tipos `Resposta<T>`, `Retorno<T>`, `Sessao`, `PartidaConcluida`, `PartidaRegistrada` |
| `gdx/rede` (novo) | `ServidorHttp` (adaptador com `Gdx.net`) e `JsonDoServidor` (conversão de JSON, testável sem janela) |
| `gdx` | `LoginScreen` e `FormularioDeLogin` (novos), `HistoricoScreen` (nova); `MenuScreen`, `NovaPartidaScreen`, `GameScreen`, `RankingScreen` e `MissaoMarteGame` alterados |
| `lwjgl3` | `Lwjgl3Launcher` monta o `ServidorHttp`; `build.gradle` repassa `-Dmissaomarte.servidor` |
| `application`, `infrastructure`, `domain` | `RankingService`, `RankingRepository`, `FileRankingRepository` **mantidos** para o modo convidado. `domain` sem alteração |

## 4. Alterações em etapas

1. Esqueleto: `cliente/` copiado sem alterações e `servidor/` com Boot, wrapper 9.8.2 e teste de contexto.
2. Persistência: `Usuario` e `Partida` com repositórios.
3. Cadastro e login: BCrypt, token JWT, segurança por padrão e testes.
4. Partidas, histórico e ranking: endpoints, validação e consulta do Top 10.
5. Cliente: porta, adaptador, sessão em memória, telas e menu.
6. Integração: servidor e cliente juntos.
7. Documentação.

## 5. Padrões e por quê

| Padrão | Onde | Motivo |
| --- | --- | --- |
| Camadas (controller, serviço, repositório) | Servidor | Separa HTTP, regra e persistência |
| Repository (Spring Data JPA) | Servidor | Persistência por interface |
| DTO (records) | Servidor e cliente | A API não expõe entidades nem o hash da senha |
| Token sem estado (JWT) | Segurança | Funciona igual no desktop e no Android |
| Port and Adapter | `ServidorDoJogo` e `ServidorHttp` | A aplicação não conhece HTTP nem libGDX |
| Callback assíncrono | Cliente | `Gdx.net` não bloqueia; as telas mostram "Carregando" |
| State | Telas (`Screen`) e estados de envio e carga | Cada tela e cada fase do envio é um estado |
| Injeção de dependência manual | `Lwjgl3Launcher` | Monta o adaptador e passa ao jogo |

## 6. Referencial teórico

O `PROMPT_MESTRE` determina que o `tutorial-api-gradle-libgdx.md` não é base das Fases 5 a 7. As fontes são a documentação oficial citada no tutorial (Spring Boot, Spring Security, Spring Data JPA, `Gdx.net` do libGDX e OWASP).

## 7. Versões e divergências

| Item | Versão | Observação |
| --- | --- | --- |
| JDK | 17.0.10 | Spring Boot 4.1 exige Java 17 ou superior |
| Spring Boot | 4.1.2 (estável em 2026-10-30) | A 3.5.x teve o suporte gratuito encerrado em 2026-06; a 4.0.x o encerra em 2026-12; a 4.1.x vai até 2027-07. A 4.2.0 estava em release candidate |
| Gradle do servidor | 9.8.2 | Boot 4.1 exige Gradle 8.14+ ou 9.x. O 8.7 não serve |
| Gradle do cliente | 8.7 (mantido) | Segue a Fase 2 |
| libGDX | 1.12.1 (mantida) | Sem mudança |
| Banco | H2 em arquivo | Zero instalação; trocável por PostgreSQL |
| Hibernate / Tomcat | 7.4.13 / 11.0.27 | Trazidos pelo Boot |
| springdoc-openapi | 3.2.0 | Swagger UI e OpenAPI (adicionado após a aprovação, a pedido do autor). A matriz oficial liga o Boot 4.1.x às versões 3.1.x e 3.2.x |

**Divergências aprovadas:** (a) dois builds Gradle com versões diferentes de wrapper; (b) o jogo pode ser jogado sem login, ao contrário do `PROMPT_MESTRE` (decisão 6).

## 8. Riscos e mitigações

| Risco | Mitigação |
| --- | --- |
| Dois wrappers diferentes | Dois builds independentes, documentados no README |
| Spring Boot 4 mudou starters, pacotes de teste e Jackson (3) | Seguir o guia de migração 4.0 e confirmar os nomes na compilação |
| Segredo do JWT no código | Vem de variável de ambiente; sem ela, gera um aleatório |
| Senha sem proteção | BCrypt, limite de 72 bytes validado, `toString` dos DTOs sem a senha, teste do hash |
| Pontuação falsificada | Fora do alcance por regra (o servidor não executa o jogo); só limites básicos |
| Enumeração de usuários | Mesma resposta (e tempo parecido) para usuário inexistente e senha errada |
| Força bruta no login | Limitação registrada |
| HTTP simples | Servidor escuta só em `127.0.0.1`; aviso na documentação |
| Android exige configuração para HTTP em texto claro e a permissão `INTERNET` | Ponto de atenção da Fase 6; URL configurável |
| Servidor fora do ar | Mensagem clara; o modo convidado continua disponível; tentativa de reenvio com `R` |
| Retorno fora da thread de desenho | `Gdx.app.postRunnable` |
| Arquivo do H2 travado por duas instâncias | Documentado; pasta `dados` ignorada pelo git |

## 9. Critérios de conclusão

- Servidor: `gradlew test` passa; senha nunca em texto, em resposta ou em log.
- Cliente: `gradlew clean build` passa; testes de `domain` mantidos.
- `domain` idêntico ao da Fase 4; nenhum `com.badlogic` fora de `gdx` e `lwjgl3`.
- Fluxo ponta a ponta: cadastrar, entrar, jogar, enviar partida, ver histórico e ver o ranking global.
- Token ausente, inválido ou expirado recebe 401.
- Modo convidado funcionando como na Fase 4.
- Fases anteriores e `exercicio10/src` sem alteração.

## 10. Decisões aprovadas

| # | Decisão | Resultado |
| --- | --- | --- |
| 1 | Versões | Boot 4.1.2 com servidor em Gradle 9.8.2 |
| 2 | Layout | Dois builds independentes, `cliente/` e `servidor/` |
| 3 | Banco | H2 em arquivo |
| 4 | Autenticação | JWT (HS256), 2 horas, segredo por variável de ambiente |
| 5 | Hash de senha | BCrypt |
| 6 | Ranking local | **Alterada pelo autor:** o ranking local continua quando não há login (modo convidado); logado, usa o remoto. O menu muda conforme o modo e o nome do piloto vem da conta quando logado |
| 7 | Partidas enviadas | `VITORIA` e `DERROTA`; abortadas não |
| 8 | Acesso à API | Tudo exige token, exceto cadastro e login |
| 9 | HTTP no cliente | `Gdx.net` com `JsonWriter`/`JsonReader` da libGDX |
| 10 | Esquema do banco | `ddl-auto=update` |
| 11 | Cadastro | Dentro do jogo, na tela de entrada (`F2`) |

## 11. Estratégia de validação e resultado

1. Testes do servidor (34, 31 da API e 3 da documentação): cadastro, hash, login, 401, histórico só do próprio usuário, ranking com vários jogadores, empates e limite de 10, validações, e Swagger público sem liberar a API. **Passaram.**
2. Servidor em execução com chamadas reais por PowerShell (cadastro, 409, 400, 401, login, partida, histórico, ranking). **Conforme esperado.**
3. Testes do cliente (97, dos quais 31 novos): conversão de JSON, formulário, dados e histórico. **Passaram.**
4. Cliente contra o servidor real com teclas simuladas, semente fixa e capturas de tela da janela:
   - convidado: menu, ranking local vazio, vitória gravada em `ranking.json`;
   - cadastro e entrada, menu logado, vitória enviada ("Resultado enviado ao servidor."), ranking global com o jogador no Top 10, histórico, saída da conta;
   - senha errada ("Usuário ou senha inválidos.") e derrota enviada, com o histórico mostrando as duas partidas;
   - servidor desligado ("Não foi possível conectar ao servidor").
5. Não verificado: áudio, macOS e Linux, HTTPS, e o arquivo do banco aberto à mão (o hash é conferido pelo teste automatizado).
