# Publicar o servidor do Missão Marte (teste acadêmico)

Este roteiro serve para o celular conseguir falar com o servidor de ranking. **É um exemplo acadêmico**: não há alta
disponibilidade, backup, limite de tentativas de login nem monitoramento. Use dados de teste (nomes e senhas inventados).

Há dois caminhos. Escolha o mais simples para o seu caso.


| Caminho                       | Quando usar                | Custo                                      | HTTPS              |
| ------------------------------- | ---------------------------- | -------------------------------------------- | -------------------- |
| A. Rede Wi-Fi local (LAN)     | Celular e PC na mesma rede | Nenhum                                     | Não (HTTP)        |
| B. Contêiner Docker na nuvem | Acessar de qualquer rede   | Depende do provedor (há planos gratuitos) | Sim, pelo provedor |

## Variáveis de ambiente do servidor


| Variável                 | Padrão            | Para que serve                                                                                                                |
| --------------------------- | -------------------- | ------------------------------------------------------------------------------------------------------------------------------- |
| `SERVER_ADDRESS`          | `127.0.0.1`        | Endereço em que o servidor escuta. `0.0.0.0` aceita conexões de outros aparelhos.                                           |
| `PORT`                    | `8080`             | Porta. Provedores de nuvem costumam informar a porta por esta variável.                                                      |
| `MISSAOMARTE_JWT_SEGREDO` | vazio (aleatório) | Segredo que assina os tokens (mínimo 32 caracteres). Vazio: gera um a cada início e os tokens deixam de valer ao reiniciar. |
| `MISSAOMARTE_DADOS`       | `./dados`          | Pasta do banco H2 (arquivo).                                                                                                  |

O servidor tem o endpoint público `GET /api/saude`, que responde `{"status":"ok"}`. O jogo usa esse endpoint no botão
**Testar conexão** da tela Servidor, e os provedores de nuvem podem usá-lo como verificação de saúde.

## Caminho A: Wi-Fi local

1. Gere o JAR e inicie o servidor aceitando outros aparelhos (PowerShell, na pasta `servidor`). São **comandos separados: execute um por vez** (um Enter em cada linha). Se colar tudo de uma vez e o terminal juntar as linhas, o `bootJar` recebe os demais textos como argumentos e falha.

   Gerar o JAR:

   ```powershell
   .\gradlew.bat bootJar
   ```

   Definir as variáveis (valem só neste terminal) e iniciar:

   ```powershell
   $env:SERVER_ADDRESS = "0.0.0.0"
   $env:MISSAOMARTE_JWT_SEGREDO = "troque-por-um-texto-com-32-ou-mais-caracteres"
   & "$env:JAVA_HOME\bin\java.exe" -jar build\libs\app.jar
   ```

   Ou, em uma única linha (comandos separados por `;`):

   ```powershell
   $env:SERVER_ADDRESS = "0.0.0.0"; $env:MISSAOMARTE_JWT_SEGREDO = "troque-por-um-texto-com-32-ou-mais-caracteres"; & "$env:JAVA_HOME\bin\java.exe" -jar build\libs\app.jar
   ```

   Sem `SERVER_ADDRESS=0.0.0.0` o servidor aceita só conexões do próprio PC e o celular não consegue chegar nele.
2. Descubra o IP do PC na rede: `ipconfig` e procure o **Endereço IPv4** do adaptador Wi-Fi (por exemplo `192.168.0.10`).
3. Libere a porta no Firewall do Windows. Na primeira execução o Windows pergunta se o Java pode receber conexões:
   permita apenas em **rede privada**. Para criar a regra manualmente (PowerShell como administrador):

   ```powershell
   New-NetFirewallRule -DisplayName "Missao Marte 8080" -Direction Inbound -Protocol TCP -LocalPort 8080 -Action Allow -Profile Private
   ```

   Remova a regra ao terminar: `Remove-NetFirewallRule -DisplayName "Missao Marte 8080"`.
