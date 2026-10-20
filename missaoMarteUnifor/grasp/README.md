# Tutorial GRASP — Missão Marte

O exemplo em [`src/graspexercicio10`](src/graspexercicio10/README.md) demonstra a
distribuição de responsabilidades com GRASP e prepara o jogo de console para uma
futura interface gráfica. O exercício original permanece em `src/exercicio10` para comparação.

Leia o [guia do exemplo e da migração gráfica](src/graspexercicio10/README.md) para
entender o controller, as regras da partida, o estado imutável e os nove princípios.
As [apostilas](apostilas-grasp/README.md) complementam a discussão conceitual.
O [tutorial completo](apostilas-grasp/tutorial-grasp-missao-marte.md) acompanha a refatoração passo a passo.

## Executar (JDK 11 ou superior)

Na raiz do repositório, em PowerShell:

```powershell
$fontesGrasp = @(Get-ChildItem src/graspexercicio10 -Recurse -Filter *.java | ForEach-Object { $_.FullName })
javac -encoding UTF-8 --release 11 -d build-grasp $fontesGrasp
java -cp build-grasp graspexercicio10.Main
```

O console usa `w/s/a/d` para mover, `c` para embarcar e `q` para abortar a missão.
O ranking é armazenado em `ranking-grasp.json` no diretório de execução.

## Testar sem interface

```powershell
$fontesGrasp = @(Get-ChildItem src/graspexercicio10 -Recurse -Filter *.java | ForEach-Object { $_.FullName })
$testesGrasp = @(Get-ChildItem tests/graspexercicio10 -Recurse -Filter *.java | ForEach-Object { $_.FullName })
javac -encoding UTF-8 --release 11 -d build-grasp $fontesGrasp $testesGrasp
java -cp build-grasp graspexercicio10.JogoTest
```

Os testes usam cenários e relógio controlados, ranking em memória e um arquivo
temporário em `build-grasp`; não alteram o ranking do jogador. Não há bibliotecas externas.
