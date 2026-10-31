package br.edu.unifor.missaomarte.domain;

import java.util.Random;

/**
 * Perigo que se move a cada passo (GRASP Information Expert: ele sabe se mover).
 * <p>Fase 7: a escolha da proxima celula e delegada a um {@link MovimentoDoInimigo} (Strategy). Sem estrategia, move-se ao acaso.
 */
public class Inimigo implements Perigo {
    private final MovimentoDoInimigo movimento;
    private Posicao posicao;

    public Inimigo(int x, int y) {
        this(x, y, MovimentoAleatorio.INSTANCIA);
    }

    public Inimigo(int x, int y, MovimentoDoInimigo movimento) {
        this.posicao = new Posicao(x, y);
        this.movimento = movimento;
    }

    @Override
    public Posicao getPosicao() {
        return posicao;
    }

    @Override
    public char getSimbolo() {
        return movimento.simbolo();
    }

    public boolean persegue() {
        return movimento.persegue();
    }

    public void moverAleatoriamente(int min, int max, Random random) {
        posicao = MovimentoAleatorio.INSTANCIA.proxima(posicao, null, min, max, random);
    }

    /** Move um passo conforme a estrategia do inimigo; o alvo e a posicao que ele pode perseguir. */
    public void mover(Posicao alvo, int min, int max, Random random) {
        posicao = movimento.proxima(posicao, alvo, min, max, random);
    }
}
