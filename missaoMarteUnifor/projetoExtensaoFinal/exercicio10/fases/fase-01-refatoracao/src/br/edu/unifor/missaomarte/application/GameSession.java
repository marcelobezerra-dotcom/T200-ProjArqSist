package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Passageiro;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameSession {
    private final String piloto;
    private final Dificuldade dificuldade;
    private final Missao missao;
    private final int min;
    private final int max;
    private final Random random;
    private final long inicioMillis;

    private int pontuacao;
    private int movimentos;
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
        this.estado = EstadoPartida.EM_ANDAMENTO;
    }

    public ResultadoTurno processarAcao(Acao acao) {
        List<String> mensagens = new ArrayList<>();

        if (acao == Acao.INVALIDA) {
            mensagens.add("Comando invalido.");
            return new ResultadoTurno(estado, mensagens);
        }

        if (acao == Acao.SAIR) {
            estado = EstadoPartida.ABORTADA;
            mensagens.add("Missao abortada pelo jogador.");
            return new ResultadoTurno(estado, mensagens);
        }

        if (acao == Acao.EMBARCAR) {
            embarcarPassageiro(mensagens);
        } else {
            moverNave(acao);
            pontuacao -= 1;
            movimentos += 1;
        }

        missao.moverInimigosAleatoriamente(min, max, random);
        verificarColisao(mensagens);
        verificarPontuacao(mensagens);
        verificarVitoria(mensagens);

        return new ResultadoTurno(estado, mensagens);
    }

    private void embarcarPassageiro(List<String> mensagens) {
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

    private void verificarColisao(List<String> mensagens) {
        if (!missao.houveColisaoComPerigo() || estado != EstadoPartida.EM_ANDAMENTO) {
            return;
        }

        int removidos = missao.removerPerigosNaPosicaoDaNave();
        missao.getNave().perderVida();
        pontuacao -= 5 * Math.max(1, removidos);

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

    private void verificarVitoria(List<String> mensagens) {
        if (!missao.todosPassageirosForamResgatados() || estado != EstadoPartida.EM_ANDAMENTO) {
            return;
        }

        if (missao.getNave().getX() == 0 && missao.getNave().getY() == 0) {
            estado = EstadoPartida.VITORIA;
        } else {
            mensagens.add("Todos resgatados! Retorne para a base (0,0) para concluir.");
        }
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

    public EstadoPartida getEstado() {
        return estado;
    }

    public long getDuracaoSegundos() {
        return (System.currentTimeMillis() - inicioMillis) / 1000;
    }
}
