# language: pt
Funcionalidade: Gerenciar Grupos de Evento
  Como um organizador de eventos
  Quero criar e moderar grupos de comunicação vinculados aos meus eventos
  Para que os participantes confirmados possam interagir e se engajar antes do evento

  Cenário: Organizador cria grupo de evento com sucesso
    Dado que o organizador está autenticado
    E o evento está ativo
    Quando ele cria um grupo para o evento com nome e regras definidas
    Então o grupo é criado com sucesso e vinculado ao evento

  Cenário: Participante inscrito acessa o grupo do evento
    Dado que o participante possui inscrição confirmada no evento
    E o evento possui um grupo ativo
    Quando ele acessa o grupo
    Então o acesso é concedido e ele pode visualizar e postar conteúdos

  Cenário: Participante não inscrito tenta acessar o grupo
    Dado que o participante não possui inscrição confirmada no evento
    Quando ele tenta acessar o grupo do evento
    Então o acesso é negado
    E exibe a mensagem "Inscrição necessária para acessar este grupo"

  Cenário: Menor de idade tenta participar do grupo
    Dado que o participante possui menos de 18 anos
    E o evento possui um grupo ativo
    Quando ele tenta acessar o grupo
    Então o acesso é negado
    E exibe a mensagem "Menores de idade não podem participar do grupo"

  Cenário: Organizador edita regras do grupo
    Dado que o organizador está autenticado
    E o grupo do evento existe
    Quando ele edita as regras do grupo
    Então as novas regras são salvas e exibidas no grupo

  Cenário: Grupo é encerrado automaticamente após o evento
    Dado que o evento foi encerrado
    Quando o sistema processa o encerramento do evento
    Então o grupo vinculado é removido automaticamente

  Cenário: Organizador deleta o grupo manualmente
    Dado que o organizador está autenticado
    E o grupo do evento existe
    Quando ele exclui o grupo
    Então o grupo é removido e os participantes perdem o acesso
