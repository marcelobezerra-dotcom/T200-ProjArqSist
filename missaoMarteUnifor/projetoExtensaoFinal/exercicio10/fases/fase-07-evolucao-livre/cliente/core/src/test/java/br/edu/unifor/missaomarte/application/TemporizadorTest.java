package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TemporizadorTest {

    @Test
    void naoAceitaIntervaloNaoPositivo() {
        assertThrows(IllegalArgumentException.class, () -> new Temporizador(0f));
        assertThrows(IllegalArgumentException.class, () -> new Temporizador(-1f));
    }

    @Test
    void contaSoOsIntervalosCompletos() {
        Temporizador t = new Temporizador(1f);

        assertEquals(0, t.avancar(0.4f));
        assertEquals(0, t.avancar(0.4f));
        assertEquals(1, t.avancar(0.4f));
        assertEquals(0, t.avancar(0.5f));
        assertEquals(1, t.avancar(0.5f));
    }

    @Test
    void umAvancoGrandeDevolveVariosPassos() {
        Temporizador t = new Temporizador(0.5f);

        assertEquals(3, t.avancar(1.6f));
        assertEquals(1, t.avancar(0.4f));
    }

    @Test
    void ignoraTempoNegativoOuZero() {
        Temporizador t = new Temporizador(1f);
        t.avancar(0.5f);

        assertEquals(0, t.avancar(-3f));
        assertEquals(0, t.avancar(0f));
        assertEquals(1, t.avancar(0.5f));
    }

    @Test
    void passoFixoNaoDependeDaTaxaDeQuadros() {
        Temporizador rapido = new Temporizador(0.25f);
        Temporizador lento = new Temporizador(0.25f);
        int passosRapidos = 0;
        for (int i = 0; i < 120; i++) {
            passosRapidos += rapido.avancar(1f / 60f);
        }
        int passosLentos = 0;
        for (int i = 0; i < 30; i++) {
            passosLentos += lento.avancar(1f / 15f);
        }

        assertEquals(passosLentos, passosRapidos);
        assertEquals(8, passosRapidos);
    }

    @Test
    void reiniciarZeraOAcumulado() {
        Temporizador t = new Temporizador(1f);
        t.avancar(0.9f);

        t.reiniciar();

        assertEquals(0, t.avancar(0.2f));
    }
}
