package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.Acao;
import com.badlogic.gdx.Input.Keys;

/**
 * Traduz teclas do libGDX em {@link Acao}; teclas desconhecidas viram {@link Acao#INVALIDA}.
 * <p>Fase 6: o Android entrega os botoes de um controle (Bluetooth ou USB) como teclas. No libGDX o direcional ja tem o mesmo codigo
 * das setas (DPAD_UP = UP, e assim por diante), entao basta mapear A (ou o centro do direcional) como embarcar e B (ou a tecla
 * Voltar) como abortar. O mapeamento e testado, mas nao foi verificado com um controle real.
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
            case Keys.BUTTON_A:
            case Keys.DPAD_CENTER:
                return Acao.EMBARCAR;
            case Keys.Q:
            case Keys.ESCAPE:
            case Keys.BACK:
            case Keys.BUTTON_B:
                return Acao.SAIR;
            default:
                return Acao.INVALIDA;
        }
    }
}
