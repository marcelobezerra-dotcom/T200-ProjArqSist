package br.edu.unifor.missaomarte.servidor.saude;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Rota publica para saber se o servidor esta de pe: usada pelo botao "Testar conexao" do jogo e por provedores de nuvem. */
@RestController
@RequestMapping("/api/saude")
@Tag(name = "Saúde")
public class SaudeController {

    @GetMapping
    @Operation(summary = "Informa que o servidor está no ar (não exige token)")
    public Map<String, String> saude() {
        return Map.of("status", "ok");
    }
}
