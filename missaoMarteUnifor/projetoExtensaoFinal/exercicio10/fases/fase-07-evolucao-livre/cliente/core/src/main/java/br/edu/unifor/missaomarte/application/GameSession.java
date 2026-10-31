package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.ResultadoDoTiro;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Regras de uma partida, turno a turno: movimento, embarque, entrega na base, colisao, pontuacao e vitoria.
 * <p>GRASP: Controller do caso de uso (camada application), Information Expert do estado da partida, Low Coupling (nao conhece o console).
 * <p>SOLID: SRP (regra de partida, sem entrada nem saida). Devolve {@link ResultadoTurno} em vez de imprimir.
 * <p>Limite: recebe Random e min/max diretamente, e o fim por pontuacao zerada e checado antes da vitoria no mesmo turno.
 * <p>Fase 7: no {@link ModoDeJogo#DINAMICO} a partida corre sozinha. A tela chama {@link #avancar(float)} a cada quadro com o tempo
 * decorrido; a sessao o converte em passos fixos ({@link Temporizador}) e aplica as mesmas regras de colisao, entrega, pontuacao e
 * vitoria. O tempo entra como parametro, entao tudo pode ser testado sem relogio real. O modo classico continua igual.
 */
public class GameSession {
    private static final float TOLERANCIA_DE_TEMPO = 1e-4f;

    private final String piloto;
    private final Dificuldade dificuldade;
    private final Missao missao;
    private final int min;
    private final int max;
    private final Random random;
    private final long inicioMillis;
    private long fimMillis = -1;

    private final ModoDeJogo modo;
    private final ConfiguracaoDeTempoReal tempoReal;
    private final Temporizador relogioDosInimigos;
    private final Temporizador relogioDosPerseguidores;
    private final Temporizador relogioDaPontuacao;
    private final List<Acao> direcoesPressionadas = new ArrayList<>();
    private Acao mira = Acao.DIREITA;
    private Acao passoPendente;
    private float esperaDaNave;
    private float esperaDoTiro;
    private float contagemRestante;
    private boolean pausada;
    private double tempoDeJogo;
    private ResultadoDoTiro ultimoTiro;

    private int pontuacao;
    private int movimentos;
    private int resgatados;
    private final int totalPassageiros;
    private EstadoPartida estado;

    public GameSession(String piloto, Dificuldade dificuldade, Missao missao, int pontuacaoInicial, int min, int max, Random random) {
        this(piloto, dificuldade, missao, pontuacaoInicial, min, max, random, ModoDeJogo.CLASSICO);
    }

    public GameSession(String piloto, Dificuldade dificuldade, Missao missao, int pontuacaoInicial, int min, int max, Random random,
            ModoDeJogo modo) {
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
        this.modo = modo;
        if (modo == ModoDeJogo.DINAMICO) {
            this.tempoReal = RegrasDeTempoReal.obter(dificuldade);
            this.relogioDosInimigos = new Temporizador(tempoReal.getIntervaloDosInimigos());
            this.relogioDosPerseguidores = new Temporizador(tempoReal.getIntervaloDosPerseguidores());
            this.relogioDaPontuacao = new Temporizador(tempoReal.getIntervaloDaPontuacao());
            this.contagemRestante = tempoReal.getContagemInicial();
        } else {
            this.tempoReal = null;
            this.relogioDosInimigos = null;
            this.relogioDosPerseguidores = null;
            this.relogioDaPontuacao = null;
        }
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

        if (modo == ModoDeJogo.DINAMICO) {
            return new ResultadoTurno(estado, mensagens, eventos);
        }

        if (acao == Acao.ATIRAR) {
            mensagens.add("A nave nao tem arma no modo classico.");
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
        if (modo == ModoDeJogo.DINAMICO) {
            return (long) tempoDeJogo;
        }
        long fim = fimMillis >= 0 ? fimMillis : System.currentTimeMillis();
        return (fim - inicioMillis) / 1000;
    }

    public ModoDeJogo getModo() {
        return modo;
    }

    public int getMunicao() {
        return missao.getNave().getMunicao();
    }

    public boolean isPausada() {
        return pausada;
    }

    /** Segundos que faltam para a partida comecar (0 quando ja esta valendo). */
    public float getContagemRestante() {
        return contagemRestante;
    }

    /** Direcao em que o proximo tiro sai (a ultima direcao pressionada). */
    public Acao getMira() {
        return mira;
    }

    /** Ultimo tiro disparado, ou null. A tela usa para desenhar o raio. */
    public ResultadoDoTiro getUltimoTiro() {
        return ultimoTiro;
    }

    // ---- Modo dinamico ----

    /**
     * Direcao pressionada: a nave anda enquanto ela continuar pressionada. A ultima pressionada vale.
     * Um toque mais curto que um quadro ainda vale um passo (guardado em {@code passoPendente} ate a nave poder andar).
     */
    public void pressionar(Acao direcao) {
        if (modo != ModoDeJogo.DINAMICO || !ehDirecao(direcao)) {
            return;
        }
        direcoesPressionadas.remove(direcao);
        direcoesPressionadas.add(direcao);
        mira = direcao;
        passoPendente = direcao;
    }

    public void soltar(Acao direcao) {
        direcoesPressionadas.remove(direcao);
    }

    public void soltarTudo() {
        direcoesPressionadas.clear();
    }

    public void pausar() {
        if (modo == ModoDeJogo.DINAMICO && estado == EstadoPartida.EM_ANDAMENTO) {
            pausada = true;
            direcoesPressionadas.clear();
            passoPendente = null;
        }
    }

    public void retomar() {
        pausada = false;
    }

    public void alternarPausa() {
        if (pausada) {
            retomar();
        } else {
            pausar();
        }
    }

    private static boolean ehDirecao(Acao acao) {
        return acao == Acao.CIMA || acao == Acao.BAIXO || acao == Acao.ESQUERDA || acao == Acao.DIREITA;
    }

    /**
     * Faz o tempo andar. Soma o tempo (limitado por quadro), conta a contagem inicial e converte o resto em passos fixos:
     * a nave anda se ha direcao pressionada, os inimigos e os perseguidores andam no ritmo da dificuldade e a pontuacao cai 1 ponto
     * por segundo. Sem efeito no modo classico, em pausa ou depois do fim da partida.
     */
    public ResultadoTurno avancar(float delta) {
        List<String> mensagens = new ArrayList<>();
        List<EventoJogo> eventos = new ArrayList<>();
        if (modo != ModoDeJogo.DINAMICO || estado != EstadoPartida.EM_ANDAMENTO || pausada || delta <= 0f) {
            return new ResultadoTurno(estado, mensagens, eventos);
        }

        float passo = Math.min(delta, tempoReal.getDeltaMaximo());
        if (contagemRestante > 0f) {
            contagemRestante -= passo;
            if (contagemRestante > 0f) {
                return new ResultadoTurno(estado, mensagens, eventos);
            }
            passo = -contagemRestante;
            contagemRestante = 0f;
        }

        tempoDeJogo += passo;
        esperaDoTiro = Math.max(0f, esperaDoTiro - passo);
        esperaDaNave = Math.max(0f, esperaDaNave - passo);

        if (esperaDaNave <= TOLERANCIA_DE_TEMPO && (!direcoesPressionadas.isEmpty() || passoPendente != null)) {
            Acao direcao = direcoesPressionadas.isEmpty()
                    ? passoPendente : direcoesPressionadas.get(direcoesPressionadas.size() - 1);
            passoPendente = null;
            passoDaNave(direcao, mensagens, eventos);
            esperaDaNave = tempoReal.getIntervaloDaNave();
        }

        for (int i = relogioDosInimigos.avancar(passo); i > 0 && estado == EstadoPartida.EM_ANDAMENTO; i--) {
            missao.moverInimigosComuns(min, max, random);
            aplicarRegrasDoPasso(mensagens, eventos);
        }
        if (missao.temPerseguidores()) {
            for (int i = relogioDosPerseguidores.avancar(passo); i > 0 && estado == EstadoPartida.EM_ANDAMENTO; i--) {
                missao.moverPerseguidores(min, max, random);
                aplicarRegrasDoPasso(mensagens, eventos);
            }
        }
        for (int i = relogioDaPontuacao.avancar(passo); i > 0 && estado == EstadoPartida.EM_ANDAMENTO; i--) {
            pontuacao -= 1;
            verificarPontuacao(mensagens);
        }

        concluirPasso(eventos);
        return new ResultadoTurno(estado, mensagens, eventos);
    }

    // O embarque e automatico: a nave que passa sobre um passageiro com lugar livre o recolhe.
    private void passoDaNave(Acao direcao, List<String> mensagens, List<EventoJogo> eventos) {
        Posicao antes = missao.getNave().getPosicao();
        moverNave(direcao);
        if (!missao.getNave().getPosicao().equals(antes)) {
            movimentos += 1;
            eventos.add(EventoJogo.MOVIMENTO);
        }
        if (missao.passageiroNaPosicaoDaNave() != null && !missao.getNave().estaCheia()) {
            embarcarPassageiro(mensagens, eventos);
        }
        processarBase(mensagens, eventos);
        aplicarRegrasDoPasso(mensagens, eventos);
    }

    private void aplicarRegrasDoPasso(List<String> mensagens, List<EventoJogo> eventos) {
        verificarColisao(mensagens, eventos);
        verificarPontuacao(mensagens);
        verificarVitoria();
    }

    private void concluirPasso(List<EventoJogo> eventos) {
        if (estado == EstadoPartida.VITORIA) {
            eventos.add(EventoJogo.VITORIA);
        } else if (estado == EstadoPartida.DERROTA) {
            eventos.add(EventoJogo.DERROTA);
        }
        registrarFim();
    }

    /**
     * Atira na direcao da mira se ha municao e a arma recarregou. O disparo destroi o primeiro asteroide ou inimigo da linha.
     * Cada tiro custa pontos. Sem efeito fora do modo dinamico, em pausa, na contagem inicial ou depois do fim.
     */
    public ResultadoTurno atirar() {
        List<String> mensagens = new ArrayList<>();
        List<EventoJogo> eventos = new ArrayList<>();
        boolean podeAtirar = modo == ModoDeJogo.DINAMICO && estado == EstadoPartida.EM_ANDAMENTO && !pausada && contagemRestante <= 0f;
        if (!podeAtirar || esperaDoTiro > TOLERANCIA_DE_TEMPO) {
            return new ResultadoTurno(estado, mensagens, eventos);
        }
        if (!missao.getNave().gastarMunicao()) {
            mensagens.add("Sem municao.");
            return new ResultadoTurno(estado, mensagens, eventos);
        }

        esperaDoTiro = tempoReal.getRecargaDoTiro();
        pontuacao -= tempoReal.getCustoDoTiro();
        int dx = mira == Acao.ESQUERDA ? -1 : mira == Acao.DIREITA ? 1 : 0;
        int dy = mira == Acao.CIMA ? -1 : mira == Acao.BAIXO ? 1 : 0;
        ultimoTiro = missao.disparar(missao.getNave().getPosicao(), dx, dy, min, max);
        eventos.add(EventoJogo.TIRO);
        if (ultimoTiro.acertou()) {
            eventos.add(EventoJogo.PERIGO_DESTRUIDO);
            mensagens.add("Alvo destruido!");
        }
        verificarPontuacao(mensagens);
        concluirPasso(eventos);
        return new ResultadoTurno(estado, mensagens, eventos);
    }
}
