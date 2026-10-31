package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RepetidorDeTeclaTest {

    @Test
    void naoRepeteAntesDeIniciar() {
        RepetidorDeTecla repetidor = new RepetidorDeTecla(0.3f, 0.1f);

        assertFalse(repetidor.atualizar(1f));
    }

    @Test
    void esperaOAtrasoEDepoisRepetePeloIntervalo() {
        RepetidorDeTecla repetidor = new RepetidorDeTecla(0.3f, 0.1f);
        repetidor.iniciar();

        assertFalse(repetidor.atualizar(0.2f));
        assertTrue(repetidor.atualizar(0.11f));

        assertFalse(repetidor.atualizar(0.05f));
        assertTrue(repetidor.atualizar(0.06f));
        assertTrue(repetidor.atualizar(0.1f));
    }

    @Test
    void naoDisparaMaisDeUmaVezPorChamada() {
        RepetidorDeTecla repetidor = new RepetidorDeTecla(0.3f, 0.1f);
        repetidor.iniciar();

        assertTrue(repetidor.atualizar(5f));
        assertTrue(repetidor.atualizar(0f), "o acumulado excedente gera repeticoes nas chamadas seguintes");
    }

    @Test
    void pararInterrompeERecomecaDoAtraso() {
        RepetidorDeTecla repetidor = new RepetidorDeTecla(0.3f, 0.1f);
        repetidor.iniciar();
        assertTrue(repetidor.atualizar(0.35f));

        repetidor.parar();
        assertFalse(repetidor.atualizar(1f));

        repetidor.iniciar();
        assertFalse(repetidor.atualizar(0.2f));
        assertTrue(repetidor.atualizar(0.11f));
    }
}
