# Enunciado - Introdução ao libGDX no Missão Marte

## 1. Contexto

Você recebeu o jogo **Missão Marte** da Fase 2: um projeto Gradle de módulo único, com as regras separadas em `domain` e `application`, o ranking em `infrastructure` e a interface em modo texto (`console`). Há 28 testes JUnit que passam.

A equipe quer uma **janela gráfica** e, nas próximas fases, sprites, sons, servidor de ranking e Android. Para isso, o jogo precisa passar a usar o **libGDX**, mantendo as regras como estão. Sua tarefa é **adaptar gradualmente o jogo para a estrutura do libGDX (`Game`, `Screen` e ciclo de vida), reaproveitando a lógica existente**, e **documentar o que foi feito e por quê**.

Nesta fase o visual é simples: formas geométricas e a fonte padrão. Imagens, animações e sons ficam para a Fase 4.

## 2. Objetivos de aprendizagem

- Explicar o que é o libGDX, seus módulos e seus backends.
- Descrever o ciclo de vida `create()`, `render()`, `resize()`, `pause()`, `resume()` e `dispose()` e como `Game` e `Screen` o estendem.
- Montar um projeto Gradle multi-módulo (`core` e `lwjgl3`).
- Reaproveitar regras existentes trocando apenas a interface.
- Tratar entrada de teclado com `InputProcessor` e converter teclas em ações do jogo.
- Usar câmera e viewport para que a janela possa ser redimensionada.
- Gerenciar recursos que precisam de `dispose()`.
- Manter a regra de dependência entre camadas e justificar a mudança feita no ranking.

## 3. Entrada

Use como ponto de partida a pasta `fase-02-gradle`.

Regras:

- **Não altere** `fase-02-gradle`, `fase-01-refatoracao` nem `exercicio10/src`. Trabalhe em uma pasta nova (por exemplo, `fase-03-libgdx`).
- **Não altere as regras do jogo.** `domain` e `GameSession` devem se comportar como antes. O que muda é a interface e a organização do projeto.
- Os arquivos de `domain` e `application` que não precisarem mudar devem ser apenas copiados (verifique por hash).

## 4. Ambiente

| Item | Requisito |
|---|---|
| JDK | 17 (`JAVA_HOME` e `PATH`) |
| Gradle do wrapper | 8.7 (pode copiar o wrapper da Fase 2) |
| libGDX | 1.12.1 (versão do referencial; a decisão deve estar justificada no documento) |
| Rede | Acesso ao Maven Central |
| Vídeo | Placa com suporte a OpenGL 2.0 ou superior |

## 5. O que deve ser feito

### 5.1 Estrutura do projeto

```text
fase-03-libgdx/
  settings.gradle
  build.gradle                 (raiz: versão do libGDX e configuração comum)
  gradlew, gradlew.bat, gradle/wrapper/
  core/
    build.gradle
    src/main/java/br/edu/unifor/missaomarte/{domain, application, infrastructure, gdx}
    src/test/java/...
  lwjgl3/
    build.gradle
    src/main/java/br/edu/unifor/missaomarte/lwjgl3/
  docs/
  README.md
  .gitignore
```

- `core`: o jogo, independente de plataforma.
- `lwjgl3`: apenas o launcher da janela desktop.
- Os pacotes `console` e a classe `Main` **não** fazem parte desta fase (a versão console continua na Fase 2).

### 5.2 Requisitos do build

1. `settings.gradle` define o nome do projeto e inclui os módulos `core` e `lwjgl3`.
2. O `build.gradle` da raiz fixa a versão do libGDX em **um único lugar** e aplica Java 17, UTF-8 e `mavenCentral()` aos módulos.
3. O `core` depende da API do libGDX (`gdx`) e do JUnit 5 para os testes (`testRuntimeOnly` com o `junit-platform-launcher`).
4. O `lwjgl3` depende do `core`, do backend `gdx-backend-lwjgl3` e das bibliotecas nativas `gdx-platform` (`natives-desktop`), todos na mesma versão.
5. O `lwjgl3` usa o plugin `application` e define a classe principal do launcher.
6. A tarefa `run` do `lwjgl3` deve executar com o diretório de trabalho na **raiz da fase**, para o `ranking.json` ficar ali.
7. Em macOS, a tarefa `run` precisa do argumento `-XstartOnFirstThread` (adicione condicionalmente).
8. `.gitignore` exclui `build/`, `.gradle/`, `out/` e `ranking.json`.

### 5.3 Camadas e dependências

