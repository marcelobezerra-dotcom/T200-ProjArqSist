# Enunciado - Refatoracao Arquitetural do Missao Marte (Console)

## 1. Contexto

Voce recebeu o jogo **Missao Marte** em modo texto. Todo o codigo de fluxo, regras, desenho do mapa e ranking esta concentrado em um unico `Main.java` com metodos estaticos. O objetivo da equipe e preparar o projeto para trocar a interface (por exemplo, para LibGDX) sem reescrever as regras do jogo.

Sua tarefa e **refatorar o codigo mantendo o jogo funcionando** e **documentar as decisoes de projeto**.

## 2. Objetivos de aprendizagem

- Separar responsabilidades em camadas.
- Aplicar e justificar principios SOLID.
- Aplicar e justificar padroes GRASP.
- Identificar e aplicar pelo menos um padrao de projeto (GoF ou equivalente).
- Validar a refatoracao com testes de execucao e relatar limitacoes com honestidade.

## 3. Entrada

Use como ponto de partida o `Main.java` original do exercicio. Nao altere o codigo original: trabalhe em uma pasta separada (por exemplo, `fase-01-refatoracao`).

## 4. O que deve ser feito

### 4.1 Organizacao em camadas

Crie ao menos quatro pacotes com responsabilidades distintas:

| Pacote | Responsabilidade esperada |
|---|---|
| `domain` | Entidades e regras que pertencem ao negocio (nave, passageiro, perigos, missao, posicao). |
| `application` | Coordenacao da partida e casos de uso (turno, criacao do cenario, dificuldade). |
| `console` | Entrada e saida textual (menu, leitura de comandos, desenho do mapa). |
| `infrastructure` | Persistencia do ranking. |

Regra de dependencia: `domain` e `application` **nao podem importar** classes de `console` nem de `infrastructure`.

### 4.2 Requisitos de projeto

1. `Main` deve apenas montar as dependencias e iniciar o jogo.
2. As regras de turno (movimento, embarque, colisao, pontuacao, vitoria e derrota) devem ficar fora da camada `console`.
3. Os comandos digitados devem ser convertidos para um tipo proprio (por exemplo, um `enum`) antes de chegar na regra.
4. A criacao do cenario por dificuldade deve estar isolada em uma classe propria.
5. O ranking deve ser acessado por uma **interface**; a implementacao em arquivo deve ser substituivel.
6. O desenho do mapa nao pode ter simbolos de entidades espalhados como texto fixo. Cada elemento do mapa deve informar a propria posicao e o proprio simbolo, e a legenda deve ser montada a partir das entidades.
7. Passageiros de tipos diferentes devem ser tratados de forma uniforme pela regra do jogo (sem `if` por tipo).

### 4.3 Regras de jogo que devem ser preservadas e coerentes

Ao refatorar, garanta que:

- Nenhum passageiro, asteroide, inimigo, nave ou base comece na mesma celula.
- Ao chegar na base com passageiros a bordo, eles desembarcam e a capacidade da nave e liberada.
- A vitoria so ocorre quando **todos os passageiros forem entregues na base**.
- Os indicadores exibidos ao jogador fecham entre si. Exemplo: `a bordo + entregues + restantes no mapa = total de passageiros`.
- A capacidade da nave e coerente com a quantidade de passageiros da dificuldade escolhida (ou voce explica no relatorio a regra adotada).

Se voce decidir mudar algum comportamento em relacao ao original (simbolos, tamanho do mapa, tamanho do ranking, etc.), registre cada mudanca na documentacao.

### 4.4 Script de build

Crie um comando unico para compilar o projeto (e, opcionalmente, executa-lo). Exemplo de uso esperado:

```powershell
.\build.ps1          # compila
.\build.ps1 -Run     # compila e executa
```

## 5. Documentacao obrigatoria

Entregue um arquivo `docs/tutorial.md` (ou nome equivalente) contendo as secoes abaixo.

### 5.1 O que foi feito na migracao

- Problemas encontrados no codigo original (liste os trechos e metodos).
- Passo a passo da refatoracao.
- Tabela "responsabilidade -> onde estava -> onde ficou".
- Bugs ou inconsistencias encontrados durante os testes e como foram corrigidos.

