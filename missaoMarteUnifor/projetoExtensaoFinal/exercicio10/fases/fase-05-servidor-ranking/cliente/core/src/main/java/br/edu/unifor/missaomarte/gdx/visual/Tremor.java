package br.edu.unifor.missaomarte.gdx.visual;

/** Tremor da tela (apos uma colisao): deslocamento que decai ate zero. Determinista, sem aleatoriedade. */
public final class Tremor {
    private float duracao;
    private float amplitude;
    private float tempo;
    private boolean ativo;

    public void iniciar(float duracao, float amplitude) {
        this.duracao = duracao;
        this.amplitude = amplitude;
        this.tempo = 0;
        this.ativo = true;
    }

    public void atualizar(float delta) {
        if (!ativo) {
            return;
        }
        tempo += delta;
        if (tempo >= duracao) {
            ativo = false;
        }
    }

    public boolean ativo() {
        return ativo;
    }

    /** Fracao restante do tremor, de 1 (inicio) a 0 (fim). */
    public float intensidade() {
        return ativo ? 1f - tempo / duracao : 0f;
    }

    public float deslocamentoX() {
        return ativo ? (float) Math.sin(tempo * 70) * amplitude * intensidade() : 0f;
    }

    public float deslocamentoY() {
        return ativo ? (float) Math.cos(tempo * 53) * amplitude * intensidade() : 0f;
    }
}
