package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.recursos.Recursos;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;
import java.util.List;

/**
 * HUD da partida: barra superior (vidas, pontuacao, tempo, som), painel lateral (a bordo, entregas, mensagens) e rodape.
 * <p>E desenhado depois do mundo, para ficar por cima. Apenas le o estado da {@link GameSession}; nao altera nada.
 * <p>SOLID: SRP (so apresentacao do HUD).
 */
public class HudRenderer {
    private static final float PAINEL_X = 580f;
    private static final float PAINEL_LARGURA = 200f;
    private static final float BARRA_Y = 372f;
    private static final float BARRA_ALTURA = 12f;
    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);
    private static final Color CINZA = new Color(0.75f, 0.75f, 0.8f, 1f);

    private final Recursos recursos;
    private final SpriteBatch batch;
    private final ShapeRenderer formas;
    private final BitmapFont fonte;
    private int vidasMaximas;

    public HudRenderer(Recursos recursos, SpriteBatch batch, ShapeRenderer formas, BitmapFont fonte) {
        this.recursos = recursos;
        this.batch = batch;
        this.formas = formas;
        this.fonte = fonte;
    }

    public void desenhar(GameSession session, List<String> mensagens, boolean mudo, long segundos) {
        Nave nave = session.getMissao().getNave();
        vidasMaximas = Math.max(vidasMaximas, nave.getVidas());

        desenharFundos(session);

        batch.begin();
        desenharBarraSuperior(session, nave, mudo, segundos);
        desenharPainel(session, nave, mensagens);
        fonte.setColor(CINZA);
        fonte.draw(batch, "W/A/S/D ou setas: mover   E: embarcar   M: som   Q ou Esc: abortar", 24f, 26f);
        fonte.setColor(Color.WHITE);
        batch.end();
    }

    private void desenharFundos(GameSession session) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(0f, 0f, 0f, 0.55f);
        formas.rect(0f, 564f, 800f, 36f);
        formas.rect(0f, 0f, 800f, 36f);
        formas.setColor(0f, 0f, 0f, 0.45f);
        formas.rect(PAINEL_X - 10f, 36f, PAINEL_LARGURA + 30f, 528f);

        float progresso = session.getTotalPassageiros() == 0 ? 0f : (float) session.getResgatados() / session.getTotalPassageiros();
        formas.setColor(0.2f, 0.2f, 0.28f, 1f);
        formas.rect(PAINEL_X, BARRA_Y, PAINEL_LARGURA, BARRA_ALTURA);
        formas.setColor(0.3f, 0.85f, 0.5f, 1f);
        formas.rect(PAINEL_X, BARRA_Y, PAINEL_LARGURA * progresso, BARRA_ALTURA);
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void desenharBarraSuperior(GameSession session, Nave nave, boolean mudo, long segundos) {
        for (int i = 0; i < vidasMaximas; i++) {
            Imagem icone = i < nave.getVidas() ? Imagem.ICONE_VIDA : Imagem.ICONE_VIDA_VAZIA;
            batch.draw(recursos.quadro(icone), 24f + i * 28f, 570f, 24f, 24f);
        }

        fonte.setColor(Color.WHITE);
        fonte.draw(batch, "Pontuação: " + session.getPontuacao(), 220f, 590f);
        fonte.draw(batch, String.format("Tempo: %02d:%02d", segundos / 60, segundos % 60), 420f, 590f);
        fonte.setColor(mudo ? CINZA : Color.WHITE);
        fonte.draw(batch, "Som: " + (mudo ? "desligado" : "ligado") + " (M)", 640f, 590f);
        fonte.setColor(Color.WHITE);
    }

    private void desenharPainel(GameSession session, Nave nave, List<String> mensagens) {
        fonte.getData().setScale(1.3f);
        fonte.setColor(DESTAQUE);
        fonte.draw(batch, "MISSÃO MARTE", PAINEL_X, 552f);
        fonte.getData().setScale(1f);

        fonte.setColor(Color.WHITE);
        fonte.draw(batch, "Piloto: " + session.getPiloto(), PAINEL_X, 520f);
        fonte.draw(batch, "Dificuldade: " + session.getDificuldade(), PAINEL_X, 498f);

        int aBordo = nave.getPassageiros().size();
        fonte.draw(batch, "A bordo: " + aBordo + "/" + nave.getCapacidade(), PAINEL_X, 462f);
        for (int i = 0; i < nave.getCapacidade(); i++) {
            Imagem icone = i < aBordo ? Imagem.ICONE_PASSAGEIRO : Imagem.ICONE_VAGA;
            batch.draw(recursos.quadro(icone), PAINEL_X + i * 25f, 430f, 24f, 24f);
        }

        fonte.draw(batch, "Entregues: " + session.getResgatados() + "/" + session.getTotalPassageiros(), PAINEL_X, 402f);
        fonte.draw(batch, "Restantes no mapa: " + session.getMissao().getPassageiros().size(), PAINEL_X, 346f);
        fonte.draw(batch, "Movimentos: " + session.getMovimentos(), PAINEL_X, 324f);

        if (!mensagens.isEmpty()) {
            fonte.setColor(DESTAQUE);
            fonte.draw(batch, String.join("\n", mensagens), PAINEL_X, 286f, PAINEL_LARGURA, Align.left, true);
            fonte.setColor(Color.WHITE);
        }
    }
}
