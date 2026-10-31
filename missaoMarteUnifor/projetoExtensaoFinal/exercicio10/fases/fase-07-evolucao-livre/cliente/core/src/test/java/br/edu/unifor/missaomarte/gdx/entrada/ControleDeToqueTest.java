package br.edu.unifor.missaomarte.gdx.entrada;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.gdx.layout.LayoutDaPartida;
import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ControleDeToqueTest {
    private static final float[][] MUNDOS = {
        {800f, 600f}, {1067f, 600f}, {1333f, 600f}, {800f, 900f}, {800f, 1067f}, {800f, 1422f}, {800f, 1778f}
    };

    private static ControleDeToque controles(float[] mundo, boolean dinamico, float alvoMinimo) {
        LayoutDaPartida layout = LayoutDaPartida.calcular(mundo[0], mundo[1], 11, true, MargensSeguras.NENHUMA);
        return new ControleDeToque(layout.controles(), layout.emRetrato(), dinamico, alvoMinimo);
    }

    private static BotaoVirtual botao(ControleDeToque c, String id) {
        for (BotaoVirtual b : c.getBotoes()) {
            if (b.getId().equals(id)) {
                return b;
            }
        }
        return null;
    }

    private static RetanguloDeTela retangulo(BotaoVirtual b) {
        return new RetanguloDeTela(b.getX(), b.getY(), b.getLargura(), b.getAltura());
    }

    @Test
    void botaoContemPontosDentroDoRetanguloIncluindoAsBordas() {
        BotaoVirtual b = new BotaoVirtual("x", "X", 10f, 20f, 30f, 40f);

        assertTrue(b.contem(10f, 20f));
        assertTrue(b.contem(40f, 60f));
        assertTrue(b.contem(25f, 40f));
        assertFalse(b.contem(9.9f, 40f));
        assertFalse(b.contem(25f, 60.1f));
    }

    @Test
    void modoDinamicoTemSetasTiroPausaSomEAbortarMasNaoEmbarcar() {
        ControleDeToque c = controles(MUNDOS[2], true, 53f);

        assertEquals(8, c.getBotoes().size());
        for (String id : new String[] {"cima", "baixo", "esquerda", "direita", "atirar", "pausa", "som", "abortar"}) {
            assertNotNull(botao(c, id), id);
        }
        assertNull(botao(c, "embarcar"));
    }

    @Test
    void modoClassicoTemEmbarcarEMantemAsFuncoesDaFase6() {
        ControleDeToque c = controles(MUNDOS[2], false, 53f);

        assertEquals(7, c.getBotoes().size());
        assertNotNull(botao(c, "embarcar"));
        assertNull(botao(c, "atirar"));
        assertNull(botao(c, "pausa"));
    }

    @Test
    void botoesFicamDentroDaAreaDeControles() {
        for (float[] mundo : MUNDOS) {
            for (boolean dinamico : new boolean[] {false, true}) {
                LayoutDaPartida layout = LayoutDaPartida.calcular(mundo[0], mundo[1], 11, true, MargensSeguras.NENHUMA);
                ControleDeToque c = new ControleDeToque(layout.controles(), layout.emRetrato(), dinamico, 53f);
                for (BotaoVirtual b : c.getBotoes()) {
                    assertTrue(retangulo(b).estaDentroDe(layout.controles()), b.getId() + " fora em " + mundo[0] + "x" + mundo[1]);
                }
            }
        }
    }

    @Test
    void botoesNaoSeSobrepoemEmNenhumaProporcao() {
        for (float[] mundo : MUNDOS) {
            for (boolean dinamico : new boolean[] {false, true}) {
                List<BotaoVirtual> lista = new ArrayList<>(controles(mundo, dinamico, 53f).getBotoes());
                for (BotaoVirtual a : lista) {
                    for (BotaoVirtual b : lista) {
                        if (a != b) {
                            assertFalse(retangulo(a).sobrepoe(retangulo(b)), a.getId() + " x " + b.getId() + " em " + mundo[0] + "x" + mundo[1]);
                        }
                    }
                }
            }
        }
    }

    @Test
    void cadaBotaoRespeitaOAlvoMinimoDeToque() {
        for (float[] mundo : MUNDOS) {
            for (float alvo : new float[] {32f, 53f, 70f}) {
                for (BotaoVirtual b : controles(mundo, true, alvo).getBotoes()) {
                    assertTrue(b.getLargura() >= alvo - 0.001f && b.getAltura() >= alvo - 0.001f,
                            b.getId() + " menor que " + alvo + " em " + mundo[0] + "x" + mundo[1]);
                }
            }
        }
    }

    @Test
    void asSetasFormamUmaCruzEmPaisagemEEmRetrato() {
        for (float[] mundo : new float[][] {MUNDOS[2], MUNDOS[5]}) {
            ControleDeToque c = controles(mundo, true, 53f);
            BotaoVirtual cima = botao(c, "cima");
            BotaoVirtual baixo = botao(c, "baixo");
            BotaoVirtual esquerda = botao(c, "esquerda");
            BotaoVirtual direita = botao(c, "direita");

            assertTrue(cima.getY() > esquerda.getY() && esquerda.getY() > baixo.getY());
            assertEquals(cima.getX(), baixo.getX(), 0.001f);
            assertEquals(esquerda.getY(), direita.getY(), 0.001f);
            assertTrue(esquerda.getX() < cima.getX() && cima.getX() < direita.getX());
        }
    }

    @Test
    void emRetratoAsSetasFicamAEsquerdaEAsAcoesADireita() {
        ControleDeToque c = controles(MUNDOS[5], true, 53f);

        assertTrue(botao(c, "direita").getX() + botao(c, "direita").getLargura() <= botao(c, "atirar").getX());
        assertTrue(botao(c, "atirar").getX() > 400f);
    }

    @Test
    void emDevolveOBotaoSobOPontoOuNull() {
        ControleDeToque c = controles(MUNDOS[2], true, 53f);
        BotaoVirtual atirar = botao(c, "atirar");

        assertEquals("atirar", c.em(atirar.getX() + 5f, atirar.getY() + 5f).getId());
        assertNull(c.em(0f, 0f));
    }

    @Test
    void traduzBotaoEmAcao() {
        assertEquals(Acao.CIMA, ControleDeToque.acaoDe("cima"));
        assertEquals(Acao.BAIXO, ControleDeToque.acaoDe("baixo"));
        assertEquals(Acao.ESQUERDA, ControleDeToque.acaoDe("esquerda"));
        assertEquals(Acao.DIREITA, ControleDeToque.acaoDe("direita"));
        assertEquals(Acao.EMBARCAR, ControleDeToque.acaoDe("embarcar"));
        assertEquals(Acao.ATIRAR, ControleDeToque.acaoDe("atirar"));
        assertEquals(Acao.SAIR, ControleDeToque.acaoDe("abortar"));
        assertEquals(Acao.INVALIDA, ControleDeToque.acaoDe("som"));
        assertEquals(Acao.INVALIDA, ControleDeToque.acaoDe("pausa"));
        assertEquals(Acao.INVALIDA, ControleDeToque.acaoDe("qualquer"));
    }

    @Test
    void soAsSetasRepetem() {
        assertTrue(ControleDeToque.repete("cima"));
        assertTrue(ControleDeToque.repete("direita"));
        assertTrue(ControleDeToque.ehSeta("baixo"));
        assertFalse(ControleDeToque.repete("embarcar"));
        assertFalse(ControleDeToque.repete("atirar"));
        assertFalse(ControleDeToque.repete("pausa"));
        assertFalse(ControleDeToque.repete("som"));
    }
}
