# GRASP na Missão Marte

Estas apostilas estudam a atribuição de responsabilidades usando o código de
[`src/graspexercicio10`](../src/graspexercicio10/README.md). O exercício original
está em [`src/exercicio10`](../src/exercicio10/Main.java), para comparação.

O jogo continua executável no console e está organizado para receber uma
interface gráfica. Os exemplos e diagramas destas páginas descrevem as classes
e os métodos presentes no código atual.

## Roteiro de estudo

1. Leia a [introdução](introducao.md) e o [agrupamento dos princípios](grasp-grouping.md).
2. Acompanhe o [tutorial da refatoração](tutorial-grasp-missao-marte.md).
3. Explore os nove princípios na tabela abaixo.
4. [Compile, execute e teste](RUNNING.md) antes de realizar os exercícios.

| Princípio | Pergunta no jogo | Classes e métodos |
| --- | --- | --- |
| [Information Expert](info-expert.md) | Quem tem os dados para decidir? | `ElementoMapa.ocupa`, `Nave.embarcar`, `Missao.verificaColisao` |
| [Creator](creator.md) | Quem possui os dados de criação? | `FabricaMissao.criar`, criação de `RankingEntry` por `RankingService` |
| [Controller](controller.md) | Quem recebe os eventos da aplicação? | `GameController.executar` |
| [Low Coupling](low-coupling.md) | Quais dependências limitam a evolução? | Controller sem console; jogo sem persistência |
| [High Cohesion](high-cohesion.md) | Quais responsabilidades pertencem juntas? | Domínio, turnos, classificação, apresentação e armazenamento |
| [Polymorphism](polymorphism.md) | Qual comportamento varia por elemento? | `getSimbolo`, `getPontuacao`, `Perigo.atualizar` |
| [Pure Fabrication](pure-fabrication.md) | Que objeto de software ajuda a organizar o projeto? | `MapaRenderer`, `FabricaMissao`, `RankingRepository` |
| [Indirection](indirection.md) | Quem intermedeia a colaboração? | `GameController`, `IRankingRepository` |
| [Protected Variations](protected-variations.md) | Que contrato protege uma mudança prevista? | `ElementoMapa`, ações/eventos, snapshots e repositório |

## Modelo compartilhado

`ElementoMapa` é uma classe abstrata com coordenadas `x` e `y` encapsuladas
e o método abstrato `char getSimbolo()`. `Nave`, `Base`, `Passageiro` e
`Perigo` herdam dela. Cada elemento define seu próprio símbolo.

`Missao.getElementos()` reúne os elementos pelo contrato comum.
`EntidadeEstado` copia símbolo, descrição, nome, coordenadas e a característica
resgatável. `EstadoJogo` reúne essas cópias com os indicadores da partida.

## Fluxo da aplicação

```text
ConsoleJogo → GameController → JogoService → Missao / ElementoMapa
                    ↓
              RankingService → IRankingRepository → RankingRepository

GameController.consultarEstado() → EstadoJogo → MapaRenderer
```

`ConsoleJogo` lê e traduz teclas; `GameController` coordena casos de uso;
`JogoService` executa turnos; `RankingService` decide a classificação;
`RankingRepository` armazena os registros. O símbolo pertence ao modelo e
o desenho do console pertence à apresentação.

Consulte o [índice de diagramas](diagrams/README.md) e o
[guia do código](../src/graspexercicio10/README.md) para navegar pela implementação.
