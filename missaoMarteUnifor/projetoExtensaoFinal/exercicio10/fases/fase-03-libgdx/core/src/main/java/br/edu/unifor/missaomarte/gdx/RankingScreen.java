package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import java.util.List;

/**
 * Mostra o Top 10; apos uma vitoria tambem mostra o resumo da partida.
 * <p>Recebe a sessao so para leitura (null quando aberta pelo menu).
 */
public class RankingScreen extends TelaBase {
    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);

    private final GameSession resumo;
    private final boolean novoRegistro;
    private final List<RankingEntry> ranking;

    public RankingScreen(MissaoMarteGame jogo, GameSession resumo, boolean novoRegistro) {
        super(jogo);
        this.resumo = resumo;
        this.novoRegistro = novoRegistro;
        this.ranking = jogo.rankingService().top();
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Keys.ESCAPE || keycode == Keys.SPACE) {
                    jogo.setScreen(new MenuScreen(jogo));
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void desenhar(float delta) {
        jogo.batch().begin();
        float y = 560f;

        if (resumo != null) {
            escala(1.5f);
            cor(DESTAQUE);
            texto("PARABENS, " + resumo.getPiloto() + "!", 60f, y);
            y -= 34f;
            escala(1f);
            cor(Color.WHITE);
            texto("Dificuldade: " + resumo.getDificuldade()
                    + "   Pontuacao final: " + resumo.getPontuacao()
                    + "   Movimentos: " + resumo.getMovimentos()
                    + "   Tempo: " + resumo.getDuracaoSegundos() + "s", 60f, y);
            y -= 26f;
            if (novoRegistro) {
                cor(DESTAQUE);
                texto("Novo registro no ranking!", 60f, y);
                y -= 26f;
            }
            y -= 20f;
        }

        escala(1.5f);
        cor(DESTAQUE);
        texto("=== RANKING TOP 10 ===", 60f, y);
        y -= 40f;

        escala(1f);
        cor(Color.WHITE);
        if (ranking.isEmpty()) {
            texto("Sem registros ainda.", 60f, y);
        }
        int posicao = 1;
        for (RankingEntry entrada : ranking) {
            texto(posicao + ". " + entrada.getNome() + " - " + entrada.getPontuacao() + " pts - "
                    + entrada.getDuracaoSegundos() + "s - " + entrada.getMovimentos() + " mov", 60f, y);
            y -= 24f;
            posicao++;
        }

        cor(Color.LIGHT_GRAY);
        textoCentralizado("ENTER ou ESC: voltar ao menu", MissaoMarteGame.LARGURA / 2f, 40f);
        jogo.batch().end();
    }
}
