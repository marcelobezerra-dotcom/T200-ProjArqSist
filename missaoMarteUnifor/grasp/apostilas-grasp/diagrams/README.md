# Diagramas da Missão Marte

Os diagramas principais correspondem ao código em
[`src/graspexercicio10`](../../src/graspexercicio10/README.md).
Os arquivos `.mmd` usam Mermaid; os `.puml` usam PlantUML.

## Modelo e arquitetura

| Diagrama | Conteúdo |
| --- | --- |
| [Modelo de domínio](class-v1.mmd) | Hierarquia de `ElementoMapa`, com coordenadas e símbolo abstrato, e relações da missão |
| [Arquitetura](class-v2.mmd) | Controller, console, serviços, snapshots e persistência |

O nome `class-v1-fixed.mmd` foi preservado para compatibilidade com links antigos;
seu conteúdo acompanha o modelo atual de `class-v1.mmd`.

## Diagramas por princípio

| Apostila | Classes | Sequência |
| --- | --- | --- |
| [Information Expert](../info-expert.md) | [Especialistas](info-expert-class.mmd) | [Embarque](info-expert-sequence.mmd) |
| [Creator](../creator.md) | [Modelo](class-v1.mmd) | [Criação](creator.mmd), [PlantUML](creator-sequence.puml) |
| [Controller](../controller.md) | [Arquitetura](class-v2.mmd) | [Ação do console](controller.mmd), [PlantUML](controller-sequence.puml) |
| [Low Coupling](../low-coupling.md) | [Dependências](low-coupling-class.mmd) | [Persistência por contrato](low-coupling-sequence.mmd) |
| [High Cohesion](../high-cohesion.md) | [Distribuição](high-cohesion-class.mmd) | [Turno](high-cohesion-sequence.mmd) |
| [Polymorphism](../polymorphism.md) | [Hierarquia](polymorphism-class.mmd) | [Símbolos e snapshot](polymorphism-sequence.mmd) |
| [Pure Fabrication](../pure-fabrication.md) | [Objetos de software](pure-fabrication-class.mmd) | [Desenho do estado](pure-fabrication-sequence.mmd) |
| [Indirection](../indirection.md) | [Repositório](indirection-class.mmd) | [Registro de vitória](indirection-sequence.mmd) |
| [Protected Variations](../protected-variations.md) | [Contrato](protected-variations-class.mmd) | [Armazenamento](protected-variations-sequence.mmd) |

`polymorphism-class-clean.mmd` e `polymorphism-class-fixed.mmd` também acompanham
o modelo atual. Os nomes foram preservados para os links de materiais existentes.

Nos diagramas de sequência, o repositório é uma instância concreta acessada por
`IRankingRepository`. A interface não representa outro objeto intermediário em
tempo de execução. O diagrama do turno destaca o caminho de movimento válido;
um movimento bloqueado retorna antes do consumo de pontos e da atualização de perigos.

## Exemplos complementares de outro domínio

Os arquivos abaixo foram preservados como atividades separadas e não descrevem
classes executáveis da Missão Marte:

- [Pagamento e pedidos](class-v2-payment.mmd): exemplo de colaboração em outro domínio.
- [CRUD de produto e tipo](atividade-crud-produto-tipo.mmd): atividade de atribuição de responsabilidades.

Para executar a Missão Marte, use as [instruções das apostilas](../RUNNING.md).
