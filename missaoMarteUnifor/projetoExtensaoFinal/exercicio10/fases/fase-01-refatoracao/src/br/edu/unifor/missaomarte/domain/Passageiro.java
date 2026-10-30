package br.edu.unifor.missaomarte.domain;

public class Passageiro implements ObjetoRenderizavel {
    private final String nome;
    private Posicao posicao;

    public Passageiro(String nome, int x, int y) {
        this.nome = nome;
        this.posicao = new Posicao(x, y);
    }

    public String getNome() {
        return nome;
    }

    public int getX() {
        return posicao.getX();
    }

    public int getY() {
        return posicao.getY();
    }

    @Override
    public Posicao getPosicao() {
        return posicao;
    }

    public void setPosicao(int x, int y) {
        this.posicao = new Posicao(x, y);
    }

    public int getPontuacao() {
        return 10;
    }

    @Override
    public char getSimbolo() {
        return 'P';
    }
}
