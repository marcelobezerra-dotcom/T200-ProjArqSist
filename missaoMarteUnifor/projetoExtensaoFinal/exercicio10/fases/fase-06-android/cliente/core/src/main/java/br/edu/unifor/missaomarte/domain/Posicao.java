package br.edu.unifor.missaomarte.domain;

import java.util.Objects;

/**
 * Coordenada imutavel no mapa.
 * <p>Padrao: Value Object (equals/hashCode por valor; mover devolve nova instancia).
 * <p>GRASP: Information Expert (sabe se limitar ao mapa). SOLID: SRP.
 */
public final class Posicao {
    private final int x;
    private final int y;

    public Posicao(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Posicao mover(int dx, int dy, int min, int max) {
        int novoX = Math.max(min, Math.min(max, x + dx));
        int novoY = Math.max(min, Math.min(max, y + dy));
        return new Posicao(novoX, novoY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Posicao)) {
            return false;
        }
        Posicao posicao = (Posicao) o;
        return x == posicao.x && y == posicao.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
