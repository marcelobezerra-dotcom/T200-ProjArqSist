package br.edu.unifor.missaomarte.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;

/**
 * Menu principal: 1 Jogar, 2 Ver ranking, 3 Limpar ranking, 4 Sair.
 * <p>GRASP: Controller de interface (traduz tecla em navegacao). SOLID: SRP (so o menu).
 */
public class MenuScreen extends TelaBase {
    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);

    private String mensagem = "";

    public MenuScreen(MissaoMarteGame jogo) {
        super(jogo);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(new InputAdapter() {
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
                jogo.setScreen(new NovaPartidaScreen(jogo));
                return true;
            case Keys.NUM_2:
            case Keys.NUMPAD_2:
                jogo.setScreen(new RankingScreen(jogo, null, false));
                return true;
            case Keys.NUM_3:
            case Keys.NUMPAD_3:
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

        escala(2.5f);
        cor(DESTAQUE);
        textoCentralizado("MISSAO MARTE", MissaoMarteGame.LARGURA / 2f, 480f);

        escala(1.5f);
        cor(Color.WHITE);
        texto("1) Jogar", 300f, 360f);
        texto("2) Ver ranking", 300f, 320f);
        texto("3) Limpar ranking", 300f, 280f);
        texto("4) Sair", 300f, 240f);

        escala(1f);
        cor(DESTAQUE);
        texto(mensagem, 300f, 180f);

        cor(Color.LIGHT_GRAY);
        textoCentralizado("Pressione o numero da opcao", MissaoMarteGame.LARGURA / 2f, 60f);

        jogo.batch().end();
    }
}
