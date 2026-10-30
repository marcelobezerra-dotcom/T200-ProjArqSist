package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Professor;
import java.util.Random;
import org.junit.jupiter.api.Test;

class GameSessionTest {
    private static final int MIN = -5;
    private static final int MAX = 5;

    private GameSession sessao(Missao missao, int pontuacaoInicial) {
        return new GameSession("Tester", Dificuldade.FACIL, missao, pontuacaoInicial, MIN, MAX, new Random(1));
    }

    @Test
    void coletaEntregaNaBaseEVence() {
        Nave nave = new Nave(0, 0, 3, 2);
        Missao missao = new Missao(nave);
        missao.adicionarPassageiro(new Professor("P1", 1, 0));
        GameSession sessao = sessao(missao, 100);

        sessao.processarAcao(Acao.DIREITA);
        sessao.processarAcao(Acao.EMBARCAR);

        assertEquals(1, nave.getPassageiros().size());
        assertEquals(0, sessao.getResgatados());
        assertEquals(EstadoPartida.EM_ANDAMENTO, sessao.getEstado());

        sessao.processarAcao(Acao.ESQUERDA);

        assertEquals(1, sessao.getResgatados());
        assertTrue(nave.getPassageiros().isEmpty());
        assertEquals(EstadoPartida.VITORIA, sessao.getEstado());
        assertEquals(100 - 1 + 20 - 1, sessao.getPontuacao());
        assertEquals(2, sessao.getMovimentos());
    }

    @Test
    void embarcarSemPassageiroNaoMudaAPontuacao() {
        Missao missao = new Missao(new Nave(0, 0, 3, 1));
        GameSession sessao = sessao(missao, 100);

        ResultadoTurno resultado = sessao.processarAcao(Acao.EMBARCAR);

        assertTrue(resultado.getMensagens().contains("Nao ha passageiro nesta posicao."));
        assertEquals(100, sessao.getPontuacao());
    }

    @Test
    void naveLotadaNaoEmbarcaOutroPassageiro() {
        Nave nave = new Nave(0, 0, 3, 1);
        Missao missao = new Missao(nave);
        missao.adicionarPassageiro(new Professor("P1", 1, 0));
        missao.adicionarPassageiro(new Astronauta("A1", 2, 0));
        GameSession sessao = sessao(missao, 100);

        sessao.processarAcao(Acao.DIREITA);
        sessao.processarAcao(Acao.EMBARCAR);
        sessao.processarAcao(Acao.DIREITA);
        ResultadoTurno resultado = sessao.processarAcao(Acao.EMBARCAR);

        assertTrue(resultado.getMensagens().stream().anyMatch(m -> m.contains("lotada")));
        assertEquals(1, nave.getPassageiros().size());
        assertEquals(1, missao.getPassageiros().size());
    }

    @Test
    void colisaoComAsteroideTiraVidaEPontos() {
        Nave nave = new Nave(0, 0, 3, 1);
        Missao missao = new Missao(nave);
        missao.adicionarAsteroide(new Asteroide(1, 0));
        GameSession sessao = sessao(missao, 100);

        sessao.processarAcao(Acao.DIREITA);

        assertEquals(2, nave.getVidas());
        assertTrue(missao.getAsteroides().isEmpty());
        assertEquals(100 - 1 - 5, sessao.getPontuacao());
    }

    @Test
    void pontuacaoZeradaEncerraEmDerrota() {
        Missao missao = new Missao(new Nave(0, 0, 3, 1));
        GameSession sessao = sessao(missao, 1);

        sessao.processarAcao(Acao.DIREITA);

        assertEquals(EstadoPartida.DERROTA, sessao.getEstado());
    }

    @Test
    void sairAbortaAPartida() {
        GameSession sessao = sessao(new Missao(new Nave(0, 0, 3, 1)), 100);

        sessao.processarAcao(Acao.SAIR);

        assertEquals(EstadoPartida.ABORTADA, sessao.getEstado());
    }

    @Test
    void comandoInvalidoNaoAlteraAPartida() {
        GameSession sessao = sessao(new Missao(new Nave(0, 0, 3, 1)), 100);

        ResultadoTurno resultado = sessao.processarAcao(Acao.INVALIDA);

        assertTrue(resultado.getMensagens().contains("Comando invalido."));
        assertEquals(100, sessao.getPontuacao());
        assertEquals(0, sessao.getMovimentos());
        assertEquals(EstadoPartida.EM_ANDAMENTO, sessao.getEstado());
    }
}
