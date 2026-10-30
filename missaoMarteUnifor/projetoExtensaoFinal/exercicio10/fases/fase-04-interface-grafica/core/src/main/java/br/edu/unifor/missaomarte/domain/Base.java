package br.edu.unifor.missaomarte.domain;

/** Ponto de entrega dos passageiros; entidade do dominio para ser renderizada como os demais elementos. */
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
