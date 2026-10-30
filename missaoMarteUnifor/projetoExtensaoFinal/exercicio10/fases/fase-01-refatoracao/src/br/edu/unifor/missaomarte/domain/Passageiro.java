package br.edu.unifor.missaomarte.domain;

/**
 * Passageiro a ser resgatado; os subtipos variam pontuacao e simbolo por sobrescrita.
 * <p>SOLID: LSP (qualquer subtipo substitui Passageiro), OCP (novo tipo sem mudar Nave, Missao ou GameSession).
 * <p>GRASP: Polymorphism (sem if por tipo). Valores: base 10, Professor 20, Engenheiro 15, Astronauta 25.
 */
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
