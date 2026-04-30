# language: pt
Funcionalidade: Gerenciar Parceiros
  Como um organizador de eventos
  Quero cadastrar e gerenciar parceiros de divulgação
  Para que eu possa ampliar o alcance dos meus eventos através de afiliados comissionados

  Cenário: Cadastrar parceiro elegível com sucesso
    Dado que o organizador está autenticado
    E o usuário a ser cadastrado como parceiro participou de pelo menos 5 eventos do organizador
    Quando o organizador cadastra o parceiro e atribui atividades de divulgação
    Então o parceiro é registrado com sucesso

  Cenário: Tentar cadastrar parceiro sem histórico mínimo de eventos
    Dado que o organizador está autenticado
    E o usuário participou de menos de 5 eventos do organizador
    Quando o organizador tenta cadastrar o usuário como parceiro
    Então o sistema rejeita o cadastro
    E exibe a mensagem "O usuário não atingiu o mínimo de 5 eventos para ser parceiro"

  Cenário: Atingir limite de atividades do parceiro
    Dado que o parceiro já possui o número máximo de atividades atribuídas
    Quando o organizador tenta adicionar mais uma atividade ao parceiro
    Então o sistema rejeita a adição
    E exibe a mensagem "Limite de atividades do parceiro atingido"

  Cenário: Participante usa cupom de parceiro e parceiro recebe saldo
    Dado que o participante utilizou um cupom vinculado a um parceiro
    Quando a compra é concluída com sucesso
    Então o parceiro recebe automaticamente um valor em saldo conforme as regras

  Cenário: Organizador edita informações do parceiro
    Dado que o organizador está autenticado
    E o parceiro está cadastrado
    Quando ele edita as informações ou atividades do parceiro
    Então as alterações são salvas com sucesso

  Cenário: Organizador exclui parceiro
    Dado que o organizador está autenticado
    E o parceiro está cadastrado
    Quando ele exclui o parceiro
    Então o parceiro é removido do sistema
