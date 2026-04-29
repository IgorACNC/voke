# language: pt
Funcionalidade: Gerenciar Carrinho
  Como um participante da plataforma
  Quero adicionar, editar e remover ingressos no meu carrinho de compras
  Para que eu possa agrupar ingressos e finalizar o pagamento de forma consolidada

  Cenário: Adicionar evento ao carrinho com sucesso
    Dado que o participante está autenticado
    E o carrinho possui menos de 2 eventos
    Quando ele adiciona um ingresso de evento ao carrinho
    Então o ingresso é adicionado com sucesso

  Cenário: Tentar adicionar terceiro evento ao carrinho antes de finalizar compra
    Dado que o participante já possui 2 eventos diferentes no carrinho
    Quando ele tenta adicionar um terceiro evento
    Então o sistema rejeita a adição
    E exibe a mensagem "Limite de 2 eventos por carrinho atingido. Finalize a compra atual para continuar"

  Cenário: Realizar pagamento via PIX sem taxa
    Dado que o participante possui itens no carrinho
    Quando ele escolhe PIX como forma de pagamento e finaliza a compra
    Então o valor cobrado é exatamente o valor dos ingressos sem acréscimos

  Cenário: Realizar pagamento via cartão de crédito com taxa
    Dado que o participante possui itens no carrinho
    Quando ele escolhe cartão de crédito como forma de pagamento
    Então o sistema aplica a taxa correspondente ao valor total
    E exibe o valor final com a taxa antes da confirmação

  Cenário: Aplicar cupom válido no carrinho
    Dado que o participante possui itens no carrinho
    E o cupom é válido e não foi utilizado pelo CPF do participante
    Quando ele aplica o cupom no carrinho
    Então o desconto do cupom é refletido no valor total

  Cenário: Tentar aplicar dois cupons no mesmo carrinho
    Dado que o participante já aplicou um cupom no carrinho
    Quando ele tenta aplicar um segundo cupom
    Então o sistema rejeita a ação
    E exibe a mensagem "Apenas um cupom pode ser utilizado por compra"

  Cenário: Tentar aplicar cupom inválido
    Dado que o participante possui itens no carrinho
    Quando ele tenta aplicar um cupom expirado ou inexistente
    Então o sistema rejeita o cupom
    E exibe a mensagem "Cupom inválido ou expirado"

  Cenário: Remover item do carrinho
    Dado que o participante possui itens no carrinho
    Quando ele remove um item
    Então o item é removido e o valor total é recalculado

  Cenário: Carrinho liberado para mais eventos após compra concluída
    Dado que o participante finalizou a compra com 2 eventos no carrinho
    Quando a compra é confirmada
    Então o carrinho é esvaziado e o participante pode adicionar novos eventos