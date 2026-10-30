package br.edu.unifor.missaomarte.infrastructure.ranking;

import java.util.List;

public interface RankingRepository {
    List<RankingEntry> carregarTop();

    void salvarTop(List<RankingEntry> ranking);
}
