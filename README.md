<div align="center">

# 🎟️ Voke

**Sistema acadêmico de gestão e venda de ingressos para eventos**
Modelado com **Domain-Driven Design** e **Clean Architecture**

[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](#pré-requisitos)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-6DB33F?logo=springboot&logoColor=white)](#2-backend-spring-boot)
[![React](https://img.shields.io/badge/React-18-61DAFB?logo=react&logoColor=black)](#3-frontend-react--vite)
[![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)](#1-banco-de-dados)

</div>

---

## 📑 Índice

- [Escopo da 1ª Entrega](#-escopo-da-1ª-entrega)
- [Módulos](#-módulos)
- [Bounded Contexts](#-bounded-contexts)
- [Descrição dos Domínios](#-descrição-dos-domínios)
- [Como Rodar o Projeto](#-como-rodar-o-projeto)
- [Como Executar os Testes](#-como-executar-os-testes)
- [Artefatos Principais](#-artefatos-principais)
- [Regras de Arquitetura](#-regras-de-arquitetura)
- [Mapeamento de Padrões de Projeto](#-mapeamento-de-padrões-de-projeto)
- [Links do Projeto](#-links-do-projeto)

---

## 🎯 Escopo da 1ª Entrega

- Linguagem onipresente por bounded context.
- Mapa de histórias de usuário.
- Modelo Context Mapper em `voke.cml`.
- 16 funcionalidades não triviais com cenários BDD em Gherkin.
- Automação BDD com Cucumber e JUnit.
- Domínio puro, sem Spring/JPA nos módulos `dominio-*`.
- Infraestrutura JPA separada no módulo `infraestrutura`.

---

## 📦 Módulos

```text
voke/
├── pai                       # POM pai com dependências compartilhadas
├── dominio-compartilhado     # Value Objects comuns (Email, Cpf, Senha…)
├── dominio-pessoa            # BC Pessoa
├── dominio-evento            # BC Evento
├── dominio-inscricao         # BC Inscrição
├── dominio-fidelidade        # BC Fidelidade
├── aplicacao                 # Casos de uso (Application Layer)
├── infraestrutura            # JPA Entities, Mappers e Repositórios Spring Data
├── apresentacao-backend      # REST Controllers + Config (Spring Boot)
└── apresentacao-frontend     # Interface do usuário (React + Vite + TypeScript)
```

---

## 🗺️ Bounded Contexts

| Bounded Context       | Responsabilidades                                                                             |
| :-------------------- | :-------------------------------------------------------------------------------------------- |
| **Pessoa**      | Participantes, organizadores, parceiros, amizades e comunidades                               |
| **Evento**      | Eventos, grupos, subgrupos, avaliações, notificações, favoritos, cupons, chat e dashboard |
| **Inscrição** | Carrinho, inscrições e cancelamentos                                                        |
| **Fidelidade**  | Carteira virtual, pontos, recompensas e sugestões                                            |

---

## 📖 Descrição dos Domínios

<details>
<summary><strong>Domínio Compartilhado</strong></summary>

Concentra conceitos reutilizados por mais de um contexto, como `Email`, `Cpf`,
`NomeCompleto`, `Senha`, `DataNascimento` e a base comum de entidades. Esse
domínio evita duplicação de regras básicas e mantém consistentes as validações
compartilhadas entre pessoas, eventos, inscrições e fidelidade.

</details>

<details>
<summary><strong>Domínio Pessoa</strong></summary>

Responsável pela identidade e pelos relacionamentos dos usuários da plataforma.
Inclui o cadastro e a manutenção de participantes, organizadores, parceiros e
administradores, além de regras de amizade, comunidades de amigos, chat privado
e recuperação de senha. Também protege invariantes ligadas a dados pessoais,
maioridade, unicidade de CPF/e-mail e restrições para exclusão ou alteração de
perfis.

</details>

<details>
<summary><strong>Domínio Evento</strong></summary>

Representa a criação, organização e acompanhamento dos eventos. Abrange eventos,
lotes, categorias, grupos e subgrupos, avaliações, favoritos, coleções,
notificações, cupons, FAQ, chat de canal e estatísticas de dashboard. Suas regras
cuidam de capacidade, período do evento, acesso a grupos, segmentação de
notificações, uso de cupons e indicadores operacionais para organizadores.

</details>

<details>
<summary><strong>Domínio Inscrição</strong></summary>

Cuida da jornada de compra e participação em eventos. Reúne carrinho,
itens de carrinho, aplicação de cupons, inscrições, status da inscrição,
cancelamentos e código validador. Esse domínio concentra regras como limite de
ingressos, vagas disponíveis, conflito de agenda, idade mínima, expiração do
carrinho e bloqueio de inscrição para eventos já iniciados.

</details>

<details>
<summary><strong>Domínio Fidelidade</strong></summary>

Gerencia os benefícios financeiros e de engajamento da plataforma. Inclui
carteira virtual, transações financeiras, conta de pontos, transações de pontos,
recompensas, sugestões de eventos, preferências do participante e comissões de
parceiros. Suas regras controlam saldo, limites diários, frequência de saques,
resgate de recompensas, estoque, congelamento de preço e pontuação por
participação.

</details>

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos

| Ferramenta | Versão mínima   |
| :--------- | :---------------- |
| Java (JDK) | 17                |
| Maven      | 3.9+              |
| Node.js    | 18+               |
| npm        | (incluso no Node) |
| MySQL      | 8                 |

> O MySQL deve estar rodando em `localhost:3306`.

---

### 1. Banco de Dados

O backend conecta em `jdbc:mysql://localhost:3306/voke` e usa
`createDatabaseIfNotExist=true`, então a base `voke` é criada automaticamente no
primeiro start. As tabelas são geradas pelo Hibernate (`ddl-auto=update`).

Credenciais padrão em `apresentacao-backend/src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=mOu9Wpsa322%p7
```

> **💡 Credenciais diferentes?** Crie o arquivo
> `apresentacao-backend/src/main/resources/application-local.properties`
> (já referenciado via `spring.config.import`) com:
>
> ```properties
> spring.datasource.username=SEU_USUARIO
> spring.datasource.password=SUA_SENHA
> ```

---

### 2. Backend (Spring Boot)

A partir da raiz do projeto:

```bash
# Compilar todos os módulos
mvn install -DskipTests

# Iniciar o servidor
mvn spring-boot:run -pl apresentacao-backend
```

---

### 3. Frontend (React + Vite)

Em outro terminal:

```bash
cd apresentacao-frontend
npm install          # apenas na primeira vez
npm install recharts # apenas na primeira vez
npm run dev
```

O frontend sobe em `http://localhost:5173`.
O Vite faz proxy de `/api` para `http://localhost:8080`, então o backend precisa estar rodando.

---

### 4. Acessar a Aplicação

| Página            | URL                             |
| :----------------- | :------------------------------ |
| Catálogo público | `http://localhost:5173/`      |
| Login              | `http://localhost:5173/login` |

Cadastre uma conta de **PARTICIPANTE** ou **ORGANIZADOR** pela tela de login (aba "Criar conta").

#### 🔑 Conta de Administrador

```
E-mail: admin@voke.com
Senha:  Admin1234
```

---

## 🧪 Como Executar os Testes

**Requisitos:** Java 17 e Maven 3.9+ no PATH.

```bash
# Rodar todos os testes
mvn test

# Rodar testes de um módulo específico
mvn -pl aplicacao -am test
```

---

## 📂 Artefatos Principais

| Artefato                | Caminho                                        |
| :---------------------- | :--------------------------------------------- |
| Context Mapper          | `voke.cml`                                   |
| Cenários BDD (Gherkin) | `dominio-*/src/test/resources/features/`     |
| Steps Cucumber          | `dominio-*/src/test/java/br/voke/bdd/steps/` |

---

## 🏛️ Regras de Arquitetura

- Módulos `dominio-*` **não devem** importar Spring, JPA ou Hibernate.
- Entidades JPA ficam apenas em `infraestrutura`.
- Casos de uso ficam em `aplicacao`.
- Repositórios são interfaces no domínio e implementações JPA na infraestrutura.
- Value Objects devem ser imutáveis e validar seus dados no construtor.

---

## 🧩 Mapeamento de Padrões de Projeto

| Padrão de Projeto        | Responsável Designado           | Arquivo(s) .java Identificado(s)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| :------------------------ | :------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Decorator**       | Arthur Leal                      | •`dominio-evento/.../grupo/GrupoEventoServicoDecorator.java` + `RestricaoEtariaGrupoDecorator.java` + `VerificacaoInscritoGrupoDecorator.java` + `PrivilegioOrganizadorGrupoDecorator.java<br>`• `dominio-evento/.../subgrupo/SubgrupoServicoDecorator.java` + `TipoFechadoSubgrupoDecorator.java` + `MembroDoGrupoPrincipalSubgrupoDecorator.java` + `PrivilegioGestorSubgrupoDecorator.java<br>`• `dominio-evento/.../chat/ChatCanalServicoDecorator.java` + `AcessoCanalDecorator.java` + `ConteudoValidoDecorator.java<br>`• `dominio-evento/.../estatistica/DashboardServicoDecorator.java` + `PrivilegioOrganizadorDashboardDecorator.java` |
| **Observer**        | Igor Couto e Júlio Vilas Boas   | •`dominio-fidelidade/.../sugestao/SugestaoObserver.java` + `NotificarParticipanteObserver.java<br>`• `dominio-fidelidade/.../recompensa/RecompensaObserver.java` + `LogRecompensaObserver.java`                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| **Proxy**           | Beatriz Galindo                  | •`dominio-pessoa/.../amizade/ComunidadeAmigosProtecaoProxy.java`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| **Strategy**        | Bruno Carvalho e William Moreira | •`dominio-inscricao/.../carrinho/EstrategiaTaxa.java` + `TaxaCartaoCredito.java` + `SemTaxa.java<br>`• `dominio-inscricao/.../carrinho/EstrategiaDesconto.java` + `DescontoFixo.java<br>`• `dominio-fidelidade/.../carteira/EstrategiaInsercaoSaldo.java` + `InsercaoSaldoPadrao.java` + `InsercaoSaldoVip.java<br>`• `dominio-fidelidade/.../pontos/EstrategiaGanhoPontos.java` + `GanhoPontosRegular.java` + `GanhoPontosCheckInBonus.java` + `GanhoPontosEventoEspecial.java`                                                                                                                                                                    |
| **Template Method** | Guilherme Almeida                | •`dominio-evento/.../cupom/UtilizacaoCupomTemplate.java` + `UtilizacaoCupomPadrao.java`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| **Iterator**        | Thiago Brayner                   | •`dominio-inscricao/.../inscricao/InscricoesAtivasIterador.java`                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |

---

## 🔗 Links do Projeto

| Recurso                                    | Link                                                                                                                                                    |
| :----------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 📋 Mapa das Histórias de Usuários (Miro) | [Abrir no Miro](https://miro.com/app/board/uXjVG1wbDB4=/?share_link_id=839589694420)                                                                       |
| 🎨 Protótipo de Baixa Fidelidade (Figma)  | [Abrir no Figma](https://www.figma.com/make/TYwxYnZHTRnuzkBmUh2sSJ/Baixa-fidelidade-prot%C3%B3tipo?code-node-id=0-9&p=f&t=xdFc6VaReFMlJVTd-0&fullscreen=1) |
