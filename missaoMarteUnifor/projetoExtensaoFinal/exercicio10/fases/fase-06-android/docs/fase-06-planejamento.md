# Fase 06 - Planejamento

Planejamento aprovado antes da implementação, com o resultado da verificação ao final.

## Objetivo

Adaptar o jogo para Android com libGDX, mantendo: versão desktop, versão Android, código compartilhado e o mesmo servidor de ranking. Controles por toque; suporte a controle Bluetooth opcional. Acréscimos pedidos na aprovação: servidor publicável (teste acadêmico) e tela no jogo para configurar IP e porta do servidor. A publicação por túnel foi dispensada.

## Decisões aprovadas

1. **Layout:** `fase-06-android/{cliente, servidor, docs}`. O cliente é o da Fase 5 mais o módulo `android`; o servidor é o da Fase 5 com acréscimos mínimos e API compatível.
2. **Versões:** libGDX 1.12.1, AGP 8.6.1 com Gradle 8.7, `compileSdk` e `targetSdk` 35, `minSdk` 26.
3. **Configuração do servidor:** tela `ConfigServidorScreen` (endereço, porta, HTTPS, Testar conexão, Salvar, Restaurar padrão), salva nas Preferences (nunca senha nem token). Precedência: salvo > padrão do lançador (`-Dmissaomarte.servidor` ou `-PservidorPadrao`) > `http://localhost:8080`. Trocar de servidor encerra a sessão. Aviso para HTTP fora de rede local.
4. **HTTP no Android:** só no build de debug (placeholder no manifesto); release só HTTPS.
5. **Controles:** botões virtuais (setas, Embarcar, Som, Abortar), toque nos menus, caixa de texto nativa no Android (senha mascarada), modo toque no desktop (`-Dmissaomarte.toque=true`).
6. **Tela:** `ExtendViewport(800, 600)` só no modo toque (controles na área extra à direita); `FitViewport` no desktop, como antes.
7. **Controle Bluetooth:** apenas mapeamento de teclas, testado em unidade e marcado como não verificado.
8. **Empacotamento:** orientação paisagem, ícone gerado a partir de `nave.png`, somente APK de debug.
9. **Publicação do servidor:** `DEPLOY.md` com dois caminhos (Wi-Fi local; contêiner na nuvem), `Dockerfile`, `GET /api/saude`. Sem Flyway, PostgreSQL, limite de login, Compose, Caddy ou túnel.
10. **Validação sem aparelho:** build, testes, inspeção do APK e simulação do toque no desktop; roteiro do celular fica com o usuário.

## Divergências registradas

| Item | Esperado ou mais novo | Usado | Motivo |
| --- | --- | --- | --- |
| libGDX | 1.14.2 (última estável verificada) | 1.12.1 | Mantém a versão das Fases 3 a 5 e a compatibilidade do Gradle 8.7; a troca é isolada em `ext.gdxVersion` |
| Android Gradle Plugin | 9.x (exige Gradle 9.6 ou superior) | 8.6.1 | Compatível com o Gradle 8.7 do cliente, até a API 35, JDK 17 |
| Servidor | Igual ao da Fase 5 | Fase 5 + `/api/saude`, `SERVER_ADDRESS`, `PORT`, encerramento gracioso, `app.jar`, Dockerfile | Necessário para publicar e para o botão Testar conexão; a API existente não mudou |
| `libGDX 1.13+` | exige `android.useAndroidX=true` | já ligado em `gradle.properties` | Facilita a troca de versão |
| Tutorial base | `tutorial-api-gradle-libgdx.md` | fontes oficiais citadas | Regra do PROMPT_MESTRE para as Fases 5 a 7 |

## Arquitetura da mudança

- **Compartilhado (core):** tudo que o jogo faz. Nenhuma classe de plataforma entra em `domain`, `application` ou `infrastructure`.
- **`Plataforma`** (core/gdx): diz se o modo toque está ligado e se há caixa de texto nativa; abre `Gdx.input.getTextInput`. Desktop e Android só diferem em como a constroem.
- **`TelaBase`:** desenha os botões da tela (lista `botoes`) e entrega o toque a `aoTocar(id, ponteiro)`. Cada tela monta a própria lista; o toque chama a mesma ação da tecla.
- **`EnderecoDoServidor`:** guarda o endereço em uso; `ServidorHttp` lê a URL a cada chamada, então a troca vale na hora.
- **Launchers:** `Lwjgl3Launcher` e `AndroidLauncher` são os únicos que sabem montar as dependências para cada plataforma.

Padrões e princípios: Ports and Adapters (`ArmazemDeTexto`, `ServidorDoJogo`), objeto de valor (`ConfiguracaoDoServidor`, `BotaoVirtual`), Template Method (`TelaBase`), DIP/SRP.

## Riscos e mitigação

| Risco | Mitigação |
| --- | --- |
| API do Java mais nova que o Android 8 | `Files.readString`, `List.of`, `isBlank`, `repeat` trocados por equivalentes; varredura por padrões no `core` (main) sem ocorrências restantes |
| Módulo Android quebrar o build do desktop sem SDK | `settings.gradle` só inclui `android` se o SDK existir |
| HTTP aberto em produção | Cleartext só no debug; aviso na tela Servidor; `DEPLOY.md` explica |
| Valor de `-PservidorPadrao` entrar no código Java | Validado por expressão regular antes de virar `BuildConfig` (testado com valor inválido) |
| Dados perdidos em planos gratuitos | Documentado em `DEPLOY.md` |

## Critérios de aceite e resultado

| Critério | Resultado |
| --- | --- |
| Desktop continua funcionando como na Fase 5 | 97 testes anteriores passando; modo padrão usa `FitViewport` e o layout anterior |
| Android compila | `:android:assembleDebug` e `build` OK; APK com 4 ABIs (`arm64-v8a`, `armeabi-v7a`, `x86`, `x86_64`), 25 arquivos de assets, ícones, `minSdk` 26, `targetSdk` 35, permissão só `INTERNET`, paisagem |
| Cleartext só no debug | `usesCleartextTraffic=true` no APK de debug e `false` no manifesto de release |
| Mesmo servidor | O cliente usa a API da Fase 5 sem mudança; `/api/saude` é novo |
| Configurar IP e porta no jogo | Verificado no desktop em modo toque: testar (sucesso e falha), aviso HTTP, endereço vazio, restaurar padrão |
| Toque | Partida inteira vencida só com cliques nos botões virtuais, cadastro e login por toque, ranking global |
| Testes | Cliente 141 (97 + 44 novos), servidor 36 (34 + 2) |
| Lint Android | 3 avisos, nenhum erro |
| Servidor publicável | JAR iniciado com `SERVER_ADDRESS=0.0.0.0`, `PORT=18080` e dados em pasta própria: `/api/saude` responde por `localhost` e pelo IP da máquina; Swagger desligado pelas variáveis `SPRINGDOC_SWAGGERUI_ENABLED` e `SPRINGDOC_APIDOCS_ENABLED` |

## Não verificado

- Execução em aparelho ou emulador (não há emulador, imagem de sistema nem aparelho neste ambiente): desempenho, caixa de texto nativa, tecla Voltar, rotação, áudio e retomada do app.
- Controle Bluetooth.
- `docker build` e `docker run` (o motor do Docker não estava em execução).
- Publicação em nuvem real e HTTPS de ponta a ponta.
- Acesso de outro aparelho pelo Wi-Fi (foi testado só o endereço da própria máquina, o que prova a escuta em `0.0.0.0`).
- Telas de proporções muito diferentes (celulares dobráveis, tablets).
