package br.edu.unifor.missaomarte.domain;

/** Passageiro que vale 15 pontos (LSP: substitui Passageiro sem alterar quem o usa). */
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
