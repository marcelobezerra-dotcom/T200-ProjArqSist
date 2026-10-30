package br.edu.unifor.missaomarte.application.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConfiguracaoDoServidorTest {

    @Test
    void padraoEhLocalhostNaPorta8080() {
        assertEquals("http://localhost:8080", ConfiguracaoDoServidor.PADRAO.urlBase());
    }

    @Test
    void montaUrlComPortaEEsquema() {
        assertEquals("http://192.168.0.10:8080", ConfiguracaoDoServidor.criar("192.168.0.10", "8080", false).urlBase());
        assertEquals("https://meu-jogo.onrender.com", ConfiguracaoDoServidor.criar("meu-jogo.onrender.com", "443", true).urlBase());
        assertEquals("https://meu.servidor.com:8443", ConfiguracaoDoServidor.criar("meu.servidor.com", 8443, true).urlBase());
    }

    @Test
    void enderecoEhNormalizadoParaMinusculasSemEspacos() {
        assertEquals("http://meu.host:9000", ConfiguracaoDoServidor.criar("  Meu.Host ", " 9000 ", false).urlBase());
    }

    @Test
    void recusaEnderecoVazioOuComCaracteresInvalidos() {
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("", "8080", false));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("   ", "8080", false));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("http://x", "8080", false));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("a/b", "8080", false));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("a b", "8080", false));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("a@b.com", "8080", false));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("host:80", "8080", false));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("-host.com", "8080", false));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("host..com", "8080", false));
    }

    @Test
    void recusaIpv4Invalido() {
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("256.1.1.1", "8080", false));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("192.168.0", "8080", false));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("1.2.3.4.5", "8080", false));
    }

    @Test
    void recusaEnderecoMuitoLongo() {
        StringBuilder longo = new StringBuilder("a");
        for (int i = 0; i < 260; i++) {
            longo.append('a');
        }
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar(longo.toString(), "8080", false));
    }

    @Test
    void portaDeveSerNumeroEntre1E65535() {
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("host", "", false));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("host", "abc", false));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("host", "0", false));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("host", "65536", false));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.criar("host", "-1", false));
        assertEquals(65535, ConfiguracaoDoServidor.criar("host", "65535", false).getPorta());
        assertEquals(1, ConfiguracaoDoServidor.criar("host", "1", false).getPorta());
    }

    @Test
    void deUrlLeEsquemaHostEPorta() {
        ConfiguracaoDoServidor c = ConfiguracaoDoServidor.deUrl("http://10.0.2.2:8080");

        assertEquals("10.0.2.2", c.getHost());
        assertEquals(8080, c.getPorta());
        assertFalse(c.isHttps());
        assertEquals("https://meu.servidor.com", ConfiguracaoDoServidor.deUrl("https://meu.servidor.com/").urlBase());
        assertEquals(443, ConfiguracaoDoServidor.deUrl("https://meu.servidor.com").getPorta());
    }

    @Test
    void deUrlRecusaFormatosInesperados() {
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.deUrl(null));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.deUrl("localhost:8080"));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.deUrl("ftp://host"));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.deUrl("http://host/api"));
        assertThrows(ConfiguracaoInvalidaException.class, () -> ConfiguracaoDoServidor.deUrl("http://user@host"));
    }

    @Test
    void reconheceLocalERedePrivada() {
        assertTrue(ConfiguracaoDoServidor.criar("localhost", "8080", false).isLocalOuRedePrivada());
        assertTrue(ConfiguracaoDoServidor.criar("127.0.0.1", "8080", false).isLocalOuRedePrivada());
        assertTrue(ConfiguracaoDoServidor.criar("10.0.2.2", "8080", false).isLocalOuRedePrivada());
        assertTrue(ConfiguracaoDoServidor.criar("192.168.1.20", "8080", false).isLocalOuRedePrivada());
        assertTrue(ConfiguracaoDoServidor.criar("172.16.0.1", "8080", false).isLocalOuRedePrivada());
        assertTrue(ConfiguracaoDoServidor.criar("172.31.255.1", "8080", false).isLocalOuRedePrivada());
        assertTrue(ConfiguracaoDoServidor.criar("meu-pc.local", "8080", false).isLocalOuRedePrivada());
        assertFalse(ConfiguracaoDoServidor.criar("172.32.0.1", "8080", false).isLocalOuRedePrivada());
        assertFalse(ConfiguracaoDoServidor.criar("8.8.8.8", "8080", false).isLocalOuRedePrivada());
        assertFalse(ConfiguracaoDoServidor.criar("meu.servidor.com", "8080", false).isLocalOuRedePrivada());
    }

    @Test
    void avisaSoQuandoHttpVaiParaForaDaRedeLocal() {
        assertTrue(ConfiguracaoDoServidor.criar("meu.servidor.com", "80", false).exigeAviso());
        assertTrue(ConfiguracaoDoServidor.criar("8.8.8.8", "8080", false).exigeAviso());
        assertFalse(ConfiguracaoDoServidor.criar("meu.servidor.com", "443", true).exigeAviso());
        assertFalse(ConfiguracaoDoServidor.criar("192.168.0.10", "8080", false).exigeAviso());
        assertFalse(ConfiguracaoDoServidor.criar("localhost", "8080", false).exigeAviso());
    }

    @Test
    void igualdadeConsideraHostPortaEEsquema() {
        ConfiguracaoDoServidor a = ConfiguracaoDoServidor.criar("host", "8080", false);

        assertEquals(a, ConfiguracaoDoServidor.criar("HOST", "8080", false));
        assertEquals(a.hashCode(), ConfiguracaoDoServidor.criar("host", "8080", false).hashCode());
        assertFalse(a.equals(ConfiguracaoDoServidor.criar("host", "8081", false)));
        assertFalse(a.equals(ConfiguracaoDoServidor.criar("host", "8080", true)));
        assertFalse(a.equals(ConfiguracaoDoServidor.criar("outro", "8080", false)));
    }
}
