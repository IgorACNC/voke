# language: pt
Funcionalidade: Gerenciar Participante
  Como um usuário da plataforma
  Quero criar e gerenciar minha conta de participante
  Para que eu possa acessar eventos e funcionalidades do sistema

  Cenário: Criar conta com dados válidos
    Dado que um usuário não possui conta no sistema
    Quando ele preenche nome, CPF válido, e-mail, data de nascimento e demais dados obrigatórios
    E possui a idade mínima exigida
    Então a conta é criada com sucesso
    E o participante recebe uma confirmação de cadastro

  Cenário: Criar conta com CPF inválido
    Dado que um usuário não possui conta no sistema
    Quando ele preenche os dados com um CPF inválido
    Então o sistema rejeita o cadastro
    E exibe a mensagem "CPF inválido"

  Cenário: Criar conta com e-mail já cadastrado
    Dado que um usuário não possui conta no sistema
    Quando ele preenche os dados com um e-mail já utilizado por outra conta
    Então o sistema rejeita o cadastro
    E exibe a mensagem "E-mail já cadastrado"

  Cenário: Criar conta abaixo da idade mínima
    Dado que um usuário não possui conta no sistema
    Quando ele preenche os dados com uma data de nascimento que não atinge a idade mínima
    Então o sistema rejeita o cadastro
    E exibe a mensagem "Idade mínima não atingida"

  Cenário: Editar dados da conta com sucesso
    Dado que o participante está autenticado no sistema
    Quando ele altera campos permitidos como nome ou e-mail
    Então os dados são atualizados com sucesso
    E o sistema exibe a mensagem "Dados atualizados com sucesso"

  Cenário: Tentar alterar data de nascimento
    Dado que o participante está autenticado no sistema
    Quando ele tenta alterar sua data de nascimento
    Então o sistema bloqueia a alteração
    E exibe a mensagem "Data de nascimento não pode ser alterada"

  Cenário: Remover conta com sucesso
    Dado que o participante está autenticado no sistema
    Quando ele solicita a remoção da sua conta
    Então a conta é removida do sistema
    E o participante não consegue mais fazer login
