package br.edu.unifor.missaomarte.domain;

import java.util.Random;

/** Anda uma celula em direcao ao alvo, no eixo em que a distancia e maior (empate: eixo X). Nao usa sorteio. */
public final class MovimentoPerseguidor implements MovimentoDoInimigo {
    public static final MovimentoPerseguidor INSTANCIA = new MovimentoPerseguidor();

    private MovimentoPerseguidor() {
    }

    @Override
    public Posicao proxima(Posicao atual, Posicao alvo, int min, int max, Random random) {
        if (alvo == null) {
            return atual;
        }
        int distanciaX = alvo.getX() - atual.getX();
        int distanciaY = alvo.getY() - atual.getY();
        if (distanciaX == 0 && distanciaY == 0) {
            return atual;
        }
        if (Math.abs(distanciaX) >= Math.abs(distanciaY)) {
            return atual.mover(Integer.signum(distanciaX), 0, min, max);
        }
        return atual.mover(0, Integer.signum(distanciaY), min, max);
    }

    /** Simbolo C (cacador): o P ja e do passageiro professor. */
    @Override
    public char simbolo() {
        return 'C';
    }

    @Override
    public boolean persegue() {
        return true;
    }
}
