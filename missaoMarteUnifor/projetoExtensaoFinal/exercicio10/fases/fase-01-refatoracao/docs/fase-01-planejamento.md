# Planejamento da Fase 01

## Objetivo

Refatorar a versão em console sem alterar o comportamento funcional, separando regras de negócio da interface textual para preparar a evolução ao LibGDX.

## Escopo

- Manter jogo no console.
- Organizar código em pacotes por responsabilidade.
- Isolar ranking em repositório.
- Preservar regras atuais de partida, dificuldade e vitória.

## Estrutura alvo

- `domain`: `Missao`, `Nave`, `Passageiro` e perigos.
- `application`: `GameSession`, `MissaoFactory`, regras de dificuldade e ações.
- `console`: `ConsoleGame` e `ConsoleRenderer`.
- `infrastructure.ranking`: `RankingRepository` e implementação em arquivo.

## Validação

- Compilação completa com `javac`.
- Execução manual de menu, início de missão e saída.
- Verificação de leitura e gravação de ranking.