- `domain`, `application` e `infrastructure` **não podem importar** nada de `com.badlogic`. Só o pacote `gdx` e o módulo `lwjgl3` conhecem o libGDX.
- `application` **não pode importar** `infrastructure`.

### 5.4 Ranking: levar a regra para a aplicação

Na Fase 2 a regra do Top 10 (quem entra, ordenação, corte da lista) está dentro do fluxo do console, e a interface `RankingRepository` está em `infrastructure`. Para que uma tela gráfica reaproveite a regra sem copiá-la:

1. Mova `RankingRepository` e `RankingEntry` para um pacote de `application` (por exemplo, `application.ranking`). A implementação em arquivo continua em `infrastructure`.
2. Crie uma classe de aplicação (sugestão: `RankingService`) que receba o repositório e ofereça: listar o Top 10, registrar uma pontuação (devolvendo se entrou no ranking) e limpar o ranking.
3. Preserve as regras atuais: pontuação menor ou igual a zero não entra; a lista é ordenada por pontuação decrescente; o máximo é 10 entradas; com a lista cheia, só entra uma pontuação **maior** que a pior.

### 5.5 Adaptação ao libGDX

**Classe do jogo (`Game`).**

1. Estende `Game`. Em `create()` cria a câmera, o `FitViewport` (proporção 800 x 600), o `SpriteBatch`, o `ShapeRenderer` e a fonte, e abre o menu. Em `dispose()` libera o que precisa ser liberado.
2. Recebe a fábrica de missões, o serviço de ranking e o `Random` **pelo construtor** (injeção de dependência). Quem os cria é o launcher.
3. Os recursos pesados são criados **uma vez** e compartilhados com as telas.

**Telas (`Screen`).** O jogo deve ter, no mínimo:

| Tela | Comportamento |
|---|---|
| Menu | `1` Jogar, `2` Ver ranking, `3` Limpar ranking, `4` Sair |
| Nova partida | Digitar o nome do piloto, escolher a dificuldade (fácil, médio, difícil) e iniciar |
| Partida | Mapa 11 x 11 (-5 a 5), painel com os indicadores e mensagens do último turno |
| Ranking | Top 10 e, após uma vitória, o resumo da partida |

Requisitos das telas:

1. Toda tela registra no log (`Gdx.app.log`) a passagem por `show()`, `resize()`, `pause()`, `resume()`, `hide()` e `dispose()`. Evite repetir esse código em cada tela.
2. `resize()` atualiza o viewport. Antes de desenhar, aplique o viewport e informe `camera.combined` ao `SpriteBatch` e ao `ShapeRenderer`.
3. Cada tela instala seu `InputProcessor` em `show()` e o remove em `hide()`.
4. Nenhuma tela contém regra de jogo: elas traduzem entrada, chamam a sessão e desenham o estado.

**Entrada.** O jogo continua **por turnos**: cada tecla válida processa um turno. Crie uma classe que converta teclas em `Acao`:

| Teclas | Ação |
|---|---|
| `W` ou seta para cima | cima |
| `S` ou seta para baixo | baixo |
| `A` ou seta para a esquerda | esquerda |
| `D` ou seta para a direita | direita |
| `E` | embarcar |
| `Q` ou `Esc` | sair (aborta a partida) |
| qualquer outra | inválida (ignorada, **sem consumir turno**) |

**Desenho do mapa.**

1. Cada célula é um retângulo; cada objeto (base, asteroides, inimigos, passageiros, nave) é um retângulo colorido com o seu símbolo, obtido de `ObjetoRenderizavel`. Não escreva os símbolos como texto fixo.
2. Quando dois objetos ocupam a mesma célula, a nave deve aparecer por cima.
3. O painel mostra: piloto, dificuldade, vidas, pontuação, a bordo/capacidade, entregues/total, restantes no mapa, movimentos e as mensagens do último turno.
4. O fim da partida mostra uma mensagem de vitória ou de "game over", e `Enter` leva ao ranking (vitória) ou ao menu (derrota). Abortar com `Q` ou `Esc` volta direto ao menu.
5. Em vitória, a pontuação é registrada pelo serviço de ranking **uma única vez**.

**Conversão de coordenadas.** No jogo o eixo Y cresce para baixo (`cima` é `y - 1`), e no libGDX cresce para cima. Isole essa conversão em **uma classe sem dependência do libGDX** (sugestão: `MapaGeometria`) e teste-a.

### 5.6 Launcher

