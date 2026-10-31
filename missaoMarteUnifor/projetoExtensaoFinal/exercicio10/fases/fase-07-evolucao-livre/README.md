# Fase 07 - Evolução livre

Missão Marte evoluído **sem quebrar a arquitetura** das fases anteriores. Esta fase traz o exemplo de referência de como estender o jogo e o roteiro para os alunos fazerem as próprias melhorias.

O que mudou em relação à Fase 6:

- **Modo dinâmico (tempo real)**, agora o padrão: o jogo corre sozinho, no ritmo dos quadros. Inimigos e relógio andam sem você apertar nada; a nave anda enquanto você segura a direção. O **modo clássico** (por turnos) continua igual ao da Fase 6.
- **Tiro:** nova ação `ATIRAR` com munição limitada.
- **Inimigo perseguidor:** segue a nave (padrão Strategy no movimento dos inimigos).
- **Layout responsivo:** a tela da partida se adapta a paisagem, retrato e tamanhos de tela (4:3, 16:9, 20:9), com alvos de toque de pelo menos 48 dp, margens para recortes e texto que cresce no celular.

O servidor é **idêntico** ao da Fase 6 (conferido por hash).

## Estrutura

```text
fase-07-evolucao-livre/
  cliente/                       jogo libGDX. Gradle 8.7, libGDX 1.12.1, AGP 8.6.1
    core/
      domain/                    + MovimentoDoInimigo (Strategy), MovimentoAleatorio, MovimentoPerseguidor, ResultadoDoTiro; Nave com munição
      application/               + ModoDeJogo, Temporizador, ConfiguracaoDeTempoReal, RegrasDeTempoReal; GameSession com o modo dinâmico
      gdx/layout/                LayoutDaPartida, RetanguloDeTela, MargensSeguras, EscalaDeTela (sem libGDX)
      gdx/entrada/               ControleDeToque (layout por orientação), BotaoVirtual
      gdx/                       GameScreen (dois modos), NovaPartidaScreen (escolha do modo), TelaBase (alvos de toque)
      gdx/hud/                   HudRenderer guiado pelo layout
    lwjgl3/                      launcher desktop
    android/                     launcher Android (orientação livre pelo sensor)
    tools/                       GeradorDeAssets (fases anteriores) e GeradorDeNovosAssets (perseguidor e som do tiro)
  servidor/                      igual ao da Fase 6 (API, /api/saude, Dockerfile, DEPLOY.md)
  docs/                          planejamento, tutorial e enunciado
```

## Requisitos

- JDK 17 em `JAVA_HOME` (confira o `PATH`: o `java` que aparece primeiro precisa ser o 17).
- Desktop: OpenGL 2.0 ou superior.
- APK: Android SDK (plataforma 35 e build-tools 34.0.0) em `cliente/local.properties` (`sdk.dir=C:/Users/voce/AppData/Local/Android/Sdk`, ignorado pelo git) ou em `ANDROID_HOME`. Sem o SDK, o módulo `android` é ignorado.
- Celular com Android 8.0 (API 26) ou superior. O emulador não foi instalado neste ambiente.

## Como executar

```powershell
# servidor (opcional: sem ele o jogo funciona como convidado)
cd exercicio10\fases\fase-07-evolucao-livre\servidor
.\gradlew.bat test                        # 36 testes
.\gradlew.bat bootRun --console=plain

# cliente
cd exercicio10\fases\fase-07-evolucao-livre\cliente
.\gradlew.bat clean build                 # 208 testes; gera os APKs se houver SDK
.\gradlew.bat :lwjgl3:run --console=plain
.\gradlew.bat :android:assembleDebug      # APK em android\build\outputs\apk\debug
```

Opções do desktop:

| Opção | Efeito |
| --- | --- |
| `-Dmissaomarte.toque=true` | Modo toque: o mouse faz o papel do dedo, com botões virtuais e o layout responsivo |
| `-Dmissaomarte.janela=540x1200` | Tamanho da janela, para simular telas de celular (retrato, 4:3, etc.) |
| `-Dmissaomarte.servidor=http://localhost:8080` | Endereço padrão do servidor |
| `-Dmissaomarte.semente=4` | Cenário reproduzível (a primeira partida) |
| `-Dmissaomarte.semaudio=true` | Sem áudio |

