package br.voke.config;

import br.voke.aplicacao.evento.*;
import br.voke.aplicacao.fidelidade.*;
import br.voke.aplicacao.inscricao.*;
import br.voke.aplicacao.pessoa.*;
import br.voke.dominio.evento.cupom.CupomRepositorio;
import br.voke.dominio.evento.cupom.CupomServico;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.EventoServico;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualRepositorio;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;
import br.voke.dominio.fidelidade.pontos.ContaPontosRepositorio;
import br.voke.dominio.fidelidade.pontos.ContaPontosServico;
import br.voke.dominio.fidelidade.recompensa.RecompensaRepositorio;
import br.voke.dominio.fidelidade.recompensa.RecompensaServico;
import br.voke.dominio.inscricao.carrinho.CarrinhoRepositorio;
import br.voke.dominio.inscricao.carrinho.CarrinhoServico;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.inscricao.inscricao.InscricaoServico;
import br.voke.dominio.pessoa.organizador.OrganizadorRepositorio;
import br.voke.dominio.pessoa.organizador.OrganizadorServico;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import br.voke.dominio.pessoa.participante.ParticipanteServico;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ParticipanteServico participanteServico(ParticipanteRepositorio r) {
        return new ParticipanteServico(r);
    }

    @Bean
    public OrganizadorServico organizadorServico(OrganizadorRepositorio r) {
        return new OrganizadorServico(r);
    }

    @Bean
    public CadastrarParticipanteCasoDeUso cadastrarParticipante(ParticipanteServico s) {
        return new CadastrarParticipanteCasoDeUso(s);
    }

    @Bean
    public CadastrarOrganizadorCasoDeUso cadastrarOrganizador(OrganizadorServico s) {
        return new CadastrarOrganizadorCasoDeUso(s);
    }

    @Bean
    public EditarParticipanteCasoDeUso editarParticipante(ParticipanteServico s) {
        return new EditarParticipanteCasoDeUso(s);
    }

    @Bean
    public RemoverParticipanteCasoDeUso removerParticipante(ParticipanteServico s) {
        return new RemoverParticipanteCasoDeUso(s);
    }

    @Bean
    public EditarOrganizadorCasoDeUso editarOrganizador(OrganizadorServico s) {
        return new EditarOrganizadorCasoDeUso(s);
    }

    @Bean
    public RemoverOrganizadorCasoDeUso removerOrganizador(OrganizadorServico s) {
        return new RemoverOrganizadorCasoDeUso(s);
    }


    @Bean
    public EventoServico eventoServico(EventoRepositorio r) {
        return new EventoServico(r);
    }

    @Bean
    public CupomServico cupomServico(CupomRepositorio r) {
        return new CupomServico(r);
    }

    @Bean
    public CriarEventoCasoDeUso criarEvento(EventoServico s) {
        return new CriarEventoCasoDeUso(s);
    }

    @Bean
    public EditarEventoCasoDeUso editarEvento(EventoServico s) {
        return new EditarEventoCasoDeUso(s);
    }

    @Bean
    public CancelarEventoCasoDeUso cancelarEvento(EventoServico s) {
        return new CancelarEventoCasoDeUso(s);
    }

    @Bean
    public CriarCupomCasoDeUso criarCupom(CupomServico s) {
        return new CriarCupomCasoDeUso(s);
    }

    @Bean
    public EditarCupomCasoDeUso editarCupom(CupomServico s) {
        return new EditarCupomCasoDeUso(s);
    }

    @Bean
    public RemoverCupomCasoDeUso removerCupom(CupomServico s) {
        return new RemoverCupomCasoDeUso(s);
    }

    @Bean
    public UtilizarCupomCasoDeUso utilizarCupom(CupomServico s) {
        return new UtilizarCupomCasoDeUso(s);
    }


    @Bean
    public InscricaoServico inscricaoServico(InscricaoRepositorio r) {
        return new InscricaoServico(r);
    }

    @Bean
    public CarrinhoServico carrinhoServico(CarrinhoRepositorio r) {
        return new CarrinhoServico(r);
    }

    @Bean
    public RealizarInscricaoCasoDeUso realizarInscricao(InscricaoServico s) {
        return new RealizarInscricaoCasoDeUso(s);
    }

    @Bean
    public CancelarInscricaoCasoDeUso cancelarInscricao(InscricaoServico s) {
        return new CancelarInscricaoCasoDeUso(s);
    }

    @Bean
    public AdicionarAoCarrinhoCasoDeUso adicionarAoCarrinho(CarrinhoServico s) {
        return new AdicionarAoCarrinhoCasoDeUso(s);
    }

    @Bean
    public RemoverDoCarrinhoCasoDeUso removerDoCarrinho(CarrinhoServico s) {
        return new RemoverDoCarrinhoCasoDeUso(s);
    }

    @Bean
    public AplicarCupomCarrinhoCasoDeUso aplicarCupomCarrinho(CarrinhoServico s,
            br.voke.dominio.inscricao.carrinho.CupomGateway gw) {
        return new AplicarCupomCarrinhoCasoDeUso(s, gw);
    }

    @Bean
    public FinalizarCompraCasoDeUso finalizarCompra(CarrinhoServico s) {
        return new FinalizarCompraCasoDeUso(s);
    }


    @Bean
    public ContaPontosServico contaPontosServico(ContaPontosRepositorio r) {
        return new ContaPontosServico(r);
    }

    @Bean
    public CarteiraVirtualServico carteiraVirtualServico(CarteiraVirtualRepositorio r) {
        return new CarteiraVirtualServico(r);
    }

    @Bean
    public RecompensaServico recompensaServico(RecompensaRepositorio r, ContaPontosRepositorio pr) {
        return new RecompensaServico(r, pr);
    }

    @Bean
    public CadastrarRecompensaCasoDeUso cadastrarRecompensa(RecompensaServico s) {
        return new CadastrarRecompensaCasoDeUso(s);
    }

    @Bean
    public EditarRecompensaCasoDeUso editarRecompensa(RecompensaServico s) {
        return new EditarRecompensaCasoDeUso(s);
    }

    @Bean
    public RemoverRecompensaCasoDeUso removerRecompensa(RecompensaServico s) {
        return new RemoverRecompensaCasoDeUso(s);
    }

    @Bean
    public InativarRecompensaCasoDeUso inativarRecompensa(RecompensaServico s) {
        return new InativarRecompensaCasoDeUso(s);
    }

    @Bean
    public ListarRecompensasOrganizadorCasoDeUso listarRecompensas(RecompensaServico s) {
        return new ListarRecompensasOrganizadorCasoDeUso(s);
    }

    @Bean
    public ResgatarRecompensaCasoDeUso resgatarRecompensa(RecompensaServico s) {
        return new ResgatarRecompensaCasoDeUso(s);
    }

    @Bean
    public ConsultarSaldoPontosCasoDeUso consultarSaldoPontos(ContaPontosServico s) {
        return new ConsultarSaldoPontosCasoDeUso(s);
    }

    @Bean
    public ConsultarSaldoCarteiraVirtualCasoDeUso consultarSaldoCarteira(CarteiraVirtualServico s) {
        return new ConsultarSaldoCarteiraVirtualCasoDeUso(s);
    }

    @Bean
    public AdicionarSaldoCasoDeUso adicionarSaldo(CarteiraVirtualServico s) {
        return new AdicionarSaldoCasoDeUso(s);
    }

    @Bean
    public RemoverSaldoCasoDeUso removerSaldo(CarteiraVirtualServico s) {
        return new RemoverSaldoCasoDeUso(s);
    }
}
