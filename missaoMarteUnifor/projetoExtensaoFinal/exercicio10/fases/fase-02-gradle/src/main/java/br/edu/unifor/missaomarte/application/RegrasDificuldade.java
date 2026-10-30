package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Dificuldade;

/**
 * Tabela de configuracao por dificuldade (switch). Nao e Strategy: nao ha comportamento intercambiavel.
 * <p>GRASP: Pure Fabrication, Protected Variations (os numeros mudam aqui e nao na fabrica).
 * <p>Limite OCP: uma nova dificuldade exige editar esta classe e o enum Dificuldade.
 */
public final class RegrasDificuldade {
    private RegrasDificuldade() {
    }

    public static ConfiguracaoDificuldade obter(Dificuldade dificuldade) {
        switch (dificuldade) {
            case MEDIO:
                return new ConfiguracaoDificuldade(70, 6, 8, 3);
            case DIFICIL:
                return new ConfiguracaoDificuldade(45, 8, 12, 5);
            default:
                return new ConfiguracaoDificuldade(100, 4, 4, 1);
        }
    }
}