### 5.2 Onde foi usado SOLID

Para **cada um dos cinco principios**, apresente:

| Principio | Classe(s) ou interface(s) | Como foi aplicado | Limite ou ponto que ainda viola |
|---|---|---|---|

Se um principio nao foi aplicado, diga isso e explique o motivo. Nao serao aceitas justificativas genericas: cite classes e metodos reais do seu codigo.

### 5.3 Onde foi usado GRASP

Identifique **no minimo cinco** padroes GRASP (por exemplo: Controller, Information Expert, Creator, Low Coupling, High Cohesion, Polymorphism, Pure Fabrication, Indirection, Protected Variations). Para cada um:

| Padrao GRASP | Classe e metodo | Por que essa classe recebeu a responsabilidade |
|---|---|---|

### 5.4 Padroes de projeto

Identifique **no minimo dois** padroes de projeto presentes no seu codigo (por exemplo: Factory, Repository, Strategy, Value Object, Template Method, Injecao de Dependencia). Para cada um:

- Nome do padrao e problema que ele resolve.
- Classes participantes.
- Trecho de codigo (antes e depois, quando aplicavel).
- Se o codigo realmente segue o padrao ou apenas se parece com ele. Seja preciso: uma tabela de configuracao com `switch`, por exemplo, nao e Strategy.

### 5.5 Diagramas

- Um diagrama da arquitetura **antes** (Mermaid ou imagem).
- Um diagrama de dependencias entre camadas **depois**.
- Um diagrama de classes do dominio.

### 5.6 Limitacoes conhecidas

Liste pelo menos tres pontos do seu proprio codigo que ainda poderiam melhorar (por exemplo, regra de negocio ainda no console, dependencia de classe concreta, `switch` que impede extensao) e proponha como resolver cada um.

### 5.7 Como executar e testar

Descreva o roteiro de validacao que voce executou e o resultado de cada passo.

## 6. Roteiro minimo de testes

Execute e registre a saida resumida (pode ser texto colado):

1. Menu: opcoes validas e uma opcao invalida.
2. Partida na dificuldade facil, medio e dificil: conferir que os indicadores fecham com o total de passageiros.
3. Embarcar em uma celula com passageiro e em uma celula sem passageiro.
4. Levar passageiros a base e verificar desembarque e vitoria.
5. Colidir com um perigo e verificar perda de vida e de pontos.
6. Vitoria gravando no ranking e posterior consulta.
7. Limpar ranking.
8. Encerrar a partida com `Q` e encerrar a entrada de dados no meio da partida.

## 7. Entregaveis

1. Pasta com o codigo refatorado e o script de build.
2. Documento `tutorial.md` com todas as secoes da etapa 5.
3. Evidencias dos testes da etapa 6.

## 8. Criterios de avaliacao

| Criterio | Peso |
|---|---|
| Separacao em camadas e regra de dependencia respeitada | 20% |
| Jogo compila, executa e as regras de jogo ficam coerentes | 20% |
| Documentacao de SOLID com classes reais e limites apontados | 20% |
| Documentacao de GRASP com justificativa de cada responsabilidade | 15% |
| Padroes de projeto identificados corretamente | 10% |
| Limitacoes, diagramas e roteiro de testes | 10% |
| Clareza e organizacao do documento | 5% |

## 9. Observacoes

- Documentar um principio que nao foi aplicado, ou nomear um padrao que o codigo nao implementa, reduz a nota da secao correspondente.
- Apontar uma limitacao real do proprio codigo vale mais do que afirmar que tudo foi aplicado perfeitamente.
- E permitido usar ferramentas de apoio, mas o documento deve refletir o codigo que voce entregou: cada classe citada precisa existir.

## 10. Desafio opcional

1. Transformar a configuracao de dificuldade em uma familia real de estrategias.
2. Mover para a camada `application` a regra de entrada no ranking.
3. Criar uma implementacao do repositorio de ranking em memoria para testes.
4. Extrair interfaces para as dependencias concretas usadas pelo controlador do console.
