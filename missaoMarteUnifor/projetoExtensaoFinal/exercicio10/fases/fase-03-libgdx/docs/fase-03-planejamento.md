# Planejamento da Fase 03 - Introdução ao libGDX

Plano aprovado antes da implementação ("aprovado com recomendações").

## Objetivo

Adaptar gradualmente o jogo da Fase 2 para a estrutura do libGDX (`Game`, `Screen`, ciclo de vida), reutilizando as regras existentes e trocando somente a interface.

## Estado de origem

- `fase-02-gradle`: módulo único, 26 arquivos em `src/main`, 28 testes JUnit, interface em console.
- `domain` e `application` não dependem de `console` nem de `infrastructure`. `GameSession.processarAcao(Acao)` devolve `ResultadoTurno` e `ObjetoRenderizavel` expõe posição e símbolo.
- Limitações que afetavam a adaptação: regra do Top 10 dentro de `ConsoleGame`, porta de ranking em `infrastructure`, eixo Y do jogo crescendo para baixo.

## Decisões aprovadas

| # | Decisão |
|---|---|
| 1 | libGDX 1.12.1 (versão do referencial); a 1.14.2 é a mais recente no Maven Central |
| 2 | `console` e `Main` não são levados para a Fase 3 (a versão console continua na Fase 2) |
| 3 | `RankingRepository` e `RankingEntry` movem para `application.ranking`; nova `RankingService` recebe a regra do Top 10 |
| 4 | Nome do piloto digitado na tela (`keyTyped`) |
| 5 | Visual com formas (`ShapeRenderer`) e fonte padrão (`BitmapFont`), sem imagens; sprites ficam para a Fase 4 |

## Escopo

- Projeto multi-módulo: `core` (jogo) e `lwjgl3` (launcher desktop).
- Telas: menu, nova partida, partida e ranking.
- Jogo por turnos: cada tecla válida processa um turno; `render()` só redesenha.
- Fora do escopo: sprites, sons, animações e HUD elaborado (Fase 4), Android (Fase 6), servidor e login (Fase 5).

## Versões

| Item | Versão |
|---|---|
| JDK | 17.0.10 |
| Gradle (wrapper) | 8.7 |
| libGDX | 1.12.1 |
| JUnit | 5.10.2 |

## Referencial teórico usado

Parte 3 (§3.1 a 3.7), Parte 2 §2.11 (multi-módulo), Parte 8 (`build.gradle`), Parte 5 (Game Loop e Screen), Parte 6 (estrutura), Parte 7 (`ShapeRenderer`). Complemento da documentação oficial para `FitViewport`/`OrthographicCamera`, `InputProcessor` e ciclo de vida.

## Divergências entre o referencial e o projeto

- Pacote `br.unifor.missaomarte` (referencial) contra `br.edu.unifor.missaomarte` (projeto).
- O referencial tem `Nave`/`Asteroide` próprios e movimento contínuo com `delta`; o projeto mantém o domínio do Exercício 10 e o jogo por turnos.
- `s01.oss.sonatype.org` e `google()` do referencial não são necessários na 1.12.1; só `mavenCentral()`.
- `core/assets` e `sourceSets...srcDirs` ficam para a Fase 4 (não há assets).
- Módulo `android` aparece no diagrama do referencial, mas pertence à Fase 6.

## Etapas

1. Esqueleto multi-módulo e `Game` mínimo com logs do ciclo de vida.
2. Telas e navegação (`MenuScreen`, `FitViewport`).
3. Partida (`GameScreen`, `TecladoParaAcao`, `MapaGeometria`).
4. Fim de partida e ranking (`RankingService`, `NovaPartidaScreen`, `RankingScreen`).
5. Documentação.

## Riscos e mitigações

| Risco | Mitigação |
|---|---|
| Eixo Y invertido | `MapaGeometria` com testes de unidade |
| Acentos na fonte padrão | Textos sem acento nesta fase (depois verificado no jar: a fonte cobre Latin-1, então foi uma escolha de convenção, não uma limitação) |
| `ranking.json` em outro diretório | `run.workingDir` na raiz da fase |
| macOS exige `-XstartOnFirstThread` | Condicional no `run` do módulo `lwjgl3` |
| Mover a porta do ranking quebra imports | Ajuste e execução dos testes |
| Janela não validável por terminal | Teste de fumaça com teclas simuladas e roteiro manual |

## Critérios de conclusão

- `gradlew --version` mostra Gradle 8.7 e JVM 17.
- `gradlew clean build` com todos os testes passando.
- `gradlew :lwjgl3:run` abre a janela e a navegação entre telas funciona.
- `domain`, `application` e `infrastructure` sem import de `com.badlogic`.
- Fases 1 e 2 e `exercicio10/src` sem alteração.
- Tutorial, README da fase e índice raiz entregues.
