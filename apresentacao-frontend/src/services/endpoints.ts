export const endpoints = {
  // Eventos
  listarEventos: '/eventos',
  listarMeusEventos: '/eventos/meus',
  buscarEvento: (id: string) => `/eventos/${id}`,
  criarEvento: '/eventos',
  editarEvento: (id: string) => `/eventos/${id}`,
  cancelarEvento: (id: string) => `/eventos/${id}`,
  criarLote: (eventoId: string) => `/eventos/${eventoId}/lotes`,

  // Inscricoes
  listarMinhasInscricoes: '/inscricoes/minhas',
  validarInscricao: (eventoId: string) => `/eventos/${eventoId}/inscricoes/validar`,
  criarInscricao: '/inscricoes',
  estimarEstorno: (inscricaoId: string) => `/inscricoes/${inscricaoId}/estorno`,
  cancelarInscricao: (inscricaoId: string) => `/inscricoes/${inscricaoId}`,

  // Cupons
  listarCupons: '/cupons',
  criarCupom: '/cupons',
  editarCupom: (id: string) => `/cupons/${id}`,
  excluirCupom: (id: string) => `/cupons/${id}`,
  validarCupom: '/cupons/validar',

  // Participantes
  perfilParticipante: (id: string) => `/participantes/${id}`,
  editarPerfil: '/participantes/perfil',
  removerConta: (id: string) => `/participantes/${id}`,
  alterarSenha: '/participantes/senha',

  // Carrinho
  consultarCarrinho: (participanteId: string) => `/carrinho/${participanteId}`,
  adicionarItemCarrinho: '/carrinho/itens',
  removerItemCarrinho: (participanteId: string, eventoId: string) => `/carrinho/itens/${participanteId}/${eventoId}`,
  aplicarCupomCarrinho: '/carrinho/cupom',
  finalizarCarrinho: '/carrinho/finalizar',

  // Convites
  enviarConvite: '/convites',
  listarConvitesRecebidos: '/convites/recebidos',
  listarConvitesEnviados: '/convites/enviados',
  aceitarConvite: (id: string) => `/convites/${id}/aceitar`,
  rejeitarConvite: (id: string) => `/convites/${id}/rejeitar`,
  cancelarConvite: (id: string) => `/convites/${id}`,

  // Favoritos
  adicionarFavorito: (eventoId: string) => `/favoritos/${eventoId}`,
  removerFavorito: (favoritoId: string) => `/favoritos/${favoritoId}`,
  listarFavoritos: '/favoritos',

  // Coleções de Favoritos
  listarColecoes: '/colecoes-favoritos',
  criarColecao: '/colecoes-favoritos',
  buscarColecao: (id: string) => `/colecoes-favoritos/${id}`,
  editarColecao: (id: string) => `/colecoes-favoritos/${id}`,
  excluirColecao: (id: string) => `/colecoes-favoritos/${id}`,
  adicionarEventoColecao: (id: string) => `/colecoes-favoritos/${id}/eventos`,
  removerEventoColecao: (id: string, eventoId: string) => `/colecoes-favoritos/${id}/eventos/${eventoId}`,
  moverEventoColecao: (id: string) => `/colecoes-favoritos/${id}/mover`,
  reordenarEventoColecao: (id: string, eventoId: string) => `/colecoes-favoritos/${id}/reordenar/${eventoId}`,
  duplicarColecao: (id: string) => `/colecoes-favoritos/${id}/duplicar`,
}
