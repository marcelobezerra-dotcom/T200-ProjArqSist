package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Professor;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class GameSessionEventosTest {
    private GameSession sessao(Missao missao) {
        return new GameSession("Tester", Dificuldade.FACIL, missao, 100, -5, 5, new Random(1));
    }

    // Sem passageiros a missao ja nasce vencida; este passageiro distante mantem a partida em andamento.
    private Missao missaoEmAndamento(Nave nave) {
        Missao missao = new Missao(nave);
        missao.adicionarPassageiro(new Professor("Distante", 5, 5));
        return missao;
    }

    @Test
    void moverGeraEventoDeMovimento() {
        GameSession sessao = sessao(missaoEmAndamento(new Nave(0, 0, 3, 1)));

        ResultadoTurno resultado = sessao.processarAcao(Acao.DIREITA);

        assertEquals(List.of(EventoJogo.MOVIMENTO), resultado.getEventos());
    }

    @Test
    void embarqueGeraEventoDeEmbarque() {
        Missao missao = new Missao(new Nave(0, 0, 3, 1));
        missao.adicionarPassageiro(new Professor("P1", 1, 0));
        GameSession sessao = sessao(missao);
        sessao.processarAcao(Acao.DIREITA);

        ResultadoTurno resultado = sessao.processarAcao(Acao.EMBARCAR);

        assertEquals(List.of(EventoJogo.EMBARQUE), resultado.getEventos());
    }

    @Test
    void embarcarSemPassageiroNaoGeraEvento() {
        GameSession sessao = sessao(missaoEmAndamento(new Nave(0, 0, 3, 1)));

        ResultadoTurno resultado = sessao.processarAcao(Acao.EMBARCAR);

        assertTrue(resultado.getEventos().isEmpty());
    }

    @Test
    void entregaNaBaseGeraEntregaEVitoriaNoMesmoTurno() {
        Missao missao = new Missao(new Nave(0, 0, 3, 1));
        missao.adicionarPassageiro(new Professor("P1", 1, 0));
        GameSession sessao = sessao(missao);
        sessao.processarAcao(Acao.DIREITA);
        sessao.processarAcao(Acao.EMBARCAR);

        ResultadoTurno resultado = sessao.processarAcao(Acao.ESQUERDA);

        assertEquals(List.of(EventoJogo.MOVIMENTO, EventoJogo.ENTREGA, EventoJogo.VITORIA), resultado.getEventos());
    }

    @Test
    void colisaoGeraEventoDeColisao() {
        Missao missao = missaoEmAndamento(new Nave(0, 0, 3, 1));
        missao.adicionarAsteroide(new Asteroide(1, 0));
        GameSession sessao = sessao(missao);

        ResultadoTurno resultado = sessao.processarAcao(Acao.DIREITA);

        assertEquals(List.of(EventoJogo.MOVIMENTO, EventoJogo.COLISAO), resultado.getEventos());
    }

    @Test
    void colisaoFatalGeraColisaoEDerrota() {
        Missao missao = new Missao(new Nave(0, 0, 1, 1));
        missao.adicionarAsteroide(new Asteroide(1, 0));
        GameSession sessao = sessao(missao);

        ResultadoTurno resultado = sessao.processarAcao(Acao.DIREITA);

        assertEquals(List.of(EventoJogo.MOVIMENTO, EventoJogo.COLISAO, EventoJogo.DERROTA), resultado.getEventos());
    }

    @Test
    void duracaoParaDeCorrerQuandoAPartidaTermina() throws InterruptedException {
        Missao missao = new Missao(new Nave(0, 0, 3, 1));
        missao.adicionarPassageiro(new Professor("P1", 1, 0));
        GameSession sessao = sessao(missao);
        sessao.processarAcao(Acao.DIREITA);
        sessao.processarAcao(Acao.EMBARCAR);
        sessao.processarAcao(Acao.ESQUERDA);
        long aoTerminar = sessao.getDuracaoSegundos();

        Thread.sleep(1200);

        assertEquals(aoTerminar, sessao.getDuracaoSegundos());
    }

    @Test
    void comandoInvalidoESairNaoGeramEventos() {
        GameSession sessao = sessao(new Missao(new Nave(0, 0, 3, 1)));

        assertTrue(sessao.processarAcao(Acao.INVALIDA).getEventos().isEmpty());
        assertTrue(sessao.processarAcao(Acao.SAIR).getEventos().isEmpty());
    }
}
