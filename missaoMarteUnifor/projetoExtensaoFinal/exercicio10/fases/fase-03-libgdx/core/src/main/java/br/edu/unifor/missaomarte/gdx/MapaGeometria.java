package br.edu.unifor.missaomarte.gdx;

/**
 * Converte coordenadas de celula do jogo em coordenadas de tela do libGDX.
 * <p>No jogo o Y cresce para baixo (CIMA e y-1); no libGDX o Y cresce para cima. A inversao fica somente aqui.
 * <p>GRASP: Pure Fabrication (sem dependencia de OpenGL, testavel por unidade).
 */
public final class MapaGeometria {
    private final int min;
    private final int max;
    private final float celula;
    private final float origemX;
    private final float origemY;

    /** origemX e origemY sao o canto inferior esquerdo da grade na tela. */
    public MapaGeometria(int min, int max, float celula, float origemX, float origemY) {
        this.min = min;
        this.max = max;
        this.celula = celula;
        this.origemX = origemX;
        this.origemY = origemY;
    }

    public float xCanto(int x) {
        return origemX + (x - min) * celula;
    }

    public float yCanto(int y) {
        return origemY + (max - y) * celula;
    }

    public float xCentro(int x) {
        return xCanto(x) + celula / 2f;
    }

    public float yCentro(int y) {
        return yCanto(y) + celula / 2f;
    }

    public float celula() {
        return celula;
    }
}
