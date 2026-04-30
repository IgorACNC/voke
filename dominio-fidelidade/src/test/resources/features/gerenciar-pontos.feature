# language: pt
Funcionalidade: Gerenciar Pontos e Fidelidade
  Como um participante da plataforma
  Quero acumular e utilizar pontos de fidelidade
  Para que eu possa resgatar bonificações como recompensa pela minha participação ativa

  Cenário: Acumular pontos após presença confirmada em evento encerrado
    Dado que o participante compareceu ao evento
    E o evento foi encerrado
    Quando o sistema processa o encerramento
    Então os pontos referentes ao evento são creditados na conta do participante

  Cenário: Não acumular pontos sem presença confirmada
    Dado que o participante tinha inscrição no evento
    E o evento foi encerrado
    Mas a presença do participante não foi confirmada
    Quando o sistema processa o encerramento
    Então nenhum ponto é creditado ao participante

  Cenário: Resgatar bonificação com saldo de pontos suficiente
    Dado que o participante possui saldo de pontos igual ou superior ao valor da bonificação
    Quando ele seleciona e resgata a bonificação
    Então a bonificação é concedida e os pontos são debitados do saldo

  Cenário: Tentar resgatar bonificação com saldo de pontos insuficiente
    Dado que o participante possui saldo de pontos inferior ao valor da bonificação
    Quando ele tenta resgatar a bonificação
    Então o sistema rejeita o resgate
    E exibe a mensagem "Saldo de pontos insuficiente para esta bonificação"

  Cenário: Tentar comprar mais pontos com saldo real
    Dado que o participante está autenticado
    Quando ele tenta comprar pontos utilizando saldo real ou dinheiro
    Então o sistema rejeita a ação
    E exibe a mensagem "Não é permitido adquirir pontos com saldo real"

  Cenário: Pontos expiram após o prazo de validade
    Dado que o participante possui pontos com data de validade vencida
    Quando o sistema processa a expiração
    Então os pontos expirados são removidos do saldo do participante
    E o participante é notificado sobre a expiração