# Fase 07 - Planejamento

Planejamento aprovado antes da implementação, com as divergências e o resultado da verificação ao final.

## Pedido e decisões aprovadas

A Fase 7 do `PROMPT_MESTRE` é "evolução livre": os alunos implementam melhorias respeitando a arquitetura. Esta fase entrega (1) uma **evolução de referência**, feita para mostrar como estender o jogo sem quebrar as camadas, e (2) o **enunciado e o tutorial** para o aluno.

Decisões do autor na aprovação:

1. **Modo dinâmico (tempo real) como padrão**, mantendo o **modo clássico** (por turnos) igual ao da Fase 6.
2. **Nave anda enquanto a direção está pressionada**; inimigos e relógio correm sozinhos.
3. **Embarque automático** no modo dinâmico.
4. **Melhorias:** tiro, inimigo perseguidor e **layout responsivo** para usabilidade no celular. A personalização da nave, proposta antes, ficou de fora por escolha do autor.
5. **Servidor idêntico** ao da Fase 6 (sem registrar o modo da partida).
6. Entregáveis: planejamento, tutorial e enunciado.

## Diagnóstico do estado de origem (Fase 6)

- `GameSession.processarAcao` fazia tudo por ação: a nave andava, os inimigos andavam, a colisão era verificada e a pontuação caía 1 ponto. O jogo só "andava" quando havia tecla.
- `Inimigo` tinha o movimento aleatório dentro da própria classe; `MissaoFactory` escolhia tudo com `if`.
- O layout da partida era fixo (800x600): célula de 48, painel em x=570; só os botões virtuais acompanhavam a largura. O manifesto travava a orientação em paisagem. Os textos não cresciam com a densidade da tela e os botões não tinham tamanho mínimo em dp.
- Já estavam prontos para estender: `Acao` (teclado, toque e controle chegam por ela), `EventoJogo` (som, efeito e texto reagem a eventos), `MapaGeometria` (tamanho da célula e origem por parâmetro) e `Recursos`.

## O que foi feito

### Domínio e aplicação

| Mudança | Detalhe |
| --- | --- |
| Strategy no movimento dos inimigos | `MovimentoDoInimigo` com `MovimentoAleatorio` (mesmos sorteios de antes) e `MovimentoPerseguidor` (um passo em direção à nave, sem sorteio) |
| Tiro | `Nave` com munição; `Missao.disparar` destrói o primeiro perigo da linha e devolve `ResultadoDoTiro`; `Acao.ATIRAR`; eventos `TIRO` e `PERIGO_DESTRUIDO` |
| Passo fixo | `Temporizador` acumula o tempo e devolve quantos intervalos terminaram; `ConfiguracaoDeTempoReal` e `RegrasDeTempoReal` guardam as velocidades por dificuldade |
| Modo dinâmico | `GameSession` ganhou `ModoDeJogo`, `avancar(delta)`, `pressionar`/`soltar`, `atirar`, pausa e contagem inicial. O tempo entra como parâmetro: nada usa relógio do sistema nas regras |
| Fábrica | `MissaoFactory.criarMissao(..., modo)` acrescenta munição e perseguidores **depois** dos sorteios existentes, para o modo clássico fazer os mesmos sorteios de antes |

### Apresentação (`gdx`)

- `LayoutDaPartida` (sem libGDX) calcula barras, mapa, painel e controles para paisagem e retrato; no desktop sem toque devolve o layout fixo da Fase 6.
- `ControleDeToque` monta os botões na área de controles, com tamanho mínimo de toque; `EscalaDeTela` converte 48 dp em unidades do mundo e calcula o fator do texto.
- `HudRenderer` passou a ser guiado pelo layout (painel lateral ou faixa); `GameScreen` ganhou o loop dinâmico, pausa, contagem, raio do tiro e botões por estado; `NovaPartidaScreen` escolhe o modo; `MenuScreen` ajusta o espaçamento das linhas ao toque; `TelaBase` passou a escolher o botão de centro mais próximo do dedo.
- Assets novos pelo `GeradorDeNovosAssets`, sem alterar os existentes: `perseguidor.png` (o inimigo com as cores giradas) e `tiro.wav`.
- Manifesto: `fullSensor`. Launcher desktop: `-Dmissaomarte.janela=LxA`.

## Divergências em relação ao plano aprovado

