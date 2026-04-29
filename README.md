# Voke

Sistema academico de gestao e venda de ingressos para eventos, modelado com DDD e Clean Architecture.

## Escopo da 1a Entrega

- Linguagem onipresente por bounded context.
- Mapa de historias de usuario.
- Modelo Context Mapper em `voke.cml`.
- 16 funcionalidades nao triviais com cenarios BDD em Gherkin.
- Automacao BDD com Cucumber e JUnit.
- Dominio puro, sem Spring/JPA nos modulos `dominio-*`.
- Infraestrutura JPA separada no modulo `infraestrutura`.

## Modulos

```text
.
|-- pai
|-- dominio-compartilhado
|-- dominio-pessoa
|-- dominio-evento
|-- dominio-inscricao
|-- dominio-fidelidade
|-- aplicacao
|-- infraestrutura
|-- apresentacao-backend
`-- apresentacao-frontend
```

## Bounded Contexts

- Pessoa: participantes, organizadores, parceiros, amizades e comunidades.
- Evento: eventos, grupos, avaliacoes, notificacoes, favoritos e cupons.
- Inscricao: carrinho, inscricoes e cancelamentos.
- Fidelidade: carteira virtual, pontos, recompensas e sugestoes.

## Como Executar os Testes

Requisitos:

- Java 17.
- Maven 3.9+ instalado no PATH.

Com Maven disponivel, execute:

```bash
mvn test
```

## Artefatos Principais

- Context Mapper: `voke.cml`
- Historias: `docs/historias/historias.md`
- Linguagem onipresente: `docs/dominio/linguagem-onipresente.md`
- Cenarios BDD: `dominio-*/src/test/resources/features/`
- Steps Cucumber: `dominio-*/src/test/java/br/voke/bdd/steps/`

## Regras de Arquitetura

- Modulos `dominio-*` nao devem importar Spring, JPA ou Hibernate.
- Entidades JPA ficam apenas em `infraestrutura`.
- Casos de uso ficam em `aplicacao`.
- Repositorios sao interfaces no dominio e implementacoes JPA na infraestrutura.
- Value Objects devem ser imutaveis e validar seus dados no construtor.
