package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Dificuldade;

/** Velocidades do modo dinamico por dificuldade: quanto mais dificil, mais rapido e mais perseguidores. */
public final class RegrasDeTempoReal {
    private static final float NAVE = 0.15f;
    private static final float PONTUACAO = 1f;
    private static final float RECARGA_DO_TIRO = 0.6f;
    private static final float CONTAGEM = 3f;
    private static final float DELTA_MAXIMO = 0.1f;
    private static final int MUNICAO = 3;
    private static final int CUSTO_DO_TIRO = 2;

    private RegrasDeTempoReal() {
    }

    public static ConfiguracaoDeTempoReal obter(Dificuldade dificuldade) {
        switch (dificuldade) {
            case MEDIO:
                return nova(0.7f, 0.8f, 1);
            case DIFICIL:
                return nova(0.5f, 0.55f, 2);
            default:
                return nova(0.9f, 1.0f, 0);
        }
    }

    private static ConfiguracaoDeTempoReal nova(float inimigos, float perseguidores, int quantidadeDePerseguidores) {
        return new ConfiguracaoDeTempoReal(NAVE, inimigos, perseguidores, PONTUACAO, RECARGA_DO_TIRO, CONTAGEM, DELTA_MAXIMO,
                quantidadeDePerseguidores, MUNICAO, CUSTO_DO_TIRO);
    }
}
