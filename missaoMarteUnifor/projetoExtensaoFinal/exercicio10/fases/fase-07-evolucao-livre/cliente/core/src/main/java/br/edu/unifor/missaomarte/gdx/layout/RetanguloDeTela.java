package br.edu.unifor.missaomarte.gdx.layout;

/** Retangulo em coordenadas do mundo (origem embaixo a esquerda). Objeto de valor sem libGDX. */
public final class RetanguloDeTela {
    private final float x;
    private final float y;
    private final float largura;
    private final float altura;

    public RetanguloDeTela(float x, float y, float largura, float altura) {
        this.x = x;
        this.y = y;
        this.largura = Math.max(0f, largura);
        this.altura = Math.max(0f, altura);
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float largura() {
        return largura;
    }

    public float altura() {
        return altura;
    }

    public float direita() {
        return x + largura;
    }

    public float topo() {
        return y + altura;
    }

    public float centroX() {
        return x + largura / 2f;
    }

    public float centroY() {
        return y + altura / 2f;
    }

    public boolean contem(float px, float py) {
        return px >= x && px <= direita() && py >= y && py <= topo();
    }

    public boolean sobrepoe(RetanguloDeTela outro) {
        return x < outro.direita() && outro.x < direita() && y < outro.topo() && outro.y < topo();
    }

    /** True se este retangulo esta inteiro dentro do outro. */
    public boolean estaDentroDe(RetanguloDeTela outro) {
        return x >= outro.x - 0.001f && y >= outro.y - 0.001f
                && direita() <= outro.direita() + 0.001f && topo() <= outro.topo() + 0.001f;
    }
}
