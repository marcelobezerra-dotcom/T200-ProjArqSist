package br.edu.unifor.missaomarte.domain;

public enum Dificuldade {
    FACIL,
    MEDIO,
    DIFICIL;

    public static Dificuldade deString(String entrada) {
        if (entrada == null) {
            return FACIL;
        }
        switch (entrada.trim().toLowerCase()) {
            case "medio":
                return MEDIO;
            case "dificil":
                return DIFICIL;
            default:
                return FACIL;
        }
    }
}
