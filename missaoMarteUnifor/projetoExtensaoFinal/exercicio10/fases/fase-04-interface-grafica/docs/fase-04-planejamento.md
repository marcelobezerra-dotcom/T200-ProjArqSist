# Planejamento da Fase 04 - Interface gráfica

Plano aprovado antes da implementação ("aprovo as recomendações").

## Objetivo

Transformar a apresentação do jogo com sprites, texturas, movimentação, entrada do jogador, colisões, animações, sons e HUD, **preservando as regras de negócio**.

## Estado de origem

- `fase-03-libgdx`: mapa com retângulos coloridos e fonte padrão, sem imagens, animações, sons ou HUD formal; 39 testes.
- `ResultadoTurno` informava só o estado e mensagens de texto, o que impedia reagir ao que aconteceu no turno.
- `GameScreen` misturava desenho do mundo, painel e entrada.

## Decisões aprovadas

| # | Decisão |
|---|---|
| 1 | Assets gerados por um programa auxiliar e versionados em `core/assets` |
| 2 | `ResultadoTurno` ganha uma lista de eventos (aditivo) |
| 3 | Movimento visual suave com lógica imediata, mais repetição ao segurar a tecla |
| 4 | Colisão continua no domínio; a fase adiciona resposta visual e sonora |
| 5 | Efeitos sonoros e música curta em loop, com `M` para silenciar |
| 6 | Fonte padrão do libGDX, com textos da interface acentuados |

## Escopo

| Item | Interpretação |
|---|---|
| Sprites e texturas | Imagens para base, nave, asteroides, inimigos, 3 passageiros, fundo e ícones |
| Movimentação | Deslizamento visual entre células |
| Entrada do jogador | Repetição ao segurar a tecla |
| Colisões | Explosão, tremor, flash, piscar da nave e som |
| Animações | Propulsor, base, inimigo, explosão e brilho |
| Sons | Um efeito por evento e música de fundo |
| HUD | Vidas, pontuação, tempo, slots a bordo, progresso de entregas, mensagens e pontos flutuantes |

Fora do escopo: servidor e login (Fase 5), Android e toque (Fase 6) e novas mecânicas. O jogo não tem tiro, projéteis nem escudo, e nada disso foi criado.

## Versões

JDK 17.0.10, Gradle 8.7 (wrapper), libGDX 1.12.1 e JUnit 5.10.2. Nenhuma dependência nova.

## Referencial teórico usado

Parte 3 (Sprites e Texturas, Sons e Áudio, HUD, Detecção de Colisões e efeito de explosão), Parte 6 (`Nave`, `Asteroide` e `GameScreen` como exemplos), Parte 7 (alternativa com `ShapeRenderer`) e Parte 9 (controles e execução). Complementos da documentação oficial: *2D Animation* e *Managing your assets*.

## Divergências entre o referencial e o projeto

- O referencial usa movimento em tempo real com `delta`; o projeto é por turnos.
- No referencial a entidade carrega a própria `Texture`; aqui as texturas ficam na camada `gdx`.
- O referencial detecta colisão com `Circle`, `Rectangle` e `Intersector`; aqui a colisão é por célula, regra do domínio.
- O referencial descreve tiro, projéteis e escudo de outro projeto; nada disso existe aqui.
- O referencial gera sons por PCM em código; aqui os WAV são gerados uma vez e versionados.
- `core/assets` com `sourceSets...srcDirs`, adiado desde a Fase 3, foi adotado.

## Etapas

1. Pipeline de assets (gerador, `Recursos`, `core/assets` no classpath).
2. Sprites no mapa.
3. Movimentação suave e repetição de tecla.
4. Eventos do turno e animações.
5. Sons.
6. HUD.
7. Documentação.

## Riscos e mitigações

| Risco | Mitigação |
|---|---|
| Alterar `GameSession` quebrar regras | Mudança só aditiva; testes herdados e comparação por hash |
| Arte e áudio gerados por código são simples | Substituíveis; imagens conferidas visualmente |
| Máquina sem dispositivo de áudio | Opção `-Dmissaomarte.semaudio=true` |
| Frames divergirem entre gerador e código | Teste que lê as dimensões dos PNG |
| Vazamento de texturas e sons | Tudo pelo `AssetManager`, liberado em `dispose()` |
| Janela não validável só por terminal | Capturas de tela e execução reprodutível com semente fixa |

## Critérios de conclusão

- `gradlew clean build` com todos os testes passando.
- `gradlew :lwjgl3:run` abre a janela com sprites, animações, HUD e sons, e o fluxo menu, partida e ranking funciona.
- `domain`, `infrastructure` e as regras de `GameSession` preservados.
- `domain`, `application` e `infrastructure` sem import de `com.badlogic`.
- Fases anteriores e `exercicio10/src` sem alteração.
