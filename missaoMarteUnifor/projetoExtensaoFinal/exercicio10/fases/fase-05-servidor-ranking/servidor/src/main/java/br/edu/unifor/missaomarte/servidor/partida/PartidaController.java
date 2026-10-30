package br.edu.unifor.missaomarte.servidor.partida;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/partidas")
public class PartidaController {
    private final PartidaService servico;

    public PartidaController(PartidaService servico) {
        this.servico = servico;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartidaResposta registrar(@AuthenticationPrincipal Jwt token, @Valid @RequestBody PartidaRequisicao requisicao) {
        return PartidaResposta.de(servico.registrar(Long.valueOf(token.getSubject()), requisicao));
    }

    @GetMapping("/minhas")
    public List<PartidaResposta> minhas(
            @AuthenticationPrincipal Jwt token,
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "20") @Min(1) @Max(50) int tamanho) {
        return servico.historico(Long.valueOf(token.getSubject()), pagina, tamanho).stream()
                .map(PartidaResposta::de)
                .toList();
    }
}
