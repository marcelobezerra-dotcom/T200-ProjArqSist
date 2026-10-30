package br.edu.unifor.missaomarte.domain;

public interface Perigo {
    Posicao getPosicao();

    char getSimbolo();

    default boolean colideCom(Nave nave) {
        return getPosicao().equals(nave.getPosicao());
    }
}
