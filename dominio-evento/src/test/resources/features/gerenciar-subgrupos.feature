# language: pt
Funcionalidade: Gerenciar Subgrupos de Evento
  Como um organizador ou membro do grupo principal
  Quero criar subgrupos com diferentes categorias, tipos, limites e moderadores
  Para organizar melhor a comunicação dos participantes do meu evento

  Cenário: Organizador cria subgrupo aberto com sucesso
    Dado que existe um grupo principal ativo
    E o organizador do evento está autenticado
    Quando ele cria um subgrupo aberto com nome, categoria, tipo e limite
    Então o subgrupo é criado e vinculado ao grupo principal

  Cenário: Participante comum não pode criar subgrupo
    Dado que existe um grupo principal ativo
    E o participante está autenticado mas não é organizador
    Quando ele tenta criar um subgrupo
    Então o sistema rejeita a criação do subgrupo
    E exibe a mensagem "Apenas o organizador do evento pode criar subgrupos"

  Cenário: Participante do grupo principal entra em subgrupo aberto
    Dado que existe um subgrupo aberto vazio
    E o participante é membro do grupo principal do evento
    Quando ele tenta entrar diretamente no subgrupo
    Então ele se torna membro do subgrupo

  Cenário: Não-membro do grupo principal tenta entrar em subgrupo
    Dado que existe um subgrupo aberto vazio
    E o participante não é membro do grupo principal do evento
    Quando ele tenta entrar diretamente no subgrupo
    Então o sistema rejeita a entrada no subgrupo

  Cenário: Entrada direta em subgrupo fechado é bloqueada
    Dado que existe um subgrupo fechado vazio
    E o participante é membro do grupo principal do evento
    Quando ele tenta entrar diretamente no subgrupo
    Então o sistema rejeita a entrada no subgrupo

  Cenário: Subgrupo lotado rejeita novo membro
    Dado que existe um subgrupo aberto já lotado
    E o participante é membro do grupo principal do evento
    Quando ele tenta entrar diretamente no subgrupo
    Então o sistema rejeita a entrada no subgrupo

  Cenário: Organizador promove membro a moderador do subgrupo
    Dado que existe um subgrupo com pelo menos um membro
    E o organizador do evento está autenticado
    Quando ele promove esse membro a moderador
    Então o membro passa a ser moderador do subgrupo

  Cenário: Promover não-membro a moderador é proibido
    Dado que existe um subgrupo vazio
    E o organizador do evento está autenticado
    Quando ele tenta promover um participante que não é membro
    Então o sistema rejeita a promoção do moderador
