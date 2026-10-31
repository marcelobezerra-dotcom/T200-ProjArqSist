package br.edu.unifor.missaomarte.domain;

import java.util.Random;

/** Movimento original: sorteia -1, 0 ou 1 em cada eixo (a mesma sequencia de sorteios das fases anteriores). */
public final class MovimentoAleatorio implements MovimentoDoInimigo {
    public static final MovimentoAleatorio INSTANCIA = new MovimentoAleatorio();

    private MovimentoAleatorio() {
    }

    @Override
    public Posicao proxima(Posicao atual, Posicao alvo, int min, int max, Random random) {
        int dx = random.nextInt(3) - 1;
        int dy = random.nextInt(3) - 1;
        return atual.mover(dx, dy, min, max);
    }

    @Override
    public char simbolo() {
        return 'X';
    }

    @Override
    public boolean persegue() {
        return false;
    }
}
