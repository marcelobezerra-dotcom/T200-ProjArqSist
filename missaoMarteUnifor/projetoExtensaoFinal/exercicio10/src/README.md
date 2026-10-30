# Exercício 10: Missão Marte Unifor

## Sobre

Jogo de exploração e resgate em Marte, executado em modo texto pelo terminal. O jogador pilota a nave em um mapa, resgata passageiros e evita asteroides e inimigos.

## Funcionalidades

- Menu para iniciar uma missão, consultar o ranking, limpar o histórico ou sair.
- Três níveis de dificuldade representados pelo enum `Dificuldade`.
- Mapa textual com posições da nave, passageiros, asteroides, inimigos e plataforma de pouso.
- Estatísticas de partida: pontuação, movimentos, duração e passageiros resgatados.
- Ranking Top 5 persistido no arquivo `ranking.json`.
- Encerramento seguro da partida quando a entrada do terminal termina.

## Controles

- `w`, `s`, `a`, `d`: mover a nave.
- `c`: embarcar passageiro na posição atual.
- `q`: abortar a missão.

## Estrutura

As classes Java ficam diretamente em `src` e usam o pacote padrão, sem declaração `package`.

```text
exercicio10/
└── src/
    ├── Asteroide.java
    ├── Astronauta.java
    ├── Dificuldade.java
    ├── Engenheiro.java
    ├── Inimigo.java
    ├── Main.java
    ├── Missao.java
    ├── Nave.java
    ├── Passageiro.java
    ├── Professor.java
    └── README.md
```

## Compilar e executar

Execute os comandos a partir da pasta `exercicio10`, que contém a pasta `src`:

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

O arquivo `ranking.json` é criado no diretório de onde o jogo for iniciado.
