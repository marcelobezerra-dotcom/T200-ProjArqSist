# Enunciado - Refatoração Arquitetural do Missão Marte (Console)

## 1. Contexto

Você recebeu o jogo **Missão Marte** em modo texto. Todo o código de fluxo, regras, desenho do mapa e ranking está concentrado em um único `Main.java` com métodos estáticos. O objetivo da equipe é preparar o projeto para trocar a interface (por exemplo, para LibGDX) sem reescrever as regras do jogo.

Sua tarefa é **refatorar o código mantendo o jogo funcionando** e **documentar as decisões de projeto**.

## 2. Objetivos de aprendizagem

- Separar responsabilidades em camadas.
- Aplicar e justificar princípios SOLID.
- Aplicar e justificar padrões GRASP.
- Identificar e aplicar pelo menos um padrão de projeto (GoF ou equivalente).
- Validar a refatoração com testes de execução e relatar limitações com honestidade.

## 3. Entrada

Use como ponto de partida o `Main.java` original do exercício. Não altere o código original: trabalhe em uma pasta separada (por exemplo, `fase-01-refatoracao`).

## 4. O que deve ser feito

### 4.1 Organização em camadas

Crie ao menos quatro pacotes com responsabilidades distintas:

| Pacote | Responsabilidade esperada |
|---|---|
| `domain` | Entidades e regras que pertencem ao negócio (nave, passageiro, perigos, missão, posição). |
| `application` | Coordenação da partida e casos de uso (turno, criação do cenário, dificuldade). |
| `console` | Entrada e saída textual (menu, leitura de comandos, desenho do mapa). |
| `infrastructure` | Persistência do ranking. |

Regra de dependência: `domain` e `application` **não podem importar** classes de `console` nem de `infrastructure`.

### 4.2 Requisitos de projeto

1. `Main` deve apenas montar as dependências e iniciar o jogo.
2. As regras de turno (movimento, embarque, colisão, pontuação, vitória e derrota) devem ficar fora da camada `console`.
3. Os comandos digitados devem ser convertidos para um tipo próprio (por exemplo, um `enum`) antes de chegar na regra.
4. A criação do cenário por dificuldade deve estar isolada em uma classe própria.
5. O ranking deve ser acessado por uma **interface**; a implementação em arquivo deve ser substituível.
6. O desenho do mapa não pode ter símbolos de entidades espalhados como texto fixo. Cada elemento do mapa deve informar a própria posição e o próprio símbolo, e a legenda deve ser montada a partir das entidades.
7. Passageiros de tipos diferentes devem ser tratados de forma uniforme pela regra do jogo (sem `if` por tipo).

### 4.3 Regras de jogo que devem ser preservadas e coerentes

Ao refatorar, garanta que:

- Nenhum passageiro, asteroide, inimigo, nave ou base comece na mesma célula.
- Ao chegar na base com passageiros a bordo, eles desembarcam e a capacidade da nave é liberada.
- A vitória só ocorre quando **todos os passageiros forem entregues na base**.
- Os indicadores exibidos ao jogador fecham entre si. Exemplo: `a bordo + entregues + restantes no mapa = total de passageiros`.
- A capacidade da nave é coerente com a quantidade de passageiros da dificuldade escolhida (ou você explica no relatório a regra adotada).

Se você decidir mudar algum comportamento em relação ao original (símbolos, tamanho do mapa, tamanho do ranking, etc.), registre cada mudança na documentação.

### 4.4 Script de build

Crie um comando único para compilar o projeto (e, opcionalmente, executá-lo). Exemplo de uso esperado:

```powershell
.\build.ps1          # compila
.\build.ps1 -Run     # compila e executa
```

## 5. Documentação obrigatória

Entregue um arquivo `docs/tutorial.md` (ou nome equivalente) contendo as seções abaixo.

### 5.1 O que foi feito na migração

