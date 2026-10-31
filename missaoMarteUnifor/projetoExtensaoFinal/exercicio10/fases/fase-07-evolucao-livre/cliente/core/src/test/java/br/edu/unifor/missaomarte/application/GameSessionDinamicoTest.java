package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.MovimentoPerseguidor;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** O modo dinamico e testado com tempo simulado: nenhum teste espera o relogio de verdade. */
class GameSessionDinamicoTest {
    private static final float QUADRO = 0.05f;
    private static final int MIN = -5;
    private static final int MAX = 5;

    private final Nave nave = new Nave(0, 0, 3, 2);
    private final Missao missao = new Missao(nave);

    private GameSession nova(Dificuldade dificuldade) {
        if (missao.getPassageiros().isEmpty()) {
            // Sem passageiros a missao ja nasce cumprida; este fica longe do caminho dos testes.
            missao.adicionarPassageiro(new Astronauta("Longe", 5, 5));
        }
        nave.carregar(3);
        return new GameSession("Teste", dificuldade, missao, 100, MIN, MAX, new Random(1), ModoDeJogo.DINAMICO);
    }

    /** Avanca o tempo em quadros de 0,05 s e junta tudo o que aconteceu. */
    private static List<EventoJogo> passar(GameSession s, float segundos) {
        List<EventoJogo> eventos = new ArrayList<>();
        int quadros = Math.round(segundos / QUADRO);
        for (int i = 0; i < quadros; i++) {
            eventos.addAll(s.avancar(QUADRO).getEventos());
        }
        return eventos;
    }

    /** Termina a contagem inicial de 3 s (com folga de um quadro). */
    private static void iniciar(GameSession s) {
        passar(s, 3.1f);
        assertEquals(0f, s.getContagemRestante(), 0f);
    }

    @Test
    void naContagemInicialNadaAcontece() {
        GameSession s = nova(Dificuldade.FACIL);
        s.pressionar(Acao.DIREITA);

        passar(s, 2.5f);

        assertEquals(new Posicao(0, 0), nave.getPosicao());
        assertEquals(100, s.getPontuacao());
        assertTrue(s.getContagemRestante() > 0f);
        assertEquals(0, s.getDuracaoSegundos());
    }

    @Test
    void naveAndaUmaCelulaPorIntervaloEnquantoAcaoEstaPressionada() {
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);
        s.pressionar(Acao.DIREITA);

