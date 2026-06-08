package br.voke.aplicacao.pessoa;

import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;

import java.util.Objects;
import java.util.UUID;

public class AlterarSenhaCasoDeUso {

    private final ParticipanteRepositorio repositorio;

    public AlterarSenhaCasoDeUso(ParticipanteRepositorio repositorio) {
        Objects.requireNonNull(repositorio);
        this.repositorio = repositorio;
    }

    public void executar(UUID participanteId, String novaSenhaHash) {
        Participante p = repositorio.buscarPorId(new ParticipanteId(participanteId))
                .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado"));
        p.atualizarSenha(new Senha(novaSenhaHash));
        repositorio.salvar(p);
    }
}
