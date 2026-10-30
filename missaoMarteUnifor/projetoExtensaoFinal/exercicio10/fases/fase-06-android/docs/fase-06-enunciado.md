# Enunciado - Missão Marte no Android

## 1. Contexto

Você recebeu o **Missão Marte** da Fase 5: um jogo libGDX (`core` e `lwjgl3`) que conversa com um servidor Spring Boot por uma API REST (cadastro, login, histórico e ranking global), com modo convidado e ranking local. Há 97 testes no cliente e 34 no servidor, e todos passam. O jogo só roda no desktop, com teclado, e o servidor só aceita conexões da própria máquina.

A equipe quer jogar **no celular**. Sua tarefa é **adaptar o jogo para Android reaproveitando o mesmo código**, criar **controles por toque**, permitir **configurar o endereço do servidor dentro do jogo** e **preparar o servidor para ser publicado** (rede Wi-Fi local ou nuvem), documentando o que foi feito e por quê.

Este é um **exemplo acadêmico**: a publicação serve para testar no celular. Não se exige alta disponibilidade, backup nem proteção contra abuso, mas as limitações devem ser registradas.

## 2. Objetivos de aprendizagem

- Organizar um projeto libGDX em módulos de plataforma (`core`, `lwjgl3`, `android`) e explicar por que o `core` não conhece nenhuma plataforma.
- Configurar o Android Gradle Plugin, as versões de SDK e as bibliotecas nativas do libGDX.
- Reconhecer APIs do Java que não existem na versão mínima do Android e trocá-las por equivalentes.
- Tratar entrada por toque e por controle (gamepad) sem alterar as regras do jogo.
- Adaptar a tela a proporções diferentes com viewports.
- Digitar no celular com a caixa de texto nativa e devolver o resultado à thread do jogo.
- Isolar o que muda entre plataformas (Port and Adapter) e testar essa lógica sem janela.
- Explicar HTTP, HTTPS e o bloqueio de tráfego sem criptografia no Android.
- Preparar um servidor para publicação: configuração por variável de ambiente, verificação de saúde e contêiner.
- Dizer com clareza o que foi e o que **não** foi verificado.

## 3. Entrada

Use como ponto de partida a pasta `fase-05-servidor-ranking`.

Regras:

- **Não altere** `fase-05-servidor-ranking`, as fases anteriores nem `exercicio10/src`. Trabalhe em uma pasta nova (por exemplo, `fase-06-android`).
- **Não altere as regras do jogo.** `domain` deve ficar idêntico ao da Fase 5 (verifique por hash).
- Mantenha a **versão desktop** funcionando como antes.
- O **servidor** pode receber apenas as mudanças mínimas para publicação; as rotas existentes não podem mudar de comportamento. Registre cada mudança.
- Suporte a controle Bluetooth é **opcional**.
- Não é preciso publicar de verdade na nuvem, mas o servidor deve estar pronto para isso e o roteiro, escrito.

## 4. Ambiente

| Item | Requisito |
|---|---|
| JDK | 17 (`JAVA_HOME` **e** `PATH`: o primeiro `java` do `PATH` precisa ser o 17) |
| Cliente | Gradle com wrapper 8.7, libGDX 1.12.1, Android Gradle Plugin compatível com o Gradle 8.7 (por exemplo, 8.6.x) |
| Servidor | Spring Boot 4.1.2 e Gradle com wrapper 9.8.2, como na Fase 5 |
| Android SDK | Plataforma 35 e build-tools 34.0.0, informados por `local.properties` (`sdk.dir`) ou `ANDROID_HOME` |
| Android | `minSdk` 26, `compileSdk` e `targetSdk` 35 |
| Aparelho | Celular com Android 8.0 ou superior, com depuração USB (opcional: emulador) |
| Docker | Opcional, só para validar a imagem do servidor |

Consulte a **tabela de compatibilidade do Android Gradle Plugin** (Gradle, JDK e API máxima) antes de escolher as versões. Se usar versões diferentes das indicadas, registre a divergência e o motivo.

O módulo Android **não pode impedir** que o desktop compile em uma máquina sem o Android SDK.

## 5. O que deve ser feito

### 5.1 Estrutura do projeto

```text
fase-06-android/
  cliente/
    core/        código compartilhado
    lwjgl3/      launcher desktop
    android/     launcher Android, manifesto, recursos
    tools/       geradores de assets e de ícone
  servidor/      API da Fase 5 + publicação (Dockerfile, DEPLOY.md)
  docs/
  README.md
```

