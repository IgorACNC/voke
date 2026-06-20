package br.voke.dominio.fidelidade.sugestao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class MotorSugestoes {

    private static final int BONUS_CATEGORIA_HISTORICO = 5;
    private static final int BONUS_CATEGORIA_FAVORITO = 5;

    private final EventoConsultaGateway eventoGateway;
    private final InscricaoConsultaGateway inscricaoGateway;
    private final FavoritoConsultaGateway favoritoGateway;

    public MotorSugestoes(EventoConsultaGateway eventoGateway,
                          InscricaoConsultaGateway inscricaoGateway,
                          FavoritoConsultaGateway favoritoGateway) {
        Objects.requireNonNull(eventoGateway, "EventoConsultaGateway é obrigatório");
        Objects.requireNonNull(inscricaoGateway, "InscricaoConsultaGateway é obrigatório");
        Objects.requireNonNull(favoritoGateway, "FavoritoConsultaGateway é obrigatório");
        this.eventoGateway = eventoGateway;
        this.inscricaoGateway = inscricaoGateway;
        this.favoritoGateway = favoritoGateway;
    }

    public List<UUID> recomendar(PreferenciaParticipante preferencia, int limite) {
        return recomendar(preferencia, limite, Set.of());
    }

    public List<UUID> recomendar(PreferenciaParticipante preferencia, int limite, Set<UUID> eventosExcluidos) {
        Objects.requireNonNull(preferencia, "Preferência é obrigatória");
        Objects.requireNonNull(eventosExcluidos, "Conjunto de eventos excluídos é obrigatório");
        if (preferencia.getCategoriaIds().isEmpty()) {
            return List.of();
        }
        UUID participanteId = preferencia.getParticipanteId();

        // RN1: enriquece as categorias preferidas com sinais derivados do histórico
        // de inscrições e da lista de favoritos do participante.
        Set<UUID> categoriasHistorico = extrairCategoriasDeEventos(
                inscricaoGateway.buscarHistoricoEventosDoParticipante(participanteId));
        Set<UUID> categoriasFavoritos = extrairCategoriasDeEventos(
                favoritoGateway.buscarFavoritosDoParticipante(participanteId));

        // Conjunto de busca: categorias preferidas + derivadas (amplia o pool de candidatos)
        Set<UUID> categoriasParaBusca = new HashSet<>(preferencia.getCategoriaIds());
        categoriasParaBusca.addAll(categoriasHistorico);
        categoriasParaBusca.addAll(categoriasFavoritos);

        List<EventoConsultaGateway.EventoCandidato> validos = eventoGateway
                .buscarEventosCandidatosPorCategorias(categoriasParaBusca).stream()
                .filter(c -> !eventosExcluidos.contains(c.eventoId()))
                .filter(c -> eventoGateway.eventoEstaDisponivel(c.eventoId()))
                .filter(c -> !inscricaoGateway.participanteJaInscritoOuAguardando(participanteId, c.eventoId()))
                .filter(c -> !preferencia.deveDescartar(c.categoriaIds()))
                .toList();

        if (validos.isEmpty()) return List.of();

        // Agrupa por categoria preferida e ordena cada balde pelo score total
        // (que combina preferências, histórico e favoritos).
        List<UUID> categoriasPreferidas = new ArrayList<>(preferencia.getCategoriaIds());
        Map<UUID, List<EventoConsultaGateway.EventoCandidato>> baldes = new HashMap<>();
        for (UUID categoria : categoriasPreferidas) {
            List<EventoConsultaGateway.EventoCandidato> balde = new ArrayList<>();
            for (EventoConsultaGateway.EventoCandidato c : validos) {
                if (c.categoriaIds().contains(categoria)) balde.add(c);
            }
            balde.sort(Comparator.comparingInt(
                    (EventoConsultaGateway.EventoCandidato c) ->
                            calcularScoreCompleto(preferencia, c.categoriaIds(),
                                    categoriasHistorico, categoriasFavoritos)).reversed());
            baldes.put(categoria, balde);
        }

        // Round-robin entre baldes (uma sugestão de cada categoria preferida por rodada).
        LinkedHashSet<UUID> resultado = new LinkedHashSet<>();
        int[] cursores = new int[categoriasPreferidas.size()];
        boolean adicionouNestaRodada = true;
        while (resultado.size() < limite && adicionouNestaRodada) {
            adicionouNestaRodada = false;
            for (int i = 0; i < categoriasPreferidas.size() && resultado.size() < limite; i++) {
                List<EventoConsultaGateway.EventoCandidato> balde = baldes.get(categoriasPreferidas.get(i));
                while (cursores[i] < balde.size()) {
                    UUID eventoId = balde.get(cursores[i]).eventoId();
                    cursores[i]++;
                    if (resultado.add(eventoId)) {
                        adicionouNestaRodada = true;
                        break;
                    }
                }
            }
        }

        return new ArrayList<>(resultado);
    }

    private int calcularScoreCompleto(PreferenciaParticipante preferencia,
                                      Set<UUID> categoriasEvento,
                                      Set<UUID> categoriasHistorico,
                                      Set<UUID> categoriasFavoritos) {
        int score = preferencia.calcularScore(categoriasEvento);
        for (UUID cat : categoriasEvento) {
            if (categoriasHistorico.contains(cat)) score += BONUS_CATEGORIA_HISTORICO;
            if (categoriasFavoritos.contains(cat)) score += BONUS_CATEGORIA_FAVORITO;
        }
        return score;
    }

    private Set<UUID> extrairCategoriasDeEventos(Set<UUID> eventosIds) {
        Set<UUID> categorias = new HashSet<>();
        for (UUID eventoId : eventosIds) {
            categorias.addAll(eventoGateway.buscarCategoriasDoEvento(eventoId));
        }
        return categorias;
    }
}
