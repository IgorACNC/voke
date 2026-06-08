package br.voke.seed;

import br.voke.dominio.compartilhado.Email;
import br.voke.dominio.compartilhado.NomeCompleto;
import br.voke.dominio.compartilhado.Senha;
import br.voke.dominio.pessoa.admin.Administrador;
import br.voke.dominio.pessoa.admin.AdministradorId;
import br.voke.dominio.pessoa.admin.AdministradorRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeed implements CommandLineRunner {

    private static final String EMAIL_PADRAO = "admin@voke.com";
    private static final String NOME_PADRAO = "Administrador Voke";
    private static final String SENHA_PADRAO = "Admin1234";

    private final AdministradorRepositorio repositorio;
    private final PasswordEncoder passwordEncoder;

    public AdminSeed(AdministradorRepositorio repositorio, PasswordEncoder passwordEncoder) {
        this.repositorio = repositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Email email = new Email(EMAIL_PADRAO);
        if (repositorio.existePorEmail(email)) {
            return;
        }
        Administrador admin = new Administrador(
                AdministradorId.novo(),
                new NomeCompleto(NOME_PADRAO),
                email,
                new Senha(passwordEncoder.encode(SENHA_PADRAO)));
        repositorio.salvar(admin);
        System.out.printf("[Seed] Administrador padrão criado: %s / %s%n", EMAIL_PADRAO, SENHA_PADRAO);
    }
}
