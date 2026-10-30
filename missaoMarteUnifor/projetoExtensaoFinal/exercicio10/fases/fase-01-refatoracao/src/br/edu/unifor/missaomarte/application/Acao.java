package br.edu.unifor.missaomarte.application;

public enum Acao {
    ESQUERDA,
    DIREITA,
    CIMA,
    BAIXO,
    EMBARCAR,
    SAIR,
    INVALIDA;

    public static Acao fromInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return INVALIDA;
        }
        switch (input.trim().toLowerCase()) {
            case "a":
                return ESQUERDA;
            case "d":
                return DIREITA;
            case "w":
                return CIMA;
            case "s":
                return BAIXO;
            case "e":
                return EMBARCAR;
            case "q":
                return SAIR;
            default:
                return INVALIDA;
        }
    }
}
