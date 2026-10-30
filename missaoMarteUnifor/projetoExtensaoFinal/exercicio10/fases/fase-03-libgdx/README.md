# Fase 03 - Introdução ao libGDX

Jogo Missão Marte com janela gráfica em libGDX, reaproveitando as regras da Fase 2. O jogo continua por turnos e usa formas geométricas e a fonte padrão (sem imagens ou sons, que ficam para a Fase 4).

## Estrutura

```text
fase-03-libgdx/
  core/     jogo independente de plataforma (domain, application, infrastructure, gdx)
  lwjgl3/   launcher desktop (Lwjgl3Launcher)
  docs/     planejamento e tutorial
```

## Requisitos

- JDK 17 no PATH e em `JAVA_HOME`.
- Placa de vídeo com suporte a OpenGL 2.0 ou superior.
- Gradle instalado é opcional: use o Gradle Wrapper (8.7).

## Como executar

```powershell
cd exercicio10\fases\fase-03-libgdx
.\gradlew.bat clean build          # compila e executa os 39 testes
.\gradlew.bat :lwjgl3:run --console=plain
```

O `ranking.json` é gravado nesta pasta.

## Controles

| Tela | Teclas |
|---|---|
| Menu | `1` Jogar, `2` Ver ranking, `3` Limpar ranking, `4` Sair |
| Nova partida | digitar o nome, `Backspace` apaga, setas esquerda/direita mudam a dificuldade, `Enter` decola, `Esc` volta |
| Partida | `W/A/S/D` ou setas movem, `E` embarca, `Q` ou `Esc` aborta |
| Fim de partida e ranking | `Enter` continua |

## Documentação

- [Planejamento](docs/fase-03-planejamento.md)
- [Enunciado para o aluno](docs/fase-03-enunciado.md)
- [Tutorial](docs/fase-03-tutorial.md)
