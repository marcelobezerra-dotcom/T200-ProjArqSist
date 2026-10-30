package br.edu.unifor.missaomarte.servidor.ranking;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {
    private final RankingService servico;

    public RankingController(RankingService servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<RankingResposta> top() {
        return servico.top();
    }
}
