package br.edu.unifor.missaomarte.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;

class MovimentoDoInimigoTest {
    private static final int MIN = -5;
    private static final int MAX = 5;

    @Test
    void perseguidorAndaNoEixoDeMaiorDistancia() {
        MovimentoPerseguidor p = MovimentoPerseguidor.INSTANCIA;

        assertEquals(new Posicao(2, 0), p.proxima(new Posicao(3, 0), new Posicao(0, 0), MIN, MAX, null));
        assertEquals(new Posicao(0, -2), p.proxima(new Posicao(0, -3), new Posicao(0, 0), MIN, MAX, null));
        assertEquals(new Posicao(2, 1), p.proxima(new Posicao(3, 1), new Posicao(0, 2), MIN, MAX, null));
        assertEquals(new Posicao(-1, 3), p.proxima(new Posicao(-1, 4), new Posicao(0, 0), MIN, MAX, null));
    }

    @Test
    void perseguidorNoEmpateAndaNoEixoX() {
        Posicao proxima = MovimentoPerseguidor.INSTANCIA.proxima(new Posicao(3, 3), new Posicao(0, 0), MIN, MAX, null);

        assertEquals(new Posicao(2, 3), proxima);
    }

    @Test
    void perseguidorFicaParadoNoAlvoOuSemAlvo() {
        Posicao atual = new Posicao(2, 2);

        assertEquals(atual, MovimentoPerseguidor.INSTANCIA.proxima(atual, new Posicao(2, 2), MIN, MAX, null));
        assertEquals(atual, MovimentoPerseguidor.INSTANCIA.proxima(atual, null, MIN, MAX, null));
    }

    @Test
    void perseguidorNaoUsaSorteio() {
        Random random = new Random(7);
        long antes = new Random(7).nextLong();

        MovimentoPerseguidor.INSTANCIA.proxima(new Posicao(3, 0), new Posicao(0, 0), MIN, MAX, random);

        assertEquals(antes, random.nextLong());
    }

    @Test
    void aleatorioFazOsMesmosSorteiosDasFasesAnteriores() {
        Random a = new Random(42);
        Random b = new Random(42);
        int dx = b.nextInt(3) - 1;
        int dy = b.nextInt(3) - 1;

        Posicao proxima = MovimentoAleatorio.INSTANCIA.proxima(new Posicao(0, 0), null, MIN, MAX, a);

        assertEquals(new Posicao(0, 0).mover(dx, dy, MIN, MAX), proxima);
    }

    @Test
    void inimigoComumMoveAoAcasoEPerseguidorSegueOAlvo() {
        Inimigo comum = new Inimigo(1, 1);
        Inimigo perseguidor = new Inimigo(4, 0, MovimentoPerseguidor.INSTANCIA);

        assertFalse(comum.persegue());
        assertEquals('X', comum.getSimbolo());
        assertTrue(perseguidor.persegue());
        assertEquals('C', perseguidor.getSimbolo());

        perseguidor.mover(new Posicao(0, 0), MIN, MAX, new Random(1));

        assertEquals(new Posicao(3, 0), perseguidor.getPosicao());
    }

    @Test
    void inimigoComumIgnoraOAlvoAoMover() {
        Inimigo a = new Inimigo(0, 0);
        Inimigo b = new Inimigo(0, 0);

        a.mover(new Posicao(5, 5), MIN, MAX, new Random(3));
        b.moverAleatoriamente(MIN, MAX, new Random(3));

        assertEquals(b.getPosicao(), a.getPosicao());
    }

    @Test
    void missaoSeparaInimigosComunsEPerseguidores() {
        Missao missao = new Missao(new Nave(0, 0, 3, 1));
        Inimigo comum = new Inimigo(3, 3);
        Inimigo perseguidor = new Inimigo(4, 0, MovimentoPerseguidor.INSTANCIA);
        missao.adicionarInimigo(comum);
        missao.adicionarInimigo(perseguidor);

        missao.moverPerseguidores(MIN, MAX, new Random(1));

        assertEquals(new Posicao(3, 3), comum.getPosicao());
        assertEquals(new Posicao(3, 0), perseguidor.getPosicao());
        assertTrue(missao.temPerseguidores());
        assertSame(perseguidor, missao.getInimigos().get(1));
    }
}
