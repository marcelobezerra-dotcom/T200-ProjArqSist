package br.edu.unifor.missaomarte.domain;

public class Engenheiro extends Passageiro {
    public Engenheiro(String nome, int x, int y) {
        super(nome, x, y);
    }

    @Override
    public int getPontuacao() {
        return 15;
    }

    @Override
    public char getSimbolo() {
        return 'E';
    }
}
