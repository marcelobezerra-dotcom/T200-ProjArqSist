package br.edu.unifor.missaomarte.application;

/**
 * Comandos do jogador, independentes de teclas.
 * <p>GRASP: Protected Variations/Indirection (trocar o teclado por outra entrada nao altera GameSession).
 * <p>fromInput centraliza a traducao de texto para valor tipado.
 */
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
