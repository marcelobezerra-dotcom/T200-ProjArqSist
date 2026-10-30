package br.edu.unifor.missaomarte.domain;

/** Passageiro que vale 25 pontos (LSP: substitui Passageiro sem alterar quem o usa). */
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
