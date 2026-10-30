package br.edu.unifor.missaomarte.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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

    public boolean todosPassageirosForamResgatados() {
        return passageiros.isEmpty();
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
