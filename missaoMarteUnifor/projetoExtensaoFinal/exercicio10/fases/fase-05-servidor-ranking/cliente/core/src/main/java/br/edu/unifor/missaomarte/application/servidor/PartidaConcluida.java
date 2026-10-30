package br.edu.unifor.missaomarte.application.servidor;

import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.GameSession;

/** Dados de uma partida terminada que o cliente envia ao servidor (objeto de dados imutavel). */
public final class PartidaConcluida {
    private final String dificuldade;
    private final String resultado;
    private final int pontuacao;
    private final long duracaoSegundos;
    private final int movimentos;

    public PartidaConcluida(String dificuldade, String resultado, int pontuacao, long duracaoSegundos, int movimentos) {
        this.dificuldade = dificuldade;
        this.resultado = resultado;
        this.pontuacao = pontuacao;
        this.duracaoSegundos = duracaoSegundos;
        this.movimentos = movimentos;
    }

    /** So vale para partidas encerradas em VITORIA ou DERROTA. */
    public static PartidaConcluida de(GameSession sessao) {
        EstadoPartida estado = sessao.getEstado();
        if (estado != EstadoPartida.VITORIA && estado != EstadoPartida.DERROTA) {
            throw new IllegalStateException("A partida ainda nao terminou: " + estado);
        }
        return new PartidaConcluida(sessao.getDificuldade().name(), estado.name(), sessao.getPontuacao(),
                sessao.getDuracaoSegundos(), sessao.getMovimentos());
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public String getResultado() {
        return resultado;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public long getDuracaoSegundos() {
        return duracaoSegundos;
    }

    public int getMovimentos() {
        return movimentos;
    }
}
