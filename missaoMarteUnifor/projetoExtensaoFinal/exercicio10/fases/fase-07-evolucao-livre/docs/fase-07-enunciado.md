# Enunciado - Evolução livre do Missão Marte

## 1. Contexto

Você recebeu o **Missão Marte** da Fase 7 (referência): um jogo libGDX com `core`, `lwjgl3` e `android`, um servidor Spring Boot com ranking global e dois modos de jogo, **clássico** (por turnos) e **dinâmico** (tempo real, com tiro e inimigo perseguidor). Há 208 testes no cliente e 36 no servidor, todos passando. A tela da partida se adapta a paisagem, retrato e vários tamanhos de tela.

Sua tarefa é **escolher e implementar uma melhoria própria**, respeitando a arquitetura em camadas e **documentando o que foi feito e por quê**. Esta fase mede principalmente a sua capacidade de **estender um sistema existente sem quebrá-lo**.

## 2. Objetivos de aprendizagem

- Ler um sistema existente e identificar os pontos de extensão (e os acoplamentos que atrapalham).
- Decidir em qual camada cada parte da melhoria deve ficar.
- Aplicar padrões de projeto (Strategy, Factory, Command, Observer, Port and Adapter) quando eles resolvem um problema real, e distinguir isso de "usar o padrão pelo padrão".
- Escrever testes que protegem o comportamento antigo e provam o novo, usando tempo simulado e sementes fixas.
- Medir o impacto de uma mudança: o que mudou, o que poderia ter quebrado e o que ficou sem verificação.

## 3. Entrada

Use como ponto de partida a pasta `fase-07-evolucao-livre`.

Regras:

- **Não altere** `fase-07-evolucao-livre`, as fases anteriores nem `exercicio10/src`. Trabalhe em uma pasta nova (por exemplo, `fase-07-evolucao-livre-seunome`, copiando só o necessário, sem `build`, `.gradle`, `local.properties` nem `ranking.json`).
- O **modo clássico** deve continuar como está, **ou** você deve dizer explicitamente o que mudou nele e por quê.
- Os **testes existentes** devem continuar passando. Se um teste precisar mudar, explique o motivo no documento.
- O **servidor** só pode mudar se a melhoria exigir; nesse caso, atualize os testes dele e documente a mudança da API.
- Cada fase é um projeto independente: a sua pasta deve compilar e rodar sozinha.

## 4. Ambiente

| Item | Requisito |
|---|---|
| JDK | 17 (`JAVA_HOME` **e** `PATH`) |
| Cliente | Gradle com wrapper 8.7, libGDX 1.12.1, AGP 8.6.x |
| Servidor | Spring Boot 4.1.2, Gradle 9.8.2 (como na Fase 6) |
| Android | Opcional: SDK 35 e `local.properties` para gerar o APK. Sem o SDK o desktop continua compilando |

## 5. O que deve ser feito

### 5.1 Escolher a melhoria

Escolha **uma** das linhas abaixo (ou proponha outra de tamanho parecido e combine antes). Cada uma tem o nível de dificuldade indicado.

| Melhoria | Ideia | Nível |
|---|---|---|
| **Personalização da nave** | Escolher a cor (ou o modelo) na Nova partida e guardá-la nas Preferences | Básico |
| **Nova arma** | Por exemplo, tiro em área, tiro que atravessa, escudo ou bomba | Básico a médio |
| **Novo inimigo** | Um comportamento novo (patrulha, fuga, teleporte) como nova implementação de `MovimentoDoInimigo` | Médio |
| **Aumento de dificuldade** | A velocidade dos inimigos cresce ao longo da partida (uma dificuldade **nova** exige mudar o servidor, que valida o enum) | Médio |
| **Campanha de fases** | Sequência de mapas com dificuldade crescente e pontuação acumulada | Médio a avançado |
| **Chefão** | Inimigo com várias vidas e padrão de ataque, exige uma arma | Avançado |
| **Tela responsiva** | Adaptar mais telas (entrada, servidor, ranking) ao retrato | Médio |

