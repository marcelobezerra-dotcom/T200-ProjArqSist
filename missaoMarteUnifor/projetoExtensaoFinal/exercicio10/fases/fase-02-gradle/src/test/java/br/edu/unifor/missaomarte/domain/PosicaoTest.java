package br.edu.unifor.missaomarte.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

class PosicaoTest {

    @Test
    void moverLimitaAoMapa() {
        assertEquals(new Posicao(5, 5), new Posicao(5, 5).mover(1, 1, -5, 5));
        assertEquals(new Posicao(-5, -5), new Posicao(-5, -5).mover(-1, -1, -5, 5));
    }

    @Test
    void moverDentroDoMapaAplicaODeslocamento() {
        assertEquals(new Posicao(1, -1), new Posicao(0, 0).mover(1, -1, -5, 5));
    }

    @Test
    void igualdadeEPorValor() {
        assertEquals(new Posicao(1, 2), new Posicao(1, 2));
        assertEquals(new Posicao(1, 2).hashCode(), new Posicao(1, 2).hashCode());
    }

    @Test
    void moverDevolveNovaInstanciaSemAlterarAOriginal() {
        Posicao original = new Posicao(0, 0);
        Posicao movida = original.mover(1, 0, -5, 5);

        assertNotSame(original, movida);
        assertEquals(new Posicao(0, 0), original);
    }
}
