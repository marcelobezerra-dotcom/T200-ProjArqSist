package br.edu.unifor.missaomarte.gdx.layout;

/**
 * Converte medidas fisicas (dp) em unidades do mundo e decide a escala do texto.
 * <p>Uma unidade do mundo vale {@code pixelsPorUnidade} pixels da tela; um dp vale {@code densidade} pixels
 * (densidade 1 = 160 dpi, como no Android). Sem libGDX, para poder ser testada.
 */
public final class EscalaDeTela {
    /** Tamanho minimo recomendado para um alvo de toque, em dp. */
    public static final float ALVO_MINIMO_DP = 48f;
    /** Altura do texto padrao do jogo, em unidades do mundo (a fonte padrao do libGDX tem 15 px). */
    public static final float ALTURA_DO_TEXTO = 15f;
    /** Tamanho minimo do texto, em dp. */
    public static final float TEXTO_MINIMO_DP = 12f;
    public static final float FATOR_MAXIMO_DE_TEXTO = 1.6f;

    private EscalaDeTela() {
    }

    /** Menor lado de um alvo de toque, em unidades do mundo. */
    public static float alvoMinimoNoMundo(float densidade, float pixelsPorUnidade) {
        if (pixelsPorUnidade <= 0f || densidade <= 0f) {
            return 0f;
        }
        return ALVO_MINIMO_DP * densidade / pixelsPorUnidade;
    }

    /** Multiplicador do texto para que ele nunca fique menor que o tamanho minimo em dp (entre 1 e o maximo). */
    public static float fatorDeTexto(float densidade, float pixelsPorUnidade) {
        if (pixelsPorUnidade <= 0f || densidade <= 0f) {
            return 1f;
        }
        float necessario = TEXTO_MINIMO_DP * densidade / (ALTURA_DO_TEXTO * pixelsPorUnidade);
        return Math.max(1f, Math.min(FATOR_MAXIMO_DE_TEXTO, necessario));
    }

    public static float pixelsParaMundo(float pixels, float pixelsPorUnidade) {
        return pixelsPorUnidade <= 0f ? 0f : pixels / pixelsPorUnidade;
    }
}