Na entrega, **a melhoria precisa estar funcionando e testada**. Uma melhoria menor bem feita vale mais que uma maior pela metade.

### 5.2 Regras de arquitetura

1. `domain`, `application` e `infrastructure` **não importam** `com.badlogic`. Só `gdx`, `lwjgl3` e `android` o fazem (verifique por busca).
2. Regra de jogo fica em `domain` ou `application`, **não** em tela. A tela traduz entrada em ação e resultado em som, efeito e texto.
3. O tempo é **parâmetro** nas regras: nada em `domain` ou `application` lê o relógio do sistema nem usa `Thread.sleep`.
4. Use `Acao`, `EventoJogo` e os pontos de extensão que já existem antes de criar mecanismos novos.
5. Escolhas aleatórias recebem um `Random` (para testes com semente).
6. Novos assets saem de um programa em `tools` (como `GeradorDeNovosAssets`), **sem alterar** os assets existentes. Não copie imagens ou sons de terceiros.
7. Código Java sem acentos nos identificadores; textos mostrados ao jogador e documentos com acentos.

### 5.3 Testes

Acrescente testes JUnit 5 para a sua melhoria. No mínimo:

| O que testar | O que verificar |
|---|---|
| A regra nova no `domain` ou `application` | Casos normais, bordas (limite do mapa, munição zerada, lista vazia) e o que **não** deve acontecer |
| Integração com a sessão | Eventos emitidos, pontuação, estado da partida |
| Compatibilidade | Que o modo clássico (ou o comportamento antigo) não mudou, por exemplo comparando cenários gerados pela mesma semente |
| Apresentação | O que for lógica pura (layout, conversões, escolhas) em classes sem libGDX |

Os testes **não podem** abrir janela, usar OpenGL, rede, o SDK do Android nem esperar o relógio de verdade.

### 5.4 Comandos que devem funcionar

```powershell
# servidor
cd servidor
.\gradlew test

# cliente
cd cliente
.\gradlew clean build
.\gradlew :lwjgl3:run --console=plain
.\gradlew :lwjgl3:run -Dmissaomarte.toque=true -Dmissaomarte.janela=1200x540 --console=plain
.\gradlew :android:assembleDebug        # se tiver o Android SDK
```

## 6. Pontos de atenção

- **Passo fixo.** Não faça o jogo "andar um pouco por quadro": use o `Temporizador` e passos em células. Limite o tempo por quadro.
- **Símbolos.** O desenho vem do `getSimbolo()`. Antes de usar uma letra nova, procure se ela já existe (`P` é o passageiro professor; o perseguidor usa `C`).
- **Sorteios.** A ordem dos sorteios do `Random` define a semente. Uma melhoria que acrescenta sorteios **no meio** da criação do mapa muda todos os cenários antigos. Acrescente depois, e só no modo novo.
- **`GameSession` já é grande.** Se a sua melhoria cabe melhor em outra classe, crie outra classe.
- **Quadro mais rápido que o toque.** Um toque pode começar e acabar no mesmo quadro; garanta que a ação valha mesmo assim.
- **Ciclo de vida.** O aplicativo vai para segundo plano: o tempo real precisa pausar.
- **Alvos de toque.** Botões de pelo menos 48 dp. Teste vários tamanhos de janela (`-Dmissaomarte.janela=LxA`).
- **Ranking.** O servidor não sabe o modo da partida: pontuações de modos diferentes se misturam no ranking global. Se a sua melhoria agrava isso, registre.
- **Atalhos de teclado.** Em telas com texto digitado, letras como `M` fazem parte do texto.
- Não coloque segredos nem senhas no repositório.

## 7. Preservar o comportamento

- Os testes existentes passam sem edição (ou você explica cada edição).
- O modo clássico, a partida de semente 4 no modo clássico (`wwdwdwewedsdseaasasasasasasedwdwdw`, dificuldade fácil) e o layout 800x600 do desktop sem toque se comportam como antes, **ou** a mudança está documentada.
- Liste os arquivos de `domain`, `application` e `infrastructure` que você alterou ou criou e justifique cada um.
- Fases anteriores e `exercicio10/src` permanecem intactos (`git status`).