### 5.2 Módulo Android

1. Crie o módulo `android` com `AndroidLauncher` (um `Activity` que monta as mesmas dependências do desktop e chama `initialize`).
2. Os assets (imagens e sons) são os mesmos de `core/assets`: não duplique arquivos.
3. Empacote as bibliotecas nativas do libGDX para as quatro arquiteturas (`armeabi-v7a`, `arm64-v8a`, `x86`, `x86_64`).
4. Declare no manifesto só a permissão necessária (`INTERNET`), orientação paisagem e ícone próprio. Gere o ícone por um programa em `tools` (não desenhe à mão e não copie imagens de terceiros).
5. O ranking local do convidado fica na pasta privada do aplicativo.
6. O módulo `android` só entra no build se o SDK existir. Quem não tem o SDK continua compilando o desktop.
7. O `core` continua sem importar nada de Android e `com.badlogic` continua restrito a `gdx`, `lwjgl3` e `android`.

### 5.3 Compatibilidade de código

Revise o `core` e troque o que a API 26 não oferece (por exemplo, `Files.readString`, `Files.writeString`, `List.of`, `String.isBlank`, `String.repeat`). Explique no tutorial por que o compilador do Android converte a **linguagem** mas não cria **APIs** ausentes no aparelho, e como você procurou por esses usos.

### 5.4 Controles por toque

1. **Botões virtuais** na partida: quatro setas, Embarcar, Som e Abortar. Cada botão vira a mesma `Acao` das teclas; a sessão do jogo **não** pode saber de onde a ação veio.
2. As setas **repetem** enquanto o dedo está apoiado e param ao soltar (use o identificador do ponteiro).
3. **Menus e telas** por toque: cada opção do menu e cada botão chama o mesmo código da tecla correspondente.
4. A tecla **Voltar** do Android deve ser tratada (voltar à tela anterior, abortar a partida, sair no menu).
5. **Modo toque no desktop** (por exemplo, `-Dmissaomarte.toque=true`): o mouse faz o papel do dedo, para testar sem aparelho.
6. Sem o modo toque, o desktop se comporta exatamente como na Fase 5.
7. A lógica de layout e de tradução (botão em ação, retângulo do botão) fica em classes **sem libGDX**, com testes.

### 5.5 Tamanho de tela

- No modo toque, use um viewport que **estenda o mundo** (por exemplo, `ExtendViewport` de 800x600) e coloque os controles na área extra à direita; no desktop comum, mantenha o `FitViewport`.
- Os botões devem acompanhar a largura da tela e **não se sobrepor** entre si (teste isso).
- Registre o que acontece em telas 4:3, onde não há área extra.

### 5.6 Digitação no celular

1. Não há teclado físico: ao tocar em um campo de texto, abra a **caixa de texto nativa** do sistema (senha com o tipo apropriado e **sem** preencher o valor atual).
2. A resposta da caixa vem em outra thread: devolva-a à thread do jogo antes de alterar o formulário.
3. Os formulários ganham uma operação "definir texto" que **aplica as mesmas regras de digitação** a cada caractere (caracteres inválidos e excesso são descartados).
4. A senha continua mascarada e nunca é gravada.

### 5.7 Configurar o servidor no jogo

1. Crie um objeto de valor imutável para a configuração (endereço, porta, HTTPS) que **valida na criação**: IPv4 (octetos até 255) ou nome de máquina, porta de 1 a 65535. Texto como `http://x/a@b`, com espaço, barra ou `@`, deve ser **recusado**, não interpretado.
2. Guarde a configuração em um **armazenamento de texto** atrás de uma interface (no jogo, as Preferences do libGDX). Grave **só** endereço, porta e HTTPS: **nunca senha nem token**. Valor salvo inválido é ignorado.
3. **Precedência:** valor salvo > padrão informado pelo lançador (`-Dmissaomarte.servidor` no desktop, `-PservidorPadrao` no Android) > `http://localhost:8080`.
4. A troca vale **imediatamente**, sem recriar o adaptador HTTP. Cuidado: as Preferences só existem depois que o jogo inicia.
5. Tela de configuração, acessível pelo menu e pela tela de entrada, com: endereço, porta, HTTPS, **Testar conexão** (chama `GET /api/saude` no endereço digitado, **antes** de salvar), **Salvar** e **Restaurar padrão**.
6. Trocar de servidor **encerra a sessão** (o token só vale para o servidor que o emitiu).
7. Mostre um **aviso** quando o endereço usa HTTP fora de localhost e de redes privadas (senha e token iriam sem criptografia).
8. O valor de `-PservidorPadrao` é validado por expressão regular **antes** de virar constante do código gerado.