- Problemas encontrados no código original (liste os trechos e métodos).
- Passo a passo da refatoração.
- Tabela "responsabilidade -> onde estava -> onde ficou".
- Bugs ou inconsistências encontrados durante os testes e como foram corrigidos.

### 5.2 Onde foi usado SOLID

Para **cada um dos cinco princípios**, apresente:

| Princípio | Classe(s) ou interface(s) | Como foi aplicado | Limite ou ponto que ainda viola |
|---|---|---|---|

Se um princípio não foi aplicado, diga isso e explique o motivo. Não serão aceitas justificativas genéricas: cite classes e métodos reais do seu código.

### 5.3 Onde foi usado GRASP

Identifique **no mínimo cinco** padrões GRASP (por exemplo: Controller, Information Expert, Creator, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations). Para cada um:

| Padrão GRASP | Classe e método | Por que essa classe recebeu a responsabilidade |
|---|---|---|

### 5.4 Padrões de projeto

Identifique **no mínimo dois** padrões de projeto presentes no seu código (por exemplo: Factory, Repository, Strategy, Value Object, Template Method, Injeção de Dependência). Para cada um:

- Nome do padrão e problema que ele resolve.
- Classes participantes.
- Trecho de código (antes e depois, quando aplicável).
- Se o código realmente segue o padrão ou apenas se parece com ele. Seja preciso: uma tabela de configuração com `switch`, por exemplo, não é Strategy.

### 5.5 Diagramas

- Um diagrama da arquitetura **antes** (Mermaid ou imagem).
- Um diagrama de dependências entre camadas **depois**.
- Um diagrama de classes do domínio.

### 5.6 Limitações conhecidas

Liste pelo menos três pontos do seu próprio código que ainda poderiam melhorar (por exemplo, regra de negócio ainda no console, dependência de classe concreta, `switch` que impede extensão) e proponha como resolver cada um.

### 5.7 Como executar e testar

Descreva o roteiro de validação que você executou e o resultado de cada passo.

## 6. Roteiro mínimo de testes

Execute e registre a saída resumida (pode ser texto colado):

1. Menu: opções válidas e uma opção inválida.
2. Partida na dificuldade fácil, médio e difícil: conferir que os indicadores fecham com o total de passageiros.
3. Embarcar em uma célula com passageiro e em uma célula sem passageiro.
4. Levar passageiros à base e verificar desembarque e vitória.
5. Colidir com um perigo e verificar perda de vida e de pontos.
6. Vitória gravando no ranking e posterior consulta.
7. Limpar ranking.
8. Encerrar a partida com `Q` e encerrar a entrada de dados no meio da partida.

## 7. Entregáveis

1. Pasta com o código refatorado e o script de build.
2. Documento `tutorial.md` com todas as seções da etapa 5.
3. Evidências dos testes da etapa 6.

## 8. Critérios de avaliação

| Critério | Peso |
|---|---|
| Separação em camadas e regra de dependência respeitada | 20% |
| Jogo compila, executa e as regras de jogo ficam coerentes | 20% |
| Documentação de SOLID com classes reais e limites apontados | 20% |
| Documentação de GRASP com justificativa de cada responsabilidade | 15% |
| Padrões de projeto identificados corretamente | 10% |
| Limitações, diagramas e roteiro de testes | 10% |
| Clareza e organização do documento | 5% |

## 9. Observações

- Documentar um princípio que não foi aplicado, ou nomear um padrão que o código não implementa, reduz a nota da seção correspondente.
- Apontar uma limitação real do próprio código vale mais do que afirmar que tudo foi aplicado perfeitamente.
- É permitido usar ferramentas de apoio, mas o documento deve refletir o código que você entregou: cada classe citada precisa existir.

## 10. Desafio opcional

1. Transformar a configuração de dificuldade em uma família real de estratégias.
2. Mover para a camada `application` a regra de entrada no ranking.
3. Criar uma implementação do repositório de ranking em memória para testes.
4. Extrair interfaces para as dependências concretas usadas pelo controlador do console.
