# language: pt
Funcionalidade: Gerenciar Carteira Virtual
  Como um participante da plataforma
  Quero gerenciar minha carteira virtual de créditos
  Para que eu possa adicionar saldo, realizar compras de ingressos e solicitar retiradas

  Cenário: Adicionar saldo dentro do limite diário
    Dado que o participante está autenticado
    E o total inserido no dia não atingiu o limite diário
    Quando ele adiciona um valor dentro do limite permitido
    Então o saldo é creditado na carteira com sucesso

  Cenário: Tentar adicionar saldo excedendo o limite diário
    Dado que o participante está autenticado
    E o total inserido no dia já atingiu o limite diário
    Quando ele tenta adicionar mais saldo
    Então o sistema rejeita a operação
    E exibe a mensagem "Limite diário de inserção atingido"

  Cenário: Remover saldo dentro do limite permitido
    Dado que o participante está autenticado
    E o saldo disponível na carteira é suficiente
    E o valor a remover está dentro do limite de remoção
    Quando ele solicita a remoção de saldo
    Então o valor é debitado da carteira com sucesso

  Cenário: Tentar remover saldo excedendo o limite de remoção
    Dado que o participante está autenticado
    Quando ele tenta remover um valor acima do limite de remoção permitido
    Então o sistema rejeita a operação
    E exibe a mensagem "Limite de remoção de saldo atingido"

  Cenário: Tentar remover saldo maior do que o disponível
    Dado que o participante está autenticado
    E o saldo disponível na carteira é insuficiente para o valor solicitado
    Quando ele tenta remover saldo
    Então o sistema rejeita a operação
    E exibe a mensagem "Saldo insuficiente"

  Cenário: Compra debita saldo acima do limite de remoção sem rejeição
    Dado que o participante possui saldo suficiente na carteira
    Quando o sistema processa o pagamento de uma compra cujo valor excede o limite individual de remoção
    Então o saldo é debitado com sucesso
    E nenhum erro de limite de remoção é lançado

  Cenário: Limite diário de inserção é zerado no início do dia seguinte
    Dado que o participante atingiu o limite diário de inserção de saldo
    Quando o sistema processa a virada do dia
    Então o contador de inserção diária é reiniciado
    E o participante pode voltar a adicionar saldo normalmente