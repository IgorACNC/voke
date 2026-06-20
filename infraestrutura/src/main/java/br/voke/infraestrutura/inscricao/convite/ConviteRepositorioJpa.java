package br.voke.infraestrutura.inscricao.convite;

import br.voke.dominio.inscricao.convite.Convite;
import br.voke.dominio.inscricao.convite.ConviteId;
import br.voke.dominio.inscricao.convite.ConviteRepositorio;
import br.voke.dominio.inscricao.convite.StatusConvite;
import org.springframework.stereotype.Repository;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ConviteRepositorioJpa implements ConviteRepositorio {

    private final SpringConviteRepository repository;

    public ConviteRepositorioJpa(SpringConviteRepository repository) {
        this.repository = repository;
    }

    @Override
    public void salvar(Convite convite) {
        repository.save(ConviteJpaMapper.paraJpa(convite));
    }

    @Override
    public Optional<Convite> buscarPorId(ConviteId id) {
        return repository.findById(id.getValor()).map(ConviteJpaMapper::paraDominio);
    }

    @Override
    public List<Convite> listarRecebidos(UUID destinatarioId) {
        return repository.findByDestinatarioIdAndStatusNot(destinatarioId, StatusConvite.CANCELADO).stream()
                .map(ConviteJpaMapper::paraDominio)
                .toList();
    }

    @Override
    public List<Convite> listarEnviados(UUID remetenteId) {
        return repository.findByRemetenteId(remetenteId).stream()
                .map(ConviteJpaMapper::paraDominio)
                .toList();
    }

    @Override
    public Optional<Convite> buscarPendenteOuRejeitadoPorRemetenteEventoDestinatario(
            UUID remetenteId, UUID eventoId, UUID destinatarioId) {
        return repository.findByRemetenteIdAndEventoIdAndDestinatarioIdAndStatusIn(
                remetenteId, eventoId, destinatarioId,
                EnumSet.of(StatusConvite.PENDENTE, StatusConvite.REJEITADO))
                .map(ConviteJpaMapper::paraDominio);
    }
}
