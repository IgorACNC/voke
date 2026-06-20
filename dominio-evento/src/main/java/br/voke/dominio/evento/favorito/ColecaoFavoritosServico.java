package br.voke.dominio.evento.favorito;

import br.voke.dominio.evento.excecao.ColecaoNaoEncontradaException;
import br.voke.dominio.evento.excecao.NomeColecaoDuplicadoException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ColecaoFavoritosServico {

    private final ColecaoFavoritosRepositorio repositorio;

    public ColecaoFavoritosServico(ColecaoFavoritosRepositorio repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio);
    }

    public ColecaoFavoritos criar(UUID participanteId, String nome, VisibilidadeColecao visibilidade) {
        if (repositorio.existePorNomeEParticipante(nome.strip(), participanteId)) {
            throw new NomeColecaoDuplicadoException(nome);
        }
        ColecaoFavoritos colecao = new ColecaoFavoritos(
                ColecaoFavoritosId.novo(), nome, visibilidade,
                LocalDateTime.now(), participanteId, List.of());
        repositorio.salvar(colecao);
        return colecao;
    }

    public ColecaoFavoritos editar(ColecaoFavoritosId id, String novoNome, VisibilidadeColecao visibilidade) {
        ColecaoFavoritos colecao = buscarOuLancar(id);
        if (!colecao.getNome().equals(novoNome.strip()) &&
                repositorio.existePorNomeEParticipante(novoNome.strip(), colecao.getParticipanteId())) {
            throw new NomeColecaoDuplicadoException(novoNome);
        }
        colecao.renomear(novoNome);
        colecao.alterarVisibilidade(visibilidade);
        repositorio.salvar(colecao);
        return colecao;
    }

    public void excluir(ColecaoFavoritosId id) {
        buscarOuLancar(id);
        repositorio.remover(id);
    }

    public List<ColecaoFavoritos> listarDoParticipante(UUID participanteId) {
        return repositorio.buscarPorParticipanteId(participanteId);
    }

    public ColecaoFavoritos buscarPorId(ColecaoFavoritosId id) {
        return buscarOuLancar(id);
    }

    public ColecaoFavoritos adicionarEvento(ColecaoFavoritosId id, UUID eventoId) {
        ColecaoFavoritos colecao = buscarOuLancar(id);
        colecao.adicionarEvento(eventoId);
        repositorio.salvar(colecao);
        return colecao;
    }

    public ColecaoFavoritos removerEvento(ColecaoFavoritosId id, UUID eventoId) {
        ColecaoFavoritos colecao = buscarOuLancar(id);
        colecao.removerEvento(eventoId);
        repositorio.salvar(colecao);
        return colecao;
    }

    public ColecaoFavoritos moverEvento(ColecaoFavoritosId origemId, ColecaoFavoritosId destinoId,
                                        UUID eventoId) {
        ColecaoFavoritos origem = buscarOuLancar(origemId);
        ColecaoFavoritos destino = buscarOuLancar(destinoId);
        origem.removerEvento(eventoId);
        destino.adicionarEvento(eventoId);
        repositorio.salvar(origem);
        repositorio.salvar(destino);
        return destino;
    }

    public ColecaoFavoritos reordenar(ColecaoFavoritosId id, UUID eventoId, int novaOrdem) {
        ColecaoFavoritos colecao = buscarOuLancar(id);
        colecao.reordenar(eventoId, novaOrdem);
        repositorio.salvar(colecao);
        return colecao;
    }

    public ColecaoFavoritos duplicar(ColecaoFavoritosId id, UUID participanteId) {
        ColecaoFavoritos original = buscarOuLancar(id);
        String nomeCopia = gerarNomeCopia(original.getNome(), participanteId);
        ColecaoFavoritos copia = new ColecaoFavoritos(
                ColecaoFavoritosId.novo(), nomeCopia, original.getVisibilidade(),
                LocalDateTime.now(), participanteId, original.getItens());
        repositorio.salvar(copia);
        return copia;
    }

    private ColecaoFavoritos buscarOuLancar(ColecaoFavoritosId id) {
        return repositorio.buscarPorId(id).orElseThrow(ColecaoNaoEncontradaException::new);
    }

    private String gerarNomeCopia(String nomeOriginal, UUID participanteId) {
        String tentativa = nomeOriginal + " (cópia)";
        if (!repositorio.existePorNomeEParticipante(tentativa, participanteId)) return tentativa;
        int i = 2;
        while (true) {
            tentativa = nomeOriginal + " (cópia " + i + ")";
            if (!repositorio.existePorNomeEParticipante(tentativa, participanteId)) return tentativa;
            i++;
        }
    }
}
