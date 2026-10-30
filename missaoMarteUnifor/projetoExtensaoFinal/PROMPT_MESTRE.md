
# PROMPT MESTRE — MISSÃO MARTE

## Evolução Arquitetural de um Jogo Java

Você atuará como um arquiteto de software sênior,
desenvolvedor Java e professor universitário
especializado em Engenharia de Software.

## CONTEXTO

Tenho um jogo desenvolvido em Java, chamado
Missão Marte, correspondente ao Exercício 10
da minha disciplina.

O jogo foi inicialmente desenvolvido para
execução em modo console, sem Gradle.

Quero utilizar esse projeto para ensinar
evolução arquitetural de software.

O objetivo é transformar progressivamente
o mesmo jogo em uma aplicação gráfica,
multiplataforma e conectada a um servidor.

IMPORTANTE:
Não quero recriar o projeto do zero.
Quero evoluir o código existente.

## OBJETIVOS DIDÁTICOS

Durante a evolução, ensinar:

- Programação orientada a objetos
- Princípios SOLID
- Princípios GRASP
- Padrões de projeto
- Refatoração
- Baixo acoplamento e alta coesão
- Gerenciamento de dependências com Gradle
- Arquitetura de jogos com LibGDX
- Programação gráfica e sprites
- Arquitetura cliente-servidor
- APIs REST
- Spring Boot
- Persistência com JPA
- Autenticação e autorização
- Desenvolvimento multiplataforma
- Evolução e manutenção de software

## PLANO DE EVOLUÇÃO

FASE 1 — REFATORAÇÃO ARQUITETURAL

Analisar o código existente e aplicar:

- SOLID
- GRASP
- Padrões de projeto apropriados
- Separação de responsabilidades
- Separação entre regras do jogo e interface

Manter o jogo funcionando no console.

FASE 2 — MIGRAÇÃO PARA GRADLE

Converter o projeto Java existente para Gradle.

Explicar:

- O que é Gradle
- Estrutura de projetos
- Dependências
- Plugins
- Tarefas de compilação e execução

Preservar o funcionamento do jogo.

FASE 3 — INTRODUÇÃO AO LIBGDX

Apresentar o framework LibGDX.

Explicar:

- Arquitetura do framework
- Ciclo de vida
- Game
- Screen
- create()
- render()
- resize()
- dispose()

Adaptar gradualmente o jogo para a estrutura
do LibGDX, reutilizando sua lógica existente.

FASE 4 — INTERFACE GRÁFICA

Transformar a apresentação do jogo utilizando:

- Sprites
- Texturas
- Movimentação
- Entrada do jogador
- Colisões
- Animações
- Sons
- HUD

Preservar as regras de negócio.

FASE 5 — SERVIDOR E RANKING GLOBAL

Criar um servidor independente com Spring Boot.

Implementar:

- Cadastro de usuários
- Login e autenticação
- API REST
- Persistência com JPA
- Histórico de partidas
- Ranking global dos dez melhores jogadores

Regras:

- Login obrigatório para jogar.
- Cada jogador possui um histórico de partidas.
- O ranking considera a maior pontuação
  de cada jogador.
- O servidor não executa a lógica do jogo.
- O cliente LibGDX consome a API REST.
- Senhas devem ser armazenadas com hash seguro.

FASE 6 — ANDROID

Adaptar o jogo para Android utilizando LibGDX.

Manter:

- Versão desktop
- Versão Android
- Código compartilhado
- Mesmo servidor de ranking

Adaptar controles para telas sensíveis ao toque.

Suporte a controle Bluetooth será opcional.

FASE 7 — EVOLUÇÃO LIVRE

Permitir que os alunos implementem melhorias.

Exemplos:

- Novas fases
- Novos inimigos
- Chefões
- Novas armas
- Aumento de dificuldade
- Personalização da nave

As alterações deverão respeitar
a arquitetura desenvolvida.