No módulo `lwjgl3`, crie o launcher (composition root): instancia a fábrica, o repositório em arquivo, o serviço de ranking e o `Random`, monta o jogo e abre a janela com `Lwjgl3Application` (título, 800 x 600, vsync e 60 FPS).

### 5.7 Testes automatizados

Mantenha os 28 testes da Fase 2 passando (ajustando apenas os `import` que mudaram) e acrescente testes para o que criou:

| O que testar | O que verificar (no mínimo) |
|---|---|
| Conversão de teclas em ações | Movimento por letras e setas, embarcar, sair e tecla desconhecida |
| Conversão de coordenadas | X crescente, Y invertido e centro da célula |
| Serviço de ranking (com um repositório em memória) | Zero ou negativo não entra, ordenação, limite de 10, corte pelo pior e limpar |

Os testes não podem abrir janela nem usar OpenGL.

### 5.8 Comandos que devem funcionar

```powershell
.\gradlew --version                          # Gradle 8.7 e JVM 17
.\gradlew clean build                        # compila e testa todos os módulos
.\gradlew :lwjgl3:run --console=plain        # abre a janela
```

## 6. Pontos de atenção

Erros comuns que costumam consumir tempo:

- O texto do `BitmapFont` e o `ShapeRenderer` precisam da mesma projeção da câmera; sem ela, nada aparece onde esperado.
- Não deixe o `SpriteBatch` e o `ShapeRenderer` abertos ao mesmo tempo: termine um com `end()` antes de começar o outro.
- Não crie `BitmapFont`, `ShapeRenderer` ou `SpriteBatch` dentro de `render()`.
- O `Game` não chama o `dispose()` das telas; por isso os recursos são do jogo, não das telas.
- O `draw` do `BitmapFont` usa o `y` como o **topo** do texto.
- No desktop, `pause()` é chamado antes de `dispose()` ao fechar a janela.
- Depois de trocar de tela dentro de um evento de teclado, o processador de entrada antigo é removido em `hide()`; confira que a tela nova instala o dela.
- Teclas que não têm ação não devem consumir turno.

## 7. Preservar o comportamento

Prove que as regras não mudaram:

- Os arquivos de `domain` e de `application` copiados devem ser **idênticos** aos da Fase 2 (compare por hash). Liste os que diferem ou são novos e justifique cada um.
- A pontuação, a dificuldade, o embarque, o desembarque na base, a colisão e a vitória devem se comportar como na Fase 2.
- Fases anteriores e `exercicio10/src` permanecem intactos.

## 8. Documentação obrigatória

Entregue um arquivo `docs/tutorial.md` (ou nome equivalente) com as seções abaixo.

### 8.1 Estado de origem e destino

- Como era o projeto (módulo único, console) e como ficou (multi-módulo, janela).
- Tabela "item da Fase 2 -> equivalente na Fase 3" (por exemplo, `Main` -> launcher; `ConsoleGame` -> telas; `ConsoleRenderer` -> desenho com `ShapeRenderer` e `BitmapFont`).
- Diagrama da arquitetura **antes** e **depois** (Mermaid ou imagem), mostrando a regra de dependência.

### 8.2 Conceitos de libGDX aplicados

Explique, com exemplos do seu próprio código:

- O que é o libGDX, o que são backends e por que o projeto tem os módulos `core` e `lwjgl3`.
- O ciclo de vida da aplicação e o das telas, com a saída real do log da sua execução (inclua o caso de `resize()` aparecer duas vezes no início e de `pause()` aparecer antes de `dispose()`).
- A diferença entre `Game` e `Screen` e por que `setScreen` corresponde ao padrão State.
- O que são câmera e viewport e o que o `FitViewport` faz ao redimensionar.
- Por que o jogo por turnos desenha a cada quadro mas só atualiza o estado em um evento de tecla.
- Quais recursos precisam de `dispose()` e quem os libera.

### 8.3 Decisões de projeto

- Por que `RankingRepository` e `RankingEntry` foram movidos para `application`.
- Por que a conversão de coordenadas ficou em uma classe separada.
- Por que a classe que converte teclas em ações é separada das telas.
- Padrões usados e onde (por exemplo, State, Template Method, Adapter, Pure Fabrication, injeção de dependência), com a diferença entre o que de fato segue o padrão e o que apenas se parece com ele.

### 8.4 Passo a passo

Os comandos e as decisões na ordem em que foram feitos, incluindo problemas reais encontrados (por exemplo, janela em branco, texto fora do lugar, eixo Y invertido, tecla que consome turno) e como cada um foi resolvido.

### 8.5 Testes e evidências

