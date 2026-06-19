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

 ## Links do Projeto

**Mapa das Histórias de Usuários (Miro):** https://miro.com/app/board/uXjVG1wbDB4=/?share_link_id=839589694420

# Mapeamento de Padrões de Projeto

| Padrão de Projeto | Responsável Designado | Arquivo(s) .java Identificado(s) |
| :--- | :--- | :--- |
| **Decorator** | leal | • `dominio-evento/.../grupo/GrupoEventoServicoDecorator.java` + `RestricaoEtariaGrupoDecorator.java` + `VerificacaoInscritoGrupoDecorator.java` + `PrivilegioOrganizadorGrupoDecorator.java`<br>• `dominio-evento/.../subgrupo/SubgrupoServicoDecorator.java` + `TipoFechadoSubgrupoDecorator.java` + `MembroDoGrupoPrincipalSubgrupoDecorator.java` + `PrivilegioGestorSubgrupoDecorator.java`<br>• `dominio-evento/.../chat/ChatCanalServicoDecorator.java` + `AcessoCanalDecorator.java` + `ConteudoValidoDecorator.java`<br>• `dominio-evento/.../estatistica/DashboardServicoDecorator.java` + `PrivilegioOrganizadorDashboardDecorator.java` |
| **Observer** | Igor e Júlio | • `dominio-fidelidade/.../sugestao/SugestaoObserver.java` + `NotificarParticipanteObserver.java`<br>• `dominio-fidelidade/.../recompensa/RecompensaObserver.java` + `LogRecompensaObserver.java` |
| **Proxy** | Bia | • `dominio-pessoa/.../amizade/ComunidadeAmigosProtecaoProxy.java` |
| **Strategy** | Messi e Will | • `dominio-inscricao/.../carrinho/EstrategiaTaxa.java` + `TaxaCartaoCredito.java` + `SemTaxa.java`<br>• `dominio-inscricao/.../carrinho/EstrategiaDesconto.java` + `DescontoFixo.java`<br>• `dominio-fidelidade/.../carteira/EstrategiaInsercaoSaldo.java` + `InsercaoSaldoPadrao.java` + `InsercaoSaldoVip.java`<br>• `dominio-fidelidade/.../pontos/EstrategiaGanhoPontos.java` + `GanhoPontosRegular.java` + `GanhoPontosCheckInBonus.java` + `GanhoPontosEventoEspecial.java` |
| **Template Method** | guila | • `dominio-evento/.../cupom/UtilizacaoCupomTemplate.java` + `UtilizacaoCupomPadrao.java` |
| **Iterator** | Thigas | • `dominio-inscricao/.../inscricao/InscricoesAtivasIterador.java` |
