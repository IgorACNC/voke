# language: pt
Funcionalidade: Gerenciar Notificações
  Como um organizador de eventos
  Quero enviar, editar e remover notificações para os inscritos nos meus eventos
  Para que eu possa comunicar avisos importantes de forma oficial e unilateral

  Cenário: Organizador envia notificação para inscritos em evento ativo
    Dado que o organizador está autenticado
    E o evento está ativo e possui participantes inscritos
    Quando ele cria e envia uma notificação
    Então todos os inscritos recebem a notificação

  Cenário: Tentar enviar notificação para evento cancelado
    Dado que o organizador está autenticado
    E o evento foi cancelado
    Quando ele tenta criar e enviar uma notificação
    Então o sistema rejeita o envio
    E exibe a mensagem "Não é possível enviar notificações para eventos cancelados"

  Cenário: Organizador edita notificação já enviada
    Dado que o organizador está autenticado
    E uma notificação foi enviada anteriormente
    Quando ele edita o conteúdo da notificação
    Então a notificação atualizada é reenviada para os inscritos
    E é exibida com o indicador de "nova" no sistema

  Cenário: Organizador remove notificação enviada
    Dado que o organizador está autenticado
    E uma notificação foi enviada anteriormente
    Quando ele remove a notificação
    Então a notificação é removida do sistema

  Cenário: Ex-inscrito lê notificação de evento cancelado
    Dado que o participante tinha inscrição no evento antes do cancelamento
    E o evento foi cancelado após o envio de notificações
    Quando o ex-inscrito acessa suas notificações
    Então ele consegue visualizar as notificações enviadas antes do cancelamento
