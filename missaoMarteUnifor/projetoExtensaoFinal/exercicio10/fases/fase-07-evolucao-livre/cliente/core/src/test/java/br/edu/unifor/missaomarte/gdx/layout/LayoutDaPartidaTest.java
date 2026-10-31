package br.edu.unifor.missaomarte.gdx.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LayoutDaPartidaTest {
    private static final int CELULAS = 11;

    /** Mundos de um ExtendViewport(800, 600): paisagem de varias proporcoes e retrato de varias proporcoes. */
    private static final float[][] MUNDOS = {
        {800f, 600f}, {960f, 600f}, {1067f, 600f}, {1200f, 600f}, {1333f, 600f}, {1422f, 600f},
        {800f, 800f}, {800f, 1067f}, {800f, 1200f}, {800f, 1422f}, {800f, 1778f}
    };
    private static final MargensSeguras[] MARGENS = {
        MargensSeguras.NENHUMA, new MargensSeguras(40f, 24f, 20f, 16f), new MargensSeguras(0f, 0f, 30f, 12f)
    };

    private static LayoutDaPartida calcular(float[] mundo, boolean toque, MargensSeguras margens) {
        return LayoutDaPartida.calcular(mundo[0], mundo[1], CELULAS, toque, margens);
    }

    @Test
    void semToqueEm800x600MantemOLayoutFixoDasFasesAnteriores() {
        LayoutDaPartida l = LayoutDaPartida.calcular(800f, 600f, CELULAS, false, MargensSeguras.NENHUMA);

        assertEquals(LayoutDaPartida.Orientacao.PAISAGEM, l.orientacao());
        assertEquals(24f, l.origemX(), 0.001f);
        assertEquals(36f, l.origemY(), 0.001f);
        assertEquals(48f, l.celula(), 0.001f);
        assertEquals(570f, l.painel().x(), 0.001f);
        assertEquals(230f, l.painel().largura(), 0.001f);
        assertEquals(36f, l.painel().y(), 0.001f);
        assertEquals(528f, l.painel().altura(), 0.001f);
        assertEquals(564f, l.barraSuperior().y(), 0.001f);
        assertEquals(36f, l.barraSuperior().altura(), 0.001f);
        assertEquals(36f, l.barraInferior().altura(), 0.001f);
        assertEquals(0f, l.controles().largura(), 0f);
    }

    @Test
    void orientacaoSegueAProporcaoDoMundo() {
        assertFalse(LayoutDaPartida.calcular(1333f, 600f, CELULAS, true, MargensSeguras.NENHUMA).emRetrato());
        assertTrue(LayoutDaPartida.calcular(800f, 1422f, CELULAS, true, MargensSeguras.NENHUMA).emRetrato());
    }

    @Test
    void oMapaEQuadradoEACelulaAcompanhaOTamanho() {
        for (float[] mundo : MUNDOS) {
            for (boolean toque : new boolean[] {false, true}) {
                LayoutDaPartida l = calcular(mundo, toque, MargensSeguras.NENHUMA);

                assertEquals(l.mapa().largura(), l.mapa().altura(), 0.001f);
                assertEquals(l.mapa().largura(), l.celula() * CELULAS, 0.001f);
                assertTrue(l.celula() >= LayoutDaPartida.CELULA_MINIMA - 0.001f, "celula pequena em " + mundo[0] + "x" + mundo[1]);
            }
        }
    }

    @Test
    void asAreasFicamDentroDoMundoESemSobreposicao() {
        for (float[] mundo : MUNDOS) {
            for (MargensSeguras margens : MARGENS) {
                for (boolean toque : new boolean[] {false, true}) {
                    LayoutDaPartida l = calcular(mundo, toque, margens);
                    RetanguloDeTela mundoTodo = new RetanguloDeTela(0f, 0f, mundo[0], mundo[1]);
                    String contexto = mundo[0] + "x" + mundo[1] + " toque=" + toque;

                    assertTrue(l.mapa().estaDentroDe(mundoTodo), "mapa fora em " + contexto);
                    assertTrue(l.painel().estaDentroDe(mundoTodo), "painel fora em " + contexto);
                    assertTrue(l.controles().estaDentroDe(mundoTodo), "controles fora em " + contexto);

                    assertFalse(l.mapa().sobrepoe(l.barraSuperior()), "mapa sobre a barra superior em " + contexto);
                    assertFalse(l.mapa().sobrepoe(l.barraInferior()), "mapa sobre a barra inferior em " + contexto);
                    assertFalse(l.painel().sobrepoe(l.barraSuperior()), "painel sobre a barra superior em " + contexto);
                    assertFalse(l.painel().sobrepoe(l.barraInferior()), "painel sobre a barra inferior em " + contexto);
                    assertFalse(l.mapa().sobrepoe(l.painel()), "mapa sobre o painel em " + contexto);
                    if (toque) {
                        assertFalse(l.mapa().sobrepoe(l.controles()), "mapa sobre os controles em " + contexto);
                        assertFalse(l.painel().sobrepoe(l.controles()), "painel sobre os controles em " + contexto);
                        assertFalse(l.controles().sobrepoe(l.barraSuperior()), "controles sobre a barra em " + contexto);
                        assertFalse(l.controles().sobrepoe(l.barraInferior()), "controles sobre a barra em " + contexto);
                    }
                }
            }
        }
    }

    @Test
    void asMargensSegurasSaoRespeitadas() {
        MargensSeguras margens = new MargensSeguras(40f, 24f, 20f, 16f);
        for (float[] mundo : MUNDOS) {
            LayoutDaPartida l = calcular(mundo, true, margens);

            assertTrue(l.mapa().x() >= margens.esquerda(), "mapa na area do recorte em " + mundo[0] + "x" + mundo[1]);
            assertTrue(l.mapa().direita() <= mundo[0] - margens.direita() + 0.001f);
            assertTrue(l.controles().direita() <= mundo[0] - margens.direita() + 0.001f);
            assertTrue(l.controles().x() >= margens.esquerda() - 0.001f);
            assertTrue(l.painel().y() >= 16f - 0.001f);
            assertEquals(mundo[1] - 36f - margens.cima(), l.barraSuperior().y(), 0.001f);
        }
    }

    @Test
    void emPaisagemOMapaCresceComOEspacoEOPainelFicaAoLado() {
        LayoutDaPartida estreito = LayoutDaPartida.calcular(800f, 600f, CELULAS, true, MargensSeguras.NENHUMA);
        LayoutDaPartida largo = LayoutDaPartida.calcular(1333f, 600f, CELULAS, true, MargensSeguras.NENHUMA);

        assertTrue(estreito.mapa().largura() < largo.mapa().largura());
        assertEquals(528f, largo.mapa().largura(), 0.001f);
        assertTrue(largo.painel().x() >= largo.mapa().direita());
        assertEquals(largo.larguraDoMundo(), largo.controles().direita(), 0.001f);
        assertEquals(LayoutDaPartida.CONTROLES_LARGURA, largo.controles().largura(), 0.001f);
    }

    @Test
    void emRetratoOMapaUsaALarguraEOsControlesFicamEmbaixo() {
        LayoutDaPartida l = LayoutDaPartida.calcular(800f, 1422f, CELULAS, true, MargensSeguras.NENHUMA);

        assertTrue(l.emRetrato());
        assertEquals(776f, l.mapa().largura(), 0.001f);
        assertTrue(l.painel().topo() <= l.mapa().y());
        assertTrue(l.controles().topo() <= l.painel().y());
        assertTrue(l.controles().altura() >= LayoutDaPartida.CONTROLES_ALTURA_MINIMA - 0.001f);
        assertEquals(l.larguraDoMundo(), l.controles().largura(), 0.001f);
    }

    @Test
    void emRetratoBaixoOMapaEncolhePraDarEspacoAosControles() {
        LayoutDaPartida l = LayoutDaPartida.calcular(800f, 900f, CELULAS, true, MargensSeguras.NENHUMA);

        assertTrue(l.mapa().largura() < 776f);
        assertTrue(l.controles().altura() >= LayoutDaPartida.CONTROLES_ALTURA_MINIMA - 0.001f);
    }

    @Test
    void emRetratoSemToqueNaoReservaEspacoParaControles() {
        LayoutDaPartida l = LayoutDaPartida.calcular(800f, 1422f, CELULAS, false, MargensSeguras.NENHUMA);

        assertEquals(0f, l.controles().altura(), 0f);
        assertEquals(776f, l.mapa().largura(), 0.001f);
    }
}
