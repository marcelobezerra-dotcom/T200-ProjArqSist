# Fase 05 - Servidor e ranking global

Missão Marte com um servidor Spring Boot independente: cadastro de jogadores, login, histórico de partidas e ranking global (Top 10). O cliente libGDX continua sendo o jogo da Fase 4; agora ele conversa com o servidor por uma API REST.

Dois modos de jogo:

- **Logado:** o resultado de cada partida (vitória ou derrota) é enviado ao servidor, que mantém o histórico do jogador e o ranking global.
- **Convidado** (sem entrar): o jogo funciona como na Fase 4, com o ranking local em `ranking.json`.

> **Divergência registrada.** O `PROMPT_MESTRE` diz que o login é obrigatório para jogar. Por decisão do autor na aprovação do planejamento, o jogo continua jogável sem login (modo convidado, ranking local); com login, usa o ranking remoto.

## Estrutura

```text
fase-05-servidor-ranking/
  cliente/                      jogo libGDX (a Fase 4, evoluída). Gradle 8.7, libGDX 1.12.1
    core/                       domain, application, infrastructure, gdx
        application/servidor/   porta ServidorDoJogo, Sessao, Resposta, PartidaConcluida...
        gdx/rede/               adaptador HTTP (Gdx.net) e conversão de JSON
        gdx/                    LoginScreen, HistoricoScreen, RankingScreen, menu...
    lwjgl3/                     launcher desktop
  servidor/                     API REST em Spring Boot 4.1.2. Gradle 9.8.2, Java 17
    src/main/java/.../servidor/
        usuario/ auth/ partida/ ranking/   por funcionalidade: controller, serviço, repositório, DTOs
        seguranca/                         BCrypt, token JWT, regras de acesso
        erro/                              respostas de erro (ProblemDetail)
    requests.http               chamadas de exemplo (extensão REST Client do VS Code)
  docs/                         planejamento e tutorial
```

Os dois projetos são **builds Gradle independentes**, cada um com o seu wrapper: o Spring Boot 4.1 exige Gradle 8.14 ou superior (ou 9.x), e o cliente segue na versão 8.7 usada desde a Fase 2.

## Requisitos

- JDK 17 em `JAVA_HOME`. Confira também o `PATH`: o `java` que aparece primeiro precisa ser o 17.
- Placa de vídeo com OpenGL 2.0 ou superior (cliente).
- Não é preciso instalar Gradle nem banco de dados: os wrappers baixam o Gradle e o H2 roda dentro do servidor.

## Como executar

Abra dois terminais.

**Terminal 1 - servidor** (porta 8080, só aceita conexões da própria máquina):

```powershell
cd exercicio10\fases\fase-05-servidor-ranking\servidor
.\gradlew.bat test                      # 31 testes
.\gradlew.bat bootRun --console=plain   # sobe a API
```

**Terminal 2 - cliente:**

```powershell
cd exercicio10\fases\fase-05-servidor-ranking\cliente
.\gradlew.bat clean build               # 97 testes
.\gradlew.bat :lwjgl3:run --console=plain
```

No menu, `4` abre a tela de entrada. Com o servidor desligado o jogo continua em modo convidado.

### Opções

| Onde | Opção | Efeito |
| --- | --- | --- |
| Cliente | `-Dmissaomarte.servidor=http://localhost:8080` | Endereço do servidor |
| Cliente | `-Dmissaomarte.semente=4` | Cenário reproduzível (como na Fase 4) |
| Cliente | `-Dmissaomarte.semaudio=true` | Sem áudio |
| Servidor | variável `MISSAOMARTE_JWT_SEGREDO` | Segredo do token (mínimo 32 bytes). Sem ela, um segredo aleatório é gerado a cada início e os tokens deixam de valer ao reiniciar |
| Servidor | variável `MISSAOMARTE_DADOS` | Pasta do banco H2 (padrão `servidor\dados`, ignorada pelo git) |

Exemplo: `.\gradlew.bat :lwjgl3:run -Dmissaomarte.servidor=http://localhost:8080 --console=plain`.

## Controles

| Tela | Teclas |
| --- | --- |
| Menu (convidado) | `1` Jogar, `2` Ranking local, `3` Limpar ranking local, `4` Entrar ou criar conta, `5` Sair |
| Menu (logado) | `1` Jogar, `2` Ranking global, `3` Meu histórico, `4` Sair da conta, `5` Sair |
| Entrar / Criar conta | digitar, `Tab` ou setas trocam de campo, `F2` alterna entrar e criar conta, `Enter` confirma, `Esc` volta |
| Nova partida | convidado digita o nome; logado usa o nome da conta. Setas esquerda/direita mudam a dificuldade, `Enter` decola |
| Partida | como na Fase 4. Ao terminar logado: `R` tenta enviar de novo se o envio falhar, `Enter` continua |
| Ranking e histórico | `R` recarrega após erro, `Enter` ou `Esc` voltam |

## API (resumo)

| Método e rota | Exige token | Função |
| --- | --- | --- |
| `POST /api/usuarios` | não | Cadastra. 201, 409 (nome já existe) ou 400 |
| `POST /api/auth/login` | não | Devolve o token. 401 se usuário ou senha estiverem errados |
| `POST /api/partidas` | sim | Registra uma partida do dono do token |
| `GET /api/partidas/minhas` | sim | Histórico do jogador, da mais recente para a mais antiga |
| `GET /api/ranking` | sim | Top 10: a melhor vitória de cada jogador |

Detalhes, exemplos e regras estão no [tutorial](docs/fase-05-tutorial.md) e em `servidor/requests.http`.

## Limitações conhecidas

- O servidor **não executa a lógica do jogo**: a pontuação vem do cliente e não pode ser verificada (só há limites de valores).
- Sem HTTPS: use apenas em `localhost` ou em rede de confiança.
- Sem limite de tentativas de login, sem renovação de token e sem fila de reenvio offline: se o envio falhar e o jogador sair da tela, a partida não é gravada.
- O ranking global não é separado por dificuldade.

## Documentação

- [Planejamento](docs/fase-05-planejamento.md)
- [Enunciado para o aluno](docs/fase-05-enunciado.md)
- [Tutorial](docs/fase-05-tutorial.md)
