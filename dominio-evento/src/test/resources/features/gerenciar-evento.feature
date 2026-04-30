# language: pt
Funcionalidade: Gerenciar Evento
  Como um organizador de eventos
  Quero criar, editar e cancelar eventos na plataforma
  Para que eu possa gerenciar o ciclo de vida completo dos meus eventos

  Cenário: Criar evento com dados válidos
    Dado que o organizador está autenticado
    Quando ele preenche nome, local, data, horário, número de vagas, preço e cria um lote
    E não existe outro evento no mesmo local, data e horário
    E não existe outro evento com o mesmo nome
    Então o evento é criado com sucesso

  Cenário: Criar evento com nome duplicado
    Dado que o organizador está autenticado
    Quando ele tenta criar um evento com um nome já existente no sistema
    Então o sistema rejeita a criação
    E exibe a mensagem "Já existe um evento com este nome"

  Cenário: Criar evento com conflito de local, data e horário
    Dado que o organizador está autenticado
    Quando ele tenta criar um evento em um local, data e horário já ocupados por outro evento
    Então o sistema rejeita a criação
    E exibe a mensagem "Já existe um evento neste local, data e horário"

  Cenário: Criar segundo lote enquanto há lote ativo
    Dado que o organizador está autenticado
    E o evento já possui um lote ativo
    Quando ele tenta criar um novo lote para o mesmo evento
    Então o sistema rejeita a criação do lote
    E exibe a mensagem "Só é permitido um lote ativo por vez"

  Cenário: Editar informações do evento com sucesso
    Dado que o organizador está autenticado
    E o evento existe no sistema
    Quando ele edita campos como local, horário ou número de vagas
    Então as alterações são salvas com sucesso

  Cenário: Cancelar evento com cancelamento em cascata
    Dado que o organizador está autenticado
    E o evento possui inscrições e lotes associados
    Quando ele remove o evento
    Então o evento é cancelado
    E todas as inscrições e lotes vinculados são cancelados automaticamente
