package graspexercicio10.service;

import graspexercicio10.model.*;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Pure Fabrication / High Cohesion: executa os turnos e controla o ciclo da partida.
 * Delega posição, embarque e colisão aos Information Experts do domínio.
 * Não conhece console, biblioteca gráfica nem persistência.
 */
public class JogoService {
    private final FabricaMissao fabrica;
    private final Random random;
    private final Clock clock;
    private Missao missao;
    private String piloto = "";
    private Dificuldade dificuldade;
    private int raioMapa;
    private int pontos;
    private int movimentos;
    private long inicio;
    private long fim;
    private StatusPartida status = StatusPartida.NAO_INICIADA;

    public JogoService() {
        this(new FabricaMissao(), new Random(), Clock.systemDefaultZone());
    }

    /** Dependências substituíveis para simulações e testes reproduzíveis. */
    public JogoService(FabricaMissao fabrica, Random random, Clock clock) {
        this.fabrica = Objects.requireNonNull(fabrica);
        this.random = Objects.requireNonNull(random);
        this.clock = Objects.requireNonNull(clock);
    }

    public void iniciarPartida(String piloto, Dificuldade dificuldade, int raioMapa) {
        if (status == StatusPartida.EM_ANDAMENTO) {
            throw new IllegalStateException("Encerre a partida atual antes de iniciar outra.");
        }
        if (raioMapa < 2 || raioMapa > 20) {
            throw new IllegalArgumentException("O raio do mapa deve estar entre 2 e 20.");
        }
        Objects.requireNonNull(dificuldade, "Informe a dificuldade.");
        // Cria antes de alterar o estado: configuração inválida preserva a partida anterior.
        Missao nova = fabrica.criar(dificuldade, -raioMapa, raioMapa, -raioMapa, raioMapa, random);
        this.missao = nova;
        this.piloto = piloto == null || piloto.trim().isEmpty() ? "Piloto Anônimo" : piloto.trim();
        this.dificuldade = dificuldade;
        this.raioMapa = raioMapa;
        pontos = dificuldade.getPontuacaoInicial();
        movimentos = 0;
        inicio = clock.millis();
        fim = inicio;
        status = StatusPartida.EM_ANDAMENTO;
    }

    public EventoJogo executar(AcaoJogo acao) {
        Objects.requireNonNull(acao, "Informe a ação.");
        if (status != StatusPartida.EM_ANDAMENTO) return EventoJogo.PARTIDA_INATIVA;
        switch (acao) {
            case ABORTAR:
                encerrar(StatusPartida.ABORTADA);
                return EventoJogo.ABORTADA;
            case EMBARCAR:
                return embarcar();
            default:
                return mover(acao);
        }
    }

    private EventoJogo mover(AcaoJogo direcao) {
        if (!missao.getNave().mover(direcao, -raioMapa, raioMapa, -raioMapa, raioMapa)) {
            return EventoJogo.LIMITE_MAPA;
        }
        pontos--;
        movimentos++;
        missao.atualizarPerigos(random, -raioMapa, raioMapa, -raioMapa, raioMapa);
        boolean colisao = missao.verificaColisao();
        if (colisao) missao.getNave().perderVida();
        // Uma colisão não pode impedir a verificação de combustível no mesmo turno.
        if (missao.getNave().getVidas() == 0) {
            encerrar(StatusPartida.DERROTA);
            return EventoJogo.NAVE_DESTRUIDA;
        }
        if (pontos <= 0) {
            encerrar(StatusPartida.DERROTA);
            return EventoJogo.SEM_COMBUSTIVEL;
        }
        EventoJogo objetivo = verificarObjetivo();
        if (objetivo == EventoJogo.VITORIA) return objetivo;
        if (colisao) return EventoJogo.COLISAO;
        return objetivo;
    }

    private EventoJogo embarcar() {
        Passageiro passageiro = missao.passagemNaPosicao();
        if (passageiro == null) return EventoJogo.NENHUM_PASSAGEIRO;
        if (!missao.embarcarPassageiroNaPosicao()) return EventoJogo.NAVE_CHEIA;
        pontos += passageiro.getPontuacao(); // Polymorphism: o passageiro conhece seu valor.
        EventoJogo objetivo = verificarObjetivo();
        return objetivo == EventoJogo.MOVIMENTO ? EventoJogo.EMBARCADO : objetivo;
    }

    private EventoJogo verificarObjetivo() {
        if (!missao.todosEmbarcados()) return EventoJogo.MOVIMENTO;
        if (!missao.getNave().estaNaBase()) return EventoJogo.RETORNE_BASE;
        encerrar(StatusPartida.VITORIA);
        return EventoJogo.VITORIA;
    }

    private void encerrar(StatusPartida resultado) {
        status = resultado;
        fim = clock.millis();
    }

    /** Snapshot: a tela lê o jogo sem receber acesso ao modelo mutável. */
    public EstadoJogo consultarEstado() {
        List<EntidadeEstado> entidades = new ArrayList<>();
        int vidas = 0, capacidade = 0, embarcados = 0, restantes = 0;
        if (missao != null) {
            Nave nave = missao.getNave();
            for (ElementoMapa elemento : missao.getElementos()) {
                entidades.add(new EntidadeEstado(elemento));
            }
            vidas = nave.getVidas();
            capacidade = nave.getCapacidade();
            embarcados = nave.getPassageiros().size();
            restantes = missao.getPassageiros().size();
        }
        long tempo = status == StatusPartida.NAO_INICIADA ? 0
                : Math.max(0, ((status == StatusPartida.EM_ANDAMENTO ? clock.millis() : fim) - inicio) / 1000);
        return new EstadoJogo(status, piloto, dificuldade, raioMapa, pontos, movimentos,
                vidas, capacidade, embarcados, restantes, tempo, entidades);
    }
}
