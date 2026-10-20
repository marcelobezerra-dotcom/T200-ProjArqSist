# High Cohesion

Mantenha juntas as responsabilidades relacionadas e separe as que mudam por
motivos diferentes. O tamanho de uma classe, sozinho, não mede sua coesão.

## Distribuição das responsabilidades

| Classe | Foco |
| --- | --- |
| `ElementoMapa` | Coordenadas, operações de posição e contrato do símbolo |
| `Nave` | Movimento comandado, capacidade, embarque e vidas |
| `Missao` | Elementos do cenário, colisão e transferência de passageiros |
| `FabricaMissao` | Montagem inicial do cenário |
| `JogoService` | Sequência dos turnos, pontos, tempo e encerramento |
| `GameController` | Coordenação dos casos de uso |
| `RankingService` | Classificação, limite de cinco resultados e criação do registro |
| `RankingRepository` | Leitura, escrita e remoção do arquivo |
| `ConsoleJogo` | Entrada textual, menu e tradução de teclas |
| `MapaRenderer` | Desenho, mensagens e legenda do console |
| `EstadoJogo` / `EntidadeEstado` | Dados imutáveis para leitura |

## Um turno com responsabilidades distribuídas

`JogoService` pede o movimento à nave. Se ele for válido, desconta um ponto,
conta o movimento e pede à missão que atualize os perigos. Em seguida, consulta
a colisão, aplica perda de vida e verifica combustível e objetivo.

A nave reutiliza o movimento com limites de `ElementoMapa`. A missão
atualiza cada `Perigo` pelo contrato abstrato. As regras que dependem do
conjunto da partida ficam no serviço; as decisões locais ficam no domínio.

## Exemplo de coesão na apresentação

Trecho do [MapaRenderer](../src/graspexercicio10/presentation/MapaRenderer.java):

```java
for (EntidadeEstado e : estado.entidades) {
    if (e.x == x && e.y == y) return e.simbolo;
}
return '.';
```

Esse trecho pertence a `resolverSimbolo`. O renderer escolhe a célula que será
desenhada e usa o símbolo fornecido pelo modelo através do snapshot.
O embarque não é uma responsabilidade da apresentação.

## Critério de revisão

Pergunte quais motivos fariam cada classe mudar. Uma alteração no texto do menu
deve ficar na apresentação. Uma alteração no Top 5 deve ficar no
`RankingService`. Alterar o formato de arquivo deve ficar no repositório.

Diagramas editáveis: [classes](diagrams/high-cohesion-class.mmd) e
[sequência](diagrams/high-cohesion-sequence.mmd).

## Exercício

Por que inserir `MapaRenderer` dentro de `JogoService` dificultaria reutilizar
as regras em uma interface gráfica? Identifique quais responsabilidades seriam misturadas.
