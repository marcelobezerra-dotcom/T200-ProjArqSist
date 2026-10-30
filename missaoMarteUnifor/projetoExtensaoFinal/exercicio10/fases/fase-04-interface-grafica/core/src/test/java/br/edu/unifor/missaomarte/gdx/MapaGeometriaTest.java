package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MapaGeometriaTest {
    // Grade de -5 a 5, celula 40, canto inferior esquerdo em (20, 80).
    private final MapaGeometria mapa = new MapaGeometria(-5, 5, 40f, 20f, 80f);

    @Test
    void xCrescePraDireita() {
        assertEquals(20f, mapa.xCanto(-5));
        assertEquals(220f, mapa.xCanto(0));
        assertEquals(420f, mapa.xCanto(5));
    }

    @Test
    void yDoJogoCrescePraBaixoENaTelaPraCima() {
        assertEquals(480f, mapa.yCanto(-5));
        assertEquals(280f, mapa.yCanto(0));
        assertEquals(80f, mapa.yCanto(5));
        assertTrue(mapa.yCanto(-1) > mapa.yCanto(0));
    }

    @Test
    void celulaFracionariaFicaEntreAsCelulasVizinhas() {
        assertEquals(240f, mapa.xCanto(0.5f));
        assertEquals(260f, mapa.yCanto(0.5f));
        assertEquals((mapa.xCanto(0) + mapa.xCanto(1)) / 2f, mapa.xCanto(0.5f));
    }

    @Test
    void centroFicaNoMeioDaCelula() {
        assertEquals(240f, mapa.xCentro(0));
        assertEquals(300f, mapa.yCentro(0));
        assertEquals(40f, mapa.celula());
    }
}
