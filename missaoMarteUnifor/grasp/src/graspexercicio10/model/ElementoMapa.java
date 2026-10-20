package graspexercicio10.model;

/**
 * Information Expert: todo elemento conhece sua posição no mapa.
 * Polymorphism: cada elemento fornece seu símbolo, sem catálogo de tipos.
 */
public abstract class ElementoMapa {
    private int x;
    private int y;

    protected ElementoMapa(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public final int getX() { return x; }
    public final int getY() { return y; }
    public abstract char getSimbolo();

    public String getDescricao() { return getClass().getSimpleName(); }
    public String getNome() { return getDescricao(); }
    public boolean isResgatavel() { return false; }

    public final boolean ocupa(int x, int y) { return this.x == x && this.y == y; }
    public final boolean mesmaPosicao(ElementoMapa outro) { return ocupa(outro.getX(), outro.getY()); }

    /** Subclasses móveis reutilizam os limites sem expor setters de posição. */
    protected final boolean moverComLimites(int dx, int dy, int minX, int maxX, int minY, int maxY) {
        int novoX = x + dx;
        int novoY = y + dy;
        if (novoX < minX || novoX > maxX || novoY < minY || novoY > maxY) return false;
        if (novoX == x && novoY == y) return false;
        x = novoX;
        y = novoY;
        return true;
    }
}
