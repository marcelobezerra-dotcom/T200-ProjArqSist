# Fase 01 - Refatoracao Arquitetural (Console)

Esta fase preserva o jogo em modo texto e separa responsabilidades em camadas:

- `domain`: regras e entidades de negocio.
- `application`: orquestracao da partida e casos de uso.
- `console`: entrada e saida textual.
- `infrastructure`: persistencia do ranking em arquivo.

## Compilar

Execute na pasta `fase-01-refatoracao`:

```powershell
$files = Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -d out $files
```

## Executar

```powershell
java -cp out br.edu.unifor.missaomarte.Main
```
