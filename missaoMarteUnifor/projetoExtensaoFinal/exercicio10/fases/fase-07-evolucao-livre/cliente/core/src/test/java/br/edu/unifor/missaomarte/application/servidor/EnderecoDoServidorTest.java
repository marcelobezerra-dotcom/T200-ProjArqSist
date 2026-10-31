package br.edu.unifor.missaomarte.application.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EnderecoDoServidorTest {

    /** Armazem em memoria; conta as leituras para provar que o valor salvo so e lido uma vez. */
    private static final class ArmazemEmMemoria implements ArmazemDeTexto {
        final Map<String, String> dados = new HashMap<>();
        int leituras;

        @Override
        public String ler(String chave) {
            leituras++;
            return dados.get(chave);
        }

        @Override
        public void gravar(String chave, String valor) {
            dados.put(chave, valor);
        }

        @Override
        public void apagar(String chave) {
            dados.remove(chave);
        }
    }

    private final ArmazemEmMemoria armazem = new ArmazemEmMemoria();
    private final RepositorioDeConfiguracaoDoServidor repositorio = new RepositorioDeConfiguracaoDoServidor(armazem);
    private final ConfiguracaoDoServidor padrao = ConfiguracaoDoServidor.deUrl("http://10.0.2.2:8080");

    @Test
    void repositorioSalvaECarregaSoEnderecoPortaEEsquema() {
        repositorio.salvar(ConfiguracaoDoServidor.criar("meu.servidor.com", "443", true));

        assertEquals("https://meu.servidor.com", repositorio.carregar().get().urlBase());
        assertEquals(3, armazem.dados.size());
        assertTrue(armazem.dados.keySet().stream().noneMatch(k -> k.contains("senha") || k.contains("token")));
    }

    @Test
    void repositorioSemNadaSalvoDevolveVazio() {
        assertFalse(repositorio.carregar().isPresent());
    }

    @Test
    void repositorioIgnoraValorSalvoInvalido() {
        armazem.gravar("servidor.host", "http://isto/nao-e-host");
        armazem.gravar("servidor.porta", "8080");

        assertFalse(repositorio.carregar().isPresent());
    }

    @Test
    void repositorioLimparApagaTudo() {
        repositorio.salvar(ConfiguracaoDoServidor.criar("host", "8080", false));

        repositorio.limpar();

        assertTrue(armazem.dados.isEmpty());
        assertFalse(repositorio.carregar().isPresent());
    }

    @Test
    void semValorSalvoUsaOPadrao() {
        EnderecoDoServidor endereco = new EnderecoDoServidor(repositorio, padrao);

        assertEquals("http://10.0.2.2:8080", endereco.urlBase());
    }

    @Test
    void valorSalvoVenceOPadrao() {
        repositorio.salvar(ConfiguracaoDoServidor.criar("192.168.0.10", "9090", false));
        EnderecoDoServidor endereco = new EnderecoDoServidor(repositorio, padrao);

        assertEquals("http://192.168.0.10:9090", endereco.urlBase());
    }

    @Test
    void naoLeOArmazemAteOPrimeiroUso() {
        new EnderecoDoServidor(repositorio, padrao);

        assertEquals(0, armazem.leituras);
    }

    @Test
    void salvarTrocaAUrlNaHoraEInformaSeMudou() {
        EnderecoDoServidor endereco = new EnderecoDoServidor(repositorio, padrao);
        ConfiguracaoDoServidor nova = ConfiguracaoDoServidor.criar("meu.servidor.com", "443", true);

        assertTrue(endereco.salvar(nova));
        assertEquals("https://meu.servidor.com", endereco.urlBase());
        assertFalse(endereco.salvar(nova));
        assertEquals("https://meu.servidor.com", new EnderecoDoServidor(repositorio, padrao).urlBase());
    }

    @Test
    void restaurarPadraoApagaOSalvoEVoltaAoPadrao() {
        EnderecoDoServidor endereco = new EnderecoDoServidor(repositorio, padrao);
        endereco.salvar(ConfiguracaoDoServidor.criar("meu.servidor.com", "443", true));

        assertTrue(endereco.restaurarPadrao());

        assertEquals("http://10.0.2.2:8080", endereco.urlBase());
        assertNull(armazem.ler("servidor.host"));
        assertFalse(endereco.restaurarPadrao());
    }
}
