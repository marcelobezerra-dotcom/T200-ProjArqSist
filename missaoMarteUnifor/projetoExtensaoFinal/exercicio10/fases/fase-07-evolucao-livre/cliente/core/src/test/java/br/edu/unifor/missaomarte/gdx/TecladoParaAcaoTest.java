package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.application.Acao;
import com.badlogic.gdx.Input.Keys;
import org.junit.jupiter.api.Test;

class TecladoParaAcaoTest {

    @Test
    void wasdEmSetasMapeiamMovimento() {
        assertEquals(Acao.CIMA, TecladoParaAcao.converter(Keys.W));
        assertEquals(Acao.CIMA, TecladoParaAcao.converter(Keys.UP));
        assertEquals(Acao.BAIXO, TecladoParaAcao.converter(Keys.S));
        assertEquals(Acao.BAIXO, TecladoParaAcao.converter(Keys.DOWN));
        assertEquals(Acao.ESQUERDA, TecladoParaAcao.converter(Keys.A));
        assertEquals(Acao.ESQUERDA, TecladoParaAcao.converter(Keys.LEFT));
        assertEquals(Acao.DIREITA, TecladoParaAcao.converter(Keys.D));
        assertEquals(Acao.DIREITA, TecladoParaAcao.converter(Keys.RIGHT));
    }

    @Test
    void embarcarESair() {
        assertEquals(Acao.EMBARCAR, TecladoParaAcao.converter(Keys.E));
        assertEquals(Acao.SAIR, TecladoParaAcao.converter(Keys.Q));
        assertEquals(Acao.SAIR, TecladoParaAcao.converter(Keys.ESCAPE));
    }

    @Test
    void controleBluetoothMapeiaDirecionalAEBotoes() {
        assertEquals(Acao.CIMA, TecladoParaAcao.converter(Keys.DPAD_UP));
        assertEquals(Acao.BAIXO, TecladoParaAcao.converter(Keys.DPAD_DOWN));
        assertEquals(Acao.ESQUERDA, TecladoParaAcao.converter(Keys.DPAD_LEFT));
        assertEquals(Acao.DIREITA, TecladoParaAcao.converter(Keys.DPAD_RIGHT));
        assertEquals(Acao.EMBARCAR, TecladoParaAcao.converter(Keys.BUTTON_A));
        assertEquals(Acao.EMBARCAR, TecladoParaAcao.converter(Keys.DPAD_CENTER));
        assertEquals(Acao.SAIR, TecladoParaAcao.converter(Keys.BUTTON_B));
    }

    @Test
    void teclaVoltarDoAndroidAborta() {
        assertEquals(Acao.SAIR, TecladoParaAcao.converter(Keys.BACK));
    }

    @Test
    void teclaDesconhecidaEInvalida() {
        assertEquals(Acao.INVALIDA, TecladoParaAcao.converter(Keys.SPACE));
        assertEquals(Acao.INVALIDA, TecladoParaAcao.converter(Keys.ENTER));
        assertEquals(Acao.INVALIDA, TecladoParaAcao.converter(-1));
    }
}