O que cada teste cobre, a saída de `gradlew clean build` com a quantidade de testes, o log do ciclo de vida e a comparação por hash dos fontes copiados.

### 8.6 Limitações e próximos passos

Liste pelo menos três pontos que ainda podem melhorar (por exemplo, visual simples, ausência de sons e animações, legenda que depende de objetos de exemplo, textos sem acento) e relacione cada um com a Fase 4.

## 9. Roteiro mínimo de validação

Execute e registre o resultado de cada passo:

1. `gradlew --version` mostra Gradle 8.7 e JVM 17.
2. `gradlew clean build` termina com `BUILD SUCCESSFUL` (informe a quantidade de testes).
3. `gradlew :lwjgl3:run` abre a janela e o log mostra `create()` e as chamadas de `show()` e `resize()` do menu.
4. Menu: opções 1 a 4; a opção 3 limpa o ranking.
5. Nova partida: digitar o nome, apagar com `Backspace`, mudar a dificuldade, `Esc` volta ao menu.
6. Partida: `W` move a nave **para cima na tela**; uma tecla sem ação não consome turno.
7. Embarcar em uma célula com passageiro e em uma sem passageiro.
8. Entregar os passageiros na base e conferir os indicadores (a bordo, entregues e restantes fecham com o total).
9. Colidir com um perigo e conferir a perda de vida e de pontos.
10. Vitória, ranking e derrota (perder todas as vidas).
11. Redimensionar e minimizar a janela: a proporção se mantém e o log mostra `resize()`, `pause()` e `resume()`.
12. Fechar a janela: o log mostra `pause()` antes de `dispose()`.
13. `ranking.json` aparece na pasta da fase.
14. Verificar por busca que nenhum arquivo fora de `gdx` e `lwjgl3` importa `com.badlogic`.
15. Verificar por hash e por `git status` que as fases anteriores e `exercicio10/src` não mudaram.

## 10. Entregáveis

1. Pasta `fase-03-libgdx` com o projeto multi-módulo, o wrapper e o `.gitignore`.
2. Testes JUnit 5 que passam com `gradlew clean build`.
3. `README.md` da fase com as instruções de execução e os controles.
4. Documento `tutorial.md` com todas as seções da etapa 8.
5. Evidências do roteiro da etapa 9.

## 11. Critérios de avaliação

| Critério | Peso |
|---|---|
| Projeto multi-módulo correto (build, versões, dependências, `run` com diretório e macOS) | 15% |
| Adaptação ao libGDX (`Game`, `Screen`, ciclo de vida, viewport, recursos e `dispose`) | 20% |
| Jogo preservado e funcional (fontes idênticos, regras, turnos, ranking, validação do roteiro) | 20% |
| Camadas e dependências (nenhum `com.badlogic` fora de `gdx`/`lwjgl3`, ranking movido para `application`) | 15% |
| Testes JUnit 5 relevantes, isolados do OpenGL e passando | 10% |
| Documentação dos conceitos e das decisões com exemplos reais do projeto | 15% |
| Evidências, limitações e relação com a Fase 4 | 5% |

## 12. Observações

- Alterar as regras do jogo para "facilitar" a interface reduz a nota. Se achar que algo precisa mudar, registre como limitação.
- Descrever um comportamento, um log ou um teste que você não executou reduz a nota da seção correspondente. O documento deve refletir o projeto entregue.
- Registrar um problema real encontrado e como foi resolvido vale mais do que afirmar que tudo funcionou de primeira.
- Se a validação visual não puder ser feita no seu computador (por exemplo, sem suporte a OpenGL), declare isso e entregue as demais evidências.
- É permitido usar ferramentas de apoio, mas cada classe, comando e log citado precisa existir no seu projeto.
- Os nomes de classe sugeridos podem mudar, desde que as responsabilidades continuem separadas e justificadas.

## 13. Desafio opcional

1. Trocar o `FitViewport` por `StretchViewport` e por `ExtendViewport` e descrever a diferença ao redimensionar.
2. Acentuar os textos das telas e confirmar que a fonte padrão os desenha.
3. Adicionar uma tela de pausa acionada por `P`, que retorne à partida sem perder o estado.
4. Extrair interfaces para a fábrica de missões e para o serviço de ranking e injetá-las no jogo.
5. Criar uma implementação alternativa da conversão de teclas (por exemplo, `I/J/K/L`) sem alterar `GameSession`.
6. Executar o projeto com a libGDX 1.14.2, descrever o que mudou e se algum teste ou comando precisou de ajuste.
