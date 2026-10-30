# Fase 01 - Tutorial de Refatoracao Arquitetural

## 1. Introducao

Esta fase refatora o jogo console para separar regras de jogo da interface textual, preparando a migracao futura para LibGDX.

## 2. Objetivos de aprendizagem

- Separar o jogo em camadas (`domain`, `application`, `console`, `infrastructure`).
- Reconhecer onde cada principio SOLID foi aplicado e onde ainda ha limitacoes.
- Reconhecer os padroes GRASP usados na distribuicao de responsabilidades.
- Identificar os padroes de projeto presentes (Factory, Repository, Value Object, entre outros).
- Entender os ajustes de regra feitos durante a validacao funcional.

## 3. Conceitos teoricos

### SOLID

| Sigla | Principio | Ideia central |
|---|---|---|
| S | Single Responsibility | Uma classe deve ter um unico motivo para mudar. |
| O | Open/Closed | Aberta para extensao, fechada para modificacao. |
| L | Liskov Substitution | Subtipos devem poder substituir seus tipos base sem quebrar o uso. |
| I | Interface Segregation | Interfaces pequenas e especificas. |
| D | Dependency Inversion | Depender de abstracoes, nao de implementacoes concretas. |

### GRASP

- Information Expert: a responsabilidade fica com quem tem a informacao.
- Creator: quem agrega ou usa os objetos cria os objetos.
- Controller: um objeto recebe e coordena eventos do sistema, sem conter a interface.
- Low Coupling e High Cohesion: poucas dependencias e responsabilidades coesas.
- Polymorphism: comportamento variavel resolvido por tipo, sem `if` por tipo.
- Pure Fabrication: classe artificial criada para manter coesao (nao existe no dominio).
- Indirection e Protected Variations: um intermediario isola o ponto de variacao.

### Padroes de projeto

- Factory, Repository, Value Object, Composicao e Injecao de dependencia manual.

## 4. Problemas encontrados no codigo original

O `Main.java` original (em `exercicio10/src`) tem mais de 500 linhas e concentra:

- Menu, leitura de entrada e loop de partida (`jogarPartida`, `lerLinha`).
- Regras de turno, colisao, pontuacao e vitoria.
- Geracao do mapa e verificacao de posicao ocupada (`posicaoOcupada`).
- Regras de dificuldade em metodos estaticos (`definirPontuacaoInicial`).
- Desenho do mapa e legenda com simbolos escritos como texto fixo (`desenharMapa`).
- Persistencia do ranking com leitura e escrita de JSON manual dentro do proprio `Main` (`loadRanking`, `saveRanking`, `parseRankingJson`) e classe interna `RankingEntry`.

Consequencia: para trocar a interface (LibGDX) seria preciso reescrever tambem as regras.

## 5. Arquitetura antes da transformacao

```mermaid
flowchart LR
    Main[Main - tudo em metodos estaticos] --> Entidades[Missao, Nave, Passageiro, Perigos]
    Main --> Arquivo[(ranking.json)]
    Main --> Console[(System.in / System.out)]
```

## 6. O que foi feito na migracao

### 6.1 Passo a passo

1. Criar os pacotes `domain`, `application`, `console` e `infrastructure.ranking`.
2. Mover as regras de turno (movimento, embarque, base, colisao, pontuacao, vitoria) para `GameSession`.
3. Mover a criacao do cenario para `MissaoFactory`, sem sobreposicao de objetos.
4. Mover as regras de dificuldade para `Dificuldade`, `RegrasDificuldade` e `ConfiguracaoDificuldade`.
5. Converter a entrada textual em `Acao` (enum) para que a regra nao dependa de teclas.
6. Mover o desenho do mapa para `ConsoleRenderer`, usando a interface `ObjetoRenderizavel`.
7. Extrair o ranking para `RankingRepository` (interface) e `FileRankingRepository` (arquivo JSON).
8. Reduzir `Main` a classe de composicao: instancia as dependencias e chama `ConsoleGame.run()`.
9. Criar `build.ps1` para compilar (e opcionalmente executar) com um comando.

### 6.2 Para onde foi cada responsabilidade

