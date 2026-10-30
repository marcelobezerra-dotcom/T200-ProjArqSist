package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.RankingService;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.Random;

/**
 * Ponto de entrada do jogo no libGDX: cria os recursos compartilhados e troca de {@link com.badlogic.gdx.Screen}.
 * <p>Ciclo de vida: create() uma vez; render() ~60x/s (delegado a tela atual pela classe Game); resize() a cada mudanca de janela;
 * dispose() ao fechar. Padrao: State (cada Screen e um estado do jogo). SOLID: DIP (regras e ranking chegam pelo construtor).
 * <p>Limite: a classe Game nao chama dispose() das telas; as telas deste projeto nao guardam recursos pesados.
 */
public class MissaoMarteGame extends Game {
    public static final String TAG = "MissaoMarte";
    public static final float LARGURA = 800f;
    public static final float ALTURA = 600f;
    public static final int MIN = -5;
    public static final int MAX = 5;

    private final MissaoFactory missaoFactory;
    private final RankingService rankingService;
    private final Random random;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private BitmapFont fonte;

    public MissaoMarteGame(MissaoFactory missaoFactory, RankingService rankingService, Random random) {
        this.missaoFactory = missaoFactory;
        this.rankingService = rankingService;
        this.random = random;
    }

    @Override
    public void create() {
        Gdx.app.log(TAG, "create(): janela " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
        camera = new OrthographicCamera();
        viewport = new FitViewport(LARGURA, ALTURA, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        fonte = new BitmapFont();

        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        Gdx.app.log(TAG, "dispose()");
        super.dispose();
        fonte.dispose();
        shapes.dispose();
        batch.dispose();
    }

    MissaoFactory missaoFactory() {
        return missaoFactory;
    }

    RankingService rankingService() {
        return rankingService;
    }

    Random random() {
        return random;
    }

    OrthographicCamera camera() {
        return camera;
    }

    FitViewport viewport() {
        return viewport;
    }

    SpriteBatch batch() {
        return batch;
    }

    ShapeRenderer shapes() {
        return shapes;
    }

    BitmapFont fonte() {
        return fonte;
    }
}