### 5.8 HTTP e HTTPS no Android

- Desde o Android 9, aplicativos não aceitam HTTP sem criptografia por padrão. Habilite essa exceção **só no build de debug** (por exemplo, um *manifest placeholder* por tipo de build); o **release** deve aceitar apenas HTTPS.
- Prove com o manifesto do APK de debug e com o manifesto mesclado do release.

### 5.9 Controle Bluetooth (opcional)

O Android entrega os botões do controle como teclas. Mapeie o direcional, um botão para embarcar e outro para abortar, com teste de unidade. Se não tiver um controle para testar, registre como **não verificado**.

### 5.10 Servidor: publicação

Mudanças mínimas, sem alterar as rotas existentes:

| Mudança | Para quê |
|---|---|
| Endereço de escuta e porta por variável de ambiente (padrão: `127.0.0.1` e `8080`) | Aceitar outros aparelhos só quando pedido; provedores de nuvem informam a porta |
| `GET /api/saude` público, com teste | Testar conexão pelo jogo e verificação de saúde |
| Nome fixo do JAR e encerramento gracioso | Facilitar o contêiner |
| `Dockerfile` em duas etapas, usuário sem privilégios, dados em volume | Publicar em provedores que rodam contêineres |
| `DEPLOY.md` | Roteiro de publicação |

O `DEPLOY.md` deve ter **dois caminhos**: (A) Wi-Fi local (escutar em `0.0.0.0`, regra de firewall só para rede privada, descobrir o IP, `adb reverse` como alternativa) e (B) contêiner na nuvem (variáveis, porta, verificação de saúde, HTTPS pelo provedor). Inclua obrigatoriamente os avisos: HTTP só em rede de confiança, dados que podem ser perdidos em planos gratuitos (sistema de arquivos efêmero), partida a frio, segredo do token, Swagger e ausência de limite de tentativas de login. **Não** inclua credenciais reais.

### 5.11 Testes

Mantenha os 97 testes do cliente e os 34 do servidor passando e acrescente testes para o que criou:

| O que testar | O que verificar (no mínimo) |
|---|---|
| Configuração do servidor | URL montada; normalização; endereço e IP inválidos; porta fora da faixa; `deUrl`; redes privadas; aviso de HTTP; igualdade |
| Endereço em uso | Precedência; leitura preguiçosa do armazenamento; salvar e restaurar; valor salvo inválido ignorado; nenhuma chave de senha ou token |
| Controle de toque | Botão contém o ponto (inclusive bordas); botões dentro da área e sem sobreposição em larguras diferentes; botão em ação; só as setas repetem |
| Formulários | Servidor: caracteres e limites, definir texto, montar com validação. Login: definir texto com as mesmas regras e selecionar campo |
| Teclas | Direcional, botões do controle e tecla Voltar |
| Servidor | `/api/saude` responde 200 sem token e a API continua protegida |

Os testes **não podem** abrir janela, usar OpenGL, rede ou o SDK do Android.

### 5.12 Comandos que devem funcionar

```powershell
# servidor
cd servidor
.\gradlew test
.\gradlew bootRun --console=plain

# cliente (outro terminal)
cd cliente
.\gradlew clean build                                  # gera também os APKs se houver SDK
.\gradlew :lwjgl3:run --console=plain
.\gradlew :lwjgl3:run -Dmissaomarte.toque=true --console=plain
.\gradlew :android:assembleDebug
.\gradlew :android:assembleDebug "-PservidorPadrao=http://10.0.2.2:8080"
```

## 6. Pontos de atenção

