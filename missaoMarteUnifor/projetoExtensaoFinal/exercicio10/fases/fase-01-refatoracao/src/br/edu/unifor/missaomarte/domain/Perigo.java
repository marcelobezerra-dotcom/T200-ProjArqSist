package br.edu.unifor.missaomarte.domain;

public interface Perigo extends ObjetoRenderizavel {

    default boolean colideCom(Nave nave) {
        return getPosicao().equals(nave.getPosicao());
    }
}
