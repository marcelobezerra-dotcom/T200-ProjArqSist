package br.edu.unifor.missaomarte.application.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Professor;
import java.util.Random;
import org.junit.jupiter.api.Test;

class DadosDoServidorTest {

    private GameSession novaSessao() {
        Nave nave = new Nave(0, 0, 3, 2);
        Missao missao = new Missao(nave);
        missao.adicionarPassageiro(new Professor("P1", 1, 0));
        return new GameSession("Tester", Dificuldade.MEDIO, missao, 100, -5, 5, new Random(1));
    }

    @Test
    void partidaVencidaViraPartidaConcluida() {
        GameSession sessao = novaSessao();
        sessao.processarAcao(Acao.DIREITA);
        sessao.processarAcao(Acao.EMBARCAR);
        sessao.processarAcao(Acao.ESQUERDA);

        PartidaConcluida partida = PartidaConcluida.de(sessao);

        assertEquals("VITORIA", partida.getResultado());
        assertEquals("MEDIO", partida.getDificuldade());
        assertEquals(sessao.getPontuacao(), partida.getPontuacao());
        assertEquals(sessao.getMovimentos(), partida.getMovimentos());
        assertEquals(sessao.getDuracaoSegundos(), partida.getDuracaoSegundos());
    }

    @Test
    void partidaEmAndamentoNaoPodeSerEnviada() {
        GameSession sessao = novaSessao();

        assertThrows(IllegalStateException.class, () -> PartidaConcluida.de(sessao));
    }

    @Test
    void sessaoNaoMostraOTokenNoToString() {
        Sessao sessao = new Sessao("neo", "token-super-secreto");

        assertFalse(sessao.toString().contains("token-super-secreto"));
        assertTrue(sessao.toString().contains("neo"));
        assertEquals("token-super-secreto", sessao.getToken());
    }

    @Test
    void respostaDeSucessoTemValorESemMensagem() {
        Resposta<String> r = Resposta.sucesso("ok");

        assertTrue(r.isSucesso());
        assertEquals("ok", r.getValor());
        assertNull(r.getMensagem());
        assertFalse(r.isNaoAutorizado());
    }

    @Test
    void respostaDeErroTemMensagemESemValor() {
        Resposta<String> r = Resposta.erro("falhou");

        assertFalse(r.isSucesso());
        assertNull(r.getValor());
        assertEquals("falhou", r.getMensagem());
        assertFalse(r.isNaoAutorizado());
    }

    @Test
    void erroNaoAutorizadoFicaMarcado() {
        Resposta<String> r = Resposta.erroNaoAutorizado("expirou");

        assertFalse(r.isSucesso());
        assertTrue(r.isNaoAutorizado());
    }
}
