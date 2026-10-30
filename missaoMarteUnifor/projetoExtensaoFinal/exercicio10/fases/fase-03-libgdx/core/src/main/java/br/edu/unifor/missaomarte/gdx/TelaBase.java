package br.edu.unifor.missaomarte.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Base das telas: limpa a janela, aplica o viewport e registra o ciclo de vida da Screen no log.
 * <p>Padrao: Template Method (render() fixa a sequencia e chama {@link #desenhar(float)}). SOLID: SRP (so infraestrutura de tela).
 * <p>Metodos de texto devem ser usados entre batch.begin() e batch.end().
 */
abstract class TelaBase implements Screen {
    protected final MissaoMarteGame jogo;
    private final GlyphLayout layout = new GlyphLayout();

    protected TelaBase(MissaoMarteGame jogo) {
        this.jogo = jogo;
    }

    protected abstract void desenhar(float delta);

    @Override
    public void show() {
        log("show()");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.03f, 0.03f, 0.10f, 1f);
        jogo.viewport().apply();
        jogo.batch().setProjectionMatrix(jogo.camera().combined);
        jogo.shapes().setProjectionMatrix(jogo.camera().combined);

        desenhar(delta);

        jogo.fonte().setColor(Color.WHITE);
        jogo.fonte().getData().setScale(1f);
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
