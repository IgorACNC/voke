package br.voke.infraestrutura.evento.estatistica;

import br.voke.dominio.evento.estatistica.CurvaVendasConsulta;
import br.voke.dominio.evento.estatistica.PontoCurvaVendas;
import br.voke.dominio.inscricao.inscricao.StatusInscricao;
import br.voke.infraestrutura.inscricao.inscricao.SpringInscricaoRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class CurvaVendasConsultaJpa implements CurvaVendasConsulta {

    private final SpringInscricaoRepository repo;

    public CurvaVendasConsultaJpa(SpringInscricaoRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<PontoCurvaVendas> curvaVendas(UUID eventoId) {
        List<Object[]> linhas = repo.curvaVendas(eventoId, StatusInscricao.CANCELADA);
        return linhas.stream().map(l -> {
            LocalDate data = toLocalDate(l[0]);
            long ingressos = ((Number) l[1]).longValue();
            BigDecimal receita = l[2] != null ? (BigDecimal) l[2] : BigDecimal.ZERO;
            return new PontoCurvaVendas(data, ingressos, receita);
        }).toList();
    }

    private LocalDate toLocalDate(Object o) {
        if (o instanceof LocalDate ld) return ld;
        if (o instanceof Date d) return d.toLocalDate();
        if (o instanceof java.time.LocalDateTime dt) return dt.toLocalDate();
        return LocalDate.parse(o.toString());
    }
}
