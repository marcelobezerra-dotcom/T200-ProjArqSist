package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.gdx.MapaGeometria;
import br.edu.unifor.missaomarte.gdx.MissaoMarteGame;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.recursos.Recursos;
import br.edu.unifor.missaomarte.gdx.recursos.SimboloParaImagem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.List;

/**
 * Desenha o mundo do jogo com sprites: terreno, objetos (na posicao visual), nave com propulsor, efeitos e textos flutuantes.
 * <p>O sprite de cada objeto vem do simbolo que ele informa ({@link SimboloParaImagem}); nenhuma classe de dominio conhece texturas.
 * <p>SOLID: SRP (so desenho do mundo). Nao contem regra de jogo.
 */
public class MundoRenderer {
    private static final Color VERDE = new Color(0.45f, 1f, 0.6f, 1f);
    private static final Color VERMELHO = new Color(1f, 0.4f, 0.4f, 1f);

    private final Recursos recursos;
    private final SpriteBatch batch;
    private final BitmapFont fonte;
    private final MapaGeometria mapa;
    private final GlyphLayout layout = new GlyphLayout();
    private final Animation<TextureRegion> animacaoBase;
    private final Animation<TextureRegion> animacaoInimigo;
    private final Animation<TextureRegion> animacaoPropulsor;

    public MundoRenderer(Recursos recursos, SpriteBatch batch, BitmapFont fonte, MapaGeometria mapa) {
        this.recursos = recursos;
        this.batch = batch;
        this.fonte = fonte;
        this.mapa = mapa;
        this.animacaoBase = recursos.animacao(Imagem.BASE, 0.25f, PlayMode.LOOP);
        this.animacaoInimigo = recursos.animacao(Imagem.INIMIGO, 0.3f, PlayMode.LOOP);
        this.animacaoPropulsor = recursos.animacao(Imagem.PROPULSOR, 0.08f, PlayMode.LOOP);
    }

    /**
     * @param anguloNave graus no sentido anti-horario (0 aponta para cima)
     * @param naveVisivel false em quadros alternados durante o piscar apos uma colisao
     * @param deslocX deslocamento do tremor da tela
     */
    public void desenhar(Missao missao, PosicoesVisuais posicoes, EfeitosVisuais efeitos, MensagensFlutuantes flutuantes,
            float tempo, float anguloNave, boolean naveVisivel, float deslocX, float deslocY) {
        float celula = mapa.celula();
        batch.begin();

        TextureRegion terreno = recursos.quadro(Imagem.TERRENO);
        for (int y = MissaoMarteGame.MIN; y <= MissaoMarteGame.MAX; y++) {
            for (int x = MissaoMarteGame.MIN; x <= MissaoMarteGame.MAX; x++) {
                batch.draw(terreno, mapa.xCanto(x) + deslocX, mapa.yCanto(y) + deslocY, celula, celula);
            }
        }

        desenharObjetos(List.of(missao.getBase()), posicoes, tempo, deslocX, deslocY);
        desenharObjetos(missao.getAsteroides(), posicoes, tempo, deslocX, deslocY);
        desenharObjetos(missao.getPassageiros(), posicoes, tempo, deslocX, deslocY);
        desenharObjetos(missao.getInimigos(), posicoes, tempo, deslocX, deslocY);

        if (naveVisivel) {
            desenharNave(missao, posicoes, tempo, anguloNave, deslocX, deslocY);
        }

        desenharEfeitos(efeitos, deslocX, deslocY);
        desenharFlutuantes(flutuantes, deslocX, deslocY);

        batch.end();
    }

    private void desenharObjetos(List<? extends ObjetoRenderizavel> objetos, PosicoesVisuais posicoes, float tempo, float dx, float dy) {
        float celula = mapa.celula();
        for (ObjetoRenderizavel objeto : objetos) {
            PosicaoVisual visual = posicoes.de(objeto);
            batch.draw(regiao(objeto, tempo), mapa.xCanto(visual.x()) + dx, mapa.yCanto(visual.y()) + dy, celula, celula);
        }
    }

    private TextureRegion regiao(ObjetoRenderizavel objeto, float tempo) {
        Imagem imagem = SimboloParaImagem.de(objeto.getSimbolo());
        switch (imagem) {
            case BASE:
                return animacaoBase.getKeyFrame(tempo, true);
            case INIMIGO:
                return animacaoInimigo.getKeyFrame(tempo, true);
            default:
                return recursos.quadro(imagem);
        }
    }

    private void desenharNave(Missao missao, PosicoesVisuais posicoes, float tempo, float angulo, float dx, float dy) {
        float celula = mapa.celula();
        PosicaoVisual visual = posicoes.de(missao.getNave());
        float x = mapa.xCanto(visual.x()) + dx;
        float y = mapa.yCanto(visual.y()) + dy;
        batch.draw(animacaoPropulsor.getKeyFrame(tempo, true), x, y, celula / 2f, celula / 2f, celula, celula, 1f, 1f, angulo);
        batch.draw(recursos.quadro(Imagem.NAVE), x, y, celula / 2f, celula / 2f, celula, celula, 1f, 1f, angulo);
    }

    private void desenharEfeitos(EfeitosVisuais efeitos, float dx, float dy) {
        for (EfeitosVisuais.Efeito efeito : efeitos.ativos()) {
            TextureRegion[] quadros = recursos.quadros(efeito.imagem());
            float tamanho = mapa.celula() * (efeito.imagem() == Imagem.EXPLOSAO ? 1.6f : 1.2f);
            batch.draw(quadros[efeito.quadro(quadros.length)],
                    mapa.xCentro(efeito.x()) - tamanho / 2f + dx, mapa.yCentro(efeito.y()) - tamanho / 2f + dy, tamanho, tamanho);
        }
    }

    private void desenharFlutuantes(MensagensFlutuantes flutuantes, float dx, float dy) {
        fonte.getData().setScale(1.3f);
        for (MensagensFlutuantes.Mensagem mensagem : flutuantes.ativas()) {
            Color base = mensagem.positiva() ? VERDE : VERMELHO;
            fonte.setColor(base.r, base.g, base.b, mensagem.alfa());
            layout.setText(fonte, mensagem.texto());
            fonte.draw(batch, layout, mapa.xCentro(mensagem.x()) - layout.width / 2f + dx,
                    mapa.yCentro(mensagem.y()) + layout.height / 2f + dy);
        }
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(1f);
    }
}
