package br.edu.unifor.missaomarte.domain;

import java.util.Random;

/**
 * Como um inimigo escolhe a proxima celula (padrao Strategy): trocar o comportamento nao altera {@link Inimigo}.
 * <p>O alvo (normalmente a nave) pode ser null para estrategias que nao o usam.
 */
public interface MovimentoDoInimigo {
    Posicao proxima(Posicao atual, Posicao alvo, int min, int max, Random random);

    /** Simbolo usado para escolher o desenho (X comum, C perseguidor). */
    char simbolo();

    /** True se o inimigo depende da posicao do alvo e, por isso, anda no ritmo proprio dos perseguidores. */
    boolean persegue();
}
