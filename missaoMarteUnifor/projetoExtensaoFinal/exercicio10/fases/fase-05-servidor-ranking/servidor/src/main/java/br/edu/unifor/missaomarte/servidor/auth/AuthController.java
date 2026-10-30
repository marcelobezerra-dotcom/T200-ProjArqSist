package br.edu.unifor.missaomarte.servidor.auth;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação")
public class AuthController {
    private final AutenticacaoService servico;

    public AuthController(AutenticacaoService servico) {
        this.servico = servico;
    }

    @PostMapping("/login")
    @Operation(summary = "Entra e devolve o token (401 se usuário ou senha estiverem errados)")
    public TokenResposta login(@Valid @RequestBody LoginRequisicao requisicao) {
        var emitido = servico.entrar(requisicao.nome(), requisicao.senha());
        return new TokenResposta(emitido.token(), "Bearer", emitido.expiraEm());
    }
}
