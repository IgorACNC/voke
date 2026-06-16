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

  # --- Notificações Agendadas ---

  Cenário: Organizador agenda notificação para data futura
    Dado que o organizador está autenticado
    E o evento está ativo e possui participantes inscritos
    Quando ele cria uma notificação agendada para uma data futura
    Então a notificação fica com status "AGENDADA"
    E não é enviada imediatamente aos inscritos

  Cenário: Sistema processa notificação agendada na data programada
    Dado que existe uma notificação agendada cuja data de envio já chegou
    Quando o sistema processa as notificações agendadas
    Então a notificação é enviada para todos os destinatários elegíveis
    E o status muda para "ENVIADA"

  Cenário: Organizador cancela notificação agendada antes do envio
    Dado que existe uma notificação agendada para uma data futura
    Quando o organizador cancela a notificação agendada
    Então o status muda para "CANCELADA"
    E os inscritos não recebem a notificação

  Cenário: Tentar agendar notificação para data no passado
    Dado que o organizador está autenticado
    E o evento está ativo e possui participantes inscritos
    Quando ele tenta agendar uma notificação para uma data que já passou
    Então o sistema rejeita o agendamento
    E exibe a mensagem "A data de agendamento deve ser no futuro"

  # --- Limite de Edições ---

  Cenário: Organizador edita notificação dentro do limite permitido
    Dado que o organizador está autenticado
    E uma notificação foi enviada com 0 edições realizadas
    Quando ele edita o conteúdo da notificação
    Então a edição é aplicada com sucesso
    E o contador de edições é incrementado para 1

  Cenário: Organizador tenta editar notificação que atingiu o limite de edições
    Dado que o organizador está autenticado
    E uma notificação já foi editada 3 vezes
    Quando ele tenta editar o conteúdo novamente
    Então o sistema rejeita a edição
    E exibe a mensagem "Limite de edições atingido para esta notificação"

  # --- Notificação Segmentada ---

  Cenário: Organizador envia notificação segmentada para grupo do evento
    Dado que o evento possui o grupo "VIP" com participantes inscritos
    Quando o organizador envia uma notificação segmentada para o grupo "VIP"
    Então apenas os membros do grupo "VIP" que possuem inscrição ativa recebem a notificação

  Cenário: Organizador envia notificação segmentada por lote
    Dado que o evento possui inscritos em diferentes lotes
    Quando o organizador envia uma notificação segmentada para inscritos do lote 3
    Então apenas os inscritos do lote 3 recebem a notificação

  Cenário: Segmentação resulta em zero destinatários
    Dado que o organizador segmenta por um grupo sem membros com inscrição ativa
    Quando ele tenta enviar a notificação segmentada
    Então o sistema rejeita o envio
    E exibe a mensagem "Nenhum destinatário elegível para o critério selecionado"
