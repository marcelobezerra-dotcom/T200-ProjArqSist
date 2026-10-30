package br.edu.unifor.missaomarte.gdx.recursos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Carrega, guarda e libera todas as imagens e sons pelo AssetManager.
 * <p>Padrao: Facade (um ponto unico de acesso aos assets). Texturas e sons sao compartilhados (Flyweight informal) e
 * liberados em {@link #dispose()}. O AssetManager e uma instancia do jogo, nao um campo estatico.
 */
public class Recursos implements Disposable {
    private final AssetManager gerenciador = new AssetManager();
    private final Map<Imagem, TextureRegion[]> quadros = new EnumMap<>(Imagem.class);

    /** Carrega tudo de uma vez (poucos arquivos pequenos); bloqueia ate terminar. */
    public void carregar() {
        for (Imagem imagem : Imagem.values()) {
            gerenciador.load(imagem.caminho(), Texture.class);
        }
        for (Som som : Som.values()) {
            if (som.musica()) {
                gerenciador.load(som.caminho(), Music.class);
            } else {
                gerenciador.load(som.caminho(), Sound.class);
            }
        }
        gerenciador.finishLoading();

        for (Imagem imagem : Imagem.values()) {
            Texture textura = gerenciador.get(imagem.caminho(), Texture.class);
            textura.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            TextureRegion[][] grade = TextureRegion.split(textura, imagem.larguraQuadro(), imagem.alturaQuadro());
            quadros.put(imagem, grade[0]);
        }
        Gdx.app.log("Recursos", Imagem.values().length + " imagens e " + Som.values().length + " sons carregados");
    }

    public Texture textura(Imagem imagem) {
        return gerenciador.get(imagem.caminho(), Texture.class);
    }

    /** Primeiro quadro da imagem (a imagem inteira quando ela tem um so quadro). */
    public TextureRegion quadro(Imagem imagem) {
        return quadros.get(imagem)[0];
    }

    public TextureRegion[] quadros(Imagem imagem) {
        return quadros.get(imagem);
    }

    public Animation<TextureRegion> animacao(Imagem imagem, float duracaoDoQuadro, PlayMode modo) {
        Animation<TextureRegion> animacao = new Animation<>(duracaoDoQuadro, quadros.get(imagem));
        animacao.setPlayMode(modo);
        return animacao;
    }

    public Sound som(Som som) {
        return gerenciador.get(som.caminho(), Sound.class);
    }

    public Music musica() {
        return gerenciador.get(Som.MUSICA.caminho(), Music.class);
    }

    @Override
    public void dispose() {
        gerenciador.dispose();
    }
}
