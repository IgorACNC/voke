# language: pt
Funcionalidade: Gerenciar Avaliação
  Como um participante que compareceu a um evento
  Quero avaliar o evento com nota e comentário
  Para que outros usuários possam tomar decisões informadas e o organizador receba feedback

  Cenário: Avaliar evento finalizado com inscrição confirmada
    Dado que o participante possui inscrição confirmada
    E o evento foi finalizado
    Quando ele submete uma avaliação com nota e comentário
    Então a avaliação é registrada com sucesso

  Cenário: Tentar avaliar evento que ainda não foi finalizado
    Dado que o participante possui inscrição confirmada
    E o evento ainda está em andamento ou ativo
    Quando ele tenta submeter uma avaliação
    Então o sistema rejeita a avaliação
    E exibe a mensagem "Só é possível avaliar após o encerramento do evento"

  Cenário: Tentar avaliar um evento sem inscrição confirmada
    Dado que o participante não possui inscrição confirmada no evento
    E o evento foi finalizado
    Quando ele tenta submeter uma avaliação
    Então o sistema rejeita a avaliação
    E exibe a mensagem "Apenas participantes com inscrição confirmada podem avaliar"

  Cenário: Tentar avaliar o mesmo evento duas vezes
    Dado que o participante já submeteu uma avaliação para o evento
    Quando ele tenta submeter uma nova avaliação para o mesmo evento
    Então o sistema rejeita a segunda avaliação
    E exibe a mensagem "Você já avaliou este evento"

  Cenário: Editar avaliação existente
    Dado que o participante já submeteu uma avaliação
    Quando ele edita a nota ou o comentário
    Então a avaliação é atualizada com sucesso

  Cenário: Excluir avaliação existente
    Dado que o participante já submeteu uma avaliação
    Quando ele solicita a exclusão da avaliação
    Então a avaliação é removida do sistema
