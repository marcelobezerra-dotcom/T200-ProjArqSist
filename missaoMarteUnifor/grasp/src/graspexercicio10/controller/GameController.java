package graspexercicio10.controller;

import graspexercicio10.model.AcaoJogo;
import graspexercicio10.model.Dificuldade;
import graspexercicio10.model.EventoJogo;
import graspexercicio10.repository.RankingEntry;
import graspexercicio10.service.EstadoJogo;
import graspexercicio10.service.JogoService;
import graspexercicio10.service.RankingService;
import java.util.List;
import java.util.Objects;

/**
 * GRASP Controller: recebe eventos da aplicação e coordena os casos de uso.
 * Indirection: console ou GUI usam as mesmas operações sem acessar o modelo mutável.
 * Não lê teclado, desenha telas ou implementa regras dos turnos.
 */
public class GameController {
    private final JogoService jogo;
    private final RankingService ranking;

    public GameController(JogoService jogo, RankingService ranking) {
        this.jogo = Objects.requireNonNull(jogo);
        this.ranking = Objects.requireNonNull(ranking);
    }

    public EstadoJogo iniciarPartida(String piloto, Dificuldade dificuldade, int raioMapa) {
        jogo.iniciarPartida(piloto, dificuldade, raioMapa);
        return consultarEstado();
    }

    public EventoJogo executar(AcaoJogo acao) {
        EventoJogo evento = jogo.executar(acao);
        // Só a transição para vitória registra o resultado; comandos posteriores são ignorados.
        if (evento == EventoJogo.VITORIA) ranking.registrarVitoria(consultarEstado());
        return evento;
    }

    public EstadoJogo consultarEstado() { return jogo.consultarEstado(); }
    public List<RankingEntry> consultarRanking() { return ranking.consultar(); }
    public void resetarRanking() { ranking.resetar(); }
}
