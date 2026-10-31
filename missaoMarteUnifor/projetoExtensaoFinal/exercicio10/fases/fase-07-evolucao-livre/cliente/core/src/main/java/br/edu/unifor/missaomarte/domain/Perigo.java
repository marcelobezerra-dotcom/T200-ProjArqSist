package br.edu.unifor.missaomarte.domain;

/**
 * Obstaculo que causa colisao com a nave.
 * <p>SOLID: OCP/LSP (Asteroide e Inimigo sao tratados de forma uniforme por Missao).
 * <p>GRASP: Polymorphism e Information Expert (colideCom compara posicoes). Usa default method para compartilhar a regra.
 */
public interface Perigo extends ObjetoRenderizavel {

    default boolean colideCom(Nave nave) {
        return getPosicao().equals(nave.getPosicao());
    }
}