- O módulo `android` precisa do `gdx` **também como dependência própria**: o `core` o declara como `implementation`, então ele não aparece no classpath de compilação do Android (erro "cannot access Application").
- No `local.properties`, as barras invertidas do caminho do Windows são caracteres de escape; use barras normais (`C:/Users/...`).
- As bibliotecas nativas do libGDX vêm em `.jar`; sem extrair os `.so` para a pasta de JNI, o app abre e fecha na hora.
- No libGDX, o direcional do controle tem o **mesmo código** das setas (`DPAD_UP` é igual a `UP`): dois `case` no mesmo `switch` não compilam.
- A tecla Voltar só chega ao jogo se for **capturada**; senão o Android fecha o aplicativo.
- A resposta da caixa de texto nativa e da rede chega fora da thread de desenho.
- O Preferences do libGDX não existe antes de `create()`: não leia o valor salvo no construtor do launcher.
- No emulador, o PC é `10.0.2.2` (não `localhost`); no aparelho, use `adb reverse` ou o IP do PC na mesma rede.
- Servidor escutando só em `127.0.0.1` **não** é alcançado por outro aparelho, mesmo com o firewall liberado.
- O Spring lê variáveis de ambiente removendo os hífens do nome da propriedade (`springdoc.swagger-ui.enabled` vira `SPRINGDOC_SWAGGERUI_ENABLED`).
- Planos gratuitos de nuvem costumam apagar o banco em arquivo e desligar o serviço por inatividade; o primeiro acesso pode passar do tempo limite do jogo.
- Não guarde senha nem token em arquivo, em log ou em tela, nem coloque segredos no repositório.

## 7. Preservar o comportamento

- `domain` idêntico ao da Fase 5 (compare por hash). Liste os arquivos de `application`, `infrastructure` e `gdx` que são novos ou mudaram e justifique cada um.
- A versão desktop sem o modo toque se comporta como na Fase 5.
- As rotas e regras do servidor da Fase 5 continuam iguais.
- Fases anteriores e `exercicio10/src` permanecem intactos (`git status`).

## 8. Documentação obrigatória

Entregue `docs/tutorial.md` (ou nome equivalente) com as seções abaixo, e um `README.md` na pasta da fase.

### 8.1 Estado de origem e destino

Como era o jogo (só desktop, servidor local, endereço fixo) e como ficou; tabela "item da Fase 5 -> equivalente na Fase 6" e diagrama da arquitetura (Mermaid ou imagem) com `core`, `lwjgl3`, `android` e o servidor.

### 8.2 Conceitos aplicados

Explique, com exemplos do seu próprio código:

- O papel de cada módulo e por que o `core` não conhece a plataforma.
- Como o Android empacota o aplicativo (manifesto, `minSdk`, `targetSdk`, ABIs, assets).
- A diferença entre o que o compilador converte (linguagem) e o que ele não cria (APIs).
- Eventos de toque e `unproject` (de tela para mundo); ponteiros e repetição.
- Viewports (`FitViewport` e `ExtendViewport`) e o que cada um faz com a proporção.
- Por que o Android bloqueia HTTP sem criptografia e como a exceção do debug foi feita.
- Variáveis de ambiente, endereço de escuta (`127.0.0.1` e `0.0.0.0`) e o que é uma verificação de saúde.
- O que é um contêiner e o que cada etapa do seu `Dockerfile` faz.
- Port and Adapter no seu código (por exemplo, o armazenamento de texto e o servidor do jogo).

### 8.3 Decisões e limitações de segurança

- O que foi tratado: validação do endereço, o que é e o que não é gravado no aparelho, HTTP só no debug, aviso de HTTP fora da rede local, token ligado ao servidor que o emitiu, valor do `-PservidorPadrao` validado.
- O que **não** foi tratado: HTTPS de ponta a ponta, força bruta no login, assinatura de release, pontuação falsificada pelo cliente, dados perdidos na nuvem gratuita.

### 8.4 Decisões de projeto

Versões escolhidas e divergências em relação às mais novas (e o motivo), módulo `android` condicional, modo toque no desktop, servidor com mudanças mínimas, padrões usados (distinguindo o que realmente segue o padrão do que apenas se parece).

### 8.5 Passo a passo

Comandos e decisões na ordem em que foram feitos, com os **problemas reais** encontrados e como foram resolvidos (veja a seção 6 para exemplos).

### 8.6 Testes e evidências

O que cada teste cobre; saída de `gradlew test` e `gradlew clean build` com a quantidade de testes; **capturas de tela** do modo toque (menu, configuração do servidor com teste de conexão, aviso de HTTP, partida com botões virtuais, fim de partida, ranking); inspeção do APK (manifesto, ABIs, assets, ícone); verificação do `usesCleartextTraffic` no debug e no release; respostas de `/api/saude`; comparação por hash de `domain`.