| Responsabilidade | Original (`Main`) | Fase 01 |
|---|---|---|
| Menu e loop de entrada | `main`, `lerLinha` | `ConsoleGame` |
| Regras de turno e vitoria | `jogarPartida` | `GameSession` |
| Resultado de um turno | variaveis locais | `ResultadoTurno`, `EstadoPartida` |
| Comando do jogador | `String` comparada em `if` | `Acao.fromInput` |
| Criacao do cenario | `posicaoOcupada` e geracao inline | `MissaoFactory` |
| Parametros de dificuldade | `definirPontuacaoInicial` | `RegrasDificuldade`, `ConfiguracaoDificuldade` |
| Desenho do mapa e legenda | `desenharMapa` | `ConsoleRenderer` |
| Persistencia do ranking | `loadRanking`, `saveRanking` | `RankingRepository`, `FileRankingRepository` |
| Composicao | `main` | `Main` |

### 6.3 Ajustes de regra feitos durante a validacao funcional

Os testes executados no console revelaram inconsistencias que foram corrigidas:

- Passageiros e perigos podiam nascer na mesma celula (inclusive na base e na nave). `MissaoFactory` agora controla as posicoes ocupadas com um `Set`.
- A nave lotada impedia concluir a missao. Agora, ao chegar na base (`processarBase`), todos os passageiros desembarcam, liberando a capacidade, e a contagem de entregues sobe.
- A vitoria exige todos os passageiros entregues na base (nao apenas coletados).
- A capacidade da nave estava fixa em 3 enquanto a missao podia ter 4, 6 ou 8 passageiros. Agora e igual a quantidade de passageiros da dificuldade.
- O status do mapa tinha indicadores redundantes. Hoje mostra: `Vidas | Pontuacao | A bordo (atual/capacidade) | Entregues na base | Restantes no mapa`.
- A base virou a entidade `Base`, renderizavel como os demais objetos, e a legenda deixou de ser texto fixo.

### 6.4 Diferencas em relacao ao original

A refatoracao nao foi 100% neutra. Pontos que mudaram e devem ser decididos pela equipe:

- Simbolos do mapa: original usa `@`, `#`, `L`; esta fase usa `N`, `*`, `B`.
- Tamanho do mapa: original pergunta ao jogador; esta fase usa fixo de -5 a 5.
- Ranking: original mostra Top 5; esta fase mantem Top 10.
- Capacidade da nave: agora acompanha a dificuldade.

## 7. Codigo antes e depois

### Legenda e simbolos

Antes (`Main`): texto fixo, sem relacao com as entidades.

```java
System.out.println("Legenda: @=Nave, P=Professor, E=Engenheiro, T=Astronauta, #=Asteroide, X=Inimigo, L=Plataforma de Pouso, .=Vazio");
```

Depois: cada entidade informa o proprio simbolo e o renderer apenas desenha.

```java
public interface ObjetoRenderizavel {
    Posicao getPosicao();
    char getSimbolo();
}

// ConsoleRenderer
private List<ObjetoRenderizavel> coletarObjetosRenderizaveis(Missao missao) {
    List<ObjetoRenderizavel> objetos = new ArrayList<>();
    objetos.add(missao.getBase());
    objetos.addAll(missao.getAsteroides());
    objetos.addAll(missao.getInimigos());
    objetos.addAll(missao.getPassageiros());
    objetos.add(missao.getNave());
    return objetos;
}
```

### Composicao

Antes: `main` fazia tudo. Depois:

```java
ConsoleGame game = new ConsoleGame(
        new Scanner(System.in),
        new Random(),
        new ConsoleRenderer(),
        new MissaoFactory(),
        new FileRankingRepository(rankingPath)
);
game.run();
```

### Capacidade da nave

```java
// MissaoFactory
Nave nave = new Nave(0, 0, 3, cfg.getQuantidadePassageiros());
```

## 8. Onde foi usado SOLID

