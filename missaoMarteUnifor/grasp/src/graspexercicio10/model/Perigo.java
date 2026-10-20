package graspexercicio10.model;

import java.util.Random;

/**
 * Polymorphism: cada perigo decide como atualizar sua posição e verificar colisão.
 */
public abstract class Perigo extends ElementoMapa {
    protected Perigo(int x, int y) { super(x, y); }
    public boolean colideCom(Nave nave) { return mesmaPosicao(nave); }
    public abstract void atualizar(Random random, int minX, int maxX, int minY, int maxY);
}
