package br.edu.unifor.missaomarte.application;

/**
 * Conta passos de tempo fixo: acumula o tempo recebido e diz quantos intervalos completos se passaram.
 * <p>E a base do passo fixo (fixed timestep): o jogo avanca em passos iguais, independentes da taxa de quadros.
 */
public final class Temporizador {
    private static final float TOLERANCIA = 1e-4f;

    private final float intervalo;
    private float acumulado;

    public Temporizador(float intervalo) {
        if (intervalo <= 0f) {
            throw new IllegalArgumentException("O intervalo deve ser positivo.");
        }
        this.intervalo = intervalo;
    }

    /** Soma o tempo e devolve quantos intervalos completos terminaram; o resto fica acumulado. */
    public int avancar(float delta) {
        if (delta > 0f) {
            acumulado += delta;
        }
        int passos = 0;
        while (acumulado + TOLERANCIA >= intervalo) {
            acumulado -= intervalo;
            passos++;
        }
        if (acumulado < 0f) {
            acumulado = 0f;
        }
        return passos;
    }

    public void reiniciar() {
        acumulado = 0f;
    }

    public float getIntervalo() {
        return intervalo;
    }
}
