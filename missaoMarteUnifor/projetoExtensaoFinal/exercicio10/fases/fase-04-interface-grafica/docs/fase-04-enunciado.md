# Enunciado - Interface gráfica do Missão Marte

## 1. Contexto

Você recebeu o jogo **Missão Marte** da Fase 3: um projeto Gradle multi-módulo (`core` e `lwjgl3`) que abre uma janela em libGDX, com menu, nova partida, partida e ranking. O visual ainda é provisório: retângulos coloridos e a fonte padrão, sem imagens, animações, sons nem HUD. Há 39 testes JUnit que passam.

A equipe quer um jogo com aparência e resposta de jogo de verdade, **sem mexer nas regras de negócio**. Sua tarefa é **transformar a apresentação do jogo (sprites, movimentação, entrada, colisões, animações, sons e HUD)**, preservando a arquitetura em camadas, e **documentar o que foi feito e por quê**.

O jogo continua **por turnos**: cada tecla válida processa um turno em `GameSession`. O que se movimenta e se anima é o desenho.

## 2. Objetivos de aprendizagem

- Carregar e liberar imagens e sons com o `AssetManager`.
- Explicar a cadeia PNG, `Texture`, `TextureRegion` e animação por quadros (`Animation`).
- Separar a posição lógica (célula) da posição visual (desenho) para criar movimento suave.
- Fazer a interface reagir ao que aconteceu em cada turno sem colocar regra de negócio na tela.
- Construir um HUD que apenas lê o estado da partida.
- Explicar por que a colisão deste jogo é uma regra do domínio e não usa `Intersector`.
- Tratar entrada com repetição ao segurar a tecla.
- Testar a lógica de apresentação sem OpenGL nem áudio.

## 3. Entrada

Use como ponto de partida a pasta `fase-03-libgdx`.

Regras:

- **Não altere** `fase-03-libgdx`, `fase-02-gradle`, `fase-01-refatoracao` nem `exercicio10/src`. Trabalhe em uma pasta nova (por exemplo, `fase-04-interface-grafica`).
- **Não altere as regras do jogo.** `domain` deve ficar idêntico ao da Fase 3 (verifique por hash). A pontuação, a dificuldade, o embarque, a entrega na base, a colisão e a vitória devem se comportar como antes.
- Em `application` só são permitidas mudanças **aditivas** (seção 5.4). Justifique cada arquivo alterado.
- O jogo **não tem** tiro, projéteis nem escudo; não crie funcionalidades que não existem.

## 4. Ambiente

| Item | Requisito |
|---|---|
| JDK | 17 (`JAVA_HOME` e `PATH`) |
| Gradle do wrapper | 8.7 (copie o wrapper da Fase 3) |
| libGDX | 1.12.1 (a mesma da Fase 3; nenhuma dependência nova) |
| Vídeo | Placa com suporte a OpenGL 2.0 ou superior |
| Áudio | Dispositivo de som para ouvir o jogo (há opção para desativar) |
| Rede | Acesso ao Maven Central (e a um banco de arte, se for usar arte pronta) |

## 5. O que deve ser feito

### 5.1 Estrutura do projeto

```text
fase-04-interface-grafica/
  settings.gradle, build.gradle, gradlew, gradle/wrapper/, .gitignore
  core/
    assets/imagens, assets/sons
    src/main/java/br/edu/unifor/missaomarte/{domain, application, infrastructure, gdx}
    src/test/java/...
  lwjgl3/
    build.gradle
    src/main/java/br/edu/unifor/missaomarte/lwjgl3/
  docs/
  README.md
```

Imagens e sons ficam em `core/assets`, a convenção do libGDX. No módulo `lwjgl3` essa pasta deve entrar no classpath por meio de `sourceSets.main.resources.srcDirs`.

### 5.2 Imagens e sons (assets)

Você pode **criar** (por exemplo, no Piskel), **gerar por código** ou **usar arte pronta** de um banco gratuito (Kenney, itch.io, OpenGameArt). Se usar arte de terceiros, respeite a licença e registre autor, origem e licença em um arquivo de créditos.

