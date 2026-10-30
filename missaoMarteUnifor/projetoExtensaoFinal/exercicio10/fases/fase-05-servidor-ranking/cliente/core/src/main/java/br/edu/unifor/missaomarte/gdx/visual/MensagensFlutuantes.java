package br.edu.unifor.missaomarte.gdx.visual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Textos que sobem e desaparecem sobre o mapa (por exemplo "+20" ou "-6"). Posicoes em unidades de celula. */
public final class MensagensFlutuantes {
    private static final float SUBIDA_POR_SEGUNDO = 0.9f;

    public static final class Mensagem {
        private final String texto;
        private final float x;
        private final float yInicial;
        private final float duracao;
        private final boolean positiva;
        private float tempo;

        private Mensagem(String texto, float x, float y, float duracao, boolean positiva) {
            this.texto = texto;
            this.x = x;
            this.yInicial = y;
            this.duracao = duracao;
            this.positiva = positiva;
        }

        public String texto() {
            return texto;
        }

        public float x() {
            return x;
        }

        /** No jogo o Y cresce para baixo, entao subir na tela significa diminuir o Y da celula. */
        public float y() {
            return yInicial - SUBIDA_POR_SEGUNDO * tempo;
        }

        public boolean positiva() {
            return positiva;
        }

        /** Opacidade, de 1 (inicio) a 0 (fim). */
        public float alfa() {
            return Math.max(0f, 1f - tempo / duracao);
        }
    }

    private final List<Mensagem> ativas = new ArrayList<>();

    public void adicionar(String texto, float x, float y, boolean positiva) {
        ativas.add(new Mensagem(texto, x, y, 1.1f, positiva));
    }

    public void atualizar(float delta) {
        for (Mensagem mensagem : ativas) {
            mensagem.tempo += delta;
        }
        ativas.removeIf(mensagem -> mensagem.tempo >= mensagem.duracao);
    }

    public List<Mensagem> ativas() {
        return Collections.unmodifiableList(ativas);
    }
}
