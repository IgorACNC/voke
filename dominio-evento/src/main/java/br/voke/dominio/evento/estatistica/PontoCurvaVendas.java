package br.voke.dominio.evento.estatistica;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PontoCurvaVendas(LocalDate data, long ingressos, BigDecimal receita) {}