O jogo precisa, no mínimo, de:

| Elemento | Quadros |
|---|---|
| Fundo (800 x 600) e terreno da célula | 1 |
| Nave | 1 |
| Propulsor da nave | 2 ou mais, em laço |
| Base | 2 ou mais, em laço |
| Asteroide | 1 |
| Inimigo | 2 ou mais, em laço |
| Os três tipos de passageiro (professor, engenheiro, astronauta) | 1 cada |
| Imagem genérica para símbolo desconhecido | 1 |
| Explosão (colisão) | 4 ou mais, uma execução |
| Brilho (embarque e entrega) | 3 ou mais, uma execução |
| Ícones do HUD: vida cheia, vida vazia, vaga ocupada e vaga livre | 1 cada |

Sons mínimos: mover, selecionar (menu), embarcar, entregar, colisão, vitória, derrota e uma **música de fundo** curta para tocar em laço. Use um formato que o backend desktop toque (WAV, OGG ou MP3).

Requisitos:

1. Imagens com vários quadros são **faixas horizontais** de quadros do mesmo tamanho.
2. Crie um **catálogo** (por exemplo, enums `Imagem` e `Som`) com o caminho, o tamanho do quadro e a quantidade de quadros de cada imagem. O catálogo **não pode** depender do libGDX.
3. Crie uma classe (sugestão: `Recursos`) que carregue tudo com o `AssetManager` uma única vez, entregue `Texture`, `TextureRegion`, `Animation`, `Sound` e `Music`, e libere tudo em `dispose()`. O `AssetManager` **não pode ser `static`**.
4. O `Game` cria os recursos em `create()` e os libera em `dispose()`. As telas não criam nem liberam texturas.

### 5.3 Camadas e dependências

- `domain`, `application` e `infrastructure` **não podem importar** nada de `com.badlogic`. Só o pacote `gdx` e o módulo `lwjgl3` conhecem o libGDX.
- Nenhuma classe de domínio pode guardar `Texture`, `Sprite` ou som. A imagem de cada objeto é escolhida **na camada `gdx`**, a partir do símbolo que o objeto já informa (`ObjetoRenderizavel#getSimbolo()`). Símbolo desconhecido usa a imagem genérica. Não use `instanceof` para escolher o sprite.

### 5.4 Eventos do turno (mudança aditiva em `application`)

Hoje `ResultadoTurno` traz só o estado e mensagens de texto. Para a tela disparar sons e efeitos sem interpretar texto, faça `ResultadoTurno` informar também uma **lista de eventos** do turno:

| Evento | Quando ocorre |
|---|---|
| Movimento | A nave se move |
| Embarque | Um passageiro embarca |
| Entrega | Passageiros desembarcam na base |
| Colisão | A nave colide com um perigo |
| Vitória | A partida termina em vitória |
| Derrota | A partida termina em derrota |

Regras:

1. A mudança deve ser **aditiva**: o construtor antigo de `ResultadoTurno` continua válido e os testes herdados continuam passando **sem alteração**.
2. `GameSession` apenas **registra** o que a regra já decidia; nenhuma regra pode mudar. Comando inválido e abortar a partida não geram eventos.
3. Teste cada evento (sugestão: uma classe `GameSessionEventosTest`). Atenção: uma missão **sem passageiros já nasce vencida**; nos testes, inclua um passageiro distante para manter a partida em andamento.
4. A duração da partida deve **parar de correr** quando a partida termina, para que o resumo do ranking mostre o mesmo tempo que foi gravado. Teste esse comportamento.

### 5.5 Sprites e desenho do mundo

1. O mapa 11 x 11 é desenhado com imagens: terreno em todas as células e, por cima, base, asteroides, passageiros, inimigos e nave (a nave sempre por último).
2. A nave gira conforme a última direção (cima, direita, baixo e esquerda) e mostra o propulsor animado atrás dela.
3. Base e inimigos usam animações em laço (`Animation` com `getKeyFrame(tempo, true)`).
4. Crie uma classe só para desenhar o mundo (sugestão: `MundoRenderer`). `GameScreen` não deve conter o desenho do mapa.

