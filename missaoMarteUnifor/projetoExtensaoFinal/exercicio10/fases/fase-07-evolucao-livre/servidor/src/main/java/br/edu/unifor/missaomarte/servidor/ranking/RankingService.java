package br.edu.unifor.missaomarte.servidor.ranking;

import br.edu.unifor.missaomarte.servidor.partida.PartidaRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Ranking global: cada jogador aparece uma vez, com a sua melhor vitoria. */
@Service
public class RankingService {
    public static final int TAMANHO_MAXIMO = 10;

    private final PartidaRepository repositorio;

    public RankingService(PartidaRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<RankingResposta> top() {
        return repositorio.melhoresPartidasPorJogador(PageRequest.of(0, TAMANHO_MAXIMO)).stream()
                .map(RankingResposta::de)
                .toList();
    }
}
