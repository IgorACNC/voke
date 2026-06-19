package br.voke.infraestrutura.fidelidade.sugestao;

import br.voke.dominio.evento.favorito.Favorito;
import br.voke.dominio.evento.favorito.FavoritoRepositorio;
import br.voke.dominio.fidelidade.sugestao.FavoritoConsultaGateway;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class FavoritoConsultaGatewayAdapter implements FavoritoConsultaGateway {

    private final FavoritoRepositorio favoritoRepositorio;

    public FavoritoConsultaGatewayAdapter(FavoritoRepositorio favoritoRepositorio) {
        this.favoritoRepositorio = favoritoRepositorio;
    }

    @Override
    public Set<UUID> buscarFavoritosDoParticipante(UUID participanteId) {
        return favoritoRepositorio.buscarPorParticipanteId(participanteId).stream()
                .map(Favorito::getEventoId)
                .collect(Collectors.toSet());
    }
}
