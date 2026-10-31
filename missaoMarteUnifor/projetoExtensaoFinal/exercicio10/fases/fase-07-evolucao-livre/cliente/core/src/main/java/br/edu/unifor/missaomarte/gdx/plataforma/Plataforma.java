package br.edu.unifor.missaomarte.gdx.plataforma;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.OnscreenKeyboardType;
import com.badlogic.gdx.Input.TextInputListener;
import java.util.function.Consumer;

/**
 * O que muda entre desktop e celular, num lugar so: se o modo toque esta ligado e se existe digitacao nativa.
 * <p>No celular nao ha teclado fisico, entao a tela abre a caixa de texto do sistema ({@code Gdx.input.getTextInput}).
 * No desktop, mesmo no modo toque (mouse no lugar do dedo), o teclado fisico continua valendo.
 */
public final class Plataforma {
    public enum TipoDeTexto { NORMAL, SENHA, NUMERO }

    private final boolean toque;
    private final boolean textoNativo;

    public Plataforma(boolean toque, boolean textoNativo) {
        this.toque = toque;
        this.textoNativo = textoNativo;
    }

    /** Desktop com teclado e mouse (comportamento das fases anteriores). */
    public static Plataforma desktop() {
        return new Plataforma(false, false);
    }

    public boolean usaToque() {
        return toque;
    }

    public boolean temTextoNativo() {
        return textoNativo;
    }

    /** Abre a caixa de texto do sistema; se o jogador cancelar, nada e devolvido. */
    public void pedirTexto(String titulo, String atual, TipoDeTexto tipo, Consumer<String> aoConfirmar) {
        if (!textoNativo) {
            return;
        }
        OnscreenKeyboardType teclado = tipo == TipoDeTexto.SENHA ? OnscreenKeyboardType.Password
                : tipo == TipoDeTexto.NUMERO ? OnscreenKeyboardType.NumberPad : OnscreenKeyboardType.Default;
        Gdx.input.getTextInput(new TextInputListener() {
            @Override
            public void input(String texto) {
                // A caixa de texto responde fora da thread do jogo; a tela so pode ser alterada na thread principal.
                Gdx.app.postRunnable(() -> aoConfirmar.accept(texto));
            }

            @Override
            public void canceled() {
            }
        }, titulo, atual, "", teclado);
    }
}
