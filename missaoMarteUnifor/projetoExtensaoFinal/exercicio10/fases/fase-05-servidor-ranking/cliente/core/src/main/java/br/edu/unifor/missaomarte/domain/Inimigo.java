package br.edu.unifor.missaomarte.domain;

import java.util.Random;

/** Perigo que se move aleatoriamente a cada turno (GRASP Information Expert: ele sabe se mover). */
public class Inimigo implements Perigo {
    private Posicao posicao;

    public Inimigo(int x, int y) {
        this.posicao = new Posicao(x, y);
    }

    @Override
    public Posicao getPosicao() {
        return posicao;
    }

    @Override
    public char getSimbolo() {
        return 'X';
    }

    public void moverAleatoriamente(int min, int max, Random random) {
        int dx = random.nextInt(3) - 1;
        int dy = random.nextInt(3) - 1;
        posicao = posicao.mover(dx, dy, min, max);
    }
}
