# language: pt
Funcionalidade: Gerenciar Inscrição
  Como um participante da plataforma
  Quero realizar e cancelar inscrições em eventos
  Para que eu possa garantir minha vaga e gerenciar minha participação nos eventos

  Cenário: Realizar inscrição com sucesso
    Dado que o participante está autenticado
    E o evento está ativo e possui vagas disponíveis
    E o participante atinge a idade mínima do evento
    E não possui outra inscrição no mesmo horário e dia
    Quando ele realiza a inscrição no evento
    Então a inscrição é confirmada com sucesso

  Cenário: Tentar se inscrever abaixo da idade mínima
    Dado que o participante está autenticado
    E o evento exige uma idade mínima maior do que a idade do participante
    Quando ele tenta realizar a inscrição
    Então o sistema rejeita a inscrição
    E exibe a mensagem "Idade mínima não atingida para este evento"

  Cenário: Tentar se inscrever em dois eventos no mesmo horário e dia
    Dado que o participante já possui inscrição confirmada em um evento no dia X às 14h
    Quando ele tenta se inscrever em outro evento no mesmo dia X às 14h
    Então o sistema rejeita a inscrição
    E exibe a mensagem "Você já possui uma inscrição neste horário"

  Cenário: Realizar inscrição na última vaga disponível
    Dado que o evento possui apenas 1 vaga disponível
    E dois participantes tentam se inscrever simultaneamente
    Quando o primeiro participante confirma a inscrição
    Então a inscrição do primeiro é confirmada
    E o sistema informa ao segundo que não há mais vagas

  Cenário: Exceder o número máximo de ingressos por CPF
    Dado que o participante já atingiu o limite máximo de ingressos permitidos por CPF para o evento
    Quando ele tenta realizar mais uma inscrição
    Então o sistema rejeita a inscrição
    E exibe a mensagem "Limite máximo de ingressos por CPF atingido"

  Cenário: Cancelar inscrição com devolução integral
    Dado que o participante está autenticado e possui inscrição confirmada
    E o cancelamento ocorre dentro do prazo para devolução integral
    Quando ele cancela a inscrição
    Então a inscrição é cancelada
    E o valor total é devolvido ao participante

  Cenário: Cancelar inscrição com devolução parcial
    Dado que o participante está autenticado e possui inscrição confirmada
    E o cancelamento ocorre em prazo com devolução parcial conforme política vigente
    Quando ele cancela a inscrição
    Então a inscrição é cancelada
    E apenas o valor proporcional é devolvido ao participante

  Cenário: Cancelar inscrição sem devolução
    Dado que o participante está autenticado e possui inscrição confirmada
    E o cancelamento ocorre fora do prazo de devolução
    Quando ele cancela a inscrição
    Então a inscrição é cancelada
    E nenhum valor é devolvido ao participante
