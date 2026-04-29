# language: pt
Funcionalidade: Gerenciar Organizador
  Como um usuário que deseja promover eventos
  Quero criar e gerenciar minha conta de organizador
  Para que eu possa publicar eventos e receber repasses financeiros

  Cenário: Criar conta de organizador com dados válidos e idade adequada
    Dado que um usuário deseja se cadastrar como organizador
    Quando ele preenche os dados com CPF válido, e-mail único e data de nascimento comprovando 18 anos ou mais
    Então a conta de organizador é criada com sucesso

  Cenário: Criar conta de organizador com menos de 18 anos
    Dado que um usuário deseja se cadastrar como organizador
    Quando ele preenche os dados com uma data de nascimento indicando menos de 18 anos
    Então o sistema rejeita o cadastro
    E exibe a mensagem "Organizador deve ter pelo menos 18 anos"

  Cenário: Criar conta de organizador com CPF inválido
    Dado que um usuário deseja se cadastrar como organizador
    Quando ele preenche os dados com um CPF inválido
    Então o sistema rejeita o cadastro
    E exibe a mensagem "CPF inválido"

  Cenário: Criar conta de organizador com e-mail já cadastrado
    Dado que um usuário deseja se cadastrar como organizador
    Quando ele informa um e-mail que já existe no sistema
    Então o sistema rejeita o cadastro
    E exibe a mensagem "E-mail já cadastrado"

  Cenário: Editar dados da conta do organizador
    Dado que o organizador está autenticado no sistema
    Quando ele altera campos permitidos como nome ou telefone
    Então os dados são atualizados com sucesso

  Cenário: Tentar alterar data de nascimento do organizador
    Dado que o organizador está autenticado no sistema
    Quando ele tenta alterar sua data de nascimento
    Então o sistema bloqueia a alteração
    E exibe a mensagem "Data de nascimento não pode ser alterada"

  Cenário: Remover conta de organizador
    Dado que o organizador está autenticado no sistema
    Quando ele solicita a remoção da sua conta
    Então a conta é removida do sistema
    E o organizador não consegue mais fazer login
