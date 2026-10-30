package br.edu.unifor.missaomarte.domain;

public class Astronauta extends Passageiro {
    public Astronauta(String nome, int x, int y) {
        super(nome, x, y);
    }

    @Override
    public int getPontuacao() {
        return 25;
    }

    @Override
    public char getSimbolo() {
        return 'A';
    }
}
