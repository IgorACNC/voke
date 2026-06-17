package br.voke.agendamento;

import br.voke.dominio.inscricao.carrinho.CarrinhoRepositorio;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CarrinhoExpiradoScheduler {

    private static final long MINUTOS_EXPIRACAO = 15;

    private final CarrinhoRepositorio repositorio;

    public CarrinhoExpiradoScheduler(CarrinhoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void limparCarrinhosExpirados() {
        repositorio.removerExpirados(LocalDateTime.now().minusMinutes(MINUTOS_EXPIRACAO));
    }
}
