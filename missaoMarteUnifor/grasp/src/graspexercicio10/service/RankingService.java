package graspexercicio10.service;

import graspexercicio10.model.StatusPartida;
import graspexercicio10.repository.IRankingRepository;
import graspexercicio10.repository.RankingEntry;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** High Cohesion: regras de classificação; o repositório cuida apenas do armazenamento. */
public class RankingService {
    private static final int LIMITE = 5;
    private final IRankingRepository repositorio;
    private final Clock clock;

    public RankingService(IRankingRepository repositorio) {
        this(repositorio, Clock.systemDefaultZone());
    }

    public RankingService(IRankingRepository repositorio, Clock clock) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.clock = Objects.requireNonNull(clock);
    }

    public List<RankingEntry> consultar() {
        return repositorio.carregar().stream()
                .sorted(Comparator.comparingInt((RankingEntry e) -> e.score).reversed())
                .limit(LIMITE).collect(Collectors.toList());
    }

    public void registrarVitoria(EstadoJogo estado) {
        if (estado.status != StatusPartida.VITORIA || estado.pontos <= 0) return;
        List<RankingEntry> ranking = new ArrayList<>(consultar());
        if (ranking.size() == LIMITE && estado.pontos <= ranking.get(LIMITE - 1).score) return;
        ranking.add(new RankingEntry(estado.piloto, estado.pontos, estado.dificuldade,
                estado.embarcados, LocalDateTime.now(clock).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                estado.tempoSegundos));
        ranking.sort(Comparator.comparingInt((RankingEntry e) -> e.score).reversed());
        repositorio.salvar(new ArrayList<>(ranking.subList(0, Math.min(LIMITE, ranking.size()))));
    }

    public void resetar() { repositorio.resetar(); }
}
