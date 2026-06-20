package br.voke.dto;

import java.time.LocalDate;

public record CadastroOrganizadorRequisicao(
        String nome,
        String cpf,
        String email,
        String senha,
        LocalDate dataNascimento
) {}