### 8.7 O que não foi verificado

Liste com honestidade o que **não** foi testado (por exemplo, aparelho real ou emulador, controle Bluetooth, `docker build`, publicação em nuvem, acesso de outro aparelho pelo Wi-Fi) e como o usuário pode verificar.

### 8.8 Limitações e próximos passos

Pelo menos três pontos que ainda podem melhorar e como eles se relacionam com a Fase 7 (por exemplo, ranking por dificuldade, HTTPS com certificado próprio, assinatura e publicação do aplicativo, reenvio offline, layout para tablets).

## 9. Roteiro mínimo de validação

Execute e registre o resultado de cada passo:

1. `gradlew --version` mostra a versão e o JVM esperados em cada subprojeto.
2. `gradlew test` (servidor) e `gradlew clean build` (cliente) terminam com `BUILD SUCCESSFUL` (informe a quantidade de testes).
3. O desktop **sem** `-Dmissaomarte.toque` abre em 800x600 e funciona como na Fase 5.
4. O desktop **com** `-Dmissaomarte.toque=true` mostra os botões virtuais; uma partida completa é vencida **só com cliques**.
5. Menu, opção 6: **Testar conexão** com o servidor no ar (sucesso) e com um endereço inexistente (mensagem de erro, sem exceção).
6. Endereço com HTTP e nome público: o aviso aparece. Endereço vazio ou IP inválido: mensagem de validação e nada é salvo.
7. Salvar, fechar e abrir o jogo: o endereço salvo é mantido. **Restaurar padrão** volta ao padrão.
8. Trocar de servidor estando logado encerra a sessão.
9. `gradlew :android:assembleDebug` gera o APK; `aapt2 dump badging` mostra `minSdk 26`, `targetSdk 35` e só a permissão `INTERNET`; o APK contém as quatro ABIs e os assets.
10. O manifesto do APK de debug tem `usesCleartextTraffic=true` e o mesclado do release tem `false`.
11. `-PservidorPadrao` com valor inválido faz o build falhar; com valor válido aparece em `BuildConfig`.
12. `GET /api/saude` responde `{"status":"ok"}` sem token e `GET /api/ranking` sem token continua 401.
13. O servidor iniciado com escuta em todas as interfaces e outra porta (por variáveis de ambiente) responde por `localhost` e pelo IP da máquina.
14. Verifique por busca que nada fora de `gdx`, `lwjgl3` e `android` importa `com.badlogic`, e por hash e `git status` que `domain`, as fases anteriores e `exercicio10/src` não mudaram.
15. **No celular** (se houver): instale o APK, configure o servidor, entre, jogue pelos botões, use a tecla Voltar e digite a senha pela caixa nativa. Registre o resultado. Sem aparelho, registre "não verificado".

## 10. Entregáveis

1. Pasta `fase-06-android` com `cliente/` (com o módulo `android`) e `servidor/` (cada um com seu wrapper e `.gitignore`).
2. Testes JUnit 5 que passam nos dois subprojetos.
3. `README.md` da fase com instruções de execução, controles, opções e limitações.
4. `servidor/DEPLOY.md` e o `Dockerfile`.
5. Documento `tutorial.md` com todas as seções da etapa 8.
6. Evidências do roteiro da etapa 9, com capturas de tela.

## 11. Critérios de avaliação

| Critério | Peso |
|---|---|
| Módulo Android (build, manifesto, ABIs, assets, ícone, compatibilidade de API, módulo condicional) | 15% |
| Controles por toque e tamanho de tela (botões virtuais, repetição, menus, Voltar, viewport, testes sem sobreposição) | 15% |
| Configuração do servidor no jogo (validação, armazenamento, precedência, teste de conexão, aviso, troca encerra a sessão) | 15% |
| Segurança de rede (HTTP só no debug, nada sensível gravado, parâmetro de build validado) | 10% |
| Servidor publicável (variáveis, `/api/saude`, `Dockerfile`, `DEPLOY.md` com os dois caminhos e os avisos) | 10% |
| Preservação das regras e das camadas (hash de `domain`, `com.badlogic` restrito, desktop como na Fase 5) | 10% |
| Testes JUnit 5 relevantes, isolados de rede, janela e Android, e passando | 10% |
| Documentação, evidências e honestidade sobre o que não foi verificado | 15% |
