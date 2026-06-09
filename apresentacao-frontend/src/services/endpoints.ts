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
  criarInscricao: (eventoId: string) => `/eventos/${eventoId}/inscricoes`,
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
}