### 5.6 Movimentação suave

A regra do jogo é instantânea: `GameSession` muda a posição inteira da nave de uma vez. O **desenho** deve deslizar entre as células.

1. Crie uma posição visual (em unidades de célula, fracionária) que **persegue** a posição lógica a uma velocidade fixa (sugestão: 12 células por segundo), andando em linha reta e sem ultrapassar o alvo.
2. Uma classe deve guardar a posição visual de cada objeto **comparando por identidade**, sem alterar o domínio, e esquecer os objetos que sumiram do mapa (embarcados ou destruídos).
3. Objetos novos começam na posição lógica, sem deslizar.
4. A coordenada fracionária de célula deve ser convertida em tela pela classe de geometria que você já tem (o eixo Y continua invertido).
5. A lógica **nunca espera** o desenho: duas teclas rápidas processam dois turnos imediatamente.

### 5.7 Entrada com repetição

1. Segurar uma tecla de movimento repete a ação: espera um atraso inicial (sugestão: 0,28 s) e depois repete a cada intervalo (sugestão: 0,14 s).
2. Cada repetição é **um turno completo** (custa um ponto), como se o jogador apertasse a tecla de novo.
3. A repetição para ao soltar a tecla, ao terminar a partida e ao sair da tela.
4. A contagem do tempo de repetição deve ficar em uma classe sem dependência do libGDX (testável).
5. Teclas sem ação continuam sendo ignoradas, sem consumir turno.

### 5.8 Colisões: resposta visual e sonora

A colisão **continua sendo uma regra do domínio** (mesma célula). Não use `Intersector`, `Circle` nem `Rectangle` e não crie uma segunda regra de colisão na tela. A fase adiciona apenas a **resposta** ao evento de colisão:

- animação de explosão na célula da nave;
- tremor da tela que diminui até zero (classe sem dependência do libGDX);
- flash vermelho sobre o mapa;
- nave piscando por cerca de 1 segundo;
- texto flutuante com a variação de pontos (por exemplo, "-6");
- som de colisão.

### 5.9 Animações e efeitos

| Quando | Efeito |
|---|---|
| Embarque | Brilho na nave, som e texto "+pontos" |
| Entrega | Brilho na base, som e texto "Entregue!" |
| Colisão | Ver seção 5.8 |
| Vitória e derrota | Som, música pausada e caixa com a mensagem final |

1. Efeitos de uma execução (explosão, brilho) guardam o próprio tempo, distribuem os quadros igualmente pela duração e **desaparecem sozinhos**. Essa lógica fica em classe sem dependência do libGDX.
2. Textos flutuantes sobem, ficam transparentes e somem (classe sem dependência do libGDX). Atenção: no jogo o Y cresce para baixo, então "subir" na tela diminui o Y da célula.
3. O texto de variação de pontos só aparece quando a variação em módulo é maior que 1, para não poluir a tela com o "-1" de cada passo.

### 5.10 Sons

1. Um som para cada evento da tabela da seção 5.9 e para o movimento, além do som de seleção no menu.
2. A música de fundo toca em laço, **pausa** em vitória e derrota (para ouvir o som do resultado) e **retoma** ao voltar ao menu.
3. A tecla `M` liga ou desliga o som em todas as telas, **exceto na de nova partida**, porque a letra M faz parte do nome digitado. Instale o atalho de forma central, e não em cada tela.
4. Crie uma classe só para tocar sons e controlar o mudo. Ela não carrega nem libera arquivos.
5. Ofereça uma forma de **desativar o áudio** para máquinas sem dispositivo de som (sugestão: uma propriedade de sistema lida pelo launcher).

### 5.11 HUD

O HUD é desenhado **depois** do mundo e apenas **lê** a `GameSession`. Deve conter:

