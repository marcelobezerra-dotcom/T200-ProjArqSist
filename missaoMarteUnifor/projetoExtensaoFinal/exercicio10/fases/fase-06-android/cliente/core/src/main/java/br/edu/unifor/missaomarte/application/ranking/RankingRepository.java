package br.edu.unifor.missaomarte.application.ranking;

import java.util.List;

/**
 * Abstracao de armazenamento do ranking (porta da camada application; a implementacao fica em infrastructure).
 * <p>Padrao: Repository. SOLID: DIP (quem usa depende desta interface), ISP (2 metodos).
 * <p>GRASP: Indirection/Protected Variations (trocar arquivo por memoria ou banco nao afeta o jogo).
 */
public interface RankingRepository {
    List<RankingEntry> carregarTop();

    void salvarTop(List<RankingEntry> ranking);
}
