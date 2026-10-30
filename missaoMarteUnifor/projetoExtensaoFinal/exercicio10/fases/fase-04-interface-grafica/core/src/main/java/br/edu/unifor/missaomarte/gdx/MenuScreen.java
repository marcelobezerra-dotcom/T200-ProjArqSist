package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Menu principal: 1 Jogar, 2 Ver ranking, 3 Limpar ranking, 4 Sair.
 * <p>GRASP: Controller de interface (traduz tecla em navegacao). SOLID: SRP (so o menu).
 */
public class MenuScreen extends TelaBase {
    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);

    private final Animation<TextureRegion> propulsor;
    private String mensagem = "";

    public MenuScreen(MissaoMarteGame jogo) {
        super(jogo);
        this.propulsor = jogo.recursos().animacao(Imagem.PROPULSOR, 0.08f, PlayMode.LOOP);
    }

    @Override
    public void show() {
        super.show();
        jogo.sons().retomarMusica();
        definirEntrada(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return tratarTecla(keycode);
            }
        });
    }

    private boolean tratarTecla(int keycode) {
        switch (keycode) {
            case Keys.NUM_1:
            case Keys.NUMPAD_1:
                jogo.sons().tocar(Som.SELECIONAR);
                jogo.setScreen(new NovaPartidaScreen(jogo));
                return true;
            case Keys.NUM_2:
            case Keys.NUMPAD_2:
                jogo.sons().tocar(Som.SELECIONAR);
                jogo.setScreen(new RankingScreen(jogo, null, false));
                return true;
            case Keys.NUM_3:
            case Keys.NUMPAD_3:
                jogo.sons().tocar(Som.SELECIONAR);
                jogo.rankingService().limpar();
                mensagem = "Ranking limpo.";
                return true;
            case Keys.NUM_4:
            case Keys.NUMPAD_4:
                Gdx.app.exit();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void desenhar(float delta) {
        jogo.batch().begin();

        float flutuar = (float) Math.sin(tempo * 2f) * 6f;
        jogo.batch().draw(propulsor.getKeyFrame(tempo, true), 560f, 330f + flutuar, 128f, 128f);
        jogo.batch().draw(jogo.recursos().quadro(Imagem.NAVE), 560f, 330f + flutuar, 128f, 128f);

        escala(2.5f);
        cor(DESTAQUE);
        textoCentralizado("MISSÃO MARTE", MissaoMarteGame.LARGURA / 2f, 500f);

        escala(1.5f);
        cor(Color.WHITE);
        texto("1) Jogar", 280f, 380f);
        texto("2) Ver ranking", 280f, 340f);
        texto("3) Limpar ranking", 280f, 300f);
        texto("4) Sair", 280f, 260f);

        escala(1f);
        cor(DESTAQUE);
        texto(mensagem, 280f, 200f);

        cor(Color.LIGHT_GRAY);
        textoCentralizado("Pressione o número da opção   |   M: ligar ou desligar o som",
                MissaoMarteGame.LARGURA / 2f, 60f);

        jogo.batch().end();
    }
}
