package br.voke.infraestrutura.fidelidade.pontos;

import br.voke.dominio.fidelidade.pontos.TipoTransacaoPontos;
import br.voke.dominio.fidelidade.pontos.TransacaoPontos;
import br.voke.dominio.fidelidade.pontos.TransacaoPontosId;
import br.voke.dominio.fidelidade.pontos.TransacaoPontosRepositorio;
import br.voke.infraestrutura.compartilhado.DominioReflection;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class TransacaoPontosRepositorioJpa implements TransacaoPontosRepositorio {

    private final SpringTransacaoPontosRepository repository;

    public TransacaoPontosRepositorioJpa(SpringTransacaoPontosRepository repository) {
        this.repository = repository;
    }

    @Override
    public void salvar(TransacaoPontos t) {
        repository.save(new TransacaoPontosJpa(t.getId().getValor(), t.getParticipanteId(),
                t.getTipo(), t.getPontos(), t.getDescricao(), t.getReferenciaId(), t.getDataHora()));
    }

    @Override
    public List<TransacaoPontos> buscarPorParticipanteId(UUID participanteId) {
        return repository.findByParticipanteIdOrderByDataHoraDesc(participanteId).stream()
                .map(this::paraDominio)
                .toList();
    }

    @Override
    public int somarPontosPorTipoAteData(UUID participanteId, TipoTransacaoPontos tipo, LocalDateTime ate) {
        Integer total = repository.somarPontosPorTipoAteData(participanteId, tipo, ate);
        return total != null ? total : 0;
    }

    @Override
    public int somarPontosPorTipo(UUID participanteId, TipoTransacaoPontos tipo) {
        Integer total = repository.somarPontosPorTipo(participanteId, tipo);
        return total != null ? total : 0;
    }

    private TransacaoPontos paraDominio(TransacaoPontosJpa j) {
        TransacaoPontos t = new TransacaoPontos(new TransacaoPontosId(j.getId()), j.getParticipanteId(),
                j.getTipo(), j.getPontos(), j.getDescricao(), j.getReferenciaId());
        DominioReflection.definirCampo(t, "dataHora", j.getDataHora());
        return t;
    }
}
