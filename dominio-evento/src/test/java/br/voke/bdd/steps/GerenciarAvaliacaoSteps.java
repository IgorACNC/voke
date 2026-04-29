package br.voke.bdd.steps;

import br.voke.dominio.evento.avaliacao.Avaliacao;
import br.voke.dominio.evento.avaliacao.AvaliacaoId;
import br.voke.dominio.evento.avaliacao.AvaliacaoRepositorio;
import br.voke.dominio.evento.avaliacao.AvaliacaoServico;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GerenciarAvaliacaoSteps {

    private final ContextoEvento contexto;
    private final Map<AvaliacaoId, Avaliacao> banco = new HashMap<>();
    private AvaliacaoRepositorio repositorio;
    private AvaliacaoServico servico;
    private Avaliacao avaliacao;
    private boolean eventoFinalizado;
    private boolean inscricaoConfirmada;

    public GerenciarAvaliacaoSteps(ContextoEvento contexto) {
        this.contexto = contexto;
    }

    private AvaliacaoRepositorio criarRepositorioEmMemoria() {
        return new AvaliacaoRepositorio() {
            @Override public void salvar(Avaliacao avaliacao) { banco.put(avaliacao.getId(), avaliacao); }
            @Override public Optional<Avaliacao> buscarPorId(AvaliacaoId id) { return Optional.ofNullable(banco.get(id)); }
            @Override public Optional<Avaliacao> buscarPorParticipanteEEvento(UUID participanteId, UUID eventoId) {
                return banco.values().stream()
                        .filter(avaliacao -> avaliacao.getParticipanteId().equals(participanteId)
                                && avaliacao.getEventoId().equals(eventoId))
                        .findFirst();
            }
            @Override public void remover(AvaliacaoId id) { banco.remove(id); }
            @Override public boolean existePorParticipanteEEvento(UUID participanteId, UUID eventoId) {
                return buscarPorParticipanteEEvento(participanteId, eventoId).isPresent();
            }
        };
    }

    @Dado("que o participante possui inscrição confirmada")
    public void participanteComInscricaoConfirmada() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AvaliacaoServico(repositorio);
        contexto.excecao = null;
        avaliacao = null;
        inscricaoConfirmada = true;
        eventoFinalizado = false;
    }

    @E("o evento foi finalizado")
    public void oEventoFoiFinalizado() {
        eventoFinalizado = true;
    }

    @Quando("ele submete uma avaliação com nota e comentário")
    public void eleSubmeteAvaliacao() {
        try {
            avaliacao = servico.avaliar(UUID.randomUUID(), UUID.randomUUID(), 5,
                    "Excelente evento!", eventoFinalizado, inscricaoConfirmada);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("a avaliação é registrada com sucesso")
    public void aAvaliacaoERegistrada() {
        assertNull(contexto.excecao);
        assertNotNull(avaliacao);
    }

    @E("o evento ainda está em andamento ou ativo")
    public void oEventoEmAndamento() {
        eventoFinalizado = false;
    }

    @Quando("ele tenta submeter uma avaliação")
    public void eleTentaSubmeterAvaliacao() {
        try {
            avaliacao = servico.avaliar(UUID.randomUUID(), UUID.randomUUID(), 4,
                    "Bom", eventoFinalizado, inscricaoConfirmada);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o sistema rejeita a avaliação")
    public void oSistemaRejeitaAAvaliacao() {
        assertNotNull(contexto.excecao);
    }

    @Dado("que o participante não possui inscrição confirmada no evento")
    public void participanteSemInscricao() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AvaliacaoServico(repositorio);
        contexto.excecao = null;
        avaliacao = null;
        inscricaoConfirmada = false;
        eventoFinalizado = false;
    }

    @Dado("que o participante já submeteu uma avaliação para o evento")
    public void participanteJaSubmeteuAvaliacao() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AvaliacaoServico(repositorio);
        contexto.excecao = null;
        UUID participanteId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();
        avaliacao = servico.avaliar(participanteId, eventoId, 4, "Legal", true, true);
    }

    @Quando("ele tenta submeter uma nova avaliação para o mesmo evento")
    public void eleTentaSubmeterNovaAvaliacao() {
        try {
            servico.avaliar(avaliacao.getParticipanteId(), avaliacao.getEventoId(), 5, "Ótimo", true, true);
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("o sistema rejeita a segunda avaliação")
    public void oSistemaRejeitaSegundaAvaliacao() {
        assertNotNull(contexto.excecao);
    }

    @Dado("que o participante já submeteu uma avaliação")
    public void participanteJaSubmeteu() {
        banco.clear();
        repositorio = criarRepositorioEmMemoria();
        servico = new AvaliacaoServico(repositorio);
        contexto.excecao = null;
        avaliacao = servico.avaliar(UUID.randomUUID(), UUID.randomUUID(), 3, "OK", true, true);
    }

    @Quando("ele edita a nota ou o comentário")
    public void eleEditaNotaOuComentario() {
        try {
            servico.editar(avaliacao.getId(), 5, "Atualizei minha opinião - foi ótimo!");
            avaliacao = repositorio.buscarPorId(avaliacao.getId()).orElseThrow();
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("a avaliação é atualizada com sucesso")
    public void aAvaliacaoEAtualizada() {
        assertNull(contexto.excecao);
        assertEquals(5, avaliacao.getNota());
    }

    @Quando("ele solicita a exclusão da avaliação")
    public void eleSolicitaExclusaoDaAvaliacao() {
        try {
            servico.remover(avaliacao.getId());
        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Então("a avaliação é removida do sistema")
    public void aAvaliacaoERemovidaDoSistema() {
        assertNull(contexto.excecao);
        assertFalse(repositorio.buscarPorId(avaliacao.getId()).isPresent());
    }
}
