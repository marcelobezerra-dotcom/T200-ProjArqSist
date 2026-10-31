package br.edu.unifor.missaomarte.gdx.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EscalaDeTelaTest {

    @Test
    void alvoMinimoDe48dpConverteParaUnidadesDoMundo() {
        // Celular: densidade 2,6 e 1,8 pixel por unidade (1080 px de altura para 600 unidades).
        assertEquals(48f * 2.6f / 1.8f, EscalaDeTela.alvoMinimoNoMundo(2.6f, 1.8f), 0.001f);
    }

    @Test
    void textoDoCelularCresceAteOTamanhoMinimo() {
        float fator = EscalaDeTela.fatorDeTexto(2.6f, 1.35f);

        assertTrue(fator > 1.4f && fator <= EscalaDeTela.FATOR_MAXIMO_DE_TEXTO, "fator = " + fator);
    }

    @Test
    void textoNuncaFicaMenorQueOPadraoENemPassaDoMaximo() {
        assertEquals(1f, EscalaDeTela.fatorDeTexto(0.6f, 0.9f), 0f);
        assertEquals(EscalaDeTela.FATOR_MAXIMO_DE_TEXTO, EscalaDeTela.fatorDeTexto(4f, 0.5f), 0f);
    }

    @Test
    void valoresInvalidosNaoQuebram() {
        assertEquals(1f, EscalaDeTela.fatorDeTexto(0f, 1f), 0f);
        assertEquals(1f, EscalaDeTela.fatorDeTexto(2f, 0f), 0f);
        assertEquals(0f, EscalaDeTela.alvoMinimoNoMundo(2f, 0f), 0f);
        assertEquals(0f, EscalaDeTela.pixelsParaMundo(10f, 0f), 0f);
        assertEquals(5f, EscalaDeTela.pixelsParaMundo(10f, 2f), 0.001f);
    }
}
