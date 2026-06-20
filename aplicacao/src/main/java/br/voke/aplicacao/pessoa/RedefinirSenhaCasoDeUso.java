package br.voke.aplicacao.pessoa;

import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteId;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import br.voke.dominio.pessoa.participante.TokenRecuperacaoSenha;
import br.voke.dominio.pessoa.participante.TokenRecuperacaoSenhaRepositorio;

import java.util.Objects;

public class RedefinirSenhaCasoDeUso {

    private final ParticipanteRepositorio participanteRepositorio;
    private final TokenRecuperacaoSenhaRepositorio tokenRepositorio;

    public RedefinirSenhaCasoDeUso(ParticipanteRepositorio participanteRepositorio,
                                    TokenRecuperacaoSenhaRepositorio tokenRepositorio) {
        Objects.requireNonNull(participanteRepositorio);
        Objects.requireNonNull(tokenRepositorio);
        this.participanteRepositorio = participanteRepositorio;
        this.tokenRepositorio = tokenRepositorio;
    }

    public void executar(String tokenValor, String novaSenhaHash) {
        TokenRecuperacaoSenha token = tokenRepositorio.buscarPorToken(tokenValor)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));
        if (!token.estaValido()) {
            throw new IllegalArgumentException("Token expirado ou já utilizado");
        }
        Participante p = participanteRepositorio.buscarPorId(new ParticipanteId(token.getParticipanteId()))
                .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado"));
        p.atualizarSenha(new Senha(novaSenhaHash));
        participanteRepositorio.salvar(p);
        token.marcarComoUsado();
        tokenRepositorio.salvar(token);
    }
}
