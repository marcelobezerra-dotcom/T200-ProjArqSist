# Fase 06 - Android

Missão Marte no celular. O mesmo código do jogo roda no **desktop** e no **Android** (LibGDX), e as duas versões usam o mesmo servidor de ranking da Fase 5. O servidor pode ser publicado (rede Wi-Fi local ou contêiner na nuvem) e o endereço dele é configurado dentro do jogo.

> Este é um exemplo acadêmico. A publicação serve para testar no celular; não há alta disponibilidade, backup nem proteção contra abuso.

## Estrutura

```text
fase-06-android/
  cliente/                       jogo libGDX. Gradle 8.7, libGDX 1.12.1, AGP 8.6.1
    core/                        código compartilhado (domain, application, infrastructure, gdx)
        application/servidor/    + ConfiguracaoDoServidor, EnderecoDoServidor, repositório de configuração
        gdx/entrada/             botões virtuais (BotaoVirtual, ControleDeToque)
        gdx/plataforma/          Plataforma (toque, teclado nativo) e Preferences
        gdx/                     + ConfigServidorScreen, TelaBase com toque
    lwjgl3/                      launcher desktop (teclado e mouse; modo toque opcional)
    android/                     launcher Android, manifesto, ícone
    tools/                       GeradorDeAssets, GeradorDeIcone
  servidor/                      API da Fase 5 + /api/saude + Dockerfile + DEPLOY.md. Gradle 9.8.2
  docs/                          planejamento e tutorial
```

O `domain` é idêntico ao da Fase 5. Só `gdx`, `lwjgl3` e `android` importam `com.badlogic`.

## Requisitos

- JDK 17 em `JAVA_HOME` (confira o `PATH`: o `java` que aparece primeiro precisa ser o 17).
- Desktop: OpenGL 2.0 ou superior.
- Android (só para gerar o APK): Android SDK com `platforms;android-35` e `build-tools;34.0.0`. Informe o caminho em `cliente/local.properties` (`sdk.dir=C:/Users/voce/AppData/Local/Android/Sdk`, arquivo ignorado pelo git) ou na variável `ANDROID_HOME`. **Sem o SDK, o módulo `android` é ignorado** e o desktop continua compilando.
- Celular com Android 8.0 (API 26) ou superior. O emulador não foi instalado neste ambiente.

## Como executar

### Servidor (porta 8080)

```powershell
cd exercicio10\fases\fase-06-android\servidor
.\gradlew.bat test                        # 36 testes
.\gradlew.bat bootRun --console=plain
```

### Cliente desktop

```powershell
cd exercicio10\fases\fase-06-android\cliente
.\gradlew.bat clean build                 # 141 testes; gera também os APKs se houver SDK
.\gradlew.bat :lwjgl3:run --console=plain
```

Opções do desktop:

| Opção | Efeito |
| --- | --- |
| `-Dmissaomarte.servidor=http://localhost:8080` | Endereço **padrão** do servidor (o salvo na tela Servidor vale mais) |
| `-Dmissaomarte.toque=true` | Modo toque: o mouse faz o papel do dedo, com botões virtuais (janela 1200x540) |
| `-Dmissaomarte.semente=4` | Cenário reproduzível |
| `-Dmissaomarte.semaudio=true` | Sem áudio |

### App Android

```powershell
cd exercicio10\fases\fase-06-android\cliente
.\gradlew.bat :android:assembleDebug
# APK: android\build\outputs\apk\debug\android-debug.apk
adb install -r android\build\outputs\apk\debug\android-debug.apk
```

O endereço padrão embutido no app é `http://localhost:8080`, que funciona com o celular ligado por USB e `adb reverse tcp:8080 tcp:8080`. Para outro padrão, use `-PservidorPadrao`:

| Situação | Comando |
| --- | --- |
| Emulador falando com o servidor do PC | `-PservidorPadrao=http://10.0.2.2:8080` |
| Celular no mesmo Wi-Fi do PC | `-PservidorPadrao=http://192.168.0.10:8080` (o IP do PC) |
| Servidor na nuvem | `-PservidorPadrao=https://meu-servico.exemplo.com` |

Em qualquer caso o jogador pode trocar o endereço no jogo: menu, opção `6) Servidor`.

## Controles

| Tela | Teclado (desktop) | Toque (celular ou `-Dmissaomarte.toque=true`) |
| --- | --- | --- |
| Menu | `1` a `6` | toque na opção; Voltar do Android sai |
| Entrar / Criar conta | digitar, `Tab`, `F2` alterna modo, `F3` servidor, `Enter`, `Esc` | toque nos campos (abre o teclado do sistema), botões Entrar/Criar conta, Servidor, Voltar |
| Servidor | digitar, `Tab`, `F2` HTTPS, `F5` testar, `Enter` salvar, `F6` padrão, `Esc` | toque nos campos e botões Testar conexão, Salvar, Restaurar padrão, Voltar |
| Nova partida | nome, setas, `Enter` | toque no nome, botões `<` `>`, Decolar, Voltar |
| Partida | `W A S D` ou setas, `E` embarcar, `M` som, `Q`/`Esc` abortar | botões virtuais: setas (repetem ao segurar), Embarcar, Som, Abortar |
| Fim da partida | `Enter`, `R` reenvia | botões Ver ranking ou Voltar ao menu, Tentar de novo |
| Controle Bluetooth | direcional = setas, `A` = embarcar, `B` = abortar | (não verificado, ver limitações) |

## Servidor: publicação

O passo a passo está em [servidor/DEPLOY.md](servidor/DEPLOY.md): Wi-Fi local (`SERVER_ADDRESS=0.0.0.0`, firewall, `ipconfig`) e contêiner Docker na nuvem. O servidor ganhou `GET /api/saude` (público) e lê `PORT`, `SERVER_ADDRESS`, `MISSAOMARTE_JWT_SEGREDO` e `MISSAOMARTE_DADOS`.

## Limitações conhecidas

- **Não foi testado em aparelho nem emulador.** Foram verificados: compilação, 141 testes do cliente, 36 do servidor, conteúdo do APK, manifesto e o fluxo completo no desktop em modo toque. Veja o planejamento para a lista do que depende do celular.
- Controle Bluetooth: só o mapeamento de teclas, testado em unidade e **não verificado com controle real**.
- A imagem Docker **não foi construída** (o Docker Desktop não estava em execução). O servidor foi validado como JAR com as mesmas variáveis de ambiente.
- Publicação em nuvem real não foi feita. Planos gratuitos costumam apagar o banco H2 (sistema de arquivos efêmero) e desligar o serviço por inatividade.
- HTTP puro só em rede local de confiança. O APK de debug aceita HTTP; o release só aceita HTTPS.
- APK de debug apenas (sem assinatura de release nem publicação na loja).
- Telas 4:3 (por exemplo tablets): os botões virtuais ficam sobre o painel lateral da partida.

## Documentação

- [Planejamento](docs/fase-06-planejamento.md)
- [Enunciado para o aluno](docs/fase-06-enunciado.md)
- [Tutorial](docs/fase-06-tutorial.md)
- [Publicação do servidor](servidor/DEPLOY.md)
