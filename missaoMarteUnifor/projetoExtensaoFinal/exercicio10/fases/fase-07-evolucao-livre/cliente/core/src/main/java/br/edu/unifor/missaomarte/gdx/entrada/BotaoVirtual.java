package br.edu.unifor.missaomarte.gdx.entrada;

/**
 * Retangulo tocavel na tela (coordenadas do mundo, origem embaixo a esquerda). Um objeto de valor sem libGDX,
 * para poder ser testado. O rotulo vazio desenha so o contorno (area de toque sobre um campo de texto).
 */
public final class BotaoVirtual {
    private final String id;
    private final String rotulo;
    private final float x;
    private final float y;
    private final float largura;
    private final float altura;

    public BotaoVirtual(String id, String rotulo, float x, float y, float largura, float altura) {
        this.id = id;
        this.rotulo = rotulo;
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
    }

    public boolean contem(float px, float py) {
        return px >= x && px <= x + largura && py >= y && py <= y + altura;
    }

    public String getId() {
        return id;
    }

    public String getRotulo() {
        return rotulo;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getLargura() {
        return largura;
    }

    public float getAltura() {
        return altura;
    }
}
