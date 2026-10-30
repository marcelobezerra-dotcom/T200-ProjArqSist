# Fase 01 - Refatoração Arquitetural (Console)

Esta fase preserva o jogo em modo texto e separa responsabilidades em camadas:

- `domain`: regras e entidades de negócio.
- `application`: orquestração da partida e casos de uso.
- `console`: entrada e saída textual.
- `infrastructure`: persistência do ranking em arquivo.

## Compilar

Execute na pasta `fase-01-refatoracao`:

```powershell
$files = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -d out $files
```

Ou use comando único:

```powershell
.\build.ps1
```

## Executar

```powershell
java -cp out br.edu.unifor.missaomarte.Main
```

Para compilar e executar em um único comando:

```powershell
.\build.ps1 -Run
```
