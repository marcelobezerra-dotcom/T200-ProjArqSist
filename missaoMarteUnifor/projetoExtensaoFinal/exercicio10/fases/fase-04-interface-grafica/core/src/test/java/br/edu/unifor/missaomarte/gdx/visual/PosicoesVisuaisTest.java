package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Nave;
import java.util.List;
import org.junit.jupiter.api.Test;

class PosicoesVisuaisTest {

    @Test
    void posicaoVisualAnda_ateOAlvoSemUltrapassar() {
        PosicaoVisual visual = new PosicaoVisual(0f, 0f);
        visual.irPara(2f, 0f);

        visual.atualizar(0.1f, 10f);
        assertEquals(1f, visual.x(), 0.0001f);
        assertTrue(visual.emMovimento());

        visual.atualizar(0.5f, 10f);
        assertEquals(2f, visual.x(), 0.0001f);
        assertFalse(visual.emMovimento());
    }

    @Test
    void posicaoVisualAndaEmLinhaRetaNaDiagonal() {
        PosicaoVisual visual = new PosicaoVisual(0f, 0f);
        visual.irPara(3f, 4f);

        visual.atualizar(0.1f, 10f);

        assertEquals(0.6f, visual.x(), 0.0001f);
        assertEquals(0.8f, visual.y(), 0.0001f);
    }

    @Test
    void objetoNovoComecaNaPosicaoLogicaSemDeslizar() {
        PosicoesVisuais posicoes = new PosicoesVisuais(10f);
        Nave nave = new Nave(2, 3, 3, 1);

        posicoes.sincronizar(List.of(nave), 0.016f);

        assertEquals(2f, posicoes.de(nave).x());
        assertEquals(3f, posicoes.de(nave).y());
    }

    @Test
    void quandoOObjetoMudaDeCelulaAPosicaoVisualDesliza() {
        PosicoesVisuais posicoes = new PosicoesVisuais(10f);
        Nave nave = new Nave(0, 0, 3, 1);
        posicoes.sincronizar(List.of(nave), 0.016f);

        nave.moverComLimites(1, 0, -5, 5);
        posicoes.sincronizar(List.of(nave), 0.05f);

        assertEquals(0.5f, posicoes.de(nave).x(), 0.0001f);

        posicoes.sincronizar(List.of(nave), 0.2f);

        assertEquals(1f, posicoes.de(nave).x(), 0.0001f);
    }

    @Test
    void objetosQueSaemDoMapaSaoEsquecidos() {
        PosicoesVisuais posicoes = new PosicoesVisuais(10f);
        Asteroide a = new Asteroide(1, 1);
        Asteroide b = new Asteroide(2, 2);
        posicoes.sincronizar(List.of(a, b), 0.016f);
        assertEquals(2, posicoes.quantidade());

        posicoes.sincronizar(List.of(a), 0.016f);

        assertEquals(1, posicoes.quantidade());
    }
}
