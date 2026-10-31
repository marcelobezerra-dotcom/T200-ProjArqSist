package br.edu.unifor.missaomarte.gdx.hud;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.ModoDeJogo;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.gdx.layout.LayoutDaPartida;
import br.edu.unifor.missaomarte.gdx.layout.MargensSeguras;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
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
 * HUD da partida: barra superior (vidas, pontuacao, tempo, som), painel de informacoes e rodape.
 * <p>As posicoes vem do {@link LayoutDaPartida}: em paisagem o painel fica ao lado do mapa (como nas fases anteriores); em retrato
 * vira uma faixa abaixo do mapa. O texto cresce pelo fator recebido (telas de celular), sem alterar os tamanhos do desktop.
 * <p>E desenhado depois do mundo, para ficar por cima. Apenas le o estado da {@link GameSession}; nao altera nada.
 * <p>SOLID: SRP (so apresentacao do HUD).
 */
public class HudRenderer {
    private static final float BARRA_ALTURA = 12f;
    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);
    private static final Color CINZA = new Color(0.75f, 0.75f, 0.8f, 1f);

    private final Recursos recursos;
    private final SpriteBatch batch;
    private final ShapeRenderer formas;
    private final BitmapFont fonte;
    private int vidasMaximas;
    private LayoutDaPartida layout;
    private MargensSeguras margens = MargensSeguras.NENHUMA;
    private boolean modoToque;
    private float fator = 1f;

    public HudRenderer(Recursos recursos, SpriteBatch batch, ShapeRenderer formas, BitmapFont fonte) {
        this.recursos = recursos;
        this.batch = batch;
        this.formas = formas;
        this.fonte = fonte;
    }

    /** Layout do quadro atual, margens seguras, se os controles sao por toque e o multiplicador do texto. */
    public void definirLayout(LayoutDaPartida layout, MargensSeguras margens, boolean modoToque, float fatorDeTexto) {
        this.layout = layout;
        this.margens = margens;
        this.modoToque = modoToque;
        this.fator = Math.max(1f, fatorDeTexto);
    }

    public void desenhar(GameSession session, List<String> mensagens, boolean mudo, long segundos) {
        Nave nave = session.getMissao().getNave();
        vidasMaximas = Math.max(vidasMaximas, nave.getVidas());

        desenharFundos(session);

        batch.begin();
        desenharBarraSuperior(session, nave, mudo, segundos);
        if (layout.emRetrato()) {
            desenharFaixa(session, nave, mensagens);
        } else {
            desenharPainel(session, nave, mensagens);
        }
        desenharRodape(session);
        batch.end();
    }

    private float escala(float base) {
        fonte.getData().setScale(base * fator);
        return base * fator;
    }

    private void desenharFundos(GameSession session) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(0f, 0f, 0f, 0.55f);
        retangulo(layout.barraSuperior());
        retangulo(layout.barraInferior());
        formas.setColor(0f, 0f, 0f, 0.45f);
        retangulo(layout.painel());

        float progresso = session.getTotalPassageiros() == 0 ? 0f : (float) session.getResgatados() / session.getTotalPassageiros();
        RetanguloDeTela barra = areaDaBarraDeProgresso();
        formas.setColor(0.2f, 0.2f, 0.28f, 1f);
        formas.rect(barra.x(), barra.y(), barra.largura(), barra.altura());
        formas.setColor(0.3f, 0.85f, 0.5f, 1f);
        formas.rect(barra.x(), barra.y(), barra.largura() * progresso, barra.altura());
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void retangulo(RetanguloDeTela r) {
        formas.rect(r.x(), r.y(), r.largura(), r.altura());
    }

    // Em paisagem a barra fica abaixo da linha "Entregues"; em retrato, no pe da faixa.
    private RetanguloDeTela areaDaBarraDeProgresso() {
        RetanguloDeTela painel = layout.painel();
        if (layout.emRetrato()) {
            return new RetanguloDeTela(painel.x() + 8f, painel.y() + 6f, painel.largura() - 16f, 8f);
        }
        return new RetanguloDeTela(painel.x() + 10f, yDasEntregas() - 30f * fator, LayoutDaPartida.PAINEL_LARGURA_DO_TEXTO,
                BARRA_ALTURA);
    }

    // Linha de base do texto "Entregues" no painel lateral (soma dos espacos das linhas anteriores).
    private float yDasEntregas() {
        return layout.painel().topo() - 12f - (32f + 22f + 36f + 28f) * fator - 32f;
    }

    private void desenharBarraSuperior(GameSession session, Nave nave, boolean mudo, long segundos) {
        RetanguloDeTela barra = layout.barraSuperior();
        float x0 = 24f + margens.esquerda();
        for (int i = 0; i < vidasMaximas; i++) {
            Imagem icone = i < nave.getVidas() ? Imagem.ICONE_VIDA : Imagem.ICONE_VIDA_VAZIA;
            batch.draw(recursos.quadro(icone), x0 + i * 28f, barra.y() + 6f, 24f, 24f);
        }

        // O texto da barra cresce menos que o resto: ela tem so 36 unidades de altura e tres textos lado a lado.
        float fatorDaBarra = Math.min(fator, 1.2f);
        fonte.getData().setScale(fatorDaBarra);
        float baseline = barra.y() + 26f;
        float xPontos = Math.max(220f, x0 + vidasMaximas * 28f + 16f);
        float xTempo = xPontos + 200f * fatorDaBarra;
        float xSom = xTempo + 200f * fatorDaBarra + 20f;
        fonte.setColor(Color.WHITE);
        fonte.draw(batch, "Pontuação: " + session.getPontuacao(), xPontos, baseline);
        fonte.draw(batch, String.format("Tempo: %02d:%02d", segundos / 60, segundos % 60), xTempo, baseline);
        fonte.setColor(mudo ? CINZA : Color.WHITE);
        String som = modoToque ? "Som: " + (mudo ? "off" : "on") : "Som: " + (mudo ? "desligado" : "ligado") + " (M)";
        fonte.draw(batch, som, xSom, baseline);
        fonte.setColor(Color.WHITE);
    }

    private void desenharPainel(GameSession session, Nave nave, List<String> mensagens) {
        RetanguloDeTela painel = layout.painel();
        float x = painel.x() + 10f;
        float y = painel.topo() - 12f;

        escala(1.3f);
        fonte.setColor(DESTAQUE);
        fonte.draw(batch, "MISSÃO MARTE", x, y);

        escala(1f);
        fonte.setColor(Color.WHITE);
        y -= 32f * fator;
        fonte.draw(batch, "Piloto: " + session.getPiloto(), x, y);
        y -= 22f * fator;
        fonte.draw(batch, "Dificuldade: " + session.getDificuldade(), x, y);

        int aBordo = nave.getPassageiros().size();
        y -= 36f * fator;
        fonte.draw(batch, "A bordo: " + aBordo + "/" + nave.getCapacidade(), x, y);
        y -= 32f;
        for (int i = 0; i < nave.getCapacidade(); i++) {
            Imagem icone = i < aBordo ? Imagem.ICONE_PASSAGEIRO : Imagem.ICONE_VAGA;
            batch.draw(recursos.quadro(icone), x + i * 25f, y, 24f, 24f);
        }

        y -= 28f * fator;
        fonte.draw(batch, "Entregues: " + session.getResgatados() + "/" + session.getTotalPassageiros(), x, y);
        y -= 30f * fator + BARRA_ALTURA + 26f * fator - BARRA_ALTURA;
        fonte.draw(batch, "Restantes no mapa: " + session.getMissao().getPassageiros().size(), x, y);
        y -= 22f * fator;
        if (session.getModo() == ModoDeJogo.DINAMICO) {
            fonte.draw(batch, "Munição: " + session.getMunicao() + "   Mira: " + simboloDaMira(session.getMira()), x, y);
        } else {
            fonte.draw(batch, "Movimentos: " + session.getMovimentos(), x, y);
        }

        if (!mensagens.isEmpty()) {
            y -= 38f * fator;
            fonte.setColor(DESTAQUE);
            fonte.draw(batch, String.join("\n", mensagens), x, y, LayoutDaPartida.PAINEL_LARGURA_DO_TEXTO, Align.left, true);
            fonte.setColor(Color.WHITE);
        }
    }

    // Faixa do modo retrato: tres linhas de texto e a barra de progresso no pe.
    private void desenharFaixa(GameSession session, Nave nave, List<String> mensagens) {
        RetanguloDeTela faixa = layout.painel();
        float x = faixa.x() + 8f;
        float linha = 22f * fator;
        float y = faixa.topo() - 8f;

        escala(1f);
        fonte.setColor(Color.WHITE);
        fonte.draw(batch, "Piloto: " + session.getPiloto() + "   " + session.getDificuldade(), x, y);
        y -= linha;
        fonte.draw(batch, "Entregues: " + session.getResgatados() + "/" + session.getTotalPassageiros()
                + "   A bordo: " + nave.getPassageiros().size() + "/" + nave.getCapacidade(), x, y);
        y -= linha;
        if (session.getModo() == ModoDeJogo.DINAMICO) {
            fonte.draw(batch, "Munição: " + session.getMunicao() + "   Mira: " + simboloDaMira(session.getMira()), x, y);
        } else {
            fonte.draw(batch, "Movimentos: " + session.getMovimentos(), x, y);
        }

        if (!mensagens.isEmpty()) {
            fonte.setColor(DESTAQUE);
            float xMensagem = faixa.x() + faixa.largura() * 0.56f;
            fonte.draw(batch, String.join(" ", mensagens), xMensagem, faixa.topo() - 8f, faixa.largura() * 0.42f, Align.left, true);
            fonte.setColor(Color.WHITE);
        }
    }

    private void desenharRodape(GameSession session) {
        RetanguloDeTela barra = layout.barraInferior();
        escala(1f);
        fonte.setColor(CINZA);
        String texto;
        if (modoToque) {
            texto = session.getModo() == ModoDeJogo.DINAMICO
                    ? "Segure as setas para mover. Atirar usa munição."
                    : "Toque nas setas para mover e em Embarcar para pegar ou entregar passageiros";
        } else {
            texto = session.getModo() == ModoDeJogo.DINAMICO
                    ? "W/A/S/D ou setas (segure): mover   F ou Espaço: atirar   P: pausa   M: som   Q ou Esc: abortar"
                    : "W/A/S/D ou setas: mover   E: embarcar   M: som   Q ou Esc: abortar";
        }
        fonte.draw(batch, texto, 24f + margens.esquerda(), barra.topo() - 10f);
        fonte.setColor(Color.WHITE);
    }

    private static String simboloDaMira(Acao mira) {
        switch (mira) {
            case ESQUERDA:
                return "<";
            case CIMA:
                return "^";
            case BAIXO:
                return "v";
            default:
                return ">";
        }
    }
}