| Região | Conteúdo |
|---|---|
| Barra superior | Ícones de vidas (cheios e vazios), pontuação, tempo e estado do som |
| Painel lateral | Piloto, dificuldade, vagas da nave com ícones (ocupadas e livres), barra de progresso das entregas, passageiros restantes no mapa, movimentos e as mensagens do último turno |
| Rodapé | Controles do jogo |

Para desenhar painéis translúcidos com o `ShapeRenderer`, habilite a mistura (*blending*) antes e desabilite depois.

Os textos da interface devem ser **acentuados** (a fonte padrão do libGDX cobre acentos). As mensagens que vêm da `GameSession` podem continuar como estão.

### 5.12 Telas

Menu, nova partida e ranking também usam o fundo e os sons: nave animada no menu, som ao selecionar e música tocando. O fluxo e as teclas continuam os da Fase 3.

### 5.13 Execução reprodutível

Permita fixar a semente do gerador aleatório por uma propriedade de sistema (sugestão: `-Dmissaomarte.semente=N`), lida pelo launcher. Com a mesma semente, o cenário e os movimentos dos inimigos se repetem, o que permite validar efeitos específicos (colisão, entrega, vitória, derrota).

### 5.14 Testes automatizados

Mantenha os 39 testes da Fase 3 passando e acrescente testes para o que criou:

| O que testar | O que verificar (no mínimo) |
|---|---|
| Eventos do turno | Cada evento, ausência de eventos em comando inválido e ao abortar, e duração parada após o fim |
| Arquivos de assets | Cada imagem do catálogo existe e tem largura (quadro x quantidade) e altura corretas; cada som existe e é um arquivo de áudio válido; existe uma única música |
| Símbolo e imagem | Cada símbolo do domínio e a imagem genérica |
| Posição visual | Anda até o alvo sem ultrapassar, em linha reta na diagonal, desliza ao mudar de célula, objeto novo não desliza e objetos que somem são esquecidos |
| Efeitos e textos flutuantes | Quadros de um efeito, remoção ao terminar, subida e transparência do texto |
| Tremor | Começa forte, decai e termina em zero |
| Repetição de tecla | Atraso, intervalo, no máximo uma repetição por chamada e reinício após parar |
| Geometria | Coordenadas fracionárias de célula |

Os testes **não podem** abrir janela, usar OpenGL nem tocar áudio.

### 5.15 Comandos que devem funcionar

```powershell
.\gradlew --version                                              # Gradle 8.7 e JVM 17
.\gradlew clean build                                            # compila e testa
.\gradlew :lwjgl3:run --console=plain                            # abre a janela
.\gradlew :lwjgl3:run -Dmissaomarte.semente=4 --console=plain    # cenário reprodutível
```

## 6. Pontos de atenção

Erros comuns que costumam consumir tempo:

- `AssetManager.load()` só **enfileira**; chame `finishLoading()` (ou `update()` em um laço) antes de usar um asset.
- Não crie `Texture`, `BitmapFont` ou `ShapeRenderer` dentro de `render()` e não use o `AssetManager` como campo estático.
- Não libere manualmente um recurso carregado pelo `AssetManager`; use o `dispose()` dele.
- Mantenha a **mesma projeção** (`camera.combined`) no `SpriteBatch` e no `ShapeRenderer`, e não deixe os dois abertos ao mesmo tempo.
- Formas translúcidas no `ShapeRenderer` só aparecem se a mistura (*blending*) estiver habilitada.
- Quadros de animação ficam em uma só imagem; as dimensões do catálogo precisam coincidir com as do arquivo.
- Sprites não quadrados serão esticados na célula quadrada; pixel art pequena fica melhor com filtro `Nearest`.
- O eixo Y do jogo cresce para baixo, e o do libGDX para cima; o desenho de uma coordenada fracionária também usa a conversão da geometria.
- Quando o jogador troca de tela dentro de um evento de teclado, o processador de entrada antigo precisa ser removido em `hide()`.
- Com a mesma semente, o resultado só se repete se a **ordem de uso** do `Random` for a mesma.

## 7. Preservar o comportamento

Prove que as regras não mudaram:

