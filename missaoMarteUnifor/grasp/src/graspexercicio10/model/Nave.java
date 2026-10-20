package graspexercicio10.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Nave extends ElementoMapa {
    private final String id;
    private final int capacidade;
    private int vidas;
    private final List<Passageiro> passageiros = new ArrayList<>();

    public Nave(String id, int capacidade) {
        super(0, 0);
        this.id        = id;
        this.capacidade = capacidade;
        this.vidas     = 3;
    }

    public String           getId()          { return id; }
    @Override public String getNome()        { return id; }
    @Override public char getSimbolo()       { return '@'; }
    public int              getCapacidade()  { return capacidade; }
    public int              getVidas()       { return vidas; }
    public List<Passageiro> getPassageiros() { return Collections.unmodifiableList(passageiros); }

    /** Information Expert: a nave conhece sua posição e respeita os limites. */
    public boolean mover(AcaoJogo direcao, int minX, int maxX, int minY, int maxY) {
        return moverComLimites(direcao.getDx(), direcao.getDy(), minX, maxX, minY, maxY);
    }

    public boolean estaNaBase() { return ocupa(0, 0); }

    public boolean embarcar(Passageiro p) {
        if (passageiros.size() < capacidade) {
            passageiros.add(p);
            return true;
        }
        return false;
    }

    public void perderVida() {
        if (vidas > 0) vidas--;
    }
}
