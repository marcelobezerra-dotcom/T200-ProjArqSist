# Enunciado - Migração do Missão Marte para Gradle

## 1. Contexto

Você recebeu o jogo **Missão Marte** já refatorado em camadas (resultado da Fase 1): `domain`, `application`, `console` e `infrastructure.ranking`. O projeto é compilado manualmente com `javac` por meio de um script (`build.ps1`), não declara dependências e não possui testes automatizados.

A equipe precisa de um build reproduzível, que qualquer pessoa consiga executar com um único comando, e que sirva de base para a Fase 3 (libGDX). Sua tarefa é **migrar o projeto para o Gradle sem alterar o comportamento do jogo** e **documentar o que foi feito e por quê**.

## 2. Objetivos de aprendizagem

- Entender o papel de uma ferramenta de build (tasks, DAG de tarefas e ciclo de vida).
- Montar um projeto Gradle com a DSL Groovy, usando o plugin `application`.
- Usar o layout padrão `src/main/java` e `src/test/java`.
- Declarar dependências com o escopo correto (`implementation`, `testImplementation`, `testRuntimeOnly`).
- Gerar e usar o Gradle Wrapper e explicar a diferença entre o Gradle instalado e o do wrapper.
- Escrever testes unitários com JUnit 5 e executá-los pelo Gradle.
- Validar que a migração não alterou o comportamento do jogo.

## 3. Entrada

Use como ponto de partida a pasta `fase-01-refatoracao` (26 arquivos `.java` em `src/br/edu/unifor/missaomarte/`).

Regras:

- **Não altere** `fase-01-refatoracao` nem o `Main.java` original em `exercicio10/src`. Trabalhe em uma pasta nova (por exemplo, `fase-02-gradle`).
- **Não altere o código-fonte do jogo.** Os arquivos `.java` devem ser apenas movidos ou copiados para o novo layout. Qualquer mudança de comportamento é considerada erro de migração.

## 4. Ambiente

| Item | Requisito |
|---|---|
| JDK | 17 (variável `JAVA_HOME` e `PATH` apontando para ele) |
| Gradle instalado | Qualquer versão compatível com o JDK usado, usada somente para gerar o wrapper |
| Gradle do wrapper | 8.7 |
| Rede | Acesso a `services.gradle.org` e ao Maven Central |

Antes de começar, confira `java -version` e `gradle --version`. O Gradle 8.7 não executa em JDKs muito novos (por exemplo, 25), por isso a versão do JDK importa.

## 5. O que deve ser feito

### 5.1 Estrutura do projeto

Crie a pasta da fase com a estrutura abaixo:

```text
fase-02-gradle/
  settings.gradle
  build.gradle
  gradlew, gradlew.bat
  gradle/wrapper/
  src/main/java/br/edu/unifor/missaomarte/...
  src/test/java/br/edu/unifor/missaomarte/...
  docs/
  README.md
  .gitignore
```

### 5.2 Requisitos do build

1. `settings.gradle` define o nome do projeto.
2. `build.gradle` usa o plugin `application` e define `mainClass` como `br.edu.unifor.missaomarte.Main`.
3. Versão do Java configurada para 17 (compatibilidade de origem e destino).
4. Compilação com codificação UTF-8 em todas as tarefas `JavaCompile`, para que os acentos funcionem no Windows.
5. Repositório de dependências: apenas `mavenCentral()`.
6. A tarefa `run` deve repassar o teclado ao jogo (`standardInput = System.in`). Sem isso, o `Scanner(System.in)` não recebe a entrada.
7. Dependências de teste com JUnit 5 e `useJUnitPlatform()` na tarefa `test`. Inclua o `junit-platform-launcher` em `testRuntimeOnly`, exigido pelo Gradle 9.
8. O jogo em si **não deve** ter dependências de execução.
9. O Gradle Wrapper deve ser gerado na versão **8.7** e versionado (`gradlew`, `gradlew.bat` e `gradle/wrapper/*`).
10. `.gitignore` deve excluir `build/`, `.gradle/`, `out/` e o `ranking.json` gerado em execução.

### 5.3 Testes automatizados

Crie testes JUnit 5 para as regras principais, mantendo a mesma estrutura de pacotes do código:

| Classe testada | O que verificar (no mínimo) |
|---|---|
| `Posicao` | Movimento retorna nova instância; `equals` e `hashCode` por coordenadas. |
| `Nave` | Embarque, capacidade e estado de nave cheia. |
| `Acao` | Conversão de texto digitado em ação, incluindo entrada inválida. |
| `MissaoFactory` | Quantidade de passageiros por dificuldade e ausência de objetos na mesma célula. |
| `GameSession` | Embarque, desembarque na base, colisão e condição de vitória. |
| `FileRankingRepository` | Gravar e ler o ranking usando um arquivo temporário (use `@TempDir`). |

Os testes não podem depender de entrada de teclado nem gravar em arquivos fora de pasta temporária.

### 5.4 Comandos que devem funcionar

Todos executados com o wrapper, na pasta da fase:

```powershell
.\gradlew --version          # deve mostrar Gradle 8.7 e JVM 17
.\gradlew clean build        # compila e testa
.\gradlew test               # apenas os testes
.\gradlew run --console=plain
.\gradlew installDist        # gera build\install\...\bin com o script de execução
```

