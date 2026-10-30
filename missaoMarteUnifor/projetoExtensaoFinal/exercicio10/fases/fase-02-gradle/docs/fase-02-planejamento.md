# Planejamento da Fase 02 - Migração para Gradle

Plano aprovado antes da implementação (revisão 2, após ajuste do ambiente).

## Objetivo

Migrar o projeto refatorado da Fase 1 para Gradle, sem alterar o comportamento do jogo, entregando um build reproduzível e instruções de execução.

## Estado de origem

- `fase-01-refatoracao`: 26 arquivos `.java` em `src/br/edu/unifor/missaomarte/`, sem dependências externas, build manual com `javac` (`build.ps1`) e sem testes automatizados.
- O jogo é interativo (`Scanner(System.in)`) e grava `ranking.json` no diretório de trabalho.

## Ambiente verificado

| Item | Valor |
|---|---|
| JDK no PATH e em `JAVA_HOME` | 17.0.10 |
| Gradle instalado | 9.8.2 (`C:\java\gradle-9.8.2`) |
| Gradle do wrapper (decisão) | 8.7, versão do referencial teórico |
| Rede | Acesso liberado a `services.gradle.org` e ao Maven Central |

## Escopo

- Projeto Gradle de módulo único, layout `src/main/java`, plugin `application`.
- Gradle Wrapper 8.7 gerado com o Gradle instalado.
- Testes unitários com JUnit 5 sobre as regras principais.
- Tutorial, README da fase e índice de fases na raiz.
- Fora do escopo: alterar o código do jogo, corrigir limitações da Fase 1, multi-módulo e libGDX (Fase 3).

## Decisões aprovadas

1. Java 17.
2. Wrapper gerado com o Gradle instalado.
3. Versão do wrapper: 8.7.
4. Incluir testes JUnit 5 mínimos (`Posicao`, `Nave`, `Acao`, `MissaoFactory`, `GameSession`, `FileRankingRepository`).
5. Criar `README.md` na raiz de `projetoExtensaoFinal/` com o índice das fases.
6. Salvar este planejamento em `docs/`.

## Referencial teórico

`tutorial-api-gradle-libgdx.md`: seções sobre API, Gradle (build tool, tasks, DAG, DSL, estrutura do `build.gradle`, dependências e escopos, Wrapper, ciclo de vida, comandos), ambiente de desenvolvimento e execução. A seção de multi-módulo foi usada apenas como prévia da Fase 3.

## Divergências entre o referencial e o projeto

- O tutorial é multi-módulo (`core` e `lwjgl3`) e usa libGDX; a Fase 2 é módulo único e sem dependências de runtime.
- Pacote do tutorial: `br.unifor...`; do projeto: `br.edu.unifor...`. Classe principal: `Main`.
- Repositórios: apenas `mavenCentral()`.
- Gradle instalado (9.8.2) difere da versão fixada no wrapper (8.7).

## Riscos e mitigações

| Risco | Mitigação |
|---|---|
| `gradle run` não repassa o teclado ao `Scanner` | `run { standardInput = System.in }` e `--console=plain` |
| `ranking.json` gravado em outro lugar | Validar que aparece na pasta da Fase 2 |
| Acentos no Windows | `options.encoding = 'UTF-8'` |
| `JAVA_HOME` apontando para JDK 25 com wrapper 8.7 | Documentar e conferir com `gradlew --version` |
| Confusão entre Gradle instalado e wrapper | Seção específica no tutorial |
| Mover arquivos alterar o jogo | Cópia sem edição e comparação por hash |

## Critérios de conclusão

- `gradlew --version` mostra Gradle 8.7 e JVM 17.
- `gradlew clean build` e `gradlew test` com sucesso.
- `gradlew run` executa o jogo com o mesmo comportamento da Fase 1.
- `ranking.json` gerado na pasta da Fase 2.
- Fontes idênticos aos da Fase 1; `fase-01-refatoracao` e `exercicio10/src` sem alteração.
- Tutorial, README da fase e índice na raiz entregues.

## Estratégia de validação

1. `gradlew --version`, `clean build`, `test`.
2. Teste de fumaça com entrada simulada nas três dificuldades e na saída pelo menu.
3. Verificação do diretório de gravação do `ranking.json`.
4. Build com o Gradle 9.8.2 instalado como checagem de compatibilidade entre versões.
5. Comparação por hash dos fontes e verificação de que as pastas anteriores não mudaram.
6. Build reproduzível a partir de um estado limpo (sem `build/` e `.gradle/`).
