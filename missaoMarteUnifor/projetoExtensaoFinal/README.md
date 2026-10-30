# Missão Marte - Evolução Arquitetural

Projeto de ensino de evolução arquitetural de software: o jogo do Exercício 10 é transformado em fases independentes. O código original fica preservado em `exercicio10/src`.

## Fases

| Fase | Pasta | Situação |
|---|---|---|
| Original | `exercicio10/src` | Preservado, sem alterações |
| 1 - Refatoração arquitetural | `exercicio10/fases/fase-01-refatoracao` | Concluída |
| 2 - Migração para Gradle | `exercicio10/fases/fase-02-gradle` | Concluída |
| 3 - Introdução ao libGDX | `exercicio10/fases/fase-03-libgdx` | Concluída |
| 4 a 7 | `fase-04-interface-grafica`, `fase-05-servidor-ranking`, `fase-06-android`, `fase-07-evolucao-livre` | Não iniciadas |

Cada fase parte da versão concluída da fase anterior e tem seu próprio tutorial em `docs/fase-XX-tutorial.md`.

## Como executar

### Fase 1 (compilação com `javac`)

```powershell
cd exercicio10\fases\fase-01-refatoracao
.\build.ps1 -Run
```

### Fase 2 (Gradle)

```powershell
cd exercicio10\fases\fase-02-gradle
.\gradlew.bat build
.\gradlew.bat run --console=plain
```

### Fase 3 (libGDX, janela desktop)

```powershell
cd exercicio10\fases\fase-03-libgdx
.\gradlew.bat build
.\gradlew.bat :lwjgl3:run --console=plain
```

## Requisitos

- JDK 17 no PATH e em `JAVA_HOME`.
- Gradle instalado é opcional: as Fases 2 e 3 usam o Gradle Wrapper.
- Fase 3: placa de vídeo com OpenGL 2.0 ou superior.

## Material de apoio

- [Tutorial de API, Gradle e libGDX](tutorial-api-gradle-libgdx.md): referencial teórico das Fases 2, 3 e 4.
- [Prompt mestre](PROMPT_MESTRE.md): regras de execução das fases.
- Tutorial da Fase 1: [docs/fase-01-tutorial.md](exercicio10/fases/fase-01-refatoracao/docs/fase-01-tutorial.md)
- Tutorial da Fase 2: [docs/fase-02-tutorial.md](exercicio10/fases/fase-02-gradle/docs/fase-02-tutorial.md)
- Tutorial da Fase 3: [docs/fase-03-tutorial.md](exercicio10/fases/fase-03-libgdx/docs/fase-03-tutorial.md)
