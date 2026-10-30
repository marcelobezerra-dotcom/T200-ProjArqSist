# Missão Marte - Evolução Arquitetural

Projeto de ensino de evolução arquitetural de software: o jogo do Exercício 10 é transformado em fases independentes. O código original fica preservado em `exercicio10/src`.

## Fases

| Fase | Pasta | Situação |
|---|---|---|
| Original | `exercicio10/src` | Preservado, sem alterações |
| 1 - Refatoração arquitetural | `exercicio10/fases/fase-01-refatoracao` | Concluída |
| 2 - Migração para Gradle | `exercicio10/fases/fase-02-gradle` | Concluída |
| 3 - Introdução ao libGDX | `exercicio10/fases/fase-03-libgdx` | Concluída |
| 4 - Interface gráfica | `exercicio10/fases/fase-04-interface-grafica` | Concluída |
| 5 - Servidor e ranking global | `exercicio10/fases/fase-05-servidor-ranking` | Concluída |
| 6 - Android | `exercicio10/fases/fase-06-android` | Concluída (sem teste em aparelho; veja o README da fase) |
| 7 | `fase-07-evolucao-livre` | Não iniciada |

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

### Fase 4 (interface gráfica: sprites, animações, sons e HUD)

```powershell
cd exercicio10\fases\fase-04-interface-grafica
.\gradlew.bat build
.\gradlew.bat :lwjgl3:run --console=plain
```

Opções: `-Dmissaomarte.semente=4` (cenário reprodutível) e `-Dmissaomarte.semaudio=true` (sem áudio).

### Fase 5 (servidor Spring Boot e ranking global)

Dois terminais. O servidor (Gradle 9.8.2) e o cliente (Gradle 8.7) são projetos independentes.

```powershell
# terminal 1: servidor em http://localhost:8080
cd exercicio10\fases\fase-05-servidor-ranking\servidor
.\gradlew.bat test
.\gradlew.bat bootRun --console=plain

# terminal 2: cliente (modo convidado sem servidor; no menu, 4 abre a tela de entrada)
cd exercicio10\fases\fase-05-servidor-ranking\cliente
.\gradlew.bat build
.\gradlew.bat :lwjgl3:run --console=plain
```

Opção do cliente: `-Dmissaomarte.servidor=http://localhost:8080`. Variáveis do servidor: `MISSAOMARTE_JWT_SEGREDO` e `MISSAOMARTE_DADOS`.

### Fase 6 (Android, controles por toque e servidor publicável)

```powershell
# terminal 1: servidor (aceita outros aparelhos só com SERVER_ADDRESS=0.0.0.0, veja servidor\DEPLOY.md)
cd exercicio10\fases\fase-06-android\servidor
.\gradlew.bat test
.\gradlew.bat bootRun --console=plain

# terminal 2: cliente desktop e APK (o APK exige o Android SDK em local.properties ou ANDROID_HOME)
cd exercicio10\fases\fase-06-android\cliente
.\gradlew.bat clean build
.\gradlew.bat :lwjgl3:run --console=plain
.\gradlew.bat :android:assembleDebug
```

Opções do desktop: `-Dmissaomarte.toque=true` (modo toque com o mouse) e `-Dmissaomarte.servidor=...`. No jogo, a opção `6) Servidor` configura endereço, porta e HTTPS. Detalhes, instalação no celular e limitações no [README da fase](exercicio10/fases/fase-06-android/README.md).

## Requisitos

- JDK 17 no PATH e em `JAVA_HOME`.
- Gradle instalado é opcional: as Fases 2 a 5 usam o Gradle Wrapper.
- Fases 3, 4 e 5 (cliente): placa de vídeo com OpenGL 2.0 ou superior; Fases 4 e 5: dispositivo de áudio para ouvir o jogo.
- Fase 5: nenhum banco de dados a instalar (o servidor usa H2 em arquivo).
- Fase 6: Android SDK (plataforma 35 e build-tools 34.0.0) só para gerar o APK; sem ele o módulo `android` é ignorado. Celular com Android 8.0 ou superior.

## Material de apoio

- [Tutorial de API, Gradle e libGDX](tutorial-api-gradle-libgdx.md): referencial teórico das Fases 2, 3 e 4.
- [Prompt mestre](PROMPT_MESTRE.md): regras de execução das fases.
- Tutorial da Fase 1: [docs/fase-01-tutorial.md](exercicio10/fases/fase-01-refatoracao/docs/fase-01-tutorial.md)
- Tutorial da Fase 2: [docs/fase-02-tutorial.md](exercicio10/fases/fase-02-gradle/docs/fase-02-tutorial.md)
- Tutorial da Fase 3: [docs/fase-03-tutorial.md](exercicio10/fases/fase-03-libgdx/docs/fase-03-tutorial.md)
- Tutorial da Fase 4: [docs/fase-04-tutorial.md](exercicio10/fases/fase-04-interface-grafica/docs/fase-04-tutorial.md)
- Tutorial da Fase 5: [docs/fase-05-tutorial.md](exercicio10/fases/fase-05-servidor-ranking/docs/fase-05-tutorial.md) (a Fase 5 não usa o tutorial de API, Gradle e libGDX como base)
- Tutorial da Fase 6: [docs/fase-06-tutorial.md](exercicio10/fases/fase-06-android/docs/fase-06-tutorial.md) (também com fontes oficiais; publicação do servidor em [DEPLOY.md](exercicio10/fases/fase-06-android/servidor/DEPLOY.md))
