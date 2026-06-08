package br.voke.config;

import br.voke.aplicacao.evento.*;
import br.voke.aplicacao.fidelidade.*;
import br.voke.aplicacao.inscricao.*;
import br.voke.aplicacao.pessoa.*;
import br.voke.dominio.evento.cupom.CupomRepositorio;
import br.voke.dominio.evento.cupom.CupomServico;
import br.voke.dominio.evento.avaliacao.AvaliacaoRepositorio;
import br.voke.dominio.evento.avaliacao.AvaliacaoServico;
import br.voke.dominio.evento.categoria.CategoriaRepositorio;
import br.voke.dominio.evento.categoria.CategoriaServico;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.EventoServico;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualRepositorio;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;
import br.voke.dominio.fidelidade.pontos.ContaPontosRepositorio;
import br.voke.dominio.fidelidade.pontos.ContaPontosServico;
import br.voke.dominio.fidelidade.recompensa.RecompensaRepositorio;
import br.voke.dominio.fidelidade.recompensa.RecompensaServico;
import br.voke.dominio.fidelidade.sugestao.EventoConsultaGateway;
import br.voke.dominio.fidelidade.sugestao.InscricaoConsultaGateway;
import br.voke.dominio.fidelidade.sugestao.MotorSugestoes;
import br.voke.dominio.fidelidade.sugestao.NotificarParticipanteObserver;
import br.voke.dominio.fidelidade.sugestao.PreferenciaParticipanteRepositorio;
import br.voke.dominio.fidelidade.sugestao.SugestaoRepositorio;
import br.voke.dominio.fidelidade.sugestao.SugestaoServico;
import br.voke.dominio.inscricao.carrinho.CarrinhoRepositorio;
import br.voke.dominio.inscricao.carrinho.CarrinhoServico;
import br.voke.dominio.inscricao.inscricao.InscricaoRepositorio;
import br.voke.dominio.inscricao.inscricao.InscricaoServico;
import br.voke.dominio.pessoa.organizador.OrganizadorRepositorio;
import br.voke.dominio.pessoa.organizador.OrganizadorServico;
import br.voke.dominio.pessoa.amizade.AmizadeRepositorio;
import br.voke.dominio.pessoa.amizade.AmizadeServico;
import br.voke.dominio.pessoa.chat.ChatPrivadoServico;
import br.voke.dominio.pessoa.chat.MensagemPrivadaRepositorio;
import br.voke.dominio.pessoa.participante.ParticipanteRepositorio;
import br.voke.dominio.pessoa.participante.ParticipanteServico;
import br.voke.dominio.pessoa.participante.TokenRecuperacaoSenhaRepositorio;
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
    public AmizadeServico amizadeServico(AmizadeRepositorio r) {
        return new AmizadeServico(r);
    }

    @Bean
    public ChatPrivadoServico chatPrivadoServico(MensagemPrivadaRepositorio mr, AmizadeRepositorio ar) {
        return new ChatPrivadoServico(mr, ar);
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
    public AlterarSenhaCasoDeUso alterarSenha(ParticipanteRepositorio r) {
        return new AlterarSenhaCasoDeUso(r);
    }

    @Bean
    public SolicitarRecuperacaoSenhaCasoDeUso solicitarRecuperacaoSenha(
            ParticipanteRepositorio pr, TokenRecuperacaoSenhaRepositorio tr) {
        return new SolicitarRecuperacaoSenhaCasoDeUso(pr, tr);
    }

    @Bean
    public RedefinirSenhaCasoDeUso redefinirSenha(
            ParticipanteRepositorio pr, TokenRecuperacaoSenhaRepositorio tr) {
        return new RedefinirSenhaCasoDeUso(pr, tr);
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
    public SolicitarAmizadeCasoDeUso solicitarAmizade(AmizadeServico s, ParticipanteRepositorio pr) {
        return new SolicitarAmizadeCasoDeUso(s, pr);
    }

    @Bean
    public AceitarAmizadeCasoDeUso aceitarAmizade(AmizadeServico s) {
        return new AceitarAmizadeCasoDeUso(s);
    }

    @Bean
    public RecusarAmizadeCasoDeUso recusarAmizade(AmizadeServico s) {
        return new RecusarAmizadeCasoDeUso(s);
    }

    @Bean
    public DesfazerAmizadeCasoDeUso desfazerAmizade(AmizadeServico s) {
        return new DesfazerAmizadeCasoDeUso(s);
    }

    @Bean
    public CriarComunidadeCasoDeUso criarComunidade(
            br.voke.dominio.pessoa.amizade.ComunidadeAmigosRepositorio cr, AmizadeServico as) {
        return new CriarComunidadeCasoDeUso(cr, as);
    }

    @Bean
    public EnviarMensagemPrivadaCasoDeUso enviarMensagemPrivada(ChatPrivadoServico s) {
        return new EnviarMensagemPrivadaCasoDeUso(s);
    }

    @Bean
    public ListarMensagensPrivadasCasoDeUso listarMensagensPrivadas(ChatPrivadoServico s) {
        return new ListarMensagensPrivadasCasoDeUso(s);
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
    public AvaliacaoServico avaliacaoServico(AvaliacaoRepositorio r) {
        return new AvaliacaoServico(r);
    }

    @Bean
    public CriarEventoCasoDeUso criarEvento(EventoServico s, CategoriaRepositorio cr) {
        return new CriarEventoCasoDeUso(s, cr);
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
    public AvaliarEventoCasoDeUso avaliarEvento(AvaliacaoServico s, EventoRepositorio er, InscricaoRepositorio ir) {
        return new AvaliarEventoCasoDeUso(s, er, ir);
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

    @Bean
    public MotorSugestoes motorSugestoes(EventoConsultaGateway eg, InscricaoConsultaGateway ig) {
        return new MotorSugestoes(eg, ig);
    }

    @Bean
    public SugestaoServico sugestaoServico(SugestaoRepositorio sr,
                                            PreferenciaParticipanteRepositorio pr,
                                            MotorSugestoes motor,
                                            EventoConsultaGateway eg) {
        SugestaoServico servico = new SugestaoServico(sr, pr, motor, eg);
        servico.registrarObserver(new NotificarParticipanteObserver());
        return servico;
    }

    @Bean
    public CadastrarSugestaoCasoDeUso cadastrarSugestao(SugestaoServico s) {
        return new CadastrarSugestaoCasoDeUso(s);
    }

    @Bean
    public EditarSugestaoCasoDeUso editarSugestao(SugestaoServico s) {
        return new EditarSugestaoCasoDeUso(s);
    }

    @Bean
    public AvaliarSugestaoCasoDeUso avaliarSugestao(SugestaoServico s) {
        return new AvaliarSugestaoCasoDeUso(s);
    }

    @Bean
    public RemoverSugestaoCasoDeUso removerSugestao(SugestaoServico s) {
        return new RemoverSugestaoCasoDeUso(s);
    }

    @Bean
    public ListarSugestoesParticipanteCasoDeUso listarSugestoes(SugestaoServico s) {
        return new ListarSugestoesParticipanteCasoDeUso(s);
    }

    @Bean
    public GerarSugestoesSemanaisCasoDeUso gerarSugestoes(SugestaoServico s) {
        return new GerarSugestoesSemanaisCasoDeUso(s);
    }

    @Bean
    public ExpirarSugestaoCasoDeUso expirarSugestao(SugestaoServico s) {
        return new ExpirarSugestaoCasoDeUso(s);
    }

    @Bean
    public ExpirarSugestoesAntigasCasoDeUso expirarSugestoesAntigas(SugestaoServico s) {
        return new ExpirarSugestoesAntigasCasoDeUso(s);
    }

    @Bean
    public ConfigurarPreferenciasCasoDeUso configurarPreferencias(SugestaoServico s) {
        return new ConfigurarPreferenciasCasoDeUso(s);
    }

    @Bean
    public CategoriaServico categoriaServico(CategoriaRepositorio r) {
        return new CategoriaServico(r);
    }

    @Bean
    public CadastrarCategoriaCasoDeUso cadastrarCategoria(CategoriaServico s) {
        return new CadastrarCategoriaCasoDeUso(s);
    }

    @Bean
    public EditarCategoriaCasoDeUso editarCategoria(CategoriaServico s) {
        return new EditarCategoriaCasoDeUso(s);
    }

    @Bean
    public RemoverCategoriaCasoDeUso removerCategoria(CategoriaServico s) {
        return new RemoverCategoriaCasoDeUso(s);
    }

    @Bean
    public ListarCategoriasCasoDeUso listarCategorias(CategoriaServico s) {
        return new ListarCategoriasCasoDeUso(s);
    }
}
