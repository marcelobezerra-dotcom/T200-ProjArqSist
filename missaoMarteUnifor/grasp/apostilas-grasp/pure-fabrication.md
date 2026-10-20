# Pure Fabrication

Quando atribuir uma responsabilidade a uma entidade do domínio prejudicaria
a coesão ou aumentaria o acoplamento, crie um objeto de software para cuidar dela.

## Fabricações no exemplo

| Classe | Responsabilidade concentrada |
| --- | --- |
| `FabricaMissao` | Montar o cenário e distribuir posições livres |
| `JogoService` | Coordenar os turnos e o ciclo da partida |
| `RankingService` | Aplicar a classificação e criar os registros |
| `RankingRepository` | Implementar o armazenamento em arquivo |
| `MapaRenderer` | Produzir a saída textual |

Esses objetos ajudam a organizar o software. Já `Nave`, `Passageiro`,
`Perigo`, `Base` e `Missao` expressam conceitos do jogo.

## Escolha da responsabilidade

O símbolo faz parte da abstração `ElementoMapa`, conforme o modelo adotado
neste exercício. O elemento fornece `getSimbolo()`; a apresentação decide
como imprimir a grade e as mensagens. Fabricar um renderer mantém a formatação
fora das entidades sem retirar delas o contrato do símbolo.

Persistir JSON exige lidar com caminho, codificação e arquivo. Essa tarefa
fica em `RankingRepository`, acessado por `RankingService` através de
`IRankingRepository`.

## Composição atual

Trecho do [Main](../src/graspexercicio10/Main.java):

```java
RankingService ranking = new RankingService(new RankingRepository(Paths.get("ranking-grasp.json")));
GameController controller = new GameController(new JogoService(), ranking);
try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
    new ConsoleJogo(controller, new MapaRenderer(System.out), scanner).iniciar();
}
```

O serviço de jogo não desenha a partida nem grava o ranking. O controller
coordena o registro de uma vitória, e o console aciona o renderer.

Diagramas editáveis: [classes](diagrams/pure-fabrication-class.mmd) e
[sequência](diagrams/pure-fabrication-sequence.mmd).

## Exercício

Explique por que criar uma classe de software pode favorecer alta coesão.
Use a distribuição entre `Missao`, `FabricaMissao` e `MapaRenderer`.