Instalar no celular, endereço do servidor e publicação: veja o [README da Fase 6](../fase-06-android/README.md) e o `servidor/DEPLOY.md` (nada disso mudou).

## Modos de jogo

Na tela **Nova partida** escolha a dificuldade e o modo (setas esquerda/direita mudam a dificuldade; cima/baixo ou `Tab` mudam o modo; no toque, os botões `<` e `>`).

| | Dinâmico (padrão) | Clássico |
| --- | --- | --- |
| Tempo | Corre sozinho. Contagem de 3 s antes de começar | Espera a sua jogada |
| Nave | Anda 1 célula a cada 0,15 s **enquanto a direção estiver pressionada** | Uma célula por tecla (a tecla segurada repete) |
| Embarque | Automático ao passar sobre o passageiro | Tecla `E` |
| Pontuação | −1 ponto por segundo | −1 ponto por movimento |
| Inimigos | Andam no ritmo da dificuldade (0,9 s, 0,7 s ou 0,5 s por passo) | Andam a cada jogada |
| Perseguidores | Médio: 1. Difícil: 2 | Não há |
| Tiro | 3 disparos, 2 pontos cada, recarga de 0,6 s | Não há |
| Pausa | Sim | Não |

O **perseguidor** (disco azul com alienígena laranja) anda um passo em direção à nave e é mais lento que ela (0,8 s no médio, 0,55 s no difícil). O **tiro** sai na última direção pressionada e destrói o primeiro asteroide ou inimigo da linha.

## Controles

| Ação | Teclado | Toque |
| --- | --- | --- |
| Mover (dinâmico) | `W A S D` ou setas, **segurando** | Segurar uma seta |
| Mover (clássico) | `W A S D` ou setas | Setas (repetem ao segurar) |
| Atirar | `F` ou `Espaço` (controle: `A`) | Botão Atirar |
| Embarcar (clássico) | `E` | Botão Embarcar |
| Pausa | `P` | Botão Pausa; tecla Voltar do Android |
| Som | `M` | Botão Som |
| Abortar | `Q` ou `Esc` (controle: `B`) | Botão Abortar |

O resto (menu, entrada, ranking, servidor) é como na Fase 6.

## Layout responsivo

`LayoutDaPartida` divide a tela em barra superior, mapa, painel de informações, controles e rodapé a partir do tamanho visível:

- **Paisagem:** `[mapa] [painel] [controles]`. O mapa cresce até ocupar a altura; em telas 4:3 ele encolhe para os controles não ficarem sobre o painel.
- **Retrato:** barra, mapa (usa a largura), faixa de informações e controles embaixo, perto do polegar. O manifesto agora segue o sensor (`fullSensor`).
- Botões com no mínimo 48 dp (convertidos para a escala da tela), margens seguras para recortes e barras do sistema, e texto maior em telas de alta densidade.
- No desktop sem toque a partida continua em 800x600 e o layout é o mesmo da Fase 6 (conferido por teste).

## Limitações conhecidas

- **Não foi testado em aparelho nem emulador.** A jogabilidade em tempo real (velocidades, recarga, penalidade por segundo), o toque, a rotação, as margens de recorte e a pausa ao ir para segundo plano foram verificados só por testes de unidade com tempo simulado e por simulação no desktop (modo toque, janelas de vários formatos).
- Telas de menu, nova partida, entrada, servidor e ranking continuam desenhadas numa área fixa de 800x600, centralizada. Em retrato elas aparecem menores e, em telas estreitas, botões ampliados para 48 dp podem encostar nos vizinhos (vale o mais próximo do dedo). Só o menu e a partida foram adaptados.
- O ranking global mistura partidas dos dois modos (o servidor não mudou e não sabe o modo). A pontuação continua informada pelo cliente e não é verificável.
- Controle Bluetooth: só o mapeamento de teclas, não verificado com controle real.
- `docker build`, publicação em nuvem e acesso de outro aparelho pela rede Wi-Fi não foram verificados nesta fase.
- `GameSession` ficou com cerca de 450 linhas por reunir os dois modos. Uma extração das regras comuns é uma melhoria sugerida no tutorial.

## Documentação

- [Planejamento](docs/fase-07-planejamento.md)
- [Enunciado para o aluno](docs/fase-07-enunciado.md)
- [Tutorial](docs/fase-07-tutorial.md)
