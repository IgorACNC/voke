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

## Descricao dos Dominios

### Dominio Compartilhado

Concentra conceitos reutilizados por mais de um contexto, como `Email`, `Cpf`,
`NomeCompleto`, `Senha`, `DataNascimento` e a base comum de entidades. Esse
dominio evita duplicacao de regras basicas e mantem consistentes as validacoes
compartilhadas entre pessoas, eventos, inscricoes e fidelidade.

### Dominio Pessoa

Responsavel pela identidade e pelos relacionamentos dos usuarios da plataforma.
Inclui o cadastro e a manutencao de participantes, organizadores, parceiros e
administradores, alem de regras de amizade, comunidades de amigos, chat privado
e recuperacao de senha. Tambem protege invariantes ligadas a dados pessoais,
maioridade, unicidade de CPF/e-mail e restricoes para exclusao ou alteracao de
perfis.

### Dominio Evento

Representa a criacao, organizacao e acompanhamento dos eventos. Abrange eventos,
lotes, categorias, grupos e subgrupos, avaliacoes, favoritos, colecoes,
notificacoes, cupons, FAQ, chat de canal e estatisticas de dashboard. Suas regras
cuidam de capacidade, periodo do evento, acesso a grupos, segmentacao de
notificacoes, uso de cupons e indicadores operacionais para organizadores.

### Dominio Inscricao

Cuida da jornada de compra e participacao em eventos. Reune carrinho,
itens de carrinho, aplicacao de cupons, inscricoes, status da inscricao,
cancelamentos e codigo validador. Esse dominio concentra regras como limite de
ingressos, vagas disponiveis, conflito de agenda, idade minima, expiracao do
carrinho e bloqueio de inscricao para eventos ja iniciados.

### Dominio Fidelidade

Gerencia os beneficios financeiros e de engajamento da plataforma. Inclui
carteira virtual, transacoes financeiras, conta de pontos, transacoes de pontos,
recompensas, sugestoes de eventos, preferencias do participante e comissoes de
parceiros. Suas regras controlam saldo, limites diarios, frequencia de saques,
resgate de recompensas, estoque, congelamento de preco e pontuacao por
participacao.

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

**Protótipo de Baixa Fidelidade (Figma):** https://www.figma.com/make/TYwxYnZHTRnuzkBmUh2sSJ/Baixa-fidelidade-prot%C3%B3tipo?code-node-id=0-9&p=f&t=xdFc6VaReFMlJVTd-0&fullscreen=1
