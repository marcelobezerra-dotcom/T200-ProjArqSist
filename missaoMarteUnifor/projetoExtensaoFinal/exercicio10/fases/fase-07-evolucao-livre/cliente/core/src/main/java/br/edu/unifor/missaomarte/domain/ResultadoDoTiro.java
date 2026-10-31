package br.edu.unifor.missaomarte.domain;

/** Resultado de um tiro: ate onde o disparo chegou e o perigo destruido (null se nao acertou nada). */
public final class ResultadoDoTiro {
    private final Posicao origem;
    private final Posicao destino;
    private final Perigo atingido;

    public ResultadoDoTiro(Posicao origem, Posicao destino, Perigo atingido) {
        this.origem = origem;
        this.destino = destino;
        this.atingido = atingido;
    }

    public Posicao getOrigem() {
        return origem;
    }

    /** Ultima celula percorrida: a do alvo atingido ou a borda do mapa. */
    public Posicao getDestino() {
        return destino;
    }

    public Perigo getAtingido() {
        return atingido;
    }

    public boolean acertou() {
        return atingido != null;
    }
}
