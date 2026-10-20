# Polymorphism

Quando um comportamento varia conforme o tipo do objeto, atribua as variações
aos tipos que as executam. O consumidor chama o contrato comum.

## Posição e símbolo na abstração do modelo

[ElementoMapa](../src/graspexercicio10/model/ElementoMapa.java) é abstrata.
Ela concentra as coordenadas e declara:

```java
public abstract char getSimbolo();
```

As subclasses fornecem seus símbolos: nave `@`, base `L`, professor `P`,
engenheiro `E`, astronauta `T`, asteroide `#` e inimigo `X`.

`Passageiro` e `Perigo` também são classes abstratas que estendem
`ElementoMapa`. As coordenadas não são repetidas nas subclasses.

## Outros comportamentos polimórficos

| Contrato | Variações atuais |
| --- | --- |
| `ElementoMapa.getSimbolo()` | Símbolo de cada elemento |
| `Passageiro.getPontuacao()` | Professor: 10; engenheiro: 15; astronauta: 20 |
| `Perigo.atualizar(Random, int, int, int, int)` | Asteroide permanece imóvel; inimigo tenta mover uma casa |
| `ElementoMapa.isResgatavel()` | Falso por padrão; verdadeiro em `Passageiro` |

O serviço soma `passageiro.getPontuacao()`. A missão chama
`perigo.atualizar(...)`. Nenhum desses consumidores escolhe o comportamento
por uma lista de tipos concretos.

## Símbolo até a apresentação

`Missao.getElementos()` devolve uma lista de `ElementoMapa`.
O serviço percorre a lista:

```java
for (ElementoMapa elemento : missao.getElementos()) {
    entidades.add(new EntidadeEstado(elemento));
}
```

O construtor de `EntidadeEstado` copia `elemento.getSimbolo()` e as coordenadas.
O renderer usa `e.simbolo`, e a legenda usa símbolo e descrição do snapshot.

## Extensão com um novo passageiro

Exemplo completo de uma nova classe, para exercício:

```java
package graspexercicio10.model;

public class Medico extends Passageiro {
    public Medico(String nome, int x, int y) {
        super(nome, "Médico", x, y);
    }

    @Override public int getPontuacao() { return 25; }
    @Override public char getSimbolo() { return 'M'; }
}
```

Para participar do cenário sorteado, sua criação deve ser acrescentada à
`FabricaMissao`. O renderer, o snapshot e as regras de embarque já trabalham
com as abstrações existentes. O cenário `extensaoSemAlterarRenderer` dos
[testes](../tests/graspexercicio10/JogoTest.java) demonstra esse comportamento.

Diagramas editáveis: [hierarquia](diagrams/polymorphism-class.mmd) e
[sequência](diagrams/polymorphism-sequence.mmd).

## Exercício

Implemente um novo perigo que estenda `Perigo`, forneça seu símbolo e defina
`atualizar(...)`. Quais consumidores conseguem usá-lo sem alteração?

