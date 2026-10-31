package br.edu.unifor.missaomarte.servidor.usuario;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários")
public class UsuarioController {
    private final UsuarioService servico;

    public UsuarioController(UsuarioService servico) {
        this.servico = servico;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma conta (409 se o nome já existe, 400 se os dados forem inválidos)")
    public UsuarioResposta cadastrar(@Valid @RequestBody CadastroRequisicao requisicao) {
        return UsuarioResposta.de(servico.cadastrar(requisicao.nome(), requisicao.senha()));
    }
}
