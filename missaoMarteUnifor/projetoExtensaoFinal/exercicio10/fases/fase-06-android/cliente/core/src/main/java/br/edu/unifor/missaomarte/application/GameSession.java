package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Passageiro;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Regras de uma partida, turno a turno: movimento, embarque, entrega na base, colisao, pontuacao e vitoria.
 * <p>GRASP: Controller do caso de uso (camada application), Information Expert do estado da partida, Low Coupling (nao conhece o console).
 * <p>SOLID: SRP (regra de partida, sem entrada nem saida). Devolve {@link ResultadoTurno} em vez de imprimir.
 * <p>Limite: recebe Random e min/max diretamente, e o fim por pontuacao zerada e checado antes da vitoria no mesmo turno.
 */
public class GameSession {
    private final String piloto;
    private final Dificuldade dificuldade;
    private final Missao missao;
    private final int min;
    private final int max;
    private final Random random;
    private final long inicioMillis;
    private long fimMillis = -1;

    private int pontuacao;
    private int movimentos;
    private int resgatados;
    private final int totalPassageiros;
    private EstadoPartida estado;

    public GameSession(String piloto, Dificuldade dificuldade, Missao missao, int pontuacaoInicial, int min, int max, Random random) {
        this.piloto = piloto;
        this.dificuldade = dificuldade;
        this.missao = missao;
        this.pontuacao = pontuacaoInicial;
        this.min = min;
        this.max = max;
        this.random = random;
        this.inicioMillis = System.currentTimeMillis();
        this.totalPassageiros = missao.getPassageiros().size();
        this.resgatados = 0;
        this.estado = EstadoPartida.EM_ANDAMENTO;
    }

    // Ordem do turno: acao do jogador, inimigos se movem, colisao, pontuacao, vitoria.
    public ResultadoTurno processarAcao(Acao acao) {
        List<String> mensagens = new ArrayList<>();
        List<EventoJogo> eventos = new ArrayList<>();

        if (acao == Acao.INVALIDA) {
            mensagens.add("Comando invalido.");
            return new ResultadoTurno(estado, mensagens);
        }

        if (acao == Acao.SAIR) {
            estado = EstadoPartida.ABORTADA;
            registrarFim();
            mensagens.add("Missao abortada pelo jogador.");
            return new ResultadoTurno(estado, mensagens);
        }

        if (acao == Acao.EMBARCAR) {
            embarcarPassageiro(mensagens, eventos);
        } else {
            moverNave(acao);
            pontuacao -= 1;
            movimentos += 1;
            eventos.add(EventoJogo.MOVIMENTO);
            processarBase(mensagens, eventos);
        }

        missao.moverInimigosAleatoriamente(min, max, random);
        verificarColisao(mensagens, eventos);
        verificarPontuacao(mensagens);
        verificarVitoria();

        if (estado == EstadoPartida.VITORIA) {
            eventos.add(EventoJogo.VITORIA);
        } else if (estado == EstadoPartida.DERROTA) {
            eventos.add(EventoJogo.DERROTA);
        }
        registrarFim();

        return new ResultadoTurno(estado, mensagens, eventos);
    }

    // A duracao deixa de correr quando a partida termina; assim o resumo mostra o mesmo tempo gravado no ranking.
    private void registrarFim() {
        if (estado != EstadoPartida.EM_ANDAMENTO && fimMillis < 0) {
            fimMillis = System.currentTimeMillis();
        }
    }

    private void embarcarPassageiro(List<String> mensagens, List<EventoJogo> eventos) {
        Passageiro passageiro = missao.passageiroNaPosicaoDaNave();
        if (passageiro == null) {
            mensagens.add("Nao ha passageiro nesta posicao.");
            return;
        }

        Nave nave = missao.getNave();
        if (nave.estaCheia()) {
            mensagens.add("Nave lotada. Leve os passageiros para a base (0,0).");
            return;
        }

        if (nave.embarcar(passageiro)) {
            missao.removerPassageiro(passageiro);
            pontuacao += passageiro.getPontuacao();
            mensagens.add("Passageiro " + passageiro.getNome() + " embarcou! +" + passageiro.getPontuacao() + " pontos.");
            eventos.add(EventoJogo.EMBARQUE);
        }
    }

    // Ao chegar na base os passageiros desembarcam: libera capacidade e conta como entregues.
    private void processarBase(List<String> mensagens, List<EventoJogo> eventos) {
        Nave nave = missao.getNave();
        if (nave.getPosicao().equals(missao.getBase().getPosicao()) && !nave.getPassageiros().isEmpty()) {
            int desembarcados = nave.desembarcarTodos();
            resgatados += desembarcados;
            mensagens.add("Base alcancada. " + desembarcados + " passageiro(s) desembarcado(s).");
            eventos.add(EventoJogo.ENTREGA);
        }
    }

    private void moverNave(Acao acao) {
        switch (acao) {
            case ESQUERDA:
                missao.getNave().moverComLimites(-1, 0, min, max);
                break;
            case DIREITA:
                missao.getNave().moverComLimites(1, 0, min, max);
                break;
            case CIMA:
                missao.getNave().moverComLimites(0, -1, min, max);
                break;
            case BAIXO:
                missao.getNave().moverComLimites(0, 1, min, max);
                break;
            default:
                break;
        }
    }

    private void verificarColisao(List<String> mensagens, List<EventoJogo> eventos) {
        if (!missao.houveColisaoComPerigo() || estado != EstadoPartida.EM_ANDAMENTO) {
            return;
        }

        int removidos = missao.removerPerigosNaPosicaoDaNave();
        missao.getNave().perderVida();
        pontuacao -= 5 * Math.max(1, removidos);
        eventos.add(EventoJogo.COLISAO);

        if (missao.getNave().getVidas() <= 0) {
            estado = EstadoPartida.DERROTA;
            mensagens.add("Colisao fatal! Suas vidas acabaram.");
        } else {
            mensagens.add("Colisao detectada! -5 pontos por perigo e -1 vida.");
        }
    }

    private void verificarPontuacao(List<String> mensagens) {
        if (pontuacao <= 0 && estado == EstadoPartida.EM_ANDAMENTO) {
            estado = EstadoPartida.DERROTA;
            mensagens.add("Pontuacao zerada. Missao falhou.");
        }
    }

    // A vitoria exige todos os passageiros entregues na base, nao apenas coletados.
    private void verificarVitoria() {
        if (resgatados < totalPassageiros || estado != EstadoPartida.EM_ANDAMENTO) {
            return;
        }

        estado = EstadoPartida.VITORIA;
    }

    public String getPiloto() {
        return piloto;
    }

    public Dificuldade getDificuldade() {
        return dificuldade;
    }

    public Missao getMissao() {
        return missao;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public int getMovimentos() {
        return movimentos;
    }

    public int getResgatados() {
        return resgatados;
    }

    public int getTotalPassageiros() {
        return totalPassageiros;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public long getDuracaoSegundos() {
        long fim = fimMillis >= 0 ? fimMillis : System.currentTimeMillis();
        return (fim - inicioMillis) / 1000;
    }
}
