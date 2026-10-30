package br.edu.unifor.missaomarte.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NaveTest {

    @Test
    void embarcaAteACapacidade() {
        Nave nave = new Nave(0, 0, 3, 2);

        assertTrue(nave.embarcar(new Professor("P1", 1, 0)));
        assertTrue(nave.embarcar(new Engenheiro("E1", 2, 0)));
        assertTrue(nave.estaCheia());
        assertFalse(nave.embarcar(new Astronauta("A1", 3, 0)));
        assertEquals(2, nave.getPassageiros().size());
    }

    @Test
    void desembarcarTodosLiberaACapacidade() {
        Nave nave = new Nave(0, 0, 3, 2);
        nave.embarcar(new Professor("P1", 1, 0));
        nave.embarcar(new Engenheiro("E1", 2, 0));

        assertEquals(2, nave.desembarcarTodos());
        assertTrue(nave.getPassageiros().isEmpty());
        assertFalse(nave.estaCheia());
    }

    @Test
    void perderVidaReduzUmaVida() {
        Nave nave = new Nave(0, 0, 3, 1);

        nave.perderVida();

        assertEquals(2, nave.getVidas());
    }

    @Test
    void moveRespeitandoOsLimites() {
        Nave nave = new Nave(5, 5, 3, 1);

        nave.moverComLimites(1, 1, -5, 5);

        assertEquals(new Posicao(5, 5), nave.getPosicao());
    }

    @Test
    void passageirosTemPontuacaoESimboloProprios() {
        assertEquals(20, new Professor("P", 0, 0).getPontuacao());
        assertEquals(15, new Engenheiro("E", 0, 0).getPontuacao());
        assertEquals(25, new Astronauta("A", 0, 0).getPontuacao());
        assertEquals('P', new Professor("P", 0, 0).getSimbolo());
        assertEquals('E', new Engenheiro("E", 0, 0).getSimbolo());
        assertEquals('A', new Astronauta("A", 0, 0).getSimbolo());
    }
}
