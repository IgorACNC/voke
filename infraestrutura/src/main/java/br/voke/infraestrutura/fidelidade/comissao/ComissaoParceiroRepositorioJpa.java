package br.voke.infraestrutura.fidelidade.comissao;

import br.voke.dominio.fidelidade.comissao.ComissaoParceiro;
import br.voke.dominio.fidelidade.comissao.ComissaoParceiroRepositorio;
import br.voke.dominio.fidelidade.comissao.StatusComissao;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import java.util.stream.Collectors;

@Repository
public class ComissaoParceiroRepositorioJpa implements ComissaoParceiroRepositorio {

    private final SpringComissaoParceiroRepository repository;

    public ComissaoParceiroRepositorioJpa(SpringComissaoParceiroRepository repository) {
        Objects.requireNonNull(repository);
        this.repository = repository;
    }

    @Override
    public void salvar(ComissaoParceiro comissao) {
        repository.save(ComissaoParceiroJpaMapper.paraJpa(comissao));
    }

    @Override
    public List<ComissaoParceiro> buscarPorParceiroId(UUID parceiroId) {
        return repository.findByParceiroId(parceiroId).stream()
                .map(ComissaoParceiroJpaMapper::paraDominio)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ComissaoParceiro> buscarPorInscricaoId(UUID inscricaoId) {
        return repository.findByInscricaoId(inscricaoId)
                .map(ComissaoParceiroJpaMapper::paraDominio);
    }

    @Override
    public BigDecimal calcularSaldoParceiro(UUID parceiroId) {
        BigDecimal creditado = repository.sumByParceiroIdAndStatus(parceiroId, StatusComissao.CREDITADA);
        return creditado != null ? creditado : BigDecimal.ZERO;
    }
}