- Os arquivos de `domain`, `infrastructure` e o restante de `application` devem ser **idênticos** aos da Fase 3 (compare por hash). Liste os que diferem ou são novos e justifique cada um (esperados: o enum de eventos, `ResultadoTurno` e `GameSession`).
- Pontuação, dificuldade, embarque, entrega, colisão e vitória se comportam como antes.
- Fases anteriores e `exercicio10/src` permanecem intactos.

## 8. Documentação obrigatória

Entregue um arquivo `docs/tutorial.md` (ou nome equivalente) com as seções abaixo.

### 8.1 Estado de origem e destino

- Como era o jogo (retângulos e texto) e como ficou.
- Tabela "item da Fase 3 -> equivalente na Fase 4" (por exemplo, retângulo -> sprite; mensagem em texto -> evento; painel de texto -> HUD).
- Diagrama da arquitetura **antes** e **depois** (Mermaid ou imagem), mostrando a regra de dependência.

### 8.2 Conceitos aplicados

Explique, com exemplos do seu próprio código:

- A cadeia PNG, `Texture`, `TextureRegion` e animação por quadros, e o que é uma *sprite sheet*.
- O que o `AssetManager` faz, por que `load()` só enfileira e por que ele não deve ser estático.
- A diferença entre `Sound` e `Music` e por que ambos precisam de `dispose()`.
- Por que a posição visual é separada da lógica e como o deslizamento funciona.
- O que é um HUD e por que é desenhado depois do mundo.
- Por que a colisão deste jogo não usa `Intersector` (compare com a abordagem do referencial).
- Por que o `ResultadoTurno` passou a trazer eventos e por que isso evita colocar regra na tela.

### 8.3 Decisões de projeto

- Origem dos assets (criados, gerados ou de terceiros) e a licença de cada um.
- Por que as texturas ficam na camada `gdx` e não nas entidades.
- Por que a escolha do sprite usa o símbolo e não `instanceof`.
- Por que o atalho de silenciar é central e a tela de nome é exceção.
- Padrões usados e onde (por exemplo, Facade, Domain Event, Template Method e injeção de dependência), distinguindo o que de fato segue o padrão do que apenas se parece com ele.

### 8.4 Passo a passo

Os comandos e as decisões na ordem em que foram feitos, incluindo problemas reais encontrados (por exemplo, sprite esticado, janela em branco, som que não toca, efeito na célula errada, eixo Y invertido) e como cada um foi resolvido.

### 8.5 Testes e evidências

O que cada teste cobre, a saída de `gradlew clean build` com a quantidade de testes, **capturas de tela** (menu, mapa com sprites, colisão, vitória e derrota), a sequência de eventos de uma execução com semente fixa e a comparação por hash dos fontes.

### 8.6 Limitações e próximos passos

Liste pelo menos três pontos que ainda podem melhorar (por exemplo, qualidade da arte e dos sons, fonte serrilhada, mensagens da aplicação sem acento, ausência de tela de carregamento) e relacione cada um com a Fase 5 ou 6 (servidor de ranking e Android).

## 9. Roteiro mínimo de validação

Execute e registre o resultado de cada passo:

1. `gradlew --version` mostra Gradle 8.7 e JVM 17.
2. `gradlew clean build` termina com `BUILD SUCCESSFUL` (informe a quantidade de testes).
3. `gradlew :lwjgl3:run` abre a janela; o log mostra a quantidade de imagens e sons carregados e nenhuma exceção.
4. Menu com fundo e nave animada; música tocando; `M` silencia e reativa.
5. Nova partida: digitar `m` no nome insere a letra e não silencia.
6. Partida: o mapa usa sprites; a nave gira e desliza entre as células.
7. Segurar `D` faz a nave andar várias células, cada uma custando um ponto.
8. Embarque: brilho, som, "+pontos" e ícone da vaga preenchido.
9. Entrega: "Entregue!", brilho na base, barra de entregas avança e som.
10. Colisão: explosão, tremor, flash, nave piscando, "-pontos", vida a menos e som.
11. Vitória (som, caixa e ranking com resumo) e derrota (som, caixa e volta ao menu com a música retomando).
12. O tempo do resumo do ranking é igual ao tempo gravado no ranking.
13. Com `-Dmissaomarte.semente=N`, o mesmo cenário se repete duas vezes seguidas.
14. Com o áudio desativado, o jogo roda sem som e sem exceção.
15. Redimensionar e minimizar a janela mantém a proporção.
16. Verificar por busca que nenhum arquivo fora de `gdx` e `lwjgl3` importa `com.badlogic`.
17. Verificar por hash e por `git status` que as fases anteriores e `exercicio10/src` não mudaram.

