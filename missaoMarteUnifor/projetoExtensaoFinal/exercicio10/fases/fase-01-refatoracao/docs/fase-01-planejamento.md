# Planejamento da Fase 01

## Objetivo

Refatorar a versao em console sem alterar o comportamento funcional, separando regras de negocio da interface textual para preparar evolucao ao LibGDX.

## Escopo

- Manter jogo no console.
- Organizar codigo em pacotes por responsabilidade.
- Isolar ranking em repositorio.
- Preservar regras atuais de partida, dificuldade e vitoria.

## Estrutura alvo

- `domain`: `Missao`, `Nave`, `Passageiro` e perigos.
- `application`: `GameSession`, `MissaoFactory`, regras de dificuldade e acoes.
- `console`: `ConsoleGame` e `ConsoleRenderer`.
- `infrastructure.ranking`: `RankingRepository` e implementacao em arquivo.

## Validacao

- Compilacao completa com `javac`.
- Execucao manual de menu, inicio de missao e saida.
- Verificacao de leitura e gravacao de ranking.
