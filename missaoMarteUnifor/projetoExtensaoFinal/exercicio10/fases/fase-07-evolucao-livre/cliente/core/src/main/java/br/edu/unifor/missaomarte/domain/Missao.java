package br.edu.unifor.missaomarte.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Cenario da partida: agrega base, nave, passageiros e perigos e responde perguntas sobre eles.
 * <p>GRASP: Information Expert (colisao, passageiro na posicao da nave), Creator (cria a Base), High Cohesion.
 * <p>Low Coupling: nao conhece console nem persistencia. As colecoes sao expostas como somente leitura.
 */
public class Missao {
    private final Base base;
    private final Nave nave;
    private final List<Passageiro> passageiros;
    private final List<Asteroide> asteroides;
    private final List<Inimigo> inimigos;

    public Missao(Nave nave) {
        this.base = new Base(0, 0);
        this.nave = nave;
        this.passageiros = new ArrayList<>();
        this.asteroides = new ArrayList<>();
        this.inimigos = new ArrayList<>();
    }

    public Base getBase() {
        return base;
    }

    public Nave getNave() {
        return nave;
    }

    public List<Passageiro> getPassageiros() {
        return Collections.unmodifiableList(passageiros);
    }

    public List<Asteroide> getAsteroides() {
        return Collections.unmodifiableList(asteroides);
    }

    public List<Inimigo> getInimigos() {
        return Collections.unmodifiableList(inimigos);
    }

    public void adicionarPassageiro(Passageiro passageiro) {
        passageiros.add(passageiro);
    }

    public void adicionarAsteroide(Asteroide asteroide) {
        asteroides.add(asteroide);
    }

    public void adicionarInimigo(Inimigo inimigo) {
        inimigos.add(inimigo);
    }

    public Passageiro passageiroNaPosicaoDaNave() {
        for (Passageiro passageiro : passageiros) {
            if (passageiro.getPosicao().equals(nave.getPosicao())) {
                return passageiro;
            }
        }
        return null;
    }

    public boolean removerPassageiro(Passageiro passageiro) {
        return passageiros.remove(passageiro);
    }

    public boolean houveColisaoComPerigo() {
        for (Asteroide asteroide : asteroides) {
            if (asteroide.colideCom(nave)) {
                return true;
            }
        }
        for (Inimigo inimigo : inimigos) {
            if (inimigo.colideCom(nave)) {
                return true;
            }
        }
        return false;
    }

    public void moverInimigosAleatoriamente(int min, int max, java.util.Random random) {
        for (Inimigo inimigo : inimigos) {
            inimigo.moverAleatoriamente(min, max, random);
        }
    }

    /** Move os inimigos que nao perseguem (os que andam ao acaso). */
    public void moverInimigosComuns(int min, int max, java.util.Random random) {
        for (Inimigo inimigo : inimigos) {
            if (!inimigo.persegue()) {
                inimigo.mover(nave.getPosicao(), min, max, random);
            }
        }
    }

    /** Move os perseguidores um passo em direcao a nave. */
    public void moverPerseguidores(int min, int max, java.util.Random random) {
        for (Inimigo inimigo : inimigos) {
            if (inimigo.persegue()) {
                inimigo.mover(nave.getPosicao(), min, max, random);
            }
        }
    }

    public boolean temPerseguidores() {
        for (Inimigo inimigo : inimigos) {
            if (inimigo.persegue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Dispara em linha reta a partir da origem (sem incluir a propria celula) e destroi o primeiro perigo encontrado.
     * Se ambos estiverem na mesma celula, o inimigo e atingido antes do asteroide.
     */
    public ResultadoDoTiro disparar(Posicao origem, int dx, int dy, int min, int max) {
        Posicao atual = origem;
        while (true) {
            Posicao proxima = atual.mover(dx, dy, min, max);
            if (proxima.equals(atual)) {
                return new ResultadoDoTiro(origem, atual, null);
            }
            atual = proxima;
            Perigo atingido = removerPerigoEm(atual);
            if (atingido != null) {
                return new ResultadoDoTiro(origem, atual, atingido);
            }
        }
    }

    private Perigo removerPerigoEm(Posicao posicao) {
        Iterator<Inimigo> iteradorInimigos = inimigos.iterator();
        while (iteradorInimigos.hasNext()) {
            Inimigo inimigo = iteradorInimigos.next();
            if (inimigo.getPosicao().equals(posicao)) {
                iteradorInimigos.remove();
                return inimigo;
            }
        }
        Iterator<Asteroide> iteradorAsteroides = asteroides.iterator();
        while (iteradorAsteroides.hasNext()) {
            Asteroide asteroide = iteradorAsteroides.next();
            if (asteroide.getPosicao().equals(posicao)) {
                iteradorAsteroides.remove();
                return asteroide;
            }
        }
        return null;
    }

    public int removerPerigosNaPosicaoDaNave() {
        int removidos = 0;
        Iterator<Asteroide> iteratorAsteroides = asteroides.iterator();
        while (iteratorAsteroides.hasNext()) {
            Asteroide asteroide = iteratorAsteroides.next();
            if (asteroide.colideCom(nave)) {
                iteratorAsteroides.remove();
                removidos++;
            }
        }

        Iterator<Inimigo> iteratorInimigos = inimigos.iterator();
        while (iteratorInimigos.hasNext()) {
            Inimigo inimigo = iteratorInimigos.next();
            if (inimigo.colideCom(nave)) {
                iteratorInimigos.remove();
                removidos++;
            }
        }

        return removidos;
    }
}
