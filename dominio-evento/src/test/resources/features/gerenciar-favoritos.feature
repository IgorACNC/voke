# language: pt
Funcionalidade: Gerenciar Favoritos
  Como um participante da plataforma
  Quero adicionar e remover eventos da minha lista de favoritos
  Para que eu possa acompanhar eventos do meu interesse e facilitar decisões de compra futuras

  Cenário: Adicionar evento publicado aos favoritos
    Dado que o participante está autenticado
    E o evento possui status "Publicado"
    Quando ele adiciona o evento à lista de favoritos
    Então o evento aparece na lista de favoritos do participante

  Cenário: Adicionar evento ativo aos favoritos
    Dado que o participante está autenticado
    E o evento possui status "Ativo"
    Quando ele adiciona o evento à lista de favoritos
    Então o evento aparece na lista de favoritos do participante

  Cenário: Tentar adicionar evento com status inválido aos favoritos
    Dado que o participante está autenticado
    E o evento possui status diferente de "Publicado" ou "Ativo" (ex.: Cancelado, Encerrado)
    Quando ele tenta adicionar o evento à lista de favoritos
    Então o sistema rejeita a ação
    E exibe a mensagem "Este evento não pode ser adicionado aos favoritos"

  Cenário: Tentar adicionar o mesmo evento duas vezes aos favoritos
    Dado que o participante já possui o evento na sua lista de favoritos
    Quando ele tenta adicionar o mesmo evento novamente
    Então o sistema rejeita a ação
    E exibe a mensagem "Este evento já está na sua lista de favoritos"

  Cenário: Remover evento dos favoritos
    Dado que o participante possui um evento na lista de favoritos
    Quando ele remove o evento da lista
    Então o evento deixa de aparecer na lista de favoritos