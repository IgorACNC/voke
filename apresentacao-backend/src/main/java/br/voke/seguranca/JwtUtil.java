package br.voke.seguranca;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey chave;
    private final long expiracao;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiracao}") long expiracao) {
        this.chave = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiracao = expiracao;
    }

    public String gerar(String email, String papel, String id) {
        return Jwts.builder()
                .subject(email)
                .claim("papel", papel)
                .claim("id", id)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracao))
                .signWith(chave)
                .compact();
    }

    public Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    public String extrairPapel(String token) {
        return extrairClaims(token).get("papel", String.class);
    }

    public boolean isValido(String token) {
        try {
            extrairClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
