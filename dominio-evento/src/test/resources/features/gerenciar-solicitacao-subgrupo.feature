# language: pt
Funcionalidade: Gerenciar Solicitação de Entrada em Subgrupo
  Como um participante membro do grupo principal
  Quero solicitar entrada em subgrupos fechados e ter minha solicitação julgada pelo gestor
  Para entrar em subgrupos cuja entrada não é livre

  Cenário: Solicitação nasce no estado PENDENTE
    Dado que existe um subgrupo fechado disponível
    E o participante é membro do grupo principal
    Quando ele solicita entrada no subgrupo
    Então a solicitação é registrada como PENDENTE

  Cenário: Solicitação aprovada vira APROVADA
    Dado que o participante tem uma solicitação pendente para entrar no subgrupo
    Quando o gestor aprova a solicitação
    Então a solicitação fica com status APROVADA

  Cenário: Solicitação rejeitada vira REJEITADA
    Dado que o participante tem uma solicitação pendente para entrar no subgrupo
    Quando o gestor rejeita a solicitação
    Então a solicitação fica com status REJEITADA

  Cenário: Aprovar uma solicitação já decidida é proibido
    Dado que o participante tem uma solicitação já aprovada
    Quando o gestor tenta aprovar a solicitação de novo
    Então o sistema rejeita a decisão duplicada da solicitação

  Cenário: Participante não pode ter duas solicitações pendentes para o mesmo subgrupo
    Dado que o participante tem uma solicitação pendente para entrar no subgrupo
    Quando ele tenta solicitar entrada novamente no mesmo subgrupo
    Então o sistema rejeita a solicitação duplicada

  Cenário: Subgrupo aberto não aceita solicitações de entrada
    Dado que existe um subgrupo aberto disponível
    E o participante é membro do grupo principal
    Quando ele tenta solicitar entrada no subgrupo
    Então o sistema rejeita a solicitação por se tratar de subgrupo aberto

  Cenário: Não-membro do grupo principal não pode solicitar entrada
    Dado que existe um subgrupo fechado disponível
    E o participante não é membro do grupo principal
    Quando ele solicita entrada no subgrupo
    Então o sistema rejeita a solicitação por falta de membership
