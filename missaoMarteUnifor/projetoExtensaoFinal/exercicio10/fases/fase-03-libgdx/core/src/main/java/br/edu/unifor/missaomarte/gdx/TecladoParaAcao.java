package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.Acao;
import com.badlogic.gdx.Input.Keys;

/**
 * Traduz teclas do libGDX em {@link Acao}; teclas desconhecidas viram {@link Acao#INVALIDA}.
 * <p>GRASP: Indirection/Protected Variations (trocar o teclado por toque ou controle nao altera GameSession). Padrao: Adapter simples.
 */
public final class TecladoParaAcao {
    private TecladoParaAcao() {
    }

    public static Acao converter(int keycode) {
        switch (keycode) {
            case Keys.W:
            case Keys.UP:
                return Acao.CIMA;
            case Keys.S:
            case Keys.DOWN:
                return Acao.BAIXO;
            case Keys.A:
            case Keys.LEFT:
                return Acao.ESQUERDA;
            case Keys.D:
            case Keys.RIGHT:
                return Acao.DIREITA;
            case Keys.E:
                return Acao.EMBARCAR;
            case Keys.Q:
            case Keys.ESCAPE:
                return Acao.SAIR;
            default:
                return Acao.INVALIDA;
        }
    }
}
