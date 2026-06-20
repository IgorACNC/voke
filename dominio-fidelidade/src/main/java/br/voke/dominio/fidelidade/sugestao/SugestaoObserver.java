package br.voke.dominio.fidelidade.sugestao;

public interface SugestaoObserver {
    void aoMudarStatus(Sugestao sugestao, StatusSugestao statusAnterior);
}
