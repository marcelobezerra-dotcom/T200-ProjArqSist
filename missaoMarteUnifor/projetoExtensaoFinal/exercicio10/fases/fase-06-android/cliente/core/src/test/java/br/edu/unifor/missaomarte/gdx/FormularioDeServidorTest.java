package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.servidor.ConfiguracaoDoServidor;
import br.edu.unifor.missaomarte.application.servidor.ConfiguracaoInvalidaException;
import org.junit.jupiter.api.Test;

class FormularioDeServidorTest {

    private static void digitar(FormularioDeServidor f, String texto) {
        for (char c : texto.toCharArray()) {
            f.digitar(c);
        }
    }

    private static FormularioDeServidor vazio() {
        FormularioDeServidor f = new FormularioDeServidor(ConfiguracaoDoServidor.PADRAO);
        f.definirTexto(FormularioDeServidor.Campo.ENDERECO, "");
        f.definirTexto(FormularioDeServidor.Campo.PORTA, "");
        return f;
    }

    @Test
    void comecaComOsValoresDaConfiguracaoAtual() {
        FormularioDeServidor f = new FormularioDeServidor(ConfiguracaoDoServidor.criar("meu.host", "443", true));

        assertEquals("meu.host", f.getEndereco());
        assertEquals("443", f.getPorta());
        assertTrue(f.isHttps());
        assertEquals(FormularioDeServidor.Campo.ENDERECO, f.getCampo());
    }

    @Test
    void enderecoAceitaSoLetrasNumerosPontoEHifen() {
        FormularioDeServidor f = vazio();

        digitar(f, "Meu-Host.com/x y:8080@");

        assertEquals("Meu-Host.comxy8080", f.getEndereco());
    }

    @Test
    void portaAceitaSoDigitosAteCinco() {
        FormularioDeServidor f = vazio();
        f.alternarCampo();

        digitar(f, "80a80-123456");

        assertEquals("80801", f.getPorta());
        assertFalse(f.digitar('9'));
    }

    @Test
    void apagarERemoveSoDoCampoAtual() {
        FormularioDeServidor f = vazio();
        digitar(f, "host");
        f.alternarCampo();
        digitar(f, "8080");

        f.apagar();

        assertEquals("host", f.getEndereco());
        assertEquals("808", f.getPorta());
    }

    @Test
    void definirTextoSubstituiOCampoFiltrandoInvalidos() {
        FormularioDeServidor f = new FormularioDeServidor(ConfiguracaoDoServidor.PADRAO);

        f.definirTexto(FormularioDeServidor.Campo.ENDERECO, "http://192.168.0.10");
        f.definirTexto(FormularioDeServidor.Campo.PORTA, "9x0y90");

        assertEquals("http192.168.0.10", f.getEndereco());
        assertEquals("9090", f.getPorta());
        assertEquals(FormularioDeServidor.Campo.ENDERECO, f.getCampo());
    }

    @Test
    void definirTextoNuloLimpaOCampo() {
        FormularioDeServidor f = new FormularioDeServidor(ConfiguracaoDoServidor.PADRAO);

        f.definirTexto(FormularioDeServidor.Campo.ENDERECO, null);

        assertEquals("", f.getEndereco());
    }

    @Test
    void alternarHttpsInverteOEsquema() {
        FormularioDeServidor f = new FormularioDeServidor(ConfiguracaoDoServidor.PADRAO);

        f.alternarHttps();

        assertTrue(f.isHttps());
        assertEquals("https://localhost:8080", f.montar().urlBase());
    }

    @Test
    void montarValidaOsCampos() {
        FormularioDeServidor f = vazio();
        assertThrows(ConfiguracaoInvalidaException.class, f::montar);

        digitar(f, "192.168.0.10");
        f.alternarCampo();
        digitar(f, "8080");

        assertEquals("http://192.168.0.10:8080", f.montar().urlBase());
    }

    @Test
    void montarRecusaIpInvalido() {
        FormularioDeServidor f = vazio();
        digitar(f, "999.1.1.1");
        f.alternarCampo();
        digitar(f, "8080");

        assertThrows(ConfiguracaoInvalidaException.class, f::montar);
    }
}
