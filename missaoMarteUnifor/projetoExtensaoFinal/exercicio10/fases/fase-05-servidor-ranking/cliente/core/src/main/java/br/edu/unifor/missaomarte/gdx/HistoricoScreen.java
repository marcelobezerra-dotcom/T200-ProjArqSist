package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import br.edu.unifor.missaomarte.application.servidor.Resposta;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import java.util.Collections;
import java.util.List;

/** Historico das ultimas 10 partidas do jogador logado, lido do servidor (assincrono). */
public class HistoricoScreen extends TelaBase {
    private enum Estado { CARREGANDO, PRONTO, ERRO }

    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);
    private static final Color VITORIA = new Color(0.45f, 0.9f, 0.55f, 1f);
    private static final Color DERROTA = new Color(1f, 0.4f, 0.4f, 1f);

    private Estado estado = Estado.CARREGANDO;
    private List<PartidaRegistrada> partidas = Collections.emptyList();
    private String mensagemDeErro = "";

    public HistoricoScreen(MissaoMarteGame jogo) {
        super(jogo);
        carregar();
    }

    private void carregar() {
        estado = Estado.CARREGANDO;
        jogo.servidor().carregarHistorico(jogo.sessao(), this::aoCarregar);
    }

    private void aoCarregar(Resposta<List<PartidaRegistrada>> resposta) {
        if (resposta.isSucesso()) {
            partidas = resposta.getValor();
            estado = Estado.PRONTO;
            return;
        }
        mensagemDeErro = resposta.getMensagem();
        estado = Estado.ERRO;
        if (resposta.isNaoAutorizado()) {
            jogo.sair();
        }
    }

    @Override
    public void show() {
        super.show();
        definirEntrada(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Keys.R && estado == Estado.ERRO && jogo.sessao() != null) {
                    carregar();
                    return true;
                }
                if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Keys.ESCAPE || keycode == Keys.SPACE) {
                    jogo.sons().tocar(Som.SELECIONAR);
                    jogo.setScreen(new MenuScreen(jogo));
                    return true;
                }
                return false;
            }
        });
    }

    /** Data e hora UTC do servidor (2026-10-30T21:05:07.123Z) em formato curto: 2026-10-30 21:05. */
    static String formatarData(String iso) {
        if (iso == null || iso.length() < 16) {
            return "";
        }
        return iso.substring(0, 10) + " " + iso.substring(11, 16);
    }

    @Override
    protected void desenhar(float delta) {
        jogo.batch().begin();
        float y = 560f;

        escala(1.5f);
        cor(DESTAQUE);
        texto("=== MEU HISTÓRICO ===", 60f, y);
        y -= 28f;
        escala(1f);
        cor(Color.LIGHT_GRAY);
        texto("Piloto: " + (jogo.sessao() != null ? jogo.sessao().getNome() : "-") + "   (últimas 10 partidas, horário UTC)", 60f, y);
        y -= 40f;

        if (estado == Estado.CARREGANDO) {
            texto("Carregando o histórico do servidor...", 60f, y);
        } else if (estado == Estado.ERRO) {
            cor(DERROTA);
            texto(mensagemDeErro, 60f, y);
            if (jogo.sessao() != null) {
                texto("R: tentar de novo", 60f, y - 24f);
            }
        } else if (partidas.isEmpty()) {
            cor(Color.WHITE);
            texto("Você ainda não jogou nenhuma partida.", 60f, y);
        } else {
            for (PartidaRegistrada p : partidas) {
                boolean vitoria = "VITORIA".equals(p.getResultado());
                cor(vitoria ? VITORIA : DERROTA);
                texto(formatarData(p.getJogadaEm()) + "   " + (vitoria ? "VITÓRIA" : "DERROTA") + "   " + p.getDificuldade()
                        + "   " + p.getPontuacao() + " pts   " + p.getDuracaoSegundos() + "s   " + p.getMovimentos() + " mov", 60f, y);
                y -= 24f;
            }
        }

        cor(Color.LIGHT_GRAY);
        textoCentralizado("Enter ou Esc: voltar ao menu", MissaoMarteGame.LARGURA / 2f, 40f);
        jogo.batch().end();
    }
}