## 10. Entregáveis

1. Pasta `fase-04-interface-grafica` com o projeto multi-módulo, o wrapper, o `.gitignore` e `core/assets`.
2. Testes JUnit 5 que passam com `gradlew clean build`.
3. `README.md` da fase com instruções de execução, controles e opções de execução.
4. Documento `tutorial.md` com todas as seções da etapa 8, incluindo o arquivo de créditos se usou arte de terceiros.
5. Evidências do roteiro da etapa 9, com capturas de tela.

## 11. Critérios de avaliação

| Critério | Peso |
|---|---|
| Assets e `AssetManager` (catálogo, carga única, liberação, sem estático, licenças registradas) | 15% |
| Sprites, animações e efeitos (símbolo para imagem, rotação, laços, uma execução, textos flutuantes) | 15% |
| Movimentação suave e entrada com repetição (posição visual separada da lógica, repetição ao segurar) | 10% |
| Eventos do turno e reação (sons, efeitos, colisão com resposta visual e sonora, sem regra na tela) | 15% |
| HUD completo e telas com fundo e sons (somente leitura, acentuado, blending) | 10% |
| Preservação das regras e das camadas (hash de `domain`, `application` aditivo, nenhum `com.badlogic` fora de `gdx`/`lwjgl3`) | 15% |
| Testes JUnit 5 relevantes, isolados de OpenGL e áudio, e passando | 10% |
| Documentação, evidências (capturas, semente fixa) e limitações | 10% |

## 12. Observações

- Alterar as regras do jogo para "facilitar" a interface reduz a nota. Se achar que algo precisa mudar, registre como limitação.
- Colocar regra de negócio na tela (por exemplo, decidir a colisão por geometria) reduz a nota da seção correspondente.
- Descrever um comportamento, uma captura ou um teste que você não executou reduz a nota. O documento deve refletir o projeto entregue.
- Se não for possível validar o áudio ou a janela no seu computador (por exemplo, sem dispositivo de som), declare isso; o que não foi verificado deve constar no documento.
- Registrar um problema real encontrado e como foi resolvido vale mais do que afirmar que tudo funcionou de primeira.
- É permitido usar ferramentas de apoio e arte de terceiros, desde que a licença seja respeitada e as fontes sejam citadas. Cada classe, arquivo e comando citado precisa existir no seu projeto.
- Os nomes de classe sugeridos podem mudar, desde que as responsabilidades continuem separadas e justificadas.

## 13. Desafio opcional

1. Substituir a fonte padrão por uma fonte própria (por exemplo, gerada com o Hiero ou carregada com `gdx-freetype`) e descrever o ganho de qualidade.
2. Acrescentar uma tela de carregamento usando `AssetManager.update()` com a barra de progresso.
3. Criar uma tela de pausa acionada por `P`, que volte à partida sem perder o estado.
4. Fazer o propulsor ficar mais forte enquanto a tecla é mantida.
5. Acrescentar faíscas na entrega usando o mesmo mecanismo dos efeitos de uma execução.
6. Escrever uma sequência de teclas que, com uma semente fixa, produza duas colisões e termine em vitória, e usá-la em um roteiro de validação.
7. Reimplementar a resposta visual da colisão com `Intersector` sobre as posições visuais e explicar por que isso cria uma segunda regra de colisão, divergente da do domínio.
8. Acentuar também as mensagens da aplicação sem acoplar `application` ao libGDX (dica: eventos com dados em vez de texto pronto).