        s.avancar(QUADRO);
        assertEquals(1, nave.getX());
        passar(s, 3 * QUADRO);
        assertEquals(2, nave.getX());
        passar(s, 3 * QUADRO);
        assertEquals(3, nave.getX());
        assertEquals(3, s.getMovimentos());
    }

    @Test
    void umToqueMaisCurtoQueUmQuadroAindaDaUmPasso() {
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);

        s.pressionar(Acao.DIREITA);
        s.soltar(Acao.DIREITA);
        s.avancar(QUADRO);

        assertEquals(1, nave.getX());
        passar(s, 1f);
        assertEquals(1, nave.getX());
    }

    @Test
    void naveParaQuandoASetaESolta() {
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);
        s.pressionar(Acao.DIREITA);
        passar(s, 0.5f);
        int x = nave.getX();

        s.soltar(Acao.DIREITA);
        passar(s, 1f);

        assertEquals(x, nave.getX());
    }

    @Test
    void aUltimaDirecaoPressionadaVale() {
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);
        s.pressionar(Acao.DIREITA);
        s.pressionar(Acao.BAIXO);

        s.avancar(QUADRO);

        assertEquals(new Posicao(0, 1), nave.getPosicao());
        assertEquals(Acao.BAIXO, s.getMira());

        s.soltar(Acao.BAIXO);
        passar(s, 0.2f);

        assertEquals(1, nave.getY());
        assertTrue(nave.getX() >= 1);
    }

    @Test
    void naveNaoConsegueAndarMaisRapidoQueOIntervalo() {
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);

        for (int i = 0; i < 10; i++) {
            s.pressionar(Acao.DIREITA);
            s.soltar(Acao.DIREITA);
            s.pressionar(Acao.DIREITA);
            s.avancar(QUADRO);
        }

        assertTrue(nave.getX() <= 4, "x = " + nave.getX());
    }

    @Test
    void naveNaoSaiDoMapaENaoContaMovimentoNaBorda() {
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);
        s.pressionar(Acao.ESQUERDA);

        passar(s, 3f);

        assertEquals(MIN, nave.getX());
        assertEquals(5, s.getMovimentos());
    }

    @Test
    void pontuacaoCaiUmPontoPorSegundo() {
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);
        int antes = s.getPontuacao();

        passar(s, 2f);

        assertEquals(antes - 2, s.getPontuacao());
    }

    @Test
    void quadroLentoNaoViraSalto() {
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);
        int antes = s.getPontuacao();

        s.avancar(10f);

        assertEquals(antes, s.getPontuacao());
        assertEquals(0, s.getDuracaoSegundos());
    }

    @Test
    void duracaoUsaOTempoDeJogoSemContarContagemNemPausa() {
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);
        passar(s, 2f);
        s.pausar();
        passar(s, 10f);
        s.retomar();
        passar(s, 1f);

        assertEquals(3, s.getDuracaoSegundos());
    }

    @Test
    void pausaCongelaOJogoELimpaAsDirecoes() {
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);
        s.pressionar(Acao.DIREITA);
        s.avancar(QUADRO);
        Posicao parada = nave.getPosicao();
        int pontos = s.getPontuacao();

        s.alternarPausa();
        passar(s, 3f);

        assertTrue(s.isPausada());
        assertEquals(parada, nave.getPosicao());
        assertEquals(pontos, s.getPontuacao());

        s.alternarPausa();
        passar(s, 1f);

        assertFalse(s.isPausada());
        assertEquals(parada, nave.getPosicao());
    }

    @Test
    void perseguidorAndaNoRitmoDaDificuldadeEColideComANave() {
        Inimigo perseguidor = new Inimigo(3, 0, MovimentoPerseguidor.INSTANCIA);
        missao.adicionarInimigo(perseguidor);
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);

        passar(s, 1f);
        assertEquals(new Posicao(2, 0), perseguidor.getPosicao());
        passar(s, 1f);
        assertEquals(new Posicao(1, 0), perseguidor.getPosicao());
        List<EventoJogo> eventos = passar(s, 1f);

        assertTrue(eventos.contains(EventoJogo.COLISAO));
        assertEquals(2, nave.getVidas());
        assertTrue(missao.getInimigos().isEmpty());
    }

    @Test
    void colisaoComAsteroideCustaVidaEPontos() {
        missao.adicionarAsteroide(new Asteroide(1, 0));
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);
        s.pressionar(Acao.DIREITA);

        List<EventoJogo> eventos = new ArrayList<>(s.avancar(QUADRO).getEventos());

        assertTrue(eventos.contains(EventoJogo.COLISAO));
        assertEquals(2, nave.getVidas());
        assertEquals(100 - 5, s.getPontuacao());
        assertTrue(missao.getAsteroides().isEmpty());
    }

    @Test
    void tresColisoesTerminamEmDerrota() {
        for (int x = 1; x <= 3; x++) {
            missao.adicionarAsteroide(new Asteroide(x, 0));
        }
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);
        s.pressionar(Acao.DIREITA);

        List<EventoJogo> eventos = passar(s, 1.5f);

        assertEquals(EstadoPartida.DERROTA, s.getEstado());
        assertTrue(eventos.contains(EventoJogo.DERROTA));
    }

    @Test
    void embarqueEEntregaSaoAutomaticosEAEntregaDaVitoria() {
        Astronauta passageiro = new Astronauta("Astro", 1, 0);
        missao.adicionarPassageiro(passageiro);
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);

        s.pressionar(Acao.DIREITA);
        List<EventoJogo> eventos = new ArrayList<>(s.avancar(QUADRO).getEventos());
        assertTrue(eventos.contains(EventoJogo.EMBARQUE));
        assertEquals(1, nave.getPassageiros().size());
        assertTrue(missao.getPassageiros().isEmpty());

        s.soltar(Acao.DIREITA);
        s.pressionar(Acao.ESQUERDA);
        eventos.addAll(passar(s, 0.5f));

        assertTrue(eventos.contains(EventoJogo.ENTREGA));
        assertTrue(eventos.contains(EventoJogo.VITORIA));
        assertEquals(EstadoPartida.VITORIA, s.getEstado());
        assertEquals(1, s.getResgatados());
    }

    @Test
    void depoisDoFimOTempoNaoAvanca() {
        missao.adicionarAsteroide(new Asteroide(1, 0));
        missao.adicionarAsteroide(new Asteroide(2, 0));
        missao.adicionarAsteroide(new Asteroide(3, 0));
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);
        s.pressionar(Acao.DIREITA);
        passar(s, 2f);
        assertEquals(EstadoPartida.DERROTA, s.getEstado());
        int pontos = s.getPontuacao();

        List<EventoJogo> eventos = passar(s, 5f);

        assertTrue(eventos.isEmpty());
        assertEquals(pontos, s.getPontuacao());
    }

    @Test
    void pontuacaoZeradaPeloTempoTerminaEmDerrota() {
        missao.adicionarPassageiro(new Astronauta("Longe", 5, 5));
        GameSession s = new GameSession("Teste", Dificuldade.FACIL, missao, 2, MIN, MAX, new Random(1), ModoDeJogo.DINAMICO);
        nave.carregar(3);
        iniciar(s);

        passar(s, 3f);

        assertEquals(EstadoPartida.DERROTA, s.getEstado());
    }

    @Test
    void tiroDestroiOAlvoGastaMunicaoEPontosEEntraEmRecarga() {
        missao.adicionarAsteroide(new Asteroide(3, 0));
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);
        int antes = s.getPontuacao();

        ResultadoTurno r = s.atirar();

        assertTrue(r.getEventos().contains(EventoJogo.TIRO));
        assertTrue(r.getEventos().contains(EventoJogo.PERIGO_DESTRUIDO));
        assertTrue(missao.getAsteroides().isEmpty());
        assertEquals(2, s.getMunicao());
        assertEquals(antes - 2, s.getPontuacao());
        assertNotNull(s.getUltimoTiro());

        assertTrue(s.atirar().getEventos().isEmpty());
        assertEquals(2, s.getMunicao());

        passar(s, 0.7f);
        ResultadoTurno segundo = s.atirar();

        assertEquals(List.of(EventoJogo.TIRO), segundo.getEventos());
        assertEquals(1, s.getMunicao());
    }

    @Test
    void tiroSegueAUltimaDirecaoPressionada() {
        missao.adicionarInimigo(new Inimigo(0, -3));
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);
        s.pressionar(Acao.CIMA);
        s.soltar(Acao.CIMA);

        ResultadoTurno r = s.atirar();

        assertTrue(r.getEventos().contains(EventoJogo.PERIGO_DESTRUIDO));
        assertEquals(new Posicao(0, -3), s.getUltimoTiro().getDestino());
    }

    @Test
    void semMunicaoNaoAtira() {
        GameSession s = nova(Dificuldade.FACIL);
        nave.carregar(0);
        iniciar(s);
        int antes = s.getPontuacao();

        ResultadoTurno r = s.atirar();

        assertTrue(r.getEventos().isEmpty());
        assertEquals(List.of("Sem municao."), r.getMensagens());
        assertEquals(antes, s.getPontuacao());
    }

    @Test
    void naoAtiraNaContagemNemEmPausa() {
        GameSession s = nova(Dificuldade.FACIL);

        assertTrue(s.atirar().getEventos().isEmpty());

        iniciar(s);
        s.pausar();

        assertTrue(s.atirar().getEventos().isEmpty());
        assertEquals(3, s.getMunicao());
    }

    @Test
    void modoClassicoIgnoraOTempoENaoTemArma() {
        Missao classica = new Missao(new Nave(0, 0, 3, 1));
        GameSession s = new GameSession("Teste", Dificuldade.FACIL, classica, 100, MIN, MAX, new Random(1));

        assertEquals(ModoDeJogo.CLASSICO, s.getModo());
        assertTrue(s.avancar(5f).getEventos().isEmpty());
        s.pressionar(Acao.DIREITA);
        s.avancar(1f);
        assertEquals(new Posicao(0, 0), classica.getNave().getPosicao());
        assertTrue(s.atirar().getEventos().isEmpty());
        assertEquals(List.of("A nave nao tem arma no modo classico."), s.processarAcao(Acao.ATIRAR).getMensagens());
        assertEquals(100, s.getPontuacao());
    }

    @Test
    void noModoDinamicoAsAcoesDeTurnoSaoIgnoradasMasSairAborta() {
        GameSession s = nova(Dificuldade.FACIL);
        iniciar(s);

        s.processarAcao(Acao.DIREITA);
        assertEquals(new Posicao(0, 0), nave.getPosicao());
        assertEquals(EstadoPartida.EM_ANDAMENTO, s.getEstado());

        s.processarAcao(Acao.SAIR);
        assertEquals(EstadoPartida.ABORTADA, s.getEstado());
    }
}