4. No celular, abra o jogo, vá em **Servidor** (opção 6 do menu), informe o IP e a porta (`192.168.0.10` e `8080`), deixe
   HTTPS desligado, toque em **Testar conexão** e depois em **Salvar**.

Alternativa sem Wi-Fi, com o celular ligado por cabo USB e a depuração USB ativa: `adb reverse tcp:8080 tcp:8080`.
O celular passa a enxergar o servidor do PC como `localhost:8080` (o endereço padrão do app).

> A conexão é HTTP, sem criptografia: senha e token trafegam em texto claro. Use só em rede de confiança (a sua, em casa
> ou no laboratório), nunca em Wi-Fi público.

## Caminho B: contêiner Docker na nuvem

O servidor tem um `Dockerfile` (build em duas etapas, executa como usuário sem privilégios). Qualquer provedor que rode
contêineres a partir de um Dockerfile serve; a interface muda de um provedor para outro, então siga a documentação dele.

1. (Opcional) Teste a imagem localmente, com o Docker Desktop aberto:

   ```powershell
   docker build -t missao-marte-servidor .
   docker run --rm -p 8080:8080 -v missaomarte-dados:/dados -e MISSAOMARTE_JWT_SEGREDO="troque-por-um-texto-com-32-ou-mais-caracteres" missao-marte-servidor
   ```
2. No provedor, crie um serviço web a partir do repositório (ou da imagem) apontando para a pasta `servidor`, com:

   - **Variável** `MISSAOMARTE_JWT_SEGREDO` com um texto de 32 ou mais caracteres (cadastre como segredo, não no código);
   - **Porta**: a que o provedor informar em `PORT` (o servidor já lê essa variável);
   - **Verificação de saúde** (se houver): `GET /api/saude`.
3. O provedor entrega um endereço HTTPS, como `meu-servico.exemplo.com`. No jogo, em **Servidor**, informe esse endereço,
   porta `443` e ligue **HTTPS**. Toque em **Testar conexão** e em **Salvar**.
4. Para o app Android já nascer apontando para a nuvem, gere o APK com o endereço como padrão:

   ```powershell
   .\gradlew.bat :android:assembleDebug "-PservidorPadrao=https://meu-servico.exemplo.com"
   ```

### Limites a conhecer

- **Os dados podem ser perdidos.** O banco é um arquivo H2. Planos gratuitos costumam ter sistema de arquivos efêmero:
  a cada novo deploy ou reinício, contas e ranking somem. Para guardar os dados, o serviço precisa de um volume
  persistente montado em `/dados` (em geral é recurso pago) ou de outro banco, o que está fora do escopo deste exemplo.
- **Partida a frio.** Planos gratuitos desligam o serviço depois de um tempo sem uso; o primeiro acesso seguinte demora e
  pode estourar o tempo limite de 5 s do jogo. Use **Testar conexão** de novo.
- **Segredo dos tokens.** Sem `MISSAOMARTE_JWT_SEGREDO`, os tokens deixam de valer a cada reinício e o jogador precisa
  entrar de novo.
- **Swagger.** A documentação interativa (`/swagger-ui.html`) fica ligada para facilitar o teste. Para desligar, defina
  `SPRINGDOC_SWAGGERUI_ENABLED=false` e `SPRINGDOC_APIDOCS_ENABLED=false` (os hífens do nome da propriedade somem na variável de ambiente).
- **Sem proteção contra tentativas em massa de login.** Não deixe um serviço assim publicado depois do teste.
  Apague o serviço quando terminar.

## Segurança em resumo

- HTTP puro só em rede local de confiança. Na internet, só HTTPS (o provedor cuida do certificado).
- O app Android de **debug** aceita HTTP (para LAN e emulador); o build **release** só aceita HTTPS.
- O jogo guarda no aparelho apenas endereço, porta e HTTPS. A senha e o token ficam só na memória e se perdem ao fechar o app.
- O jogo avisa na tela Servidor quando o endereço usa HTTP fora da rede local.