### 5.5 Preservar o comportamento

Ao final, prove que nada mudou no jogo:

- Os arquivos `.java` de `src/main/java` devem ser **idênticos** aos de `fase-01-refatoracao/src` (compare por hash, por exemplo com `Get-FileHash`).
- O jogo executado por `gradlew run` deve se comportar como na Fase 1 (menu, partida nas três dificuldades, ranking).
- O `ranking.json` deve ser gravado na pasta da Fase 2 (diretório de trabalho da execução).

## 6. Documentação obrigatória

Entregue um arquivo `docs/tutorial.md` (ou nome equivalente) com as seções abaixo.

### 6.1 Estado de origem e destino

- Como era o build antes (script com `javac`, sem dependências, sem testes).
- Como é agora (estrutura de pastas, arquivos de build e comandos).
- Tabela "item da Fase 1 -> equivalente no Gradle" (por exemplo, `build.ps1` -> `gradlew build`; pasta `out` -> `build`; `src` -> `src/main/java`).

### 6.2 Conceitos de Gradle aplicados

Explique, com exemplos do seu próprio `build.gradle`:

- O que são tasks e como elas formam um DAG (indique a cadeia de tarefas disparada por `build`).
- O papel do plugin `application` (quais tarefas ele adiciona).
- A diferença entre `implementation`, `testImplementation` e `testRuntimeOnly`.
- O que é o Wrapper e por que ele é versionado.
- A diferença entre o Gradle instalado na máquina e o Gradle fixado no wrapper.
- As fases do ciclo de vida do build (inicialização, configuração e execução).

### 6.3 Passo a passo da migração

Liste os comandos e as decisões na ordem em que foram feitos, incluindo problemas encontrados (por exemplo, JDK errado no `PATH`, entrada de teclado não chegando ao jogo, acentos quebrados) e como cada um foi resolvido.

### 6.4 Testes

Descreva o que cada classe de teste cobre, mostre o resultado de `gradlew test` e explique como o relatório HTML do Gradle é consultado (`build/reports/tests/test/index.html`).

### 6.5 Evidências de validação

Inclua a saída resumida dos comandos da seção 5.4 e do resultado da comparação por hash dos fontes.

### 6.6 Limitações e próximos passos

Liste pelo menos três pontos que ainda poderiam melhorar (por exemplo, projeto de módulo único, cobertura de testes parcial, limitações herdadas da Fase 1) e relacione com a Fase 3 (libGDX e multi-módulo).

## 7. Roteiro mínimo de validação

Execute e registre o resultado de cada passo:

1. `gradlew --version` mostra Gradle 8.7 e JVM 17.
2. `gradlew clean build` termina com `BUILD SUCCESSFUL`.
3. `gradlew test` executa todos os testes com sucesso (informe a quantidade).
4. `gradlew run --console=plain`: menu, partida nas dificuldades fácil, médio e difícil, e saída pelo menu.
5. Uma vitória grava o ranking e `ranking.json` aparece na pasta da Fase 2.
6. Comparação por hash: todos os `.java` de `src/main/java` são idênticos aos da Fase 1.
7. Build a partir de um estado limpo: apague `build/` e `.gradle/` e repita o passo 2.
8. Confirme que `fase-01-refatoracao` e `exercicio10/src` não foram alterados.

## 8. Entregáveis

1. Pasta `fase-02-gradle` com o projeto Gradle, o wrapper e o `.gitignore`.
2. Testes JUnit 5 que passam com `gradlew test`.
3. `README.md` da fase com instruções de execução.
4. Documento `tutorial.md` com todas as seções da etapa 6.
5. Evidências do roteiro da etapa 7.

## 9. Critérios de avaliação

| Critério | Peso |
|---|---|
| Projeto Gradle correto (`settings.gradle`, `build.gradle`, layout, plugin `application`, Java 17, UTF-8) | 20% |
| Wrapper 8.7 gerado e funcionando; comandos da seção 5.4 executam | 15% |
| Jogo preservado (fontes idênticos, `run` com teclado, ranking na pasta correta) | 20% |
| Testes JUnit 5 relevantes, isolados e passando | 20% |
| Documentação dos conceitos de Gradle com exemplos reais do projeto | 15% |
| Evidências, limitações e relação com a Fase 3 | 5% |
| Clareza e organização do documento | 5% |

## 10. Observações

- Alterar o código do jogo para "facilitar" o build ou os testes reduz a nota. Se achar que algo precisa mudar, registre como limitação e proponha a mudança para a próxima fase.
- Descrever um comando ou configuração que você não executou reduz a nota da seção correspondente. O documento deve refletir o projeto entregue.
- Registrar um problema real encontrado no caminho e a forma como foi resolvido vale mais do que afirmar que tudo funcionou de primeira.
- É permitido usar ferramentas de apoio, mas cada arquivo, classe e comando citado precisa existir no seu projeto.

## 11. Desafio opcional

1. Executar o build também com outra versão do Gradle instalada (por exemplo, 9.x) e explicar as diferenças encontradas.
2. Configurar a tarefa `test` para exibir o resultado de cada teste no console (`testLogging`).
3. Gerar um `distZip` e executar o jogo a partir do pacote extraído, em outra pasta.
4. Adicionar uma tarefa Gradle própria que apaga o `ranking.json` gerado.
