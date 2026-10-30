# Fase 02 - Migração para Gradle (Console)

O mesmo jogo da Fase 1, agora compilado, testado e executado pelo Gradle. Nenhuma classe do jogo foi alterada: os fontes foram apenas movidos para o layout padrão `src/main/java`.

## Requisitos

- JDK 17 (`java -version` e `JAVA_HOME`).
- Não é preciso instalar o Gradle: o projeto inclui o Gradle Wrapper (versão 8.7). Na primeira execução ele baixa a distribuição, que fica em cache em `~/.gradle`.

## Comandos

Execute na pasta `fase-02-gradle`:

```powershell
.\gradlew.bat --version                 # Gradle e JVM em uso
.\gradlew.bat build                     # compila, testa e empacota
.\gradlew.bat test                      # apenas os testes
.\gradlew.bat run --console=plain       # executa o jogo
.\gradlew.bat clean                     # remove a pasta build
.\gradlew.bat tasks                     # lista as tarefas
```

Em Linux ou macOS use `./gradlew` no lugar de `.\gradlew.bat`.

O `ranking.json` é gravado na pasta `fase-02-gradle` (ignorado pelo Git).

## Estrutura

```text
fase-02-gradle/
├── settings.gradle
├── build.gradle
├── gradlew, gradlew.bat
├── gradle/wrapper/
├── docs/
└── src/
    ├── main/java/br/edu/unifor/missaomarte/...   # código do jogo (igual à Fase 1)
    └── test/java/br/edu/unifor/missaomarte/   # testes JUnit 5
```

## Documentação

- [Planejamento](docs/fase-02-planejamento.md)
- [Enunciado para o aluno](docs/fase-02-enunciado.md)
- [Tutorial](docs/fase-02-tutorial.md)
