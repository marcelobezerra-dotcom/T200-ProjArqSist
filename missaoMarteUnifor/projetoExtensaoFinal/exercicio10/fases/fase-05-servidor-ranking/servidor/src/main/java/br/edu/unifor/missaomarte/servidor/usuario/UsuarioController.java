package br.edu.unifor.missaomarte.servidor.usuario;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService servico;

    public UsuarioController(UsuarioService servico) {
        this.servico = servico;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResposta cadastrar(@Valid @RequestBody CadastroRequisicao requisicao) {
        return UsuarioResposta.de(servico.cadastrar(requisicao.nome(), requisicao.senha()));
    }
}
