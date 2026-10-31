package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.RankingService;
import br.edu.unifor.missaomarte.application.servidor.EnderecoDoServidor;
import br.edu.unifor.missaomarte.application.servidor.ServidorDoJogo;
import br.edu.unifor.missaomarte.application.servidor.Sessao;
import br.edu.unifor.missaomarte.gdx.audio.GerenciadorDeSom;
import br.edu.unifor.missaomarte.gdx.layout.EscalaDeTela;
import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.plataforma.Plataforma;
import br.edu.unifor.missaomarte.gdx.recursos.Recursos;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Random;

/**
 * Ponto de entrada do jogo no libGDX: cria os recursos compartilhados e troca de {@link com.badlogic.gdx.Screen}.
 * <p>Ciclo de vida: create() uma vez; render() ~60x/s (delegado a tela atual pela classe Game); resize() a cada mudanca de janela;
 * dispose() ao fechar. Padrao: State (cada Screen e um estado do jogo). SOLID: DIP (regras e ranking chegam pelo construtor).
 * <p>Fase 4: os assets (imagens e sons) sao carregados uma vez por {@link Recursos} e liberados em dispose().
 * <p>Fase 5: o jogo tem dois modos. Sem sessao (convidado) usa o ranking local; com sessao (logado) usa o servidor.
 * A sessao fica so na memoria e e descartada ao sair da conta, ao expirar ou ao fechar o jogo.
 * <p>Limite: a classe Game nao chama dispose() das telas; as telas deste projeto nao guardam recursos pesados.
 * <p>Fase 6: o mesmo codigo roda no desktop e no Android. A {@link Plataforma} diz se o modo toque esta ligado; nesse modo o
 * viewport e um {@link ExtendViewport} (a area extra da tela recebe os controles virtuais), senao continua o {@link FitViewport}
 * de 800x600. O endereco do servidor vem de {@link EnderecoDoServidor} e pode ser trocado na tela de configuracao.
 */
public class MissaoMarteGame extends Game {
    public static final String TAG = "MissaoMarte";
    public static final float LARGURA = 800f;
    public static final float ALTURA = 600f;
    public static final int MIN = -5;
    public static final int MAX = 5;

    private final MissaoFactory missaoFactory;
    private final RankingService rankingService;
    private final ServidorDoJogo servidor;
    private final EnderecoDoServidor endereco;
    private final Plataforma plataforma;
    private final Random random;
    private Sessao sessao;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private BitmapFont fonte;
    private Recursos recursos;
    private GerenciadorDeSom sons;

    public MissaoMarteGame(MissaoFactory missaoFactory, RankingService rankingService, ServidorDoJogo servidor,
            EnderecoDoServidor endereco, Plataforma plataforma, Random random) {
        this.missaoFactory = missaoFactory;
        this.rankingService = rankingService;
        this.servidor = servidor;
        this.endereco = endereco;
        this.plataforma = plataforma;
        this.random = random;
    }

    @Override
    public void create() {
        Gdx.app.log(TAG, "create(): janela " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight()
                + (plataforma.usaToque() ? " (modo toque)" : ""));
        camera = new OrthographicCamera();
        viewport = plataforma.usaToque() ? new ExtendViewport(LARGURA, ALTURA, camera) : new FitViewport(LARGURA, ALTURA, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        // No Android a tecla Voltar chega como tecla comum, em vez de fechar o app sem perguntar.
        Gdx.input.setCatchKey(Keys.BACK, true);

        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        fonte = new BitmapFont();

        recursos = new Recursos();
        recursos.carregar();
        sons = new GerenciadorDeSom(recursos);

        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        Gdx.app.log(TAG, "dispose()");
        super.dispose();
        recursos.dispose();
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

    ServidorDoJogo servidor() {
        return servidor;
    }

    EnderecoDoServidor endereco() {
        return endereco;
    }

    Plataforma plataforma() {
        return plataforma;
    }

    /** True quando os controles por toque (botoes virtuais) estao ligados. */
    boolean modoToque() {
        return plataforma.usaToque();
    }

    // Pixels da tela que cabem em uma unidade do mundo (a escala do viewport).
    private float pixelsPorUnidade() {
        return viewport == null || viewport.getWorldWidth() <= 0f ? 0f : viewport.getScreenWidth() / viewport.getWorldWidth();
    }

    /** Multiplicador do texto para que ele fique legivel no celular; 1 fora do modo toque. */
    float fatorDeTexto() {
        return modoToque() ? EscalaDeTela.fatorDeTexto(Gdx.graphics.getDensity(), pixelsPorUnidade()) : 1f;
    }

    /** Menor lado de um botao tocavel (48 dp) em unidades do mundo; 0 fora do modo toque. */
    float alvoMinimo() {
        return modoToque() ? EscalaDeTela.alvoMinimoNoMundo(Gdx.graphics.getDensity(), pixelsPorUnidade()) : 0f;
    }

    /** Folgas de recortes e barras do sistema, em unidades do mundo (zero no desktop). */
    MargensSeguras margensSeguras() {
        if (!modoToque()) {
            return MargensSeguras.NENHUMA;
        }
        float escala = pixelsPorUnidade();
        return new MargensSeguras(
                EscalaDeTela.pixelsParaMundo(Gdx.graphics.getSafeInsetLeft(), escala),
                EscalaDeTela.pixelsParaMundo(Gdx.graphics.getSafeInsetRight(), escala),
                EscalaDeTela.pixelsParaMundo(Gdx.graphics.getSafeInsetTop(), escala),
                EscalaDeTela.pixelsParaMundo(Gdx.graphics.getSafeInsetBottom(), escala));
    }

    /** Sessao do jogador logado, ou null no modo convidado. */
    Sessao sessao() {
        return sessao;
    }

    void entrar(Sessao nova) {
        Gdx.app.log(TAG, "login: " + nova);
        this.sessao = nova;
    }

    void sair() {
        Gdx.app.log(TAG, "logout");
        this.sessao = null;
    }

    Random random() {
        return random;
    }

    OrthographicCamera camera() {
        return camera;
    }

    Viewport viewport() {
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

    Recursos recursos() {
        return recursos;
    }

    GerenciadorDeSom sons() {
        return sons;
    }
}
