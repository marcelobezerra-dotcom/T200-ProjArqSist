# Projeto Missão Marte — Plano de ensino e evolução arquitetural

## Objetivo
Evoluir o Exercício 10, um jogo Java em console, para um jogo gráfico multiplataforma (desktop e Android) com autenticação e ranking global. Preservar a lógica existente e ensinar evolução incremental, arquitetura e boas práticas.

## Sete fases

1. **Refatoração em Java (console):** analisar o código existente; aplicar SOLID, GRASP e padrões de projeto pertinentes; separar regras de jogo, entrada e apresentação. Entrega: jogo funcional e diagrama antes/depois.
2. **Gradle:** migrar a compilação e execução para Gradle, mantendo comportamento. Entrega: build reproduzível e instruções de execução.
3. **Estrutura LibGDX:** introduzir ciclo de vida, `Game`, `Screen`, `create`, `render` e `dispose`; adaptar a organização do projeto preservando a lógica, inicialmente sem exigir gráficos completos. Entrega: projeto LibGDX executável.
4. **Interface gráfica:** incorporar sprites, movimento, entrada, colisões, HUD e sons. Entrega: jogo desktop gráfico jogável.
5. **Spring Boot e ranking:** implementar cadastro, login obrigatório, autenticação, API REST e persistência do histórico de partidas. Ranking global exibe os dez melhores jogadores, considerando a maior pontuação de cada um. O servidor gerencia usuários e pontuações, não a lógica do jogo. Entrega: integração LibGDX–API com tela de ranking.
6. **Android:** reutilizar módulo compartilhado do LibGDX, adaptar entrada por toque e telas, manter versão desktop e Android no mesmo ecossistema. Controle Bluetooth é opcional. Entrega: jogo funcional em ambas as plataformas, consumindo o mesmo ranking.
7. **Evolução livre:** equipes adicionam nova fase, inimigos, chefão, dificuldade, personalização da nave ou outra mecânica, justificando as decisões arquiteturais. Multiplayer fica como trabalho futuro.

## Cronograma de quatro semanas
Uma aula prática semanal de **100 minutos (9h–10h40)**, acompanhada por uma aula teórica separada. O desenvolvimento continua fora da sala.

| Semana | Aula prática (100 min) | Trabalho extraclasse / entrega |
|---|---|---|
| 1 | Diagnóstico do Exercício 10; demonstração de refatoração; introdução ao Gradle | Concluir fases 1 e 2; registrar decisões e testes |
| 2 | Estrutura LibGDX; demonstração do ciclo de vida e primeiros sprites | Concluir fases 3 e 4; jogo gráfico desktop |
| 3 | API Spring Boot; modelo usuário–partidas; autenticação e ranking | Concluir fase 5; integração e teste de ranking |
| 4 | Adaptação Android; testes desktop/mobile; apresentação de melhorias | Concluir fase 6 e protótipo da fase 7; demonstração final |

### Estrutura de cada encontro
- 15 min: objetivos e ligação com teoria.
- 30 min: demonstração orientada pelo professor.
- 40 min: prática assistida e revisão de código.
- 15 min: verificação das entregas e tarefas extraclasse.

## Entregáveis de cada fase
- Código versionado no Git, com tag ou branch por fase.
- Tutorial `docs/fase-XX-tutorial.md`: objetivos, conceitos, arquitetura antes/depois, alterações passo a passo, testes e exercícios.
- Evidência de execução (captura de tela, log ou vídeo curto) e breve reflexão sobre arquitetura.

## Prompt mestre para Copilot, Codex ou Claude Code

> Atue como arquiteto de software Java e professor universitário. Analise o Exercício 10 (Missão Marte) no workspace. Evolua o código existente, sem reescrever do zero, respeitando o plano de sete fases neste documento. Execute **somente a fase que eu indicar**. Antes de editar, apresente diagnóstico, classes afetadas, plano de mudanças e riscos e aguarde aprovação. Preserve o comportamento existente sempre que possível. Após a aprovação, implemente, rode os testes possíveis e documente o que não pôde testar. Gere `docs/fase-XX-tutorial.md` com objetivos, teoria (SOLID, GRASP, padrões, Gradle, LibGDX, Spring Boot ou Android conforme a fase), código antes/depois, diagramas Mermaid, instruções de execução, testes e exercícios. Use linguagem didática para alunos que conhecem Java, mas ainda não dominam as demais tecnologias. Não introduza Angular nem JavaScript. Comece pelo diagnóstico da fase 1.

### Prompts de continuação
- **Fase 2:** “Migre o projeto refatorado para Gradle e documente a migração, sem alterar o comportamento.”
- **Fase 3:** “Adapte a estrutura ao LibGDX e explique o ciclo de vida, preservando as regras do jogo.”
- **Fase 4:** “Implemente a apresentação gráfica no LibGDX e documente cada componente.”
- **Fase 5:** “Implemente servidor Spring Boot, login obrigatório, histórico de partidas e Top 10 por melhor pontuação de cada jogador; integre o cliente LibGDX.”
- **Fase 6:** “Adicione execução Android mantendo desktop e núcleo compartilhado, com controles por toque.”
- **Fase 7:** “Proponha e implemente uma nova mecânica escolhida pela equipe, demonstrando extensibilidade.”

## Observações pedagógicas
O prazo de quatro semanas é intensivo e pressupõe atividades extraclasse e exemplos-base preparados pelo professor. Avaliar tanto a execução quanto a justificativa das decisões arquiteturais. Para um ranking competitivo, pontuações enviadas pelo cliente não são totalmente confiáveis; tratar prevenção de fraude como evolução futura.
