package graspexercicio10.model;

/** Eventos de entrada independentes de teclas, botões ou biblioteca gráfica. */
public enum AcaoJogo {
    CIMA(0, -1), BAIXO(0, 1), ESQUERDA(-1, 0), DIREITA(1, 0), EMBARCAR(0, 0), ABORTAR(0, 0);

    private final int dx;
    private final int dy;

    AcaoJogo(int dx, int dy) { this.dx = dx; this.dy = dy; }
    public int getDx() { return dx; }
    public int getDy() { return dy; }
}