## 8. Documentação obrigatória

Entregue `docs/tutorial.md` (ou nome equivalente) e um `README.md` na sua pasta, com as seções abaixo.

### 8.1 A melhoria

- O que o jogador passa a poder fazer e como (teclas, toque).
- Por que você escolheu esta melhoria.

### 8.2 Diagnóstico e decisões de arquitetura

- Que pontos de extensão da base você usou e quais **não** serviam (e como contornou).
- Em que camada cada parte ficou e por quê, com um diagrama (Mermaid ou imagem).
- Padrões usados e onde, **distinguindo** o que de fato segue o padrão do que apenas se parece com ele.

### 8.3 Passo a passo

Os comandos e as decisões na ordem em que foram feitos, incluindo **problemas reais** encontrados (teste que falhou, comportamento inesperado, decisão revertida) e como foram resolvidos.

### 8.4 Testes e evidências

O que cada teste cobre, a saída de `gradlew test` e `gradlew clean build` com a quantidade de testes, **capturas de tela** da melhoria funcionando (em pelo menos dois tamanhos de janela, se mexer na tela), a comparação do modo clássico com a base e a verificação de que `com.badlogic` continua restrito.

### 8.5 O que não foi verificado

Liste com honestidade o que você **não** testou (aparelho real, áudio, desempenho, controle Bluetooth, jogo do início ao fim, etc.).

### 8.6 Limitações e próximos passos

Pelo menos três pontos que ainda podem melhorar (balanceamento, outras telas, servidor, acessibilidade, desempenho...).

## 9. Roteiro mínimo de validação

Execute e registre o resultado de cada passo:

1. `gradlew --version` mostra a versão e o JVM esperados.
2. `gradlew test` (servidor) e `gradlew clean build` (cliente) terminam com `BUILD SUCCESSFUL` (informe a quantidade de testes de cada um).
3. O modo clássico: jogue a partida da semente 4 e confira a vitória com 4/4 passageiros e 30 movimentos (ou documente o que mudou).
4. O jogo abre em 800x600 sem toque e mostra o mesmo layout da base.
5. Com `-Dmissaomarte.toque=true`, o jogo abre e a melhoria funciona com o mouse no lugar do dedo.
6. A melhoria funciona em pelo menos **duas proporções de janela** (por exemplo, `1200x540` e `540x1000`), sem elementos fora da tela nem sobrepostos.
7. Casos limite da melhoria: o que acontece com munição zerada, com o jogo pausado, no limite do mapa, ao terminar a partida, etc.
8. `gradlew :android:assembleDebug` gera o APK, **ou** você registra que não tinha o SDK.
9. Verifique por busca que nada fora de `gdx`, `lwjgl3` e `android` importa `com.badlogic`.
10. Verifique por `git status` que as fases anteriores e `exercicio10/src` não mudaram.
11. Se tiver aparelho: instale o APK e jogue. Registre o resultado; sem aparelho, registre "não verificado".

## 10. Entregáveis

1. Sua pasta de fase, com `cliente/` e `servidor/` (cada um com seu wrapper e `.gitignore`).
2. Testes JUnit 5 que passam nos dois subprojetos.
3. `README.md` com instruções de execução, controles e limitações.
4. `docs/tutorial.md` com todas as seções da etapa 8.
5. Evidências do roteiro da etapa 9, com capturas de tela.

## 11. Critérios de avaliação

| Critério | Peso |
|---|---|
| A melhoria funciona e é utilizável (teclado e toque, quando fizer sentido) | 20% |
| Arquitetura: camadas respeitadas, ponto de extensão bem escolhido, nada de regra de jogo em tela, `com.badlogic` restrito | 20% |
| Testes: relevantes, isolados do relógio, da janela e da rede, cobrindo bordas e compatibilidade | 20% |
| Preservação do comportamento antigo (testes antigos, modo clássico, semente 4, layout) | 15% |
| Documentação: diagnóstico, decisões, problemas reais e evidências | 15% |
| Honestidade: o que não foi verificado e as limitações | 10% |
