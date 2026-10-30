package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Animacoes de uma so execucao (explosao, brilho) que ocorrem em uma celula e somem sozinhas. */
public final class EfeitosVisuais {

    public static final class Efeito {
        private final Imagem imagem;
        private final float x;
        private final float y;
        private final float duracao;
        private float tempo;

        private Efeito(Imagem imagem, float x, float y, float duracao) {
            this.imagem = imagem;
            this.x = x;
            this.y = y;
            this.duracao = duracao;
        }

        public Imagem imagem() {
            return imagem;
        }

        public float x() {
            return x;
        }

        public float y() {
            return y;
        }

        /** Indice do quadro atual, entre 0 e total-1, distribuindo os quadros igualmente pela duracao. */
        public int quadro(int total) {
            int indice = (int) (tempo / duracao * total);
            return Math.max(0, Math.min(total - 1, indice));
        }
    }

    private final List<Efeito> ativos = new ArrayList<>();

    /** x e y em unidades de celula. */
    public void adicionar(Imagem imagem, float x, float y, float duracao) {
        ativos.add(new Efeito(imagem, x, y, duracao));
    }

    public void atualizar(float delta) {
        for (Efeito efeito : ativos) {
            efeito.tempo += delta;
        }
        ativos.removeIf(efeito -> efeito.tempo >= efeito.duracao);
    }

    public List<Efeito> ativos() {
        return Collections.unmodifiableList(ativos);
    }
}
