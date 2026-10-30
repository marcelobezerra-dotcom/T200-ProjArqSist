package br.edu.unifor.missaomarte.domain;

public class Base implements ObjetoRenderizavel {
    private final Posicao posicao;

    public Base(int x, int y) {
        this.posicao = new Posicao(x, y);
    }

    @Override
    public Posicao getPosicao() {
        return posicao;
    }

    @Override
    public char getSimbolo() {
        return 'B';
    }
}