| Principio | Onde | Como |
|---|---|---|
| SRP | `GameSession`, `ConsoleGame`, `ConsoleRenderer`, `MissaoFactory`, `FileRankingRepository` | Cada classe tem um motivo para mudar: regra de partida, fluxo de menu, desenho, criacao de cenario, persistencia. |
| OCP | `ObjetoRenderizavel`, `Perigo`, `Passageiro` | Um novo tipo de passageiro ou perigo que implemente a interface aparece no mapa sem mudar `desenharMapa`. Limite: `Dificuldade`, `RegrasDificuldade` e `MissaoFactory.criarPassageiroAleatorio` usam `switch`/`if` e precisam ser editados para novos casos. |
| LSP | `Professor`, `Engenheiro`, `Astronauta` estendem `Passageiro`; `Asteroide` e `Inimigo` implementam `Perigo` | `Missao`, `Nave` e `GameSession` tratam qualquer subtipo como `Passageiro` ou `Perigo` sem checar o tipo. |
| ISP | `ObjetoRenderizavel` (2 metodos), `RankingRepository` (2 metodos), `Perigo` | Interfaces pequenas; o renderer so conhece posicao e simbolo. |
| DIP | `ConsoleGame` depende de `RankingRepository` | O fluxo do jogo nao conhece o arquivo JSON; `FileRankingRepository` e injetado em `Main`. Limite: `ConsoleGame` ainda depende das classes concretas `ConsoleRenderer` e `MissaoFactory`. |

## 9. Onde foi usado GRASP

| Padrao GRASP | Onde | Como |
|---|---|---|
| Controller | `ConsoleGame` | Recebe a entrada do jogador, traduz para `Acao` e delega a `GameSession`. |
| Information Expert | `Posicao.mover`, `Nave.estaCheia`, `Nave.embarcar`, `Missao.houveColisaoComPerigo`, `Perigo.colideCom`, `Inimigo.moverAleatoriamente` | A regra fica com o objeto que possui os dados. |
| Creator | `MissaoFactory` | Cria `Nave`, `Passageiro`, `Asteroide` e `Inimigo` e os adiciona a `Missao`. `Missao` cria a `Base`. |
| Low Coupling | camadas `domain` e `application` | Nao importam nada de `console` nem de `infrastructure`. |
| High Cohesion | classes pequenas por responsabilidade | `GameSession` so trata regras de turno; `ConsoleRenderer` so trata saida. |
| Polymorphism | `getSimbolo()`, `getPontuacao()` | Cada tipo informa a propria pontuacao (Professor 20, Engenheiro 15, Astronauta 25, base 10) e o simbolo, sem `if` por tipo. |
| Pure Fabrication | `GameSession`, `ResultadoTurno`, `MissaoFactory`, `FileRankingRepository` | Classes que nao existem no dominio, criadas para manter coesao. |
| Indirection / Protected Variations | `RankingRepository`, `ObjetoRenderizavel`, `Acao` | Isolam os pontos que mais tendem a mudar: persistencia, forma de desenhar e forma de entrada. |

## 10. Padroes de projeto utilizados

| Padrao | Onde | Observacao |
|---|---|---|
| Factory | `MissaoFactory` | Centraliza a montagem do cenario por dificuldade. |
| Repository | `RankingRepository` / `FileRankingRepository` | Esconde o formato e o local de armazenamento. |
| Value Object | `Posicao` | Imutavel (`final`), com `equals` e `hashCode`; `mover` devolve nova instancia. |
| Composicao | `Nave`, `Passageiro`, `Base` e perigos possuem uma `Posicao` | Evita herdar so para ter x e y. |
| Heranca com sobrescrita | `Passageiro` e seus subtipos | Subtipos sobrescrevem `getPontuacao()` e `getSimbolo()`. |
| Default method | `Perigo.colideCom` | Comportamento comum compartilhado por interface. |
| Enum com fabrica estatica | `Acao.fromInput`, `Dificuldade.deString` | Traduz texto em valor tipado em um unico lugar. |
| Injecao de dependencia manual | `Main` | Monta o grafo de objetos e passa pelo construtor. |
| Objeto de configuracao | `ConfiguracaoDificuldade` + `RegrasDificuldade.obter` | Tabela de parametros por dificuldade. |

Observacao: a versao anterior deste tutorial citava Strategy para a dificuldade. No codigo atual a dificuldade e uma tabela de configuracao (`switch` + objeto de valores), nao uma familia de estrategias intercambiaveis. Virar Strategy e um exercicio proposto abaixo.

## 11. Arquitetura resultante

### Camadas e dependencias

