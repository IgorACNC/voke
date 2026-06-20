# language: pt
Funcionalidade: Gerenciar Dashboard e Estatísticas (F17)
  Como um organizador
  Quero ver as métricas dos meus eventos e exportar relatórios
  Para acompanhar vendas, receita e fluxo de caixa

  Cenário: RN01 - Organizador não pode ver estatística de evento de outro organizador
    Dado que existe um evento do organizador A
    Quando o organizador B tenta consultar o dashboard desse evento
    Então o acesso ao dashboard é negado

  Cenário: RN02 - Receita inclui inscrições confirmadas e exclui canceladas
    Dado que existe um evento com snapshot zerado
    Quando duas inscrições de R$ 100 são confirmadas
    E uma das inscrições é cancelada
    Então a receita consolidada do snapshot é R$ 100,00

  Cenário: RN03 - Snapshot é atualizado imediatamente após inscrição
    Dado que existe um evento com snapshot zerado
    Quando uma inscrição de R$ 50 é confirmada
    Então o snapshot reflete 1 ingresso vendido e receita R$ 50,00 sem precisar de SUM

  Cenário: RN04 - Lista de presença exportada mascara CPF
    Dado que existe um evento com um inscrito
    Quando o organizador exporta a lista de presença
    Então a linha do CPF está mascarada e nenhum dado bancário é incluído

  Cenário: RN05 - Snapshot congelado rejeita novas mutações após encerramento
    Dado que existe um evento ativo com snapshot
    Quando o evento é encerrado
    Então qualquer tentativa de registrar nova inscrição lança EstatisticaCongeladaException
