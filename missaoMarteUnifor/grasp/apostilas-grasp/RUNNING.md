# Compilar, executar e testar a Missão Marte

O código executável está em [`../src/graspexercicio10`](../src/graspexercicio10/Main.java),
relativo a esta pasta. As apostilas não mantêm uma segunda cópia dos fontes.

Use JDK 11 ou superior, com `javac` e `java` disponíveis no PATH.
Não há bibliotecas externas.

## Diretório de execução

Todos os comandos de compilação abaixo partem da **raiz do repositório**,
onde estão `src`, `tests` e `apostilas-grasp`.

Se o terminal estiver dentro de `apostilas-grasp`, volte um nível primeiro:

```powershell
Set-Location ..
```

Em Bash, use `cd ..`.

## PowerShell: compilar e jogar

```powershell
$fontesGrasp = @(Get-ChildItem src/graspexercicio10 -Recurse -Filter *.java | ForEach-Object { $_.FullName })
javac -encoding UTF-8 --release 11 -d build-grasp $fontesGrasp
java -cp build-grasp graspexercicio10.Main
```

## PowerShell: compilar e testar

```powershell
$fontesGrasp = @(Get-ChildItem src/graspexercicio10 -Recurse -Filter *.java | ForEach-Object { $_.FullName })
$testesGrasp = @(Get-ChildItem tests/graspexercicio10 -Recurse -Filter *.java | ForEach-Object { $_.FullName })
javac -encoding UTF-8 --release 11 -d build-grasp $fontesGrasp $testesGrasp
java -cp build-grasp graspexercicio10.JogoTest
```

Resultado esperado:

```text
14 cenários de comportamento passaram.
```

Os testes usam cenários, relógio e ranking controlados. O teste de arquivo cria
um JSON temporário em `build-grasp` e o remove ao terminar; não altera o ranking
do jogador.

## Bash: compilar, jogar e testar

```bash
mkdir -p build-grasp
find src/graspexercicio10 tests/graspexercicio10 -name '*.java' > build-grasp/grasp-sources.txt
javac -encoding UTF-8 --release 11 -d build-grasp @build-grasp/grasp-sources.txt
java -cp build-grasp graspexercicio10.Main
java -cp build-grasp graspexercicio10.JogoTest
```

## Comandos e parâmetros do jogo

| Entrada | Operação |
| --- | --- |
| `w` / `s` | Cima / baixo: Y diminui / aumenta |
| `a` / `d` | Esquerda / direita: X diminui / aumenta |
| `c` | Embarcar passageiro na célula da nave |
| `q` | Abortar a missão e voltar ao menu |
| Opção `4` no menu | Encerrar o programa |

O raio deve estar entre 2 e 20. Raio 5 produz uma grade de 11 × 11 células,
de `(-5,-5)` a `(5,5)`, com a base em `(0,0)`.

O arquivo `ranking-grasp.json` é criado no diretório de execução quando uma
vitória se classifica. Os compilados ficam em `build-grasp`, separado dos
arquivos antigos existentes no repositório.

## Problemas comuns

- **`javac` não encontrado:** confira a instalação do JDK e o PATH.
- **Classe principal não encontrada:** confira se a compilação terminou sem
  erros e se o classpath aponta para `build-grasp`.
- **Terminal dentro da pasta das apostilas:** volte à raiz antes de compilar.
- **Acentos incorretos:** os fontes e exemplos usam UTF-8. Configure o terminal
  para essa codificação; no PowerShell, pode usar `chcp 65001`.
- **Erro ao ler ou gravar ranking:** a apresentação informa a falha de I/O.
  Uma vitória concluída permanece válida mesmo quando sua gravação falha.

Continue pelo [tutorial](tutorial-grasp-missao-marte.md) ou pelo
[guia do código](../src/graspexercicio10/README.md).
