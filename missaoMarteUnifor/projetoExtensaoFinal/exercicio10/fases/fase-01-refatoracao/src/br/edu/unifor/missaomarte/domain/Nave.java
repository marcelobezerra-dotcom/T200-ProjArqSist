package br.edu.unifor.missaomarte.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Nave implements ObjetoRenderizavel {
    private Posicao posicao;
    private int vidas;
    private final int capacidade;
    private final List<Passageiro> passageiros;

    public Nave(int x, int y, int vidas, int capacidade) {
        this.posicao = new Posicao(x, y);
        this.vidas = vidas;
        this.capacidade = capacidade;
        this.passageiros = new ArrayList<>();
    }

    public int getX() {
        return posicao.getX();
    }

    public int getY() {
        return posicao.getY();
    }

    @Override
    public Posicao getPosicao() {
        return posicao;
    }

    @Override
    public char getSimbolo() {
        return 'N';
    }

    public int getVidas() {
        return vidas;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public List<Passageiro> getPassageiros() {
        return Collections.unmodifiableList(passageiros);
    }

    public boolean estaCheia() {
        return passageiros.size() >= capacidade;
    }

    public boolean embarcar(Passageiro passageiro) {
        if (estaCheia()) {
            return false;
        }
        return passageiros.add(passageiro);
    }

    public int desembarcarTodos() {
        int quantidade = passageiros.size();
        passageiros.clear();
        return quantidade;
    }

    public void perderVida() {
        vidas -= 1;
    }

    public void moverComLimites(int dx, int dy, int min, int max) {
        posicao = posicao.mover(dx, dy, min, max);
    }
}
