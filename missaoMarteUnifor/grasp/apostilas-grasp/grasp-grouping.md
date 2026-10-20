# Agrupamento dos princípios GRASP

Este agrupamento é uma organização didática. Os princípios podem atuar juntos
e não impõem uma ordem fixa de implementação.

## Atribuir uma responsabilidade

| Princípio | Aplicação no código |
| --- | --- |
| Information Expert | `ElementoMapa` conhece a posição; `Nave` conhece a capacidade; `Missao` conhece os elementos |
| Creator | `FabricaMissao` monta o cenário; `RankingService` cria `RankingEntry` com dados de uma vitória |
| Controller | `GameController` recebe `AcaoJogo` e coordena os serviços |

## Avaliar a organização

| Princípio | Aplicação no código |
| --- | --- |
| Low Coupling | `JogoService` executa turnos sem depender de console ou ranking |
| High Cohesion | `RankingService` concentra classificação; `RankingRepository` concentra armazenamento |

Esses dois critérios ajudam a avaliar as demais escolhas. Uma classe pequena
ainda pode ter responsabilidades sem relação entre si.

## Tratar variações e colaborações

| Princípio | Aplicação no código |
| --- | --- |
| Polymorphism | `ElementoMapa.getSimbolo()`, `Passageiro.getPontuacao()`, `Perigo.atualizar(...)` |
| Pure Fabrication | Serviços de coordenação, fábrica, renderer e repositório |
| Indirection | Controller entre tela e serviços; repositório entre classificação e arquivo |
| Protected Variations | Hierarquia do mapa, ações/eventos, snapshots e contrato de persistência |

## Como usar em uma revisão

1. Localize o evento da aplicação e quem o recebe.
2. Identifique quem possui os dados para executar cada decisão.
3. Confira quem cria os objetos e de onde vêm os parâmetros.
4. Avalie coesão e dependências.
5. Observe as mudanças esperadas e os contratos que as acomodam.

Exercício: acompanhe o embarque e explique por que `JogoService` soma os pontos,
`Missao` transfere o passageiro e `Nave` decide se há capacidade.

Continue pela [introdução](introducao.md) ou pelo [tutorial](tutorial-grasp-missao-marte.md).

