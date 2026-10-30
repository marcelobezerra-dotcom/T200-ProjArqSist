package br.edu.unifor.missaomarte.gdx.visual;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import org.junit.jupiter.api.Test;

class EfeitosEmensagensTest {

    @Test
    void efeitoPercorreOsQuadrosEDesapareceNoFim() {
        EfeitosVisuais efeitos = new EfeitosVisuais();
        efeitos.adicionar(Imagem.EXPLOSAO, 1f, 2f, 0.6f);
        EfeitosVisuais.Efeito efeito = efeitos.ativos().get(0);

        assertEquals(0, efeito.quadro(6));

        efeitos.atualizar(0.35f);
        assertEquals(3, efeito.quadro(6));
        assertEquals(1, efeitos.ativos().size());

        efeitos.atualizar(0.3f);
        assertTrue(efeitos.ativos().isEmpty());
    }

    @Test
    void quadroNuncaPassaDoUltimo() {
        EfeitosVisuais efeitos = new EfeitosVisuais();
        efeitos.adicionar(Imagem.BRILHO, 0f, 0f, 1f);
        efeitos.atualizar(0.999f);

        assertEquals(4, efeitos.ativos().get(0).quadro(5));
    }

    @Test
    void tremorDecaiAteZero() {
        Tremor tremor = new Tremor();
        assertFalse(tremor.ativo());
        assertEquals(0f, tremor.deslocamentoX());

        tremor.iniciar(0.4f, 10f);
        assertTrue(tremor.ativo());
        assertEquals(1f, tremor.intensidade(), 0.0001f);

        tremor.atualizar(0.2f);
        assertEquals(0.5f, tremor.intensidade(), 0.0001f);
        assertTrue(Math.abs(tremor.deslocamentoX()) <= 10f * 0.5f);

        tremor.atualizar(0.3f);
        assertFalse(tremor.ativo());
        assertEquals(0f, tremor.deslocamentoX());
        assertEquals(0f, tremor.deslocamentoY());
    }

    @Test
    void mensagemFlutuanteSobeEFicaTransparente() {
        MensagensFlutuantes flutuantes = new MensagensFlutuantes();
        flutuantes.adicionar("+20", 3f, 4f, true);
        MensagensFlutuantes.Mensagem mensagem = flutuantes.ativas().get(0);

        assertEquals(4f, mensagem.y(), 0.0001f);
        assertEquals(1f, mensagem.alfa(), 0.0001f);

        flutuantes.atualizar(0.55f);

        assertTrue(mensagem.y() < 4f, "subir na tela diminui o Y da celula");
        assertTrue(mensagem.alfa() < 1f && mensagem.alfa() > 0f);
        assertTrue(mensagem.positiva());

        flutuantes.atualizar(1f);
        assertTrue(flutuantes.ativas().isEmpty());
    }
}
