package br.edu.unifor.missaomarte.domain;

/** Perigo fixo no mapa; some ao colidir com a nave. */
public class Asteroide implements Perigo {
    private final Posicao posicao;

    public Asteroide(int x, int y) {
        this.posicao = new Posicao(x, y);
    }

    @Override
    public Posicao getPosicao() {
        return posicao;
    }

    @Override
    public char getSimbolo() {
        return '*';
    }
}
