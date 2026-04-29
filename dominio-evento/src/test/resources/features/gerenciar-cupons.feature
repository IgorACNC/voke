# language: pt
Funcionalidade: Gerenciar Cupons
  Como um organizador de eventos
  Quero criar, editar e excluir cupons de desconto
  Para que eu possa oferecer promoções e impulsionar a venda de ingressos

  Cenário: Organizador cria cupom global com quantidade máxima
    Dado que o organizador está autenticado
    Quando ele cria um cupom global com código, desconto e quantidade máxima de usos
    Então o cupom fica disponível para uso em qualquer evento do organizador

  Cenário: Organizador cria cupom específico para um evento
    Dado que o organizador está autenticado
    E o evento existe no sistema
    Quando ele cria um cupom vinculado especificamente ao evento com código, desconto e quantidade máxima
    Então o cupom fica disponível apenas para aquele evento

  Cenário: Participante utiliza cupom válido pela primeira vez com seu CPF
    Dado que o participante está no fluxo de compra
    E o cupom é válido e não foi usado pelo CPF do participante
    E a quantidade máxima de usos do cupom não foi atingida
    Quando ele aplica o cupom
    Então o desconto é aplicado com sucesso

  Cenário: Participante tenta usar o mesmo cupom duas vezes com o mesmo CPF
    Dado que o participante já utilizou o cupom anteriormente
    Quando ele tenta aplicar o mesmo cupom em uma nova compra
    Então o sistema rejeita o uso
    E exibe a mensagem "Este cupom já foi utilizado pelo seu CPF"

  Cenário: Tentar usar cupom com quantidade máxima de usos esgotada
    Dado que o cupom atingiu a quantidade máxima de usos definida
    Quando qualquer participante tenta aplicar o cupom
    Então o sistema rejeita o uso
    E exibe a mensagem "Este cupom não está mais disponível"

  Cenário: Organizador edita cupom existente
    Dado que o organizador está autenticado
    E o cupom existe no sistema
    Quando ele edita o desconto ou a quantidade máxima de usos
    Então as alterações são salvas com sucesso

  Cenário: Organizador exclui cupom
    Dado que o organizador está autenticado
    E o cupom existe no sistema
    Quando ele exclui o cupom
    Então o cupom é removido e não pode mais ser utilizado
