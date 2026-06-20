package br.voke.config;

import br.voke.aplicacao.evento.*;
import br.voke.aplicacao.fidelidade.*;
import br.voke.dominio.evento.favorito.ColecaoFavoritosRepositorio;
import br.voke.dominio.evento.favorito.ColecaoFavoritosServico;
import br.voke.dominio.evento.favorito.FavoritoRepositorio;
import br.voke.dominio.evento.favorito.FavoritoServico;
import br.voke.aplicacao.convite.*;
import br.voke.aplicacao.inscricao.*;
import br.voke.aplicacao.pessoa.*;
import br.voke.dominio.inscricao.convite.ConviteRepositorio;
import br.voke.dominio.inscricao.convite.ConviteServico;
import br.voke.dominio.evento.cupom.CupomRepositorio;
import br.voke.dominio.evento.cupom.CupomServico;
import br.voke.dominio.evento.avaliacao.AvaliacaoRepositorio;
import br.voke.dominio.evento.avaliacao.AvaliacaoServico;
import br.voke.dominio.evento.categoria.CategoriaRepositorio;
import br.voke.dominio.evento.categoria.CategoriaServico;
import br.voke.dominio.evento.evento.EventoRepositorio;
import br.voke.dominio.evento.evento.EventoServico;
import br.voke.dominio.evento.faq.PerguntaFrequenteRepositorio;
import br.voke.dominio.evento.faq.PerguntaFrequenteServico;
import br.voke.dominio.evento.notificacao.NotificacaoRepositorio;
import br.voke.dominio.evento.notificacao.NotificacaoServico;
import br.voke.dominio.evento.chat.*;
import br.voke.dominio.evento.estatistica.*;
import br.voke.dominio.evento.grupo.*;
import br.voke.dominio.evento.subgrupo.*;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupoRepositorio;
import br.voke.dominio.evento.subgrupo.solicitacao.SolicitacaoSubgrupoServico;
import br.voke.aplicacao.evento.CriarGrupoEventoCasoDeUso;
import br.voke.aplicacao.evento.CriarSubgrupoCasoDeUso;
import br.voke.aplicacao.evento.SolicitarEntradaSubgrupoCasoDeUso;
import br.voke.aplicacao.evento.AprovarSolicitacaoSubgrupoCasoDeUso;
import br.voke.aplicacao.evento.RejeitarSolicitacaoSubgrupoCasoDeUso;
import br.voke.aplicacao.fidelidade.ConsultarExtratoCasoDeUso;
import br.voke.aplicacao.fidelidade.ResetarLimitesDiariosCasoDeUso;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualRepositorio;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualServico;
import br.voke.dominio.fidelidade.transacao.TransacaoFinanceiraRepositorio;
import br.voke.dominio.fidelidade.pontos.ContaPontosRepositorio;
import br.voke.dominio.fidelidade.pontos.ContaPontosServico;
import br.voke.dominio.fidelidade.recompensa.RecompensaRepositorio;
import br.voke.dominio.fidelidade.recompensa.RecompensaServico;
import br.voke.dominio.fidelidade.sugestao.EventoConsultaGateway;
import br.voke.dominio.fidelidade.sugestao.FavoritoConsultaGateway;
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
import br.voke.dominio.fidelidade.comissao.ComissaoParceiroRepositorio;
import br.voke.dominio.fidelidade.comissao.ComissaoParceiroServico;
import br.voke.aplicacao.fidelidade.ConsultarComissoesCasoDeUso;
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
    public OrganizadorServico organizadorServico(
            OrganizadorRepositorio r,
            ParticipanteRepositorio pr,
            br.voke.dominio.pessoa.organizador.EventosOrganizadorGateway eg) {
        return new OrganizadorServico(r, pr, eg);
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
    public NotificacaoServico notificacaoServico(NotificacaoRepositorio r) {
        return new NotificacaoServico(r);
    }

    @Bean
    public GrupoEventoServicoInterface grupoEventoServico(GrupoEventoRepositorio r) {
        GrupoEventoServico base = new GrupoEventoServico(r);
        return new RestricaoEtariaGrupoDecorator(
                new VerificacaoInscritoGrupoDecorator(
                        new PrivilegioOrganizadorGrupoDecorator(base, r)
                )
        );
    }

    @Bean
    public CriarGrupoEventoCasoDeUso criarGrupoEvento(GrupoEventoRepositorio r) {
        return new CriarGrupoEventoCasoDeUso(r);
    }

    @Bean
    public SubgrupoServicoInterface subgrupoServico(SubgrupoRepositorio sr) {
        SubgrupoServico base = new SubgrupoServico(sr);
        return new TipoFechadoSubgrupoDecorator(
                new MembroDoGrupoPrincipalSubgrupoDecorator(
                        new PrivilegioGestorSubgrupoDecorator(base)),
                sr);
    }

    @Bean
    public SolicitacaoSubgrupoServico solicitacaoSubgrupoServico(SolicitacaoSubgrupoRepositorio r) {
        return new SolicitacaoSubgrupoServico(r);
    }

    @Bean
    public CriarSubgrupoCasoDeUso criarSubgrupo(SubgrupoServicoInterface s) {
        return new CriarSubgrupoCasoDeUso(s);
    }

    @Bean
    public SolicitarEntradaSubgrupoCasoDeUso solicitarEntradaSubgrupo(
            SolicitacaoSubgrupoServico ss, SubgrupoRepositorio sr) {
        return new SolicitarEntradaSubgrupoCasoDeUso(ss, sr);
    }

    @Bean
    public AprovarSolicitacaoSubgrupoCasoDeUso aprovarSolicitacaoSubgrupo(
            SolicitacaoSubgrupoServico ss, SubgrupoServicoInterface subSvc) {
        return new AprovarSolicitacaoSubgrupoCasoDeUso(ss, subSvc);
    }

    @Bean
    public RejeitarSolicitacaoSubgrupoCasoDeUso rejeitarSolicitacaoSubgrupo(
            SolicitacaoSubgrupoServico ss) {
        return new RejeitarSolicitacaoSubgrupoCasoDeUso(ss);
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
    public CancelarEventoCasoDeUso cancelarEvento(EventoServico s,
                                                  InscricaoRepositorio ir,
                                                  CarteiraVirtualServico cs,
                                                  AtualizadorEstatisticaListener l) {
        return new CancelarEventoCasoDeUso(s, ir, cs, l);
    }

    @Bean
    public EncerrarEventosExpiradosCasoDeUso encerrarEventosExpirados(
            EventoServico s, EventoRepositorio er, AtualizadorEstatisticaListener l) {
        return new EncerrarEventosExpiradosCasoDeUso(s, er, l);
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
    public RealizarInscricaoCasoDeUso realizarInscricao(InscricaoServico s,
                                                        AtualizadorEstatisticaListener l) {
        return new RealizarInscricaoCasoDeUso(s, l);
    }

    @Bean
    public CancelarInscricaoCasoDeUso cancelarInscricao(InscricaoServico s,
                                                        InscricaoRepositorio ir,
                                                        AtualizadorEstatisticaListener l,
                                                        ComissaoParceiroServico cs) {
        return new CancelarInscricaoCasoDeUso(s, ir, l, cs);
    }

    @Bean
    public br.voke.aplicacao.inscricao.RealizarCheckInCasoDeUso realizarCheckIn(
            br.voke.dominio.inscricao.inscricao.InscricaoRepositorio ir,
            br.voke.dominio.evento.evento.EventoRepositorio er,
            CreditarPontosCasoDeUso cp,
            AtualizadorEstatisticaListener l) {
        return new br.voke.aplicacao.inscricao.RealizarCheckInCasoDeUso(ir, er, cp, l);
    }

    @Bean
    public AtualizadorEstatisticaListener atualizadorEstatistica(
            EstatisticaEventoRepositorio er, EventoRepositorio evr) {
        return new AtualizadorEstatisticaListener(er, evr);
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
            br.voke.dominio.inscricao.carrinho.CupomGateway gw,
            br.voke.dominio.evento.evento.EventoRepositorio eventoRepo) {
        return new AplicarCupomCarrinhoCasoDeUso(s, gw, eventoRepo);
    }

    @Bean
    public br.voke.aplicacao.inscricao.RemoverCupomCarrinhoCasoDeUso removerCupomCarrinho(
            CarrinhoServico s, br.voke.dominio.inscricao.carrinho.CupomGateway gw) {
        return new br.voke.aplicacao.inscricao.RemoverCupomCarrinhoCasoDeUso(s, gw);
    }

    @Bean
    public ConsultarCarrinhoCasoDeUso consultarCarrinho(CarrinhoRepositorio r) {
        return new ConsultarCarrinhoCasoDeUso(r);
    }

    @Bean
public FinalizarCompraCasoDeUso finalizarCompra(CarrinhoServico carrinhoServico,
                                                 CarrinhoRepositorio carrinhoRepositorio,
                                                 CarteiraVirtualServico carteiraServico,
                                                 InscricaoServico inscricaoServico,
                                                 EventoRepositorio eventoRepositorio,
                                                 ParticipanteRepositorio participanteRepositorio,
                                                 AtualizadorEstatisticaListener atualizadorEstatistica,
                                                 ComissaoParceiroServico comissaoParceiroServico,
                                                 CupomRepositorio cupomRepositorio,
                                                 br.voke.dominio.pessoa.parceiro.ParceiroRepositorio parceiroRepositorio) {
    return new FinalizarCompraCasoDeUso(carrinhoServico, carrinhoRepositorio,
            carteiraServico, inscricaoServico, eventoRepositorio, participanteRepositorio,
            atualizadorEstatistica, comissaoParceiroServico, cupomRepositorio, parceiroRepositorio);
}


    @Bean
    public ContaPontosServico contaPontosServico(ContaPontosRepositorio r,
            br.voke.dominio.fidelidade.pontos.TransacaoPontosRepositorio tr) {
        ContaPontosServico s = new ContaPontosServico(r);
        s.setTransacaoRepositorio(tr);
        return s;
    }

    @Bean
    public CarteiraVirtualServico carteiraVirtualServico(CarteiraVirtualRepositorio r,
                                                          TransacaoFinanceiraRepositorio tr) {
        return new CarteiraVirtualServico(r, tr);
    }

    @Bean
    public ConsultarExtratoCasoDeUso consultarExtrato(TransacaoFinanceiraRepositorio tr) {
        return new ConsultarExtratoCasoDeUso(tr);
    }

    @Bean
    public ResetarLimitesDiariosCasoDeUso resetarLimitesDiarios(CarteiraVirtualServico s) {
        return new ResetarLimitesDiariosCasoDeUso(s);
    }

    @Bean
    public RecompensaServico recompensaServico(RecompensaRepositorio r, ContaPontosRepositorio pr,
            br.voke.dominio.fidelidade.pontos.TransacaoPontosRepositorio tr,
            CarteiraVirtualServico carteiraServico) {
        RecompensaServico s = new RecompensaServico(r, pr);
        s.setTransacaoPontosRepositorio(tr);
        s.setCarteiraServico(carteiraServico);
        return s;
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
    public CreditarPontosCasoDeUso creditarPontos(ContaPontosServico s) {
        return new CreditarPontosCasoDeUso(s);
    }

    @Bean
    public br.voke.aplicacao.fidelidade.ExpirarPontosVencidosCasoDeUso expirarPontosVencidos(
            br.voke.dominio.fidelidade.pontos.ContaPontosRepositorio contaRepo,
            br.voke.dominio.fidelidade.pontos.TransacaoPontosRepositorio txRepo,
            ContaPontosServico servico) {
        return new br.voke.aplicacao.fidelidade.ExpirarPontosVencidosCasoDeUso(contaRepo, txRepo, servico);
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
    public MotorSugestoes motorSugestoes(EventoConsultaGateway eg,
                                          InscricaoConsultaGateway ig,
                                          FavoritoConsultaGateway fg) {
        return new MotorSugestoes(eg, ig, fg);
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

    // ======================== Favoritos ========================

    @Bean
    public FavoritoServico favoritoServico(FavoritoRepositorio r) {
        return new FavoritoServico(r);
    }

    @Bean
    public AdicionarFavoritoCasoDeUso adicionarFavorito(FavoritoServico s) {
        return new AdicionarFavoritoCasoDeUso(s);
    }

    @Bean
    public RemoverFavoritoCasoDeUso removerFavorito(FavoritoServico s) {
        return new RemoverFavoritoCasoDeUso(s);
    }

    @Bean
    public ListarFavoritosDoParticipanteCasoDeUso listarFavoritos(FavoritoRepositorio r) {
        return new ListarFavoritosDoParticipanteCasoDeUso(r);
    }

    // ======================== Coleções de Favoritos ========================

    @Bean
    public ColecaoFavoritosServico colecaoFavoritosServico(ColecaoFavoritosRepositorio r) {
        return new ColecaoFavoritosServico(r);
    }

    @Bean
    public CriarColecaoFavoritosCasoDeUso criarColecao(ColecaoFavoritosServico s) {
        return new CriarColecaoFavoritosCasoDeUso(s);
    }

    @Bean
    public EditarColecaoFavoritosCasoDeUso editarColecao(ColecaoFavoritosServico s) {
        return new EditarColecaoFavoritosCasoDeUso(s);
    }

    @Bean
    public ExcluirColecaoFavoritosCasoDeUso excluirColecao(ColecaoFavoritosServico s) {
        return new ExcluirColecaoFavoritosCasoDeUso(s);
    }

    @Bean
    public ListarColecoesDoParticipanteCasoDeUso listarColecoes(ColecaoFavoritosServico s) {
        return new ListarColecoesDoParticipanteCasoDeUso(s);
    }

    @Bean
    public BuscarColecaoCasoDeUso buscarColecao(ColecaoFavoritosServico s) {
        return new BuscarColecaoCasoDeUso(s);
    }

    @Bean
    public AdicionarEventoColecaoCasoDeUso adicionarEventoColecao(ColecaoFavoritosServico s) {
        return new AdicionarEventoColecaoCasoDeUso(s);
    }

    @Bean
    public RemoverEventoColecaoCasoDeUso removerEventoColecao(ColecaoFavoritosServico s) {
        return new RemoverEventoColecaoCasoDeUso(s);
    }

    @Bean
    public MoverEventoEntreColecoesCasoDeUso moverEventoColecao(ColecaoFavoritosServico s) {
        return new MoverEventoEntreColecoesCasoDeUso(s);
    }

    @Bean
    public ReordenarEventoColecaoCasoDeUso reordenarEventoColecao(ColecaoFavoritosServico s) {
        return new ReordenarEventoColecaoCasoDeUso(s);
    }

    @Bean
    public DuplicarColecaoCasoDeUso duplicarColecao(ColecaoFavoritosServico s) {
        return new DuplicarColecaoCasoDeUso(s);
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

    // ======================== Chat Canal ========================

    @Bean
    public ChatCanalServicoInterface chatCanalServico(MensagemCanalRepositorio r) {
        return new ConteudoValidoDecorator(
                new AcessoCanalDecorator(
                        new ChatCanalServico(r)));
    }

    @Bean
    public EnviarMensagemCanalCasoDeUso enviarMensagemCanal(ChatCanalServicoInterface s) {
        return new EnviarMensagemCanalCasoDeUso(s);
    }

    @Bean
    public ListarMensagensCanalCasoDeUso listarMensagensCanal(ChatCanalServicoInterface s) {
        return new ListarMensagensCanalCasoDeUso(s);
    }

    // ======================== Convites ========================

    @Bean
    public ConviteServico conviteServico(ConviteRepositorio r) {
        return new ConviteServico(r);
    }

    @Bean
    public EnviarConviteCasoDeUso enviarConvite(ConviteServico cs, EventoRepositorio er,
                                                 ParticipanteRepositorio pr, InscricaoRepositorio ir) {
        return new EnviarConviteCasoDeUso(cs, er, pr, ir);
    }

    @Bean
    public AceitarConviteCasoDeUso aceitarConvite(ConviteServico s) {
        return new AceitarConviteCasoDeUso(s);
    }

    @Bean
    public RejeitarConviteCasoDeUso rejeitarConvite(ConviteServico s) {
        return new RejeitarConviteCasoDeUso(s);
    }

    @Bean
    public CancelarConviteCasoDeUso cancelarConvite(ConviteServico s) {
        return new CancelarConviteCasoDeUso(s);
    }

    @Bean
    public ListarConvitesRecebidosCasoDeUso listarConvitesRecebidos(ConviteServico s) {
        return new ListarConvitesRecebidosCasoDeUso(s);
    }

    @Bean
    public ListarConvitesEnviadosCasoDeUso listarConvitesEnviados(ConviteServico s) {
        return new ListarConvitesEnviadosCasoDeUso(s);
    }

    // ======================== Parceiros ========================

    @Bean
    public br.voke.dominio.pessoa.parceiro.ParceiroServico parceiroServico(
            br.voke.dominio.pessoa.parceiro.ParceiroRepositorio r,
            br.voke.dominio.pessoa.parceiro.PresencaConsulta pc) {
        return new br.voke.dominio.pessoa.parceiro.ParceiroServico(r, pc);
    }

    @Bean
    public CadastrarParceiroCasoDeUso cadastrarParceiro(br.voke.dominio.pessoa.parceiro.ParceiroServico s) {
        return new CadastrarParceiroCasoDeUso(s);
    }

    @Bean
    public EditarParceiroCasoDeUso editarParceiro(br.voke.dominio.pessoa.parceiro.ParceiroServico s) {
        return new EditarParceiroCasoDeUso(s);
    }

    @Bean
    public RemoverParceiroCasoDeUso removerParceiro(br.voke.dominio.pessoa.parceiro.ParceiroServico s) {
        return new RemoverParceiroCasoDeUso(s);
    }

    @Bean
    public ListarParceirosOrganizadorCasoDeUso listarParceiros(br.voke.dominio.pessoa.parceiro.ParceiroServico s) {
        return new ListarParceirosOrganizadorCasoDeUso(s);
    }

    @Bean
    public ComissaoParceiroServico comissaoParceiroServico(ComissaoParceiroRepositorio r, CarteiraVirtualServico cvs) {
        return new ComissaoParceiroServico(r, cvs);
    }

    @Bean
    public ConsultarComissoesCasoDeUso consultarComissoesCasoDeUso(ComissaoParceiroServico s) {
        return new ConsultarComissoesCasoDeUso(s);
    }

    // ======================== Recompensas Ativas ========================

    @Bean
    public ListarRecompensasAtivasCasoDeUso listarRecompensasAtivas(RecompensaServico s) {
        return new ListarRecompensasAtivasCasoDeUso(s);
    }

    @Bean
    public br.voke.aplicacao.fidelidade.ListarTodasRecompensasCasoDeUso listarTodasRecompensas(
            br.voke.dominio.fidelidade.recompensa.RecompensaRepositorio r) {
        return new br.voke.aplicacao.fidelidade.ListarTodasRecompensasCasoDeUso(r);
    }

    // ======================== Dashboard / Estatistica (F17) ========================

    @Bean
    public DashboardServicoInterface dashboardServico(EstatisticaEventoRepositorio r) {
        return new PrivilegioOrganizadorDashboardDecorator(new DashboardServico(r));
    }

    @Bean
    public ConsultarOverviewOrganizadorCasoDeUso consultarOverviewOrganizador(DashboardServicoInterface s) {
        return new ConsultarOverviewOrganizadorCasoDeUso(s);
    }

    @Bean
    public ConsultarEstatisticaEventoCasoDeUso consultarEstatisticaEvento(DashboardServicoInterface s) {
        return new ConsultarEstatisticaEventoCasoDeUso(s);
    }

    @Bean
    public IncrementarVisualizacaoEventoCasoDeUso incrementarVisualizacaoEvento(
            EventoRepositorio er, EstatisticaEventoRepositorio str) {
        return new IncrementarVisualizacaoEventoCasoDeUso(er, str);
    }

    @Bean
    public ConsultarCurvaVendasCasoDeUso consultarCurvaVendas(CurvaVendasConsulta c) {
        return new ConsultarCurvaVendasCasoDeUso(c);
    }

    @Bean
    public ExportarListaPresencaCasoDeUso exportarListaPresenca(ExportacaoConsulta c) {
        return new ExportarListaPresencaCasoDeUso(c);
    }

    @Bean
    public ExportarRelatorioFinanceiroCasoDeUso exportarRelatorioFinanceiro(ExportacaoConsulta c) {
        return new ExportarRelatorioFinanceiroCasoDeUso(c);
    }

    @Bean
    public PerguntaFrequenteServico perguntaFrequenteServico(PerguntaFrequenteRepositorio r) {
        return new PerguntaFrequenteServico(r);
    }

    @Bean
    public CriarPerguntaFaqCasoDeUso criarPerguntaFaq(PerguntaFrequenteServico s) {
        return new CriarPerguntaFaqCasoDeUso(s);
    }

    @Bean
    public EditarPerguntaFaqCasoDeUso editarPerguntaFaq(PerguntaFrequenteServico s) {
        return new EditarPerguntaFaqCasoDeUso(s);
    }

    @Bean
    public ExcluirPerguntaFaqCasoDeUso excluirPerguntaFaq(PerguntaFrequenteServico s) {
        return new ExcluirPerguntaFaqCasoDeUso(s);
    }

    @Bean
    public ReordenarFaqCasoDeUso reordenarFaq(PerguntaFrequenteServico s) {
        return new ReordenarFaqCasoDeUso(s);
    }

    @Bean
    public ListarFaqDoEventoCasoDeUso listarFaq(PerguntaFrequenteServico s) {
        return new ListarFaqDoEventoCasoDeUso(s);
    }
}