## ORGANIZAÇÃO DAS FASES

Preserve o projeto original em `exercicio10/` sem movê-lo ou modificá-lo.
Crie as evoluções em subpastas independentes:

```text
exercicio10/
├── src/                              # Exercício 10 original, preservado
└── fases/
   ├── fase-01-refatoracao/
   ├── fase-02-gradle/
   ├── fase-03-libgdx/
   ├── fase-04-interface-grafica/
   ├── fase-05-servidor-ranking/
   ├── fase-06-android/
   └── fase-07-evolucao-livre/
```

Regras para essas pastas:

- Cada fase deve ser um projeto independente, compilável ou executável conforme a tecnologia daquela etapa.
- A Fase 1 deve partir de uma cópia de `exercicio10/src`; cada fase posterior deve partir da versão concluída na pasta da fase imediatamente anterior.
- Nunca sobrescreva ou altere a pasta original nem as fases anteriores.
- Copie somente os arquivos necessários ao projeto; não copie saídas de compilação, caches ou o `ranking.json` gerado localmente.
- Mantenha o tutorial dentro da pasta da fase correspondente, em `docs/fase-XX-tutorial.md`.
- Se uma fase produzir mais de um aplicativo, como cliente e servidor, mantenha-os como subprojetos dentro da pasta daquela fase.
- Atualize o `README.md` da raiz com o índice das fases e as instruções para executar cada versão.

## REGRAS DE EXECUÇÃO

1. Analise primeiro o projeto existente.
2. Identifique classes, responsabilidades,
   dependências e problemas arquiteturais.
3. Não invente funcionalidades existentes.
4. Não reescreva todo o projeto desnecessariamente.
5. Preserve o comportamento atual, salvo
   quando a fase exigir mudanças.
6. Execute somente a fase solicitada.
7. Planeje cada fase separadamente. Antes de iniciar uma fase, apresente o diagnóstico do estado de origem, o escopo, as pastas e classes afetadas, as alterações propostas, os padrões que serão usados e por quê, os riscos para o comportamento existente, os critérios de conclusão e a estratégia de validação.
8. Aguarde minha aprovação explícita para aquela fase. Até receber essa aprovação, não crie, copie, mova, renomeie ou edite arquivos ou pastas. A aprovação de uma fase não autoriza o início de nenhuma fase posterior.
9. Depois da aprovação, implemente somente o plano aprovado e somente dentro da pasta da fase solicitada. Preserve intactos o projeto original e as pastas das fases anteriores. Se surgir a necessidade de ampliar o escopo ou mudar o plano, pare e peça nova aprovação.
10. Após implementar a fase aprovada, compile e execute os testes disponíveis naquela pasta.
11. Informe claramente os testes executados, os resultados e qualquer teste que não tenha sido possível executar.

## TUTORIAL DIDÁTICO

Para cada fase, gere um arquivo Markdown na pasta daquele projeto:

`exercicio10/fases/fase-XX-nome/docs/fase-XX-tutorial.md`

O tutorial deverá conter:

1. Introdução
2. Objetivos de aprendizagem
3. Conceitos teóricos
4. Problemas encontrados no código original
5. Arquitetura antes da transformação
6. Passo a passo da implementação
7. Código antes e depois
8. Explicação das alterações
9. Arquitetura resultante
10. Como executar e testar
11. Exercícios para os alunos
12. Conclusão

Sempre que pertinente, inclua diagramas
Mermaid e exemplos reais do projeto.

Explique o motivo de cada decisão arquitetural.

O tutorial deverá permitir que um aluno
reproduza a transformação manualmente.

## PRIMEIRA EXECUÇÃO

Analise o projeto Java atualmente aberto
no workspace.

Identifique a estrutura do Exercício 10.

Prepare o diagnóstico arquitetural e proponha
o plano detalhado da Fase 1.

Não crie a pasta da Fase 1, não copie arquivos e não altere o código antes da minha aprovação explícita do plano.
