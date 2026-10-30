package br.edu.unifor.missaomarte.gdx.audio;

import br.edu.unifor.missaomarte.gdx.recursos.Recursos;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.audio.Music;

/**
 * Toca efeitos e a musica de fundo e controla o mudo. Quem reage aos eventos do jogo e a tela; aqui so se toca e se silencia.
 * <p>SOLID: SRP (so audio). Os arquivos vem de {@link Recursos}; este gerenciador nao carrega nem libera nada.
 */
public class GerenciadorDeSom {
    private final Recursos recursos;
    private boolean mudo;

    public GerenciadorDeSom(Recursos recursos) {
        this.recursos = recursos;
    }

    public void tocar(Som som) {
        if (mudo || som.musica()) {
            return;
        }
        recursos.som(som).play(som.volume());
    }

    /** Inicia ou retoma a musica em loop; nao reinicia se ja estiver tocando. */
    public void retomarMusica() {
        Music musica = recursos.musica();
        musica.setLooping(true);
        musica.setVolume(mudo ? 0f : Som.MUSICA.volume());
        if (!musica.isPlaying()) {
            musica.play();
        }
    }

    public void pausarMusica() {
        recursos.musica().pause();
    }

    /** Devolve true se o som ficou desligado. */
    public boolean alternarMudo() {
        mudo = !mudo;
        recursos.musica().setVolume(mudo ? 0f : Som.MUSICA.volume());
        return mudo;
    }

    public boolean mudo() {
        return mudo;
    }
}
