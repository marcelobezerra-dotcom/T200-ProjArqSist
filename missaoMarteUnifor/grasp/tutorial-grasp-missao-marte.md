# Tutorial GRASP — Missão Marte

O [tutorial completo e atualizado](apostilas-grasp/tutorial-grasp-missao-marte.md)
é mantido em `apostilas-grasp`. Ele acompanha o código de
[`src/graspexercicio10`](src/graspexercicio10/README.md) e apresenta a
distribuição das responsabilidades passo a passo.

Este arquivo é o ponto de entrada na raiz do repositório. A explicação detalhada
fica em um único tutorial, junto às apostilas e aos diagramas.

## Arquitetura atual

| Componente | Responsabilidade |
| --- | --- |
| `Main` | Montar as dependências e iniciar a apresentação |
| `ConsoleJogo` | Ler menu, traduzir teclas e controlar o laço do console |
| `GameController` | Receber ações da aplicação e coordenar os serviços |
| `JogoService` | Executar turnos e controlar pontos, tempo e encerramento |
| `FabricaMissao` | Montar o cenário em posições livres |
| `ElementoMapa` | Encapsular coordenadas e declarar o símbolo abstrato |
| `Missao` | Reunir os elementos, verificar colisões e transferir passageiros |
| `EstadoJogo` / `EntidadeEstado` | Transportar cópias imutáveis para leitura |
| `MapaRenderer` | Desenhar o estado e apresentar mensagens no console |
| `RankingService` | Classificar resultados e aplicar o Top 5 |
| `RankingRepository` | Armazenar registros em JSON através de `IRankingRepository` |

## Modelo do mapa

[ElementoMapa](src/graspexercicio10/model/ElementoMapa.java) é uma classe
abstrata com `x` e `y` privados, acesso por `getX()` e `getY()`,
operações de posição e o método abstrato `char getSimbolo()`.

```text
ElementoMapa
├── Nave
├── Base
├── Passageiro (abstrata)
│   ├── Professor
│   ├── Engenheiro
│   └── Astronauta
└── Perigo (abstrata)
    ├── Asteroide
    └── Inimigo
```

`Perigo` estende `ElementoMapa`, compartilha a colisão por posição e
declara `atualizar(...)`. `Passageiro` também é abstrata e declara
`getPontuacao()`. As classes concretas fornecem seus símbolos por polimorfismo.

## Operações da aplicação

[GameController](src/graspexercicio10/controller/GameController.java) recebe
`JogoService` e `RankingService` no construtor. Suas operações são:

- `iniciarPartida(piloto, dificuldade, raioMapa)`, que devolve `EstadoJogo`.
- `executar(AcaoJogo)`, que devolve `EventoJogo`.
- `consultarEstado()`, que devolve uma cópia de leitura da partida.
- `consultarRanking()` e `resetarRanking()`.

O controller recebe ações já traduzidas pelo console ou pela futura interface
gráfica. `JogoService` executa as regras dos turnos. Na transição para vitória,
o controller solicita o registro ao `RankingService`.

`RankingService` fica no pacote `service` e depende de `IRankingRepository`.
O contrato de armazenamento possui `carregar()`, `salvar(List<RankingEntry>)`
e `resetar()`.

## Continuar o estudo

- [Tutorial completo](apostilas-grasp/tutorial-grasp-missao-marte.md): refatoração e aplicação dos nove princípios.
- [Índice das apostilas](apostilas-grasp/README.md): estudo de cada princípio GRASP.
- [Diagramas atuais](apostilas-grasp/diagrams/README.md): modelo, arquitetura e sequências.
- [Compilar, executar e testar](apostilas-grasp/RUNNING.md): comandos a partir da raiz.
- [Guia da implementação](src/graspexercicio10/README.md): regras e preparação da interface gráfica.
