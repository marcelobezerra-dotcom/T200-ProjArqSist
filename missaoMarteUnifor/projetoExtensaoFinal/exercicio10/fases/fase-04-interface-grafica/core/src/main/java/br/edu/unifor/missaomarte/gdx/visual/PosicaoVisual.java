package br.edu.unifor.missaomarte.gdx.visual;

/**
 * Posicao desenhada de um objeto, em unidades de celula (pode ser fracionaria), que persegue a posicao logica.
 * <p>A regra do jogo e por turnos e instantanea; so o desenho desliza entre as celulas. Sem dependencia do libGDX.
 */
public final class PosicaoVisual {
    private float x;
    private float y;
    private float alvoX;
    private float alvoY;

    public PosicaoVisual(float x, float y) {
        this.x = x;
        this.y = y;
        this.alvoX = x;
        this.alvoY = y;
    }

    public void irPara(float alvoX, float alvoY) {
        this.alvoX = alvoX;
        this.alvoY = alvoY;
    }

    /** Anda em linha reta ate o alvo; velocidade em celulas por segundo. */
    public void atualizar(float delta, float velocidade) {
        float dx = alvoX - x;
        float dy = alvoY - y;
        float distancia = (float) Math.sqrt(dx * dx + dy * dy);
        float passo = velocidade * delta;
        if (distancia <= passo) {
            x = alvoX;
            y = alvoY;
            return;
        }
        x += dx / distancia * passo;
        y += dy / distancia * passo;
    }

    public boolean emMovimento() {
        return x != alvoX || y != alvoY;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }
}
