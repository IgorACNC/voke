package br.voke.dto;

import java.time.LocalDate;

public record CadastroParticipanteRequisicao(
        String nome,
        String cpf,
        String email,
        String senha,
        LocalDate dataNascimento
) {}
