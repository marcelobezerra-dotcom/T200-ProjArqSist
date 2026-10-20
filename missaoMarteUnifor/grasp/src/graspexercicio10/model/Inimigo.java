package graspexercicio10.model;

import java.util.Random;

// Polymorphism: inimigos se movem quando a missão avança um turno.
public class Inimigo extends Perigo {

    public Inimigo(int x, int y) {
        super(x, y);
    }

    @Override public char getSimbolo() { return 'X'; }

    @Override
    public void atualizar(Random random, int minX, int maxX, int minY, int maxY) {
        switch (random.nextInt(4)) {
            case 0: moverComLimites(1, 0, minX, maxX, minY, maxY); break;
            case 1: moverComLimites(-1, 0, minX, maxX, minY, maxY); break;
            case 2: moverComLimites(0, 1, minX, maxX, minY, maxY); break;
            case 3: moverComLimites(0, -1, minX, maxX, minY, maxY); break;
        }
    }
}
