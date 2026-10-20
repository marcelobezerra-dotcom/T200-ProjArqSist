package graspexercicio10.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * GRASP Information Expert: Missao conhece todos os elementos do tabuleiro
 * e portanto é responsável por verificar colisões e checar posições ocupadas.
 *
 * Polymorphism: atualiza todos os perigos sem conhecer seus tipos concretos.
 */
public class Missao {
    private final Base base = new Base();
    private final Nave             nave;
    private final List<Passageiro> passageiros = new ArrayList<>();
    private final List<Perigo> perigos = new ArrayList<>();

    public Missao(Nave nave) {
        this.nave = nave;
    }

    public Nave             getNave()          { return nave; }
    public Base             getBase()          { return base; }
    public List<Passageiro> getPassageiros()   { return Collections.unmodifiableList(passageiros); }

    public void addPassageiro(Passageiro p)    { passageiros.add(p); }
    public void addPerigo(Perigo perigo)      { perigos.add(perigo); }

    // GRASP Polymorphism: lista unificada pela abstração Perigo.
    public List<Perigo> getPerigos() {
        return Collections.unmodifiableList(perigos);
    }

    /** Todos podem ser desenhados pelo mesmo contrato; nave tem prioridade na célula. */
    public List<ElementoMapa> getElementos() {
        List<ElementoMapa> elementos = new ArrayList<>();
        elementos.add(nave);
        elementos.addAll(passageiros);
        elementos.addAll(perigos);
        elementos.add(base);
        return Collections.unmodifiableList(elementos);
    }

    // GRASP Information Expert: só Missao tem todos os dados necessários para esta verificação
    public boolean posicaoOcupada(int x, int y) {
        for (ElementoMapa elemento : getElementos()) {
            if (elemento.ocupa(x, y)) return true;
        }
        return false;
    }

    public boolean verificaColisao() {
        for (Perigo p : getPerigos()) {
            if (p.colideCom(nave)) return true;
        }
        return false;
    }

    public void atualizarPerigos(Random random, int minX, int maxX, int minY, int maxY) {
        for (Perigo perigo : perigos) {
            perigo.atualizar(random, minX, maxX, minY, maxY);
        }
    }

    public Passageiro passagemNaPosicao() {
        for (Passageiro p : passageiros) {
            if (p.mesmaPosicao(nave)) return p;
        }
        return null;
    }

    public boolean embarcarPassageiroNaPosicao() {
        Iterator<Passageiro> it = passageiros.iterator();
        while (it.hasNext()) {
            Passageiro p = it.next();
            if (p.mesmaPosicao(nave)) {
                boolean ok = nave.embarcar(p);
                if (ok) it.remove();
                return ok;
            }
        }
        return false;
    }

    public boolean todosEmbarcados() { return passageiros.isEmpty(); }
}
