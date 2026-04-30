# language: pt
Funcionalidade: Gerenciar Amigos e Comunidades de Amigos
  Como um participante da plataforma
  Quero formar redes de contatos e comunidades
  Para que eu possa compartilhar intenções de participação em eventos com amigos

  Cenário: Enviar solicitação de amizade
    Dado que o participante está autenticado e possui 16 anos ou mais
    Quando ele envia uma solicitação de amizade para outro participante
    Então a solicitação fica pendente até ser aceita ou recusada

  Cenário: Aceitar solicitação de amizade
    Dado que o participante recebeu uma solicitação de amizade
    Quando ele aceita a solicitação
    Então os dois participantes são vinculados como amigos no sistema

  Cenário: Recusar solicitação de amizade
    Dado que o participante recebeu uma solicitação de amizade
    Quando ele recusa a solicitação
    Então a solicitação é descartada e nenhum vínculo é criado

  Cenário: Menor de 16 anos tenta adicionar amigo
    Dado que o participante possui menos de 16 anos
    Quando ele tenta enviar uma solicitação de amizade
    Então o sistema rejeita a ação
    E exibe a mensagem "Funcionalidade disponível apenas para maiores de 16 anos"

  Cenário: Criar grupo de amigos após amizade confirmada
    Dado que o participante possui pelo menos um amigo confirmado
    Quando ele cria um grupo de amigos e compartilha um evento futuro
    Então o grupo é criado e os amigos visualizam o evento compartilhado

  Cenário: Tentar criar grupo sem amizade confirmada
    Dado que o participante não possui amizades confirmadas
    Quando ele tenta criar um grupo de amigos
    Então o sistema rejeita a ação
    E exibe a mensagem "Confirme uma amizade antes de criar um grupo"

  Cenário: Amigo se inscreve em evento compartilhado com vaga disponível
    Dado que um participante compartilhou um evento com seu grupo de amigos
    E o evento ainda possui vagas disponíveis
    Quando um amigo decide se inscrever no evento pelo grupo
    Então ele é direcionado para o fluxo de inscrição do evento

  Cenário: Amigo tenta se inscrever em evento sem vagas disponíveis
    Dado que um participante compartilhou um evento com seu grupo de amigos
    E o evento não possui mais vagas disponíveis
    Quando um amigo tenta se inscrever
    Então o sistema informa que não há vagas disponíveis

  Cenário: Remover amigo da lista
    Dado que o participante possui amigos confirmados
    Quando ele remove um amigo da sua lista
    Então o vínculo de amizade é desfeito para ambos os lados
