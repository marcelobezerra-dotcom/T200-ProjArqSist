package graspexercicio10.model;

import java.util.Random;

// Polymorphism: asteroides permanecem imóveis quando a missão avança um turno.
public class Asteroide extends Perigo {

    public Asteroide(int x, int y) {
        super(x, y);
    }

    @Override public char getSimbolo() { return '#'; }
    @Override public void atualizar(Random random, int minX, int maxX, int minY, int maxY) { }
}
