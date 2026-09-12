# Apostila – SOLID: LSP (Substituição de Liskov)

![LSP – Substituição de Liskov](../assets/solid/lsp.svg)

**Objetivo:** Garantir que subtipos possam substituir seus tipos base sem quebrar o sistema.

## Conceito

LSP (Liskov Substitution Principle) afirma que qualquer objeto de uma subclasse deve poder ser usado no lugar da classe base sem gerar comportamento inesperado.

A ideia principal é que uma subclasse não deve “enganar” o cliente ao mudar o contrato da superclasse. Ela deve apenas especializar o comportamento esperado, sem quebrar o uso normal.

## O ponto de atenção no Exercício 10

O exercício possui `Passageiro` e subclasses como `Professor`, `Engenheiro` e `Astronauta`. Um cliente pode trabalhar com o tipo base:

```java
private static int pontuar(Passageiro passageiro) {
    return passageiro.getPontuacao();
}
```

Isso só é seguro se toda nova subclasse continuar devolvendo uma pontuação válida. Uma implementação como esta compila, mas quebra o contrato esperado:

```java
public class PassageiroInvalido extends Passageiro {
    @Override
    public int getPontuacao() {
        throw new IllegalStateException("Passageiro inválido");
    }
}
```

O código original não contém essa violação explícita. O problema que ele revela é outro sinal de risco: `Main.desenharMapa` precisa testar `instanceof` para descobrir o tipo concreto do passageiro. Quando cada nova subclasse exige alterar o cliente, fica mais fácil criar um subtipo que não respeita as expectativas dos pontos onde `Passageiro` é usado.

### Direção da refatoração

A hierarquia deve definir um contrato que todos os passageiros possam cumprir. O cliente usa `Passageiro`, enquanto cada subtipo implementa apenas sua variação de pontuação. O LSP valida a substituição; ele não decide sozinho se a solução deve ser herança ou composição.

## Exemplo no tutorial

No jogo, `Professor`, `Engenheiro` e `Astronauta` são variações de `Passageiro`.

```java
public abstract class Passageiro {
    public abstract int getPontuacao();
}

public class Professor extends Passageiro {
    @Override
    public int getPontuacao() {
        return 15;
    }
}

public class Engenheiro extends Passageiro {
    @Override
    public int getPontuacao() {
        return 20;
    }
}
```

Agora este código funciona com qualquer subtipo:

```java
public class Missao {
    public int calcularBonus(Passageiro passageiro) {
        return passageiro.getPontuacao();
    }
}
```

O cliente não precisa conhecer se o passageiro é `Professor` ou `Engenheiro`. Ele apenas usa a abstração `Passageiro`.

## Exemplo de violação do LSP

```java
public class PassageiroInvalido extends Passageiro {
    @Override
    public int getPontuacao() {
        throw new IllegalStateException("Passageiro inválido");
    }
}
```

Essa subclasse viola LSP porque ela altera o contrato esperado: em vez de devolver uma pontuação, ela lança exceção. O cliente que espera um `Passageiro` não pode substituí-lo sem problema.

## Como o projeto faz sentido com LSP

A herança faz sentido quando a nova classe mantém a expectativa do tipo base:

- `Professor` e `Engenheiro` continuam sendo passageiros;
- o sistema continua tratando todos como `Passageiro`;
- a diferença está no comportamento especializado, não na quebra do contrato.

## Exercícios

1. Crie um novo subtipo de `Passageiro` e teste se ele funciona no mesmo fluxo do jogo.
2. Valide que o código cliente não precisa conhecer o tipo concreto para usar o objeto.
3. Experimente uma subclasse que lança exceção e explique por que isso quebra LSP.

## Checklist

- `Professor`, `Engenheiro` e `Astronauta` podem ser tratados como `Passageiro`?
- O comportamento esperado foi preservado?
- Nenhuma subclasse alterou o contrato da base de forma incompatível?

## Como validar

- Trocar um subtipo por outro no código não deve quebrar o funcionamento do embarque ou da pontuação.
- Se isso acontecer, a hierarquia não está adequada.

## Referências

- Apostila OO (Herança/Polimorfismo)
- Projeto do tutorial: src/tutorial-exercicio10
