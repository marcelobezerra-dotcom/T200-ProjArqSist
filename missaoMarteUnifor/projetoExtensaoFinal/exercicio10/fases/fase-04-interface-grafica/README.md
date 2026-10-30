# Fase 04 - Interface gráfica

Missão Marte com sprites, animações, sons e HUD em libGDX. As regras do jogo são as da Fase 3: o jogo continua por turnos e só a apresentação mudou.

## Estrutura

```text
fase-04-interface-grafica/
  core/
    assets/imagens, assets/sons     imagens (PNG) e sons (WAV) do jogo
    src/main/java/.../domain, application, infrastructure   regras (como na Fase 3)
    src/main/java/.../gdx           telas e apresentação
        recursos/  catálogo e carga dos assets (AssetManager)
        visual/    posição visual, efeitos, tremor, textos flutuantes, desenho do mundo
        audio/     efeitos e música
        hud/       HUD da partida
  lwjgl3/                           launcher desktop
  tools/GeradorDeAssets.java        gera as imagens e os sons de core/assets
  docs/                             planejamento e tutorial
```

## Requisitos

- JDK 17 no PATH e em `JAVA_HOME`.
- Placa de vídeo com OpenGL 2.0 ou superior e, para ouvir o jogo, um dispositivo de áudio.
- Gradle instalado é opcional: use o Gradle Wrapper (8.7).

## Como executar

```powershell
cd exercicio10\fases\fase-04-interface-grafica
.\gradlew.bat clean build                    # compila e executa os 66 testes
.\gradlew.bat :lwjgl3:run --console=plain    # abre a janela
```

Opções de execução (propriedades do Java, repassadas pelo Gradle):


| Opção                       | Efeito                                                                 |
| ------------------------------- | ------------------------------------------------------------------------ |
| `-Dmissaomarte.semente=4`     | Repete exatamente o mesmo cenário e os mesmos movimentos dos inimigos |
| `-Dmissaomarte.semaudio=true` | Desativa o áudio (para máquinas sem dispositivo de som)              |

Exemplo: `.\gradlew.bat :lwjgl3:run -Dmissaomarte.semente=4 --console=plain`.

O `ranking.json` é gravado nesta pasta.

## Controles


| Tela                               | Teclas                                                                                                     |
| ------------------------------------ | ------------------------------------------------------------------------------------------------------------ |
| Menu                               | `1` Jogar, `2` Ver ranking, `3` Limpar ranking, `4` Sair                                                   |
| Nova partida                       | digitar o nome, `Backspace` apaga, setas esquerda/direita mudam a dificuldade, `Enter` decola, `Esc` volta |
| Partida                            | `W/A/S/D` ou setas movem (segure a tecla para repetir), `E` embarca, `Q` ou `Esc` aborta                   |
| Fim de partida e ranking           | `Enter` continua                                                                                           |
| Qualquer tela, exceto Nova partida | `M` liga ou desliga o som                                                                                  |

## Imagens e sons

Os arquivos de `core/assets` foram gerados por `tools/GeradorDeAssets.java` e estão versionados. Para regerá-los:

```powershell
java tools\GeradorDeAssets.java
```

Para usar arte ou sons próprios, substitua os arquivos mantendo o nome e as dimensões (um teste confere).

## Documentação

- [Planejamento](docs/fase-04-planejamento.md)
- [Enunciado para o aluno](docs/fase-04-enunciado.md)
- [Tutorial](docs/fase-04-tutorial.md)