```mermaid
flowchart LR
    Main --> ConsoleGame
    ConsoleGame --> ConsoleRenderer
    ConsoleGame --> MissaoFactory
    ConsoleGame --> GameSession
    ConsoleGame --> RankingRepository
    GameSession --> Missao
    MissaoFactory --> Missao
    MissaoFactory --> RegrasDificuldade
    FileRankingRepository -.implementa.-> RankingRepository
    ConsoleRenderer --> ObjetoRenderizavel
```

### Dominio

```mermaid
classDiagram
    class ObjetoRenderizavel {
        <<interface>>
        +getPosicao() Posicao
        +getSimbolo() char
    }
    class Perigo {
        <<interface>>
        +colideCom(Nave) boolean
    }
    ObjetoRenderizavel <|.. Nave
    ObjetoRenderizavel <|.. Base
    ObjetoRenderizavel <|.. Passageiro
    ObjetoRenderizavel <|-- Perigo
    Perigo <|.. Asteroide
    Perigo <|.. Inimigo
    Passageiro <|-- Professor
    Passageiro <|-- Engenheiro
    Passageiro <|-- Astronauta
    Missao o-- Nave
    Missao o-- Base
    Missao o-- Passageiro
    Missao o-- Asteroide
    Missao o-- Inimigo
```

## 12. Limitacoes conhecidas e proximos passos

- `ConsoleGame.processarVitoria` ainda contem a regra de entrada no ranking (Top 10, ordenacao). Essa regra pertence a `application` e deveria sair do console (SRP).
- `ConsoleGame` depende de `ConsoleRenderer` e `MissaoFactory` concretos; extrair interfaces completaria o DIP.
- A legenda do `ConsoleRenderer` obtem os simbolos instanciando objetos de exemplo. Um catalogo de simbolos no dominio evitaria isso.
- `Dificuldade`/`RegrasDificuldade`/`criarPassageiroAleatorio` usam `switch`/`if`; novos tipos exigem editar essas classes (OCP parcial).
- `GameSession` ainda recebe `Random` e `min/max` diretamente; um objeto de configuracao do mapa simplificaria o construtor.
- Nao ha testes automatizados; a validacao foi feita por execucao simulada via console.
- `GameSession.verificarPontuacao` roda antes de `verificarVitoria`: se a pontuacao chegar a zero no mesmo turno da ultima entrega, a partida termina em derrota.
- `FileRankingRepository` ignora `IOException` em silencio e `Passageiro.setPosicao` nao e usado.

As classes do codigo-fonte tem Javadoc indicando onde SOLID, GRASP e padroes foram aplicados (e os limites de cada um).

## 13. Como executar e testar

Na pasta `fase-01-refatoracao`:

```powershell
.\build.ps1          # compila para a pasta out
.\build.ps1 -Run     # compila e executa
```

Roteiro de validacao manual:

1. Menu: opcoes 1 a 4 e opcao invalida.
2. Partida facil: conferir que `A bordo`, `Entregues na base` e `Restantes no mapa` fecham com o total de passageiros.
3. Embarcar (`E`) em uma celula com passageiro e sem passageiro.
4. Levar passageiros a base e verificar o desembarque e a vitoria.
5. Colidir com asteroide ou inimigo e conferir perda de vida e de pontos.
6. Conferir o ranking apos uma vitoria.
7. Repetir em `medio` e `dificil` e verificar que a capacidade acompanha a quantidade de passageiros.

## 14. Exercicios para os alunos

1. Adicionar um novo tipo de passageiro sem alterar o renderer.
2. Criar uma nova dificuldade e identificar quantas classes precisaram ser alteradas (e por que).
3. Transformar `RegrasDificuldade` em Strategy real, com uma implementacao por dificuldade.
4. Mover a regra de entrada no ranking de `ConsoleGame` para a camada `application`.
5. Criar `RankingRepository` em memoria para testes.
6. Extrair interfaces para `ConsoleRenderer` e `MissaoFactory` e injetar por construtor.

## 15. Conclusao

A fase separa regra de jogo, interface e persistencia, reduzindo acoplamento e preparando a troca da interface por LibGDX sem reescrever o dominio. SOLID e GRASP foram aplicados de forma parcial e as limitacoes restantes estao listadas na secao 12 para as proximas fases.
