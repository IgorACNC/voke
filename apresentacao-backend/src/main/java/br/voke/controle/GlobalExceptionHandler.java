package br.voke.controle;

import br.voke.dominio.fidelidade.excecao.LimiteDiarioInsercaoException;
import br.voke.dominio.fidelidade.excecao.LimiteFrequenciaSaqueException;
import br.voke.dominio.fidelidade.excecao.LimiteRemocaoException;
import br.voke.dominio.fidelidade.excecao.SaldoInsuficienteException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    record ErroResp(String mensagem) {}

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResp> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(LimiteDiarioInsercaoException.class)
    public ResponseEntity<ErroResp> handleLimiteDiario(LimiteDiarioInsercaoException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(LimiteRemocaoException.class)
    public ResponseEntity<ErroResp> handleLimiteRemocao(LimiteRemocaoException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<ErroResp> handleSaldoInsuficiente(SaldoInsuficienteException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }

    @ExceptionHandler(LimiteFrequenciaSaqueException.class)
    public ResponseEntity<ErroResp> handleLimiteFrequencia(LimiteFrequenciaSaqueException ex) {
        return ResponseEntity.badRequest().body(new ErroResp(ex.getMessage()));
    }
}
