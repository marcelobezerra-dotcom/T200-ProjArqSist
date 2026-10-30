package br.edu.unifor.missaomarte.gdx.entrada;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.Acao;
import org.junit.jupiter.api.Test;

class ControleDeToqueTest {

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
    void temSetasEmbarcarSomEAbortar() {
        ControleDeToque c = new ControleDeToque(1067f);

        assertEquals(7, c.getBotoes().size());
        for (String id : new String[] {"cima", "baixo", "esquerda", "direita", "embarcar", "som", "abortar"}) {
            assertTrue(c.getBotoes().stream().anyMatch(b -> b.getId().equals(id)), id);
        }
    }

    @Test
    void botoesFicamNosUltimos260PixelsEDentroDaAreaUtil() {
        for (float largura : new float[] {800f, 1067f, 1333f}) {
            ControleDeToque c = new ControleDeToque(largura);
            for (BotaoVirtual b : c.getBotoes()) {
                assertTrue(b.getX() >= largura - ControleDeToque.LARGURA_DA_AREA, b.getId() + " em " + largura);
                assertTrue(b.getX() + b.getLargura() <= largura, b.getId() + " em " + largura);
                assertTrue(b.getY() >= 36f && b.getY() + b.getAltura() <= 564f, b.getId());
            }
        }
    }

    @Test
    void botoesNaoSeSobrepoem() {
        ControleDeToque c = new ControleDeToque(1067f);
        for (BotaoVirtual a : c.getBotoes()) {
            for (BotaoVirtual b : c.getBotoes()) {
                if (a == b) {
                    continue;
                }
                boolean separados = a.getX() + a.getLargura() <= b.getX() || b.getX() + b.getLargura() <= a.getX()
                        || a.getY() + a.getAltura() <= b.getY() || b.getY() + b.getAltura() <= a.getY();
                assertTrue(separados, a.getId() + " x " + b.getId());
            }
        }
    }

    @Test
    void acompanhaALarguraDoMundo() {
        BotaoVirtual estreito = new ControleDeToque(800f).em(670f, 440f);
        BotaoVirtual largo = new ControleDeToque(1333f).em(1203f, 440f);

        assertEquals("cima", estreito.getId());
        assertEquals("cima", largo.getId());
        assertNull(new ControleDeToque(1333f).em(670f, 440f));
    }

    @Test
    void emDevolveOBotaoSobOPontoOuNull() {
        ControleDeToque c = new ControleDeToque(800f);
        BotaoVirtual embarcar = c.getBotoes().stream().filter(b -> b.getId().equals("embarcar")).findFirst().get();

        assertEquals("embarcar", c.em(embarcar.getX() + 5f, embarcar.getY() + 5f).getId());
        assertNull(c.em(0f, 0f));
        assertNotNull(c.em(embarcar.getX() + 1f, embarcar.getY() + 1f));
    }

    @Test
    void traduzBotaoEmAcao() {
        assertEquals(Acao.CIMA, ControleDeToque.acaoDe("cima"));
        assertEquals(Acao.BAIXO, ControleDeToque.acaoDe("baixo"));
        assertEquals(Acao.ESQUERDA, ControleDeToque.acaoDe("esquerda"));
        assertEquals(Acao.DIREITA, ControleDeToque.acaoDe("direita"));
        assertEquals(Acao.EMBARCAR, ControleDeToque.acaoDe("embarcar"));
        assertEquals(Acao.SAIR, ControleDeToque.acaoDe("abortar"));
        assertEquals(Acao.INVALIDA, ControleDeToque.acaoDe("som"));
        assertEquals(Acao.INVALIDA, ControleDeToque.acaoDe("qualquer"));
    }

    @Test
    void soAsSetasRepetem() {
        assertTrue(ControleDeToque.repete("cima"));
        assertTrue(ControleDeToque.repete("direita"));
        assertFalse(ControleDeToque.repete("embarcar"));
        assertFalse(ControleDeToque.repete("abortar"));
        assertFalse(ControleDeToque.repete("som"));
    }
}
