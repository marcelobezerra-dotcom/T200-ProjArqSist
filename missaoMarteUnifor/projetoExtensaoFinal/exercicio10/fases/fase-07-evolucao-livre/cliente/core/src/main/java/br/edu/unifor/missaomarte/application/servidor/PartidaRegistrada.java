package br.edu.unifor.missaomarte.application.servidor;

/** Uma linha do historico do jogador, como o servidor devolve (objeto de dados imutavel). */
public final class PartidaRegistrada {
    private final String dificuldade;
    private final String resultado;
    private final int pontuacao;
    private final long duracaoSegundos;
    private final int movimentos;
    private final String jogadaEm;

    public PartidaRegistrada(String dificuldade, String resultado, int pontuacao, long duracaoSegundos,
                             int movimentos, String jogadaEm) {
        this.dificuldade = dificuldade;
        this.resultado = resultado;
        this.pontuacao = pontuacao;
        this.duracaoSegundos = duracaoSegundos;
        this.movimentos = movimentos;
        this.jogadaEm = jogadaEm;
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

    /** Data e hora no formato ISO-8601 em UTC, como veio do servidor. */
    public String getJogadaEm() {
        return jogadaEm;
    }
}
