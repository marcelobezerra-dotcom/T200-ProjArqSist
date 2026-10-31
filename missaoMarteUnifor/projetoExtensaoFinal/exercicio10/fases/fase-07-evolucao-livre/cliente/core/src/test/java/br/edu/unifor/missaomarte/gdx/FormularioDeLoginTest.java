package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FormularioDeLoginTest {

    private static void digitar(FormularioDeLogin f, String texto) {
        for (char c : texto.toCharArray()) {
            f.digitar(c);
        }
    }

    @Test
    void comecaNoCampoNomeEmModoEntrar() {
        FormularioDeLogin f = new FormularioDeLogin();

        assertEquals(FormularioDeLogin.Campo.NOME, f.getCampo());
        assertEquals(FormularioDeLogin.Modo.ENTRAR, f.getModo());
    }

    @Test
    void nomeAceitaSoLetrasNumerosSublinhadoEHifen() {
        FormularioDeLogin f = new FormularioDeLogin();

        digitar(f, "Neo_1-x y!@");

        assertEquals("Neo_1-xy", f.getNome());
    }

    @Test
    void nomeTemNoMaximoVinteCaracteres() {
        FormularioDeLogin f = new FormularioDeLogin();

        digitar(f, "a".repeat(25));

        assertEquals(FormularioDeLogin.NOME_MAX, f.getNome().length());
        assertFalse(f.digitar('b'));
    }

    @Test
    void senhaAceitaAsciiImprimivelEFicaMascarada() {
        FormularioDeLogin f = new FormularioDeLogin();
        f.alternarCampo();

        digitar(f, "ab 1!\tç");

        assertEquals("ab 1!", f.getSenha());
        assertEquals("*****", f.getSenhaMascarada());
        assertEquals("", f.getNome());
    }

    @Test
    void senhaTemNoMaximo72Caracteres() {
        FormularioDeLogin f = new FormularioDeLogin();
        f.alternarCampo();

        digitar(f, "x".repeat(80));

        assertEquals(FormularioDeLogin.SENHA_MAX, f.getSenha().length());
    }

    @Test
    void apagarRemoveSoDoCampoAtual() {
        FormularioDeLogin f = new FormularioDeLogin();
        digitar(f, "neo");
        f.alternarCampo();
        digitar(f, "12345678");

        f.apagar();

        assertEquals("neo", f.getNome());
        assertEquals("1234567", f.getSenha());
    }

    @Test
    void apagarComCampoVazioNaoFalha() {
        FormularioDeLogin f = new FormularioDeLogin();

        f.apagar();

        assertEquals("", f.getNome());
    }

    @Test
    void entrarExigeNomeESenhaPreenchidos() {
        FormularioDeLogin f = new FormularioDeLogin();
        assertEquals("Informe o nome.", f.validar());

        digitar(f, "neo");
        assertEquals("Informe a senha.", f.validar());

        f.alternarCampo();
        digitar(f, "x");
        assertNull(f.validar());
    }

    @Test
    void cadastroExigeNomeCom3CaracteresESenhaCom8() {
        FormularioDeLogin f = new FormularioDeLogin();
        f.alternarModo();
        digitar(f, "ne");
        assertNotNull(f.validar());
        assertTrue(f.validar().contains("nome"));

        digitar(f, "o");
        f.alternarCampo();
        digitar(f, "1234567");
        assertTrue(f.validar().contains("senha"));

        digitar(f, "8");
        assertNull(f.validar());
    }

    @Test
    void limparSenhaMantemONome() {
        FormularioDeLogin f = new FormularioDeLogin();
        digitar(f, "neo");
        f.alternarCampo();
        digitar(f, "segredo123");

        f.limparSenha();

        assertEquals("neo", f.getNome());
        assertEquals("", f.getSenha());
    }

    @Test
    void definirTextoSubstituiOCampoSemMudarOCampoAtual() {
        FormularioDeLogin f = new FormularioDeLogin();
        digitar(f, "velho");

        f.definirTexto(FormularioDeLogin.Campo.NOME, "neo");
        f.definirTexto(FormularioDeLogin.Campo.SENHA, "segredo123");

        assertEquals("neo", f.getNome());
        assertEquals("segredo123", f.getSenha());
        assertEquals(FormularioDeLogin.Campo.NOME, f.getCampo());
    }

    @Test
    void definirTextoAplicaAsMesmasRegrasDeDigitacao() {
        FormularioDeLogin f = new FormularioDeLogin();

        f.definirTexto(FormularioDeLogin.Campo.NOME, "Neo Silva!" + "a".repeat(30));
        f.definirTexto(FormularioDeLogin.Campo.SENHA, "ab ç1" + "x".repeat(80));

        assertEquals(FormularioDeLogin.NOME_MAX, f.getNome().length());
        assertTrue(f.getNome().startsWith("NeoSilva"));
        assertEquals(FormularioDeLogin.SENHA_MAX, f.getSenha().length());
        assertTrue(f.getSenha().startsWith("ab 1x"));
    }

    @Test
    void definirTextoNuloLimpaOCampo() {
        FormularioDeLogin f = new FormularioDeLogin();
        digitar(f, "neo");

        f.definirTexto(FormularioDeLogin.Campo.NOME, null);

        assertEquals("", f.getNome());
    }

    @Test
    void selecionarCampoTrocaOCampoAtual() {
        FormularioDeLogin f = new FormularioDeLogin();

        f.selecionarCampo(FormularioDeLogin.Campo.SENHA);

        assertEquals(FormularioDeLogin.Campo.SENHA, f.getCampo());
    }
}