| Plano | Feito | Motivo |
| --- | --- | --- |
| Classe `PartidaEmTempoReal` e extração das regras comuns para `RegrasDoTurno` | O modo dinâmico ficou dentro de `GameSession`, com `Temporizador` como apoio | Menos risco para o modo clássico e para `PartidaConcluida`, `RankingService` e telas, que dependem de `GameSession`. Custo: a classe cresceu (cerca de 450 linhas). A extração fica como melhoria |
| Viewport `ExtendViewport` com tamanho mínimo diferente em retrato | `ExtendViewport(800, 600)` nos dois formatos | Mantém as telas de menu e formulários numa área conhecida; só a partida usa o mundo inteiro |
| Perseguidor com símbolo `P` | Símbolo `C` | O `P` já é o passageiro professor em `SimboloParaImagem` |
| Todas as telas responsivas | Partida e menu adaptados; demais telas em área fixa centralizada | Escopo e risco. Registrado como limitação |
| Nada | `Tab` alterna o modo; um toque mais curto que um quadro ainda vale um passo | Descobertos nos testes: `SendKeys` não aciona as setas no GLFW, e um toque rápido podia ser perdido |

## Riscos e como foram tratados

| Risco | Tratamento |
| --- | --- |
| Quebrar o modo clássico | 141 testes anteriores continuaram passando; partida de semente 4 refeita no modo clássico com o mesmo resultado (vitória, 4/4, 30 movimentos) |
| Colisão em tempo real (nave e inimigo trocando de célula) | A colisão é verificada depois de **cada** passo (da nave e dos inimigos), então uma troca de células não passa despercebida. Testes cobrem a colisão no passo do inimigo (perseguidor) e no passo da nave (asteroide); a troca de células em si não tem teste específico |
| Quadro lento virar salto | Limite de 0,1 s por quadro (`getDeltaMaximo`) e passo fixo por `Temporizador` (teste) |
| Perdas injustas ao voltar do segundo plano | `pause()` da tela pausa a partida; a pausa também tem botão e tecla |
| Balanceamento sem aparelho | Velocidades e custos em `RegrasDeTempoReal`, fáceis de ajustar; **não validado em jogo real** |
| Ranking misturado | Registrado como limitação (servidor sem mudanças) |
| Layout quebrar em alguma proporção | Testes de layout para 11 formatos de mundo e 3 conjuntos de margens: nada fora do mundo, nenhuma sobreposição, botões ≥ alvo mínimo |

## Critérios de conclusão e resultado

| Critério | Resultado |
| --- | --- |
| Testes anteriores passam | 141 do cliente (um deles, `ControleDeToqueTest`, reescrito para o novo layout) e 36 do servidor |
| Testes novos | Cliente: 208 no total. Cobrem passo fixo, movimento por tempo, contagem, pausa, embarque automático, pontuação por segundo, colisão, tiro, perseguidor, fábrica, velocidades, layout em vários formatos, controles e escala de tela |
| Modo clássico igual ao da Fase 6 | Layout fixo conferido por teste; partida de semente 4 refeita no desktop |
| Servidor idêntico | 51 arquivos comparados por hash, 0 diferenças |
| `domain` | Mudou de propósito, e só por extensão: 4 arquivos novos e 3 alterados (`Inimigo`, `Missao`, `Nave`) |
| Camadas | Nenhum `com.badlogic` fora de `gdx`, `lwjgl3` e `android` |
| APK | `assembleDebug` e `build` OK; APK assinado, orientação `fullSensor`, cleartext só no debug; lint com 3 avisos e nenhum erro |

### Arquivos alterados fora de `gdx`

- `domain`: novos `MovimentoDoInimigo`, `MovimentoAleatorio`, `MovimentoPerseguidor`, `ResultadoDoTiro`; alterados `Inimigo`, `Missao`, `Nave`.
- `application`: novos `ModoDeJogo`, `Temporizador`, `ConfiguracaoDeTempoReal`, `RegrasDeTempoReal`; alterados `Acao`, `EventoJogo`, `GameSession`, `MissaoFactory`.
- `infrastructure`: nenhum.

## Validação feita

- Testes de unidade e `gradlew clean build` (cliente) e `gradlew test` (servidor) sem alterações.
- Simulação no desktop em modo toque com capturas de tela: paisagem (1200x540), retrato (540x1200) e 4:3 (1024x768), com contagem, segurar direção, tiro, pausa e perseguidor; partida de semente 4 no modo clássico.
- Inspeção do APK (manifesto, assinatura, lint).

## Não verificado

- Execução em aparelho ou emulador: toque real, sensação da jogabilidade, rotação, recortes e barras do sistema, áudio, pausa ao ir para segundo plano.
- Jogo do início ao fim no modo dinâmico (as capturas cobrem contagem, movimento, tiro e pausa; a vitória e a derrota são cobertas por testes de unidade).
- Controle Bluetooth.
- `docker build`, publicação em nuvem e acesso de outro aparelho pelo Wi-Fi.
- A janela de retrato de 540x1200 foi limitada pela altura da tela do computador; o rodapé da captura ficou sob a barra de tarefas.
