# Protected Variations

Identifique mudanças previstas e crie contratos que preservem os consumidores
dessas variações. Uma abstração deve proteger uma necessidade reconhecida.

## Variações consideradas

| Mudança esperada | Contrato |
| --- | --- |
| Novos elementos com outros símbolos | `ElementoMapa.getSimbolo()` |
| Novos passageiros com outros valores | `Passageiro.getPontuacao()` |
| Perigos com outro movimento | `Perigo.atualizar(...)` |
| Entrada por console ou botões | `AcaoJogo` e operações de `GameController` |
| Outro desenho da partida | `EstadoJogo` e `EntidadeEstado` |
| Outro armazenamento de ranking | `IRankingRepository` |

`EstadoJogo` é uma cópia de leitura: campos finais, lista protegida e elementos
imutáveis. A apresentação consulta novamente para obter outro snapshot depois
de uma jogada.

## Contrato de armazenamento

Conteúdo de [IRankingRepository](../src/graspexercicio10/repository/IRankingRepository.java),
omitindo apenas o comentário:

```java
package graspexercicio10.repository;

import java.util.List;

public interface IRankingRepository {
    List<RankingEntry> carregar();
    void salvar(List<RankingEntry> ranking);
    void resetar();
}
```

O limite de cinco resultados e a decisão de elegibilidade pertencem ao
`RankingService`. O repositório recebe a lista pronta para armazenamento.

## Implementação alternativa para exercício

Esta classe é um exemplo adicional completo; não faz parte dos fontes de produção:

```java
package graspexercicio10.repository;

import java.util.ArrayList;
import java.util.List;

public class MemoriaRankingRepository implements IRankingRepository {
    private List<RankingEntry> entradas = new ArrayList<>();

    @Override public List<RankingEntry> carregar() {
        return new ArrayList<>(entradas);
    }

    @Override public void salvar(List<RankingEntry> ranking) {
        entradas = new ArrayList<>(ranking);
    }

    @Override public void resetar() {
        entradas.clear();
    }
}
```

No `Main`, a seleção passa a ser:

```java
RankingService ranking = new RankingService(new MemoriaRankingRepository());
```

A implementação em memória copia as listas para que o consumidor não altere
seu armazenamento por uma referência compartilhada.

Diagramas editáveis: [classes](diagrams/protected-variations-class.mmd) e
[sequência](diagrams/protected-variations-sequence.mmd).

## Limite da proteção

A apresentação gráfica poderá reaproveitar ações, eventos, estado e controller.
O jogo atual continua por turnos. Movimento contínuo exige rever a simulação;
o contrato de leitura não transforma essa regra automaticamente.

## Exercício

Compare o teste com ranking em memória e a implementação em arquivo.
Em que ponto a implementação concreta é escolhida? Como a tela recebe uma
falha de armazenamento?

