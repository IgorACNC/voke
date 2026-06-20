package br.voke.infraestrutura.fidelidade.recompensa;

import br.voke.dominio.fidelidade.recompensa.CupomResgatado;
import br.voke.dominio.fidelidade.recompensa.CupomResgatadoRepositorio;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CupomResgatadoRepositorioJpa implements CupomResgatadoRepositorio {

    private final SpringCupomResgatadoRepository jpa;

    public CupomResgatadoRepositorioJpa(SpringCupomResgatadoRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void salvar(CupomResgatado r) {
        jpa.save(new CupomResgatadoJpa(r.id(), r.participanteId(), r.codigoCupom(),
                r.recompensaId(), r.recompensaNome(), r.valor(), r.organizadorId(),
                r.dataResgate()));
    }

    @Override
    public List<CupomResgatado> buscarPorParticipante(UUID participanteId) {
        return jpa.findByParticipanteIdOrderByDataResgateDesc(participanteId).stream()
                .map(e -> new CupomResgatado(e.getId(), e.getParticipanteId(), e.getCodigoCupom(),
                        e.getRecompensaId(), e.getRecompensaNome(), e.getValor(),
                        e.getOrganizadorId(), e.getDataResgate()))
                .toList();
    }
}
