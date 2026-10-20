package graspexercicio10.model;

/**
 * GRASP Information Expert: a própria Dificuldade conhece sua pontuação
 * inicial.
 *
 * No exercício 10 original, Main.definirPontuacaoInicial(Dificuldade) continha
 * esse switch. Como Dificuldade possui a informação necessária (ela mesma), o
 * método pertence a ela.
 */
public enum Dificuldade {
    FACIL, MEDIO, DIFICIL;

    // Information Expert: a dificuldade reúne os parâmetros da missão.
    public int getQuantidadePassageiros() { return this == FACIL ? 4 : 5; }
    public int getQuantidadeAsteroides() { return this == FACIL ? 1 : this == DIFICIL ? 3 : 2; }
    public int getQuantidadeInimigos() { return getQuantidadeAsteroides(); }

    public int getPontuacaoInicial() {
        switch (this) {
            case FACIL:
                return 15;
            case DIFICIL:
                return 30;
            default:
                return 20;
        }
    }

    public static Dificuldade deString(String texto) {
        if (texto == null)
            return MEDIO;
        switch (texto.trim().toLowerCase()
                .replace("á", "a").replace("í", "i").replace("é", "e")) {
            case "facil":
                return FACIL;
            case "dificil":
                return DIFICIL;
            default:
                return MEDIO;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case FACIL:
                return "Fácil";
            case DIFICIL:
                return "Difícil";
            default:
                return "Médio";
        }
    }
}
