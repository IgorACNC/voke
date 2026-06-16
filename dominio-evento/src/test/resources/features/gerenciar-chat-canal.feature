# language: pt
Funcionalidade: Gerenciar Chat em Canais de Grupo e Subgrupo
  Como um participante ou organizador de um evento
  Quero enviar e receber mensagens nos canais de grupo e subgrupo
  Para me coordenar com os demais membros antes e durante o evento

  Cenário: Membro do grupo envia mensagem com sucesso
    Dado que o participante é membro do grupo de evento
    Quando ele envia uma mensagem no chat do grupo
    Então a mensagem é salva com sucesso

  Cenário: Organizador do evento envia mensagem no grupo sem ser membro
    Dado que o organizador criou o grupo mas não está em membrosIds
    Quando o organizador envia uma mensagem no chat do grupo
    Então a mensagem é salva com sucesso

  Cenário: Não-membro tenta enviar mensagem no grupo
    Dado que o participante não é membro do grupo e não é o organizador
    Quando ele tenta enviar uma mensagem no chat do grupo
    Então o acesso ao chat é negado

  Cenário: Conteúdo vazio é rejeitado
    Dado que o participante é membro do grupo de evento
    Quando ele tenta enviar uma mensagem vazia
    Então a mensagem é rejeitada por conteúdo inválido

  Cenário: Conteúdo que excede 1000 caracteres é rejeitado
    Dado que o participante é membro do grupo de evento
    Quando ele tenta enviar uma mensagem com mais de 1000 caracteres
    Então a mensagem é rejeitada por conteúdo inválido

  Cenário: Listagem retorna mensagens em ordem cronológica
    Dado que o grupo possui mensagens enviadas
    Quando um membro lista as mensagens do canal
    Então as mensagens são retornadas em ordem cronológica crescente

  Cenário: Membro do subgrupo envia mensagem com sucesso
    Dado que o participante é membro de um subgrupo
    Quando ele envia uma mensagem no chat do subgrupo
    Então a mensagem é salva com sucesso no canal do subgrupo
