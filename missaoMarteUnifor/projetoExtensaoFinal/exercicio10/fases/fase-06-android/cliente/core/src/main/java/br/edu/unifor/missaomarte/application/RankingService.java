package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import br.edu.unifor.missaomarte.application.ranking.RankingRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Regra do Top 10: quem entra, ordenacao por pontuacao e corte da lista.
 * <p>GRASP: Pure Fabrication (nao existe no dominio, criada para manter coesao) e Information Expert da regra de ranking.
 * <p>SOLID: SRP (so a regra), DIP (depende da porta {@link RankingRepository}, nao do arquivo).
 * <p>Antes da Fase 3 esta regra estava presa ao ConsoleGame; agora qualquer interface a reutiliza.
 */
public class RankingService {
    public static final int TAMANHO_MAXIMO = 10;

    private final RankingRepository repositorio;

    public RankingService(RankingRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<RankingEntry> top() {
        return repositorio.carregarTop();
    }

    public void limpar() {
        repositorio.salvarTop(new ArrayList<>());
    }

    public boolean registrarVitoria(GameSession session) {
        return registrar(new RankingEntry(
                session.getPiloto(),
                session.getPontuacao(),
                session.getDuracaoSegundos(),
                session.getMovimentos()
        ));
    }

    /** Devolve true se a entrada passou a fazer parte do Top 10. */
    public boolean registrar(RankingEntry atual) {
        List<RankingEntry> ranking = new ArrayList<>(repositorio.carregarTop());
        if (!deveEntrar(ranking, atual.getPontuacao())) {
            return false;
        }

        ranking.add(atual);
        ranking.sort(Comparator.comparingInt(RankingEntry::getPontuacao).reversed());
        while (ranking.size() > TAMANHO_MAXIMO) {
            ranking.remove(ranking.size() - 1);
        }
        repositorio.salvarTop(ranking);
        return true;
    }

    private boolean deveEntrar(List<RankingEntry> ranking, int pontuacao) {
        if (pontuacao <= 0) {
            return false;
        }
        if (ranking.size() < TAMANHO_MAXIMO) {
            return true;
        }
        int pior = Integer.MAX_VALUE;
        for (RankingEntry entrada : ranking) {
            pior = Math.min(pior, entrada.getPontuacao());
        }
        return pontuacao > pior;
    }
}
