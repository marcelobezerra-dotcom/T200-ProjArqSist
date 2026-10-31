package br.edu.unifor.missaomarte.gdx.layout;

/** Folgas, em unidades do mundo, que a tela reserva para recortes (notch) e barras do sistema. Objeto de valor imutavel. */
public final class MargensSeguras {
    public static final MargensSeguras NENHUMA = new MargensSeguras(0f, 0f, 0f, 0f);

    private final float esquerda;
    private final float direita;
    private final float cima;
    private final float baixo;

    public MargensSeguras(float esquerda, float direita, float cima, float baixo) {
        this.esquerda = Math.max(0f, esquerda);
        this.direita = Math.max(0f, direita);
        this.cima = Math.max(0f, cima);
        this.baixo = Math.max(0f, baixo);
    }

    public float esquerda() {
        return esquerda;
    }

    public float direita() {
        return direita;
    }

    public float cima() {
        return cima;
    }

    public float baixo() {
        return baixo;
    }
}
