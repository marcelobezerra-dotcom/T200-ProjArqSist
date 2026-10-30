package br.edu.unifor.missaomarte.domain;

/** Passageiro que vale 20 pontos (LSP: substitui Passageiro sem alterar quem o usa). */
public class Professor extends Passageiro {
    public Professor(String nome, int x, int y) {
        super(nome, x, y);
    }

    @Override
    public int getPontuacao() {
        return 20;
    }

    @Override
    public char getSimbolo() {
        return 'P';
    }
}
