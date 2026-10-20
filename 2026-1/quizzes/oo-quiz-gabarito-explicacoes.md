# Gabarito e explicações - Prova de POO (médio / estilo Enade)

## Instruções
- Este arquivo complementa o arquivo XML do Moodle.
- As respostas abaixo estão em ordem das questões do XML.
- Cada item inclui a justificativa da alternativa correta e a razão da distratora principal.

## Gabarito

1. Composição, porque a missão agrupa a nave como parte integrante de seu estado.
2. Garantir que o estado da nave seja alterado apenas por regras válidas e controladas.
3. Herança, porque as subclasses reutilizam propriedades e comportamentos da classe base.
4. Polimorfismo, porque a referência da classe base pode apontar para objetos concretos diferentes.
5. Programação orientada a interfaces e baixo acoplamento.
6. Baixa coesão, porque a classe concentra responsabilidades pouco relacionadas entre si.
7. Será executado o método da classe concreta Professor, mesmo a variável sendo do tipo Passageiro.
8. Manter o estado da nave sempre consistente e proteger regras de negócio.
9. Referências a objetos no heap, em que a atribuição copia a referência e não o objeto em si.
10. As alterações no conteúdo do objeto original persistem, mas a reatribuição do parâmetro local não altera a variável do chamador.
11. Distribuir as regras nas classes de domínio, como nave e missão, mantendo a lógica de colisão e embarque próximas ao objeto envolvido.
12. Agregação, porque os passageiros podem existir independentemente da viagem.
13. Diminuir acoplamento e facilitar troca de implementação sem alterar o serviço.
14. Coesão, porque a regra de colisão está relacionada ao conceito de asteroide.
15. Alta coesão e baixo acoplamento entre domínio e persistência.
16. Usar equals(), implementando a comparação do conteúdo relevante do objeto.
17. Inicializar os atributos herdados da classe base antes da criação do objeto específico.
18. Encapsulamento, porque os dados foram expostos sem controle e validação.
19. Obter entrada do usuário em tempo de execução para controlar o jogo.
20. As classes devem ter responsabilidades bem definidas e depender o mínimo possível umas das outras.

---

## Explicação dos itens

### 1) Composição
A relação entre Missao e Nave é de composição, pois a missão tem uma nave como parte essencial do seu estado e do seu funcionamento. Herança não se aplica porque a nave não é uma especialização da missão.

### 2) Encapsulamento
A principal vantagem de usar atributos privados com validação é manter o objeto sempre consistente. Isso protege o estado interno e evita que valores inválidos entrem no sistema.

### 3) Herança
Ao criar uma classe base Passageiro e subclasses Professor e Engenheiro, a equipe está reaproveitando atributos e comportamentos comuns. Isso é herança, que representa a relação "é um".

### 4) Polimorfismo
Uma referência do tipo Passageiro pode apontar para objetos concretos como Professor ou Engenheiro. O comportamento final depende do objeto real em tempo de execução.

### 5) Interface e baixo acoplamento
A interface MissaoRepository abstrai o contrato de persistência. O serviço depende do contrato, não da classe concreta, reduzindo acoplamento e facilitando testes e mudanças de implementação.

### 6) Baixa coesão
Quando uma classe executa muitas responsabilidades pouco relacionadas, ela perde coesão. Isso aumenta a complexidade e dificulta manutenção.

### 7) Polimorfismo dinâmico
Em Java, o método chamado depende do objeto real em memória, não da referência declarada. Assim, se Professor sobrescreve getTipo(), a chamada usa a implementação da subclasse.

### 8) Validação de regras de negócio
Mantém a integridade do estado da classe. Isso é importante para classes como Nave, que têm regras como capacidade máxima e restrições de embarque.

### 9) Referências em Java
Em Java, variáveis armazenam referências aos objetos, não cópias do objeto. Portanto, duas variáveis podem apontar para o mesmo objeto no heap.

### 10) Passagem por valor com referência
Java passa argumentos por valor, mas o valor passado é a referência. Assim, o método pode alterar o objeto apontado, mas não pode trocar a referência do chamador com uma reatribuição local.

### 11) Distribuição de responsabilidades
Lógica de domínio deve ficar nas classes do modelo. Main deve orquestrar o fluxo, mas não concentrar todas as regras de negócio.

### 12) Agregação
A relação em que um objeto contém outros, mas estes podem existir independentemente do todo, é agregação. O passageiro pode existir antes ou depois da viagem.

### 13) Dependência de abstração
O serviço depende da interface e não da implementação concreta. Isso torna o código mais flexível e mais fácil de evoluir.

### 14) Coesão
A regra de colisão é natural ao asteroide, porque ele sabe quando colide com uma nave. A lógica fica no conceito correto, preservando a coesão da classe.

### 15) Separação de camadas
Persistência e regra de negócio devem ser separadas para reduzir acoplamento. Isso melhora manutenção, testes e clareza arquitetônica.

### 16) Igualdade lógica
O operador == compara referência, enquanto equals() compara conteúdo relevante. Quando se quer verificar se duas naves são equivalentes logicamente, o equals() é o método adequado.

### 17) Construtor da superclasse
A chamada super(...) inicializa os atributos da superclasse antes que a subclasse complete a criação de um objeto. Isso garante que o objeto esteja em estado válido.

### 18) Encapsulamento ignorado
Quando os atributos são públicos, qualquer parte do código pode modificar valores sem validação. Isso contraria o encapsulamento e favorece inconsistências.

### 19) Scanner
O Scanner lê entradas do teclado e converte em valores úteis para o programa, permitindo interação com o usuário no console.

### 20) Arquitetura de software
A arquitetura melhora quando cada classe tem responsabilidades bem definidas e as dependências entre módulos são minimizadas. É o equilíbrio entre alta coesão e baixo acoplamento.

---

## Resumo das respostas

1. A  2. A  3. A  4. A  5. A  6. A  7. A  8. A  9. A  10. A
11. A 12. A 13. A 14. A 15. A 16. A 17. A 18. A 19. A 20. A

> Observação: no arquivo XML do Moodle, cada alternativa correta foi marcada com fração 100% e as explicações estão em generalfeedback e feedbacks específicos por resposta.
