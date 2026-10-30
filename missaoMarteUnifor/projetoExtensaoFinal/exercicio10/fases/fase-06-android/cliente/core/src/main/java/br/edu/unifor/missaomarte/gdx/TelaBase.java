package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.gdx.entrada.BotaoVirtual;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.List;

/**
 * Base das telas: limpa a janela, desenha o fundo, aplica o viewport e registra o ciclo de vida da Screen no log.
 * <p>Padrao: Template Method (render() fixa a sequencia e chama {@link #desenhar(float)}). SOLID: SRP (so infraestrutura de tela).
 * <p>Metodos de texto devem ser usados entre batch.begin() e batch.end(). A tecla M (silenciar) vale em todas as telas que usam
 * {@link #definirEntrada(InputProcessor)}.
 * <p>Fase 6: no modo toque a tela publica uma lista de {@link BotaoVirtual}; a base desenha os botoes depois da tela e entrega
 * o toque em {@link #aoTocar(String, int)}. Fora do modo toque nada disso aparece.
 */
abstract class TelaBase implements Screen {
    protected final MissaoMarteGame jogo;
    /** Tempo acumulado em segundos desde que a tela foi criada; usado nas animacoes em laco. */
    protected float tempo;
    /** Botoes tocaveis da tela (so usados e desenhados no modo toque). */
    protected final List<BotaoVirtual> botoes = new ArrayList<>();
    private final GlyphLayout layout = new GlyphLayout();
    private final Vector2 ponto = new Vector2();

    protected TelaBase(MissaoMarteGame jogo) {
        this.jogo = jogo;
    }

    protected abstract void desenhar(float delta);

    /** Telas com controles no canto direito (a partida) usam a area extra da tela; as demais ficam centralizadas. */
    protected boolean controlesNoCanto() {
        return false;
    }

    /** Toque num botao; devolve true se tratou. O ponteiro identifica o dedo (para repetir enquanto apoiado). */
    protected boolean aoTocar(String id, int ponteiro) {
        return false;
    }

    protected void aoSoltar(int ponteiro) {
    }

    /** Tecla Voltar do Android ou Esc. */
    protected static boolean ehVoltar(int keycode) {
        return keycode == Keys.ESCAPE || keycode == Keys.BACK;
    }

    @Override
    public void show() {
        log("show()");
    }

    @Override
    public void render(float delta) {
        tempo += delta;
        ScreenUtils.clear(0.03f, 0.03f, 0.10f, 1f);
        Viewport viewport = jogo.viewport();
        viewport.apply();
        float larguraDoMundo = viewport.getWorldWidth();
        float alturaDoMundo = viewport.getWorldHeight();
        float centroX = controlesNoCanto() ? larguraDoMundo / 2f : MissaoMarteGame.LARGURA / 2f;
        jogo.camera().position.set(centroX, alturaDoMundo / 2f, 0f);
        jogo.camera().update();
        jogo.batch().setProjectionMatrix(jogo.camera().combined);
        jogo.shapes().setProjectionMatrix(jogo.camera().combined);

        jogo.batch().begin();
        jogo.batch().draw(jogo.recursos().textura(Imagem.FUNDO), centroX - larguraDoMundo / 2f, 0f, larguraDoMundo, alturaDoMundo);
        jogo.batch().end();

        desenhar(delta);
        desenharBotoes();

        jogo.fonte().setColor(Color.WHITE);
        jogo.fonte().getData().setScale(1f);
    }

    private void desenharBotoes() {
        if (!jogo.modoToque() || botoes.isEmpty()) {
            return;
        }
        ShapeRenderer formas = jogo.shapes();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(0.1f, 0.15f, 0.32f, 0.75f);
        for (BotaoVirtual b : botoes) {
            if (!b.getRotulo().isEmpty()) {
                formas.rect(b.getX(), b.getY(), b.getLargura(), b.getAltura());
            }
        }
        formas.end();
        formas.begin(ShapeType.Line);
        formas.setColor(1f, 0.85f, 0.3f, 0.9f);
        for (BotaoVirtual b : botoes) {
            formas.rect(b.getX(), b.getY(), b.getLargura(), b.getAltura());
        }
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        jogo.batch().begin();
        for (BotaoVirtual b : botoes) {
            if (!b.getRotulo().isEmpty()) {
                escala(b.getRotulo().length() <= 1 ? 2f : 1.2f);
                cor(Color.WHITE);
                textoCentralizado(b.getRotulo(), b.getX() + b.getLargura() / 2f, b.getY() + b.getAltura() / 2f);
            }
        }
        jogo.batch().end();
    }

    private BotaoVirtual botaoEm(int telaX, int telaY) {
        ponto.set(telaX, telaY);
        jogo.viewport().unproject(ponto);
        for (BotaoVirtual b : botoes) {
            if (b.contem(ponto.x, ponto.y)) {
                return b;
            }
        }
        return null;
    }

    @Override
    public void resize(int width, int height) {
        log("resize(" + width + ", " + height + ")");
        jogo.viewport().update(width, height, true);
    }

    @Override
    public void pause() {
        log("pause()");
    }

    @Override
    public void resume() {
        log("resume()");
    }

    @Override
    public void hide() {
        log("hide()");
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        log("dispose()");
    }

    protected void log(String metodo) {
        Gdx.app.log(MissaoMarteGame.TAG, getClass().getSimpleName() + "." + metodo);
    }

    /** Instala o processador da tela, precedido pelos atalhos globais (M silencia) e pelos toques nos botoes virtuais. */
    protected void definirEntrada(InputProcessor processador) {
        instalarEntrada(processador, true);
    }

    /** Como {@link #definirEntrada}, mas sem o atalho M, para telas em que a letra M faz parte do texto digitado. */
    protected void definirEntradaDeTexto(InputProcessor processador) {
        instalarEntrada(processador, false);
    }

    private void instalarEntrada(InputProcessor processador, boolean atalhoSom) {
        InputProcessor atalhos = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (atalhoSom && keycode == Keys.M) {
                    boolean mudo = jogo.sons().alternarMudo();
                    log(mudo ? "som desligado" : "som ligado");
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int telaX, int telaY, int ponteiro, int botao) {
                if (!jogo.modoToque()) {
                    return false;
                }
                BotaoVirtual b = botaoEm(telaX, telaY);
                return b != null && aoTocar(b.getId(), ponteiro);
            }

            @Override
            public boolean touchUp(int telaX, int telaY, int ponteiro, int botao) {
                aoSoltar(ponteiro);
                return false;
            }
        };
        Gdx.input.setInputProcessor(new InputMultiplexer(atalhos, processador));
    }

    protected void cor(Color cor) {
        jogo.fonte().setColor(cor);
    }

    protected void escala(float escala) {
        jogo.fonte().getData().setScale(escala);
    }

    protected void texto(String texto, float x, float y) {
        jogo.fonte().draw(jogo.batch(), texto, x, y);
    }

    protected void textoCentralizado(String texto, float centroX, float centroY) {
        layout.setText(jogo.fonte(), texto);
        jogo.fonte().draw(jogo.batch(), layout, centroX - layout.width / 2f, centroY + layout.height / 2f);
    }

    /** Desenha com quebra de linha e devolve a altura ocupada. */
    protected float textoQuebrado(String texto, float x, float y, float largura) {
        return jogo.fonte().draw(jogo.batch(), texto, x, y, largura, Align.left, true).height;
    }
}
