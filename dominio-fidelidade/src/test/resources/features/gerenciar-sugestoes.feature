# language: pt
Funcionalidade: Gerenciar Sugestões do Sistema
  Como um participante da plataforma
  Quero receber sugestões personalizadas de eventos
  Para que eu possa descobrir eventos relevantes de forma prática e direcionada

  Cenário: Sistema envia 4 sugestões de eventos na semana baseadas nos interesses do usuário
    Dado que o participante possui interesses cadastrados no sistema
    Quando o sistema processa as sugestões semanais
    Então o participante recebe exatamente 4 sugestões de eventos alinhadas ao seu perfil

  Cenário: Participante avalia sugestão positivamente
    Dado que o participante recebeu uma sugestão de evento
    Quando ele indica que gostou da sugestão
    Então o sistema registra o feedback positivo
    E utiliza essa informação para refinar sugestões futuras

  Cenário: Participante avalia sugestão negativamente e sugestões são reformuladas
    Dado que o participante recebeu uma sugestão de evento
    Quando ele indica que não gostou da sugestão
    Então o sistema registra o feedback negativo
    E reformula as próximas sugestões evitando perfil semelhante ao rejeitado

  Cenário: Administrador adiciona nova sugestão para um usuário
    Dado que o administrador está autenticado no sistema
    Quando ele adiciona uma sugestão de evento para um usuário específico
    Então a sugestão é inserida na fila de sugestões do usuário

  Cenário: Administrador edita sugestão existente
    Dado que uma sugestão foi enviada ao usuário e ainda está pendente de avaliação
    Quando o administrador edita o conteúdo da sugestão
    Então a sugestão atualizada é exibida ao usuário

  Cenário: Administrador remove sugestão existente
    Dado que uma sugestão foi enviada ao usuário e ainda está pendente de avaliação
    Quando o administrador remove a sugestão
    Então a sugestão é excluída e não aparece mais para o usuário
