package br.edu.unifor.missaomarte.gdx;

/**
 * Repete uma acao enquanto a tecla e mantida: espera um atraso inicial e depois dispara a cada intervalo.
 * <p>A primeira execucao e feita pelo evento keyDown; este objeto cuida apenas das repeticoes. Sem dependencia do libGDX.
 */
public final class RepetidorDeTecla {
    private final float atraso;
    private final float intervalo;
    private boolean ativo;
    private float acumulado;
    private boolean jaRepetiu;

    public RepetidorDeTecla(float atraso, float intervalo) {
        this.atraso = atraso;
        this.intervalo = intervalo;
    }

    public void iniciar() {
        ativo = true;
        acumulado = 0;
        jaRepetiu = false;
    }

    public void parar() {
        ativo = false;
    }

    /** Devolve true no maximo uma vez por chamada, quando uma repeticao esta devida. */
    public boolean atualizar(float delta) {
        if (!ativo) {
            return false;
        }
        acumulado += delta;
        float limite = jaRepetiu ? intervalo : atraso;
        if (acumulado < limite) {
            return false;
        }
        acumulado -= limite;
        jaRepetiu = true;
        return true;
    }
}
