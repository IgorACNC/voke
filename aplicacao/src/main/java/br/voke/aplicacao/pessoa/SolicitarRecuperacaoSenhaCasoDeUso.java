package br.voke.aplicacao.pessoa;

import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.pessoa.participante.Participante;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import br.voke.dominio.pessoa.participante.TokenRecuperacaoSenha;
import br.voke.dominio.pessoa.participante.TokenRecuperacaoSenhaRepositorio;

import java.util.Objects;
import java.util.Optional;

public class SolicitarRecuperacaoSenhaCasoDeUso {

    private final ParticipanteRepositorio participanteRepositorio;
    private final TokenRecuperacaoSenhaRepositorio tokenRepositorio;

    public SolicitarRecuperacaoSenhaCasoDeUso(ParticipanteRepositorio participanteRepositorio,
                                               TokenRecuperacaoSenhaRepositorio tokenRepositorio) {
        Objects.requireNonNull(participanteRepositorio);
        Objects.requireNonNull(tokenRepositorio);
        this.participanteRepositorio = participanteRepositorio;
        this.tokenRepositorio = tokenRepositorio;
    }

    public Optional<String> executar(String emailValor) {
        Optional<Participante> opt = participanteRepositorio.buscarPorEmail(new Email(emailValor));
        if (opt.isEmpty()) return Optional.empty();

        TokenRecuperacaoSenha token = TokenRecuperacaoSenha.gerarPara(opt.get().getId().getValor());
        tokenRepositorio.salvar(token);
        System.out.printf("[Recuperação de senha] Link para %s: /redefinir-senha?token=%s%n",
                emailValor, token.getToken());
        return Optional.of(token.getToken());
    }
}
