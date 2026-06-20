package br.voke.dominio.fidelidade.recompensa;

import java.util.List;
import java.util.UUID;

public interface CupomResgatadoRepositorio {
    void salvar(CupomResgatado registro);
    List<CupomResgatado> buscarPorParticipante(UUID participanteId);
}
