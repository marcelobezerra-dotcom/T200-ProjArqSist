package br.edu.unifor.missaomarte.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TiroTest {
    private static final int MIN = -5;
    private static final int MAX = 5;

    private final Missao missao = new Missao(new Nave(0, 0, 3, 2));

    @Test
    void naveGastaMunicaoAteZerar() {
        Nave nave = missao.getNave();
        nave.carregar(2);

        assertTrue(nave.gastarMunicao());
        assertTrue(nave.gastarMunicao());
        assertFalse(nave.gastarMunicao());
        assertEquals(0, nave.getMunicao());
    }

    @Test
    void carregarNaoAceitaValorNegativo() {
        missao.getNave().carregar(-4);

        assertEquals(0, missao.getNave().getMunicao());
    }

    @Test
    void tiroDestroiOPrimeiroAsteroideDaLinha() {
        Asteroide perto = new Asteroide(2, 0);
        Asteroide longe = new Asteroide(4, 0);
        missao.adicionarAsteroide(perto);
        missao.adicionarAsteroide(longe);

        ResultadoDoTiro r = missao.disparar(new Posicao(0, 0), 1, 0, MIN, MAX);

        assertTrue(r.acertou());
        assertSame(perto, r.getAtingido());
        assertEquals(new Posicao(2, 0), r.getDestino());
        assertEquals(1, missao.getAsteroides().size());
        assertSame(longe, missao.getAsteroides().get(0));
    }

    @Test
    void tiroDestroiInimigo() {
        Inimigo inimigo = new Inimigo(0, -3);
        missao.adicionarInimigo(inimigo);

        ResultadoDoTiro r = missao.disparar(new Posicao(0, 0), 0, -1, MIN, MAX);

        assertSame(inimigo, r.getAtingido());
        assertTrue(missao.getInimigos().isEmpty());
    }

    @Test
    void naMesmaCelulaOInimigoEAtingidoAntesDoAsteroide() {
        Asteroide asteroide = new Asteroide(3, 0);
        Inimigo inimigo = new Inimigo(3, 0);
        missao.adicionarAsteroide(asteroide);
        missao.adicionarInimigo(inimigo);

        ResultadoDoTiro r = missao.disparar(new Posicao(0, 0), 1, 0, MIN, MAX);

        assertSame(inimigo, r.getAtingido());
        assertEquals(1, missao.getAsteroides().size());
    }

    @Test
    void tiroSemAlvoVaiAteABordaEnaoDestroiNada() {
        missao.adicionarAsteroide(new Asteroide(0, 3));

        ResultadoDoTiro r = missao.disparar(new Posicao(0, 0), -1, 0, MIN, MAX);

        assertFalse(r.acertou());
        assertNull(r.getAtingido());
        assertEquals(new Posicao(MIN, 0), r.getDestino());
        assertEquals(1, missao.getAsteroides().size());
    }

    @Test
    void tiroNaoAtingeNadaForaDaLinha() {
        missao.adicionarInimigo(new Inimigo(2, 1));

        ResultadoDoTiro r = missao.disparar(new Posicao(0, 0), 1, 0, MIN, MAX);

        assertFalse(r.acertou());
        assertEquals(1, missao.getInimigos().size());
    }

    @Test
    void tiroDaBordaNaoAndaParaFora() {
        ResultadoDoTiro r = missao.disparar(new Posicao(MAX, 0), 1, 0, MIN, MAX);

        assertFalse(r.acertou());
        assertEquals(new Posicao(MAX, 0), r.getDestino());
    }
}
