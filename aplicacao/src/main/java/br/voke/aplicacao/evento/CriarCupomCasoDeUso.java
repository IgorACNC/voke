package br.voke.aplicacao.evento;

import br.voke.dominio.evento.cupom.Cupom;
import br.voke.dominio.evento.cupom.CupomServico;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class CriarCupomCasoDeUso {

    private final CupomServico servico;

    public CriarCupomCasoDeUso(CupomServico servico) {
        Objects.requireNonNull(servico);
        this.servico = servico;
    }

    public Cupom executar(String codigo, BigDecimal desconto, UUID organizadorId,
                          UUID eventoId, int quantidadeMaxima) {
        return servico.criar(codigo, desconto, organizadorId, eventoId, quantidadeMaxima);
    }
}
