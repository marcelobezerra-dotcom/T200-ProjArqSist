package br.edu.unifor.missaomarte.application.ranking;

/** Registro imutavel de uma pontuacao do ranking (objeto de dados). */
public class RankingEntry {
    private final String nome;
    private final int pontuacao;
    private final long duracaoSegundos;
    private final int movimentos;

    public RankingEntry(String nome, int pontuacao, long duracaoSegundos, int movimentos) {
        this.nome = nome;
        this.pontuacao = pontuacao;
        this.duracaoSegundos = duracaoSegundos;
        this.movimentos = movimentos;
    }

    public String getNome() {
        return nome;
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
