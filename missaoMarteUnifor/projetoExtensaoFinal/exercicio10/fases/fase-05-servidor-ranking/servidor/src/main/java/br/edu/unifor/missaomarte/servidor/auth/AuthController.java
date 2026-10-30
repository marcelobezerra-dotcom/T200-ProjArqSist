package br.edu.unifor.missaomarte.servidor.auth;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AutenticacaoService servico;

    public AuthController(AutenticacaoService servico) {
        this.servico = servico;
    }

    @PostMapping("/login")
    public TokenResposta login(@Valid @RequestBody LoginRequisicao requisicao) {
        var emitido = servico.entrar(requisicao.nome(), requisicao.senha());
        return new TokenResposta(emitido.token(), "Bearer", emitido.expiraEm());
    }
}
