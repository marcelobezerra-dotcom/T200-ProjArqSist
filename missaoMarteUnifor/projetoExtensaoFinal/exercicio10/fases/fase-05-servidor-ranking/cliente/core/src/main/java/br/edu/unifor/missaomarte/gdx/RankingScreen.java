package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import br.edu.unifor.missaomarte.application.servidor.Resposta;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import java.util.Collections;
import java.util.List;

/**
 * Mostra o Top 10; apos uma vitoria tambem mostra o resumo da partida.
 * <p>Logado: o ranking e global e vem do servidor (assincrono: mostra "Carregando" ate a resposta chegar).
 * Convidado: o ranking e o local, lido do arquivo na hora.
 * <p>Recebe a sessao so para leitura (null quando aberta pelo menu).
 */
public class RankingScreen extends TelaBase {
    private enum Estado { CARREGANDO, PRONTO, ERRO }

    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);
    private static final Color ERRO = new Color(1f, 0.4f, 0.4f, 1f);

    private final GameSession resumo;
    private final boolean global;
    private boolean novoRegistro;
    private Estado estado = Estado.PRONTO;
    private List<RankingEntry> ranking = Collections.emptyList();
    private String mensagemDeErro = "";

    public RankingScreen(MissaoMarteGame jogo, GameSession resumo, boolean novoRegistroLocal) {
        super(jogo);
        this.resumo = resumo;
        this.global = jogo.sessao() != null;
        this.novoRegistro = novoRegistroLocal;
        if (global) {
            carregar();
        } else {
            this.ranking = jogo.rankingService().top();
        }
    }

    private void carregar() {
        estado = Estado.CARREGANDO;
        jogo.servidor().carregarRanking(jogo.sessao(), this::aoCarregar);
    }

    private void aoCarregar(Resposta<List<RankingEntry>> resposta) {
        if (resposta.isSucesso()) {
            ranking = resposta.getValor();
            novoRegistro = resumo != null && estaNoTop(resumo);
            estado = Estado.PRONTO;
            return;
        }
        mensagemDeErro = resposta.getMensagem();
        estado = Estado.ERRO;
        if (resposta.isNaoAutorizado()) {
            jogo.sair();
        }
    }

    private boolean estaNoTop(GameSession sessao) {
        for (RankingEntry entrada : ranking) {
            if (entrada.getNome().equalsIgnoreCase(sessao.getPiloto()) && entrada.getPontuacao() == sessao.getPontuacao()) {
                return true;
            }
        }
        return false;
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

    @Override
    protected void desenhar(float delta) {
        jogo.batch().begin();
        float y = 560f;

        if (resumo != null) {
            escala(1.5f);
            cor(DESTAQUE);
            texto("PARABÉNS, " + resumo.getPiloto() + "!", 60f, y);
            y -= 34f;
            escala(1f);
            cor(Color.WHITE);
            texto("Dificuldade: " + resumo.getDificuldade()
                    + "   Pontuação final: " + resumo.getPontuacao()
                    + "   Movimentos: " + resumo.getMovimentos()
                    + "   Tempo: " + resumo.getDuracaoSegundos() + "s", 60f, y);
            y -= 26f;
            if (novoRegistro) {
                cor(DESTAQUE);
                texto(global ? "Você está no Top 10 global!" : "Novo registro no ranking!", 60f, y);
                y -= 26f;
            }
            y -= 20f;
        }

        escala(1.5f);
        cor(DESTAQUE);
        texto(global ? "=== RANKING GLOBAL - TOP 10 ===" : "=== RANKING LOCAL - TOP 10 ===", 60f, y);
        y -= 40f;

        escala(1f);
        cor(Color.WHITE);
        if (estado == Estado.CARREGANDO) {
            cor(Color.LIGHT_GRAY);
            texto("Carregando o ranking do servidor...", 60f, y);
        } else if (estado == Estado.ERRO) {
            cor(ERRO);
            texto(mensagemDeErro, 60f, y);
            if (jogo.sessao() != null) {
                texto("R: tentar de novo", 60f, y - 24f);
            }
        } else if (ranking.isEmpty()) {
            texto("Sem registros ainda.", 60f, y);
        }
        if (estado == Estado.PRONTO) {
            int posicao = 1;
            for (RankingEntry entrada : ranking) {
                texto(posicao + ". " + entrada.getNome() + " - " + entrada.getPontuacao() + " pts - "
                        + entrada.getDuracaoSegundos() + "s - " + entrada.getMovimentos() + " mov", 60f, y);
                y -= 24f;
                posicao++;
            }
        }

        cor(Color.LIGHT_GRAY);
        textoCentralizado("Enter ou Esc: voltar ao menu", MissaoMarteGame.LARGURA / 2f, 40f);
        jogo.batch().end();
    }
}
