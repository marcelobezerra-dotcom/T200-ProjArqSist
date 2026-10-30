package br.edu.unifor.missaomarte.infrastructure.ranking;

import java.util.List;

/**
 * Abstracao de armazenamento do ranking.
 * <p>Padrao: Repository. SOLID: DIP (quem usa depende desta interface), ISP (2 metodos).
 * <p>GRASP: Indirection/Protected Variations (trocar arquivo por memoria ou banco nao afeta o jogo).
 */
public interface RankingRepository {
    List<RankingEntry> carregarTop();

    void salvarTop(List<RankingEntry> ranking);
}
