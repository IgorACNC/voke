# language: pt
Funcionalidade: Gerenciar Recompensas com Pontos
  Como um organizador de eventos
  Quero criar um catálogo de recompensas resgatáveis por pontos
  Para que os participantes possam trocar seus pontos de fidelidade por benefícios exclusivos

  Cenário: Organizador cria recompensa com sucesso
    Dado que o organizador está autenticado
    Quando ele cria uma recompensa com nome, descrição e valor em pontos
    Então a recompensa fica disponível para resgate pelos participantes

  Cenário: Organizador edita recompensa sem resgate em andamento
    Dado que o organizador está autenticado
    E nenhum participante está resgatando a recompensa no momento
    E o prazo mínimo de 1 mês desde a última alteração de valor foi respeitado
    Quando ele edita o valor ou informações da recompensa
    Então as alterações são salvas com sucesso

  Cenário: Organizador tenta editar valor da recompensa antes do prazo mínimo
    Dado que o organizador alterou o valor da recompensa há menos de 1 mês
    Quando ele tenta alterar novamente o valor da recompensa
    Então o sistema rejeita a alteração
    E exibe a mensagem "O valor da recompensa só pode ser alterado após 1 mês da última modificação"

  Cenário: Resgate de recompensa é priorizado durante edição simultânea
    Dado que um participante está resgatando uma recompensa no exato momento
    E o organizador tenta editar ou remover essa recompensa simultaneamente
    Quando o sistema processa as duas operações concorrentes
    Então o resgate do participante é concluído com os valores anteriores
    E a edição ou remoção é aplicada somente após a conclusão do resgate

  Cenário: Organizador remove recompensa sem resgate em andamento
    Dado que o organizador está autenticado
    E nenhum participante está resgatando a recompensa no momento
    Quando ele remove a recompensa
    Então a recompensa é excluída e não aparece mais para os participantes
