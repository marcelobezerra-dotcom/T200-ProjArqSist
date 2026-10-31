package br.edu.unifor.missaomarte.application;

/**
 * Como a partida corre. No classico (fases anteriores) cada acao do jogador e um turno; no dinamico o tempo passa sozinho
 * e a sessao avanca em passos de tempo fixo, com a nave andando enquanto a direcao esta pressionada.
 */
public enum ModoDeJogo {
    CLASSICO("Classico"),
    DINAMICO("Dinamico");

    private final String rotulo;

    ModoDeJogo(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
