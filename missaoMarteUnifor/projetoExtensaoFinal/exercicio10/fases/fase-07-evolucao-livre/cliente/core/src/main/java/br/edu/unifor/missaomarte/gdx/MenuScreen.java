package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.gdx.entrada.BotaoVirtual;
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
 * Menu principal. Convidado: 1 Jogar, 2 Ver ranking local, 3 Limpar ranking local, 4 Entrar, 5 Sair, 6 Servidor.
 * Logado: 1 Jogar, 2 Ver ranking global, 3 Meu histórico, 4 Sair da conta, 5 Sair, 6 Servidor.
 * <p>No modo toque cada opção vira um botão (o toque chama a mesma ação da tecla). A tecla Voltar do Android sai do jogo.
 * <p>GRASP: Controller de interface (traduz tecla e toque em navegacao). SOLID: SRP (so o menu).
 */
public class MenuScreen extends TelaBase {
    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);
    private static final int OPCOES = 6;
    private static final float ESPACO_PADRAO = 40f;

    private final Animation<TextureRegion> propulsor;
    private float primeiraLinhaY;
    private float espacoEntreLinhas;
    private String mensagem = "";

    public MenuScreen(MissaoMarteGame jogo) {
        super(jogo);
        this.propulsor = jogo.recursos().animacao(Imagem.PROPULSOR, 0.08f, PlayMode.LOOP);
        calcularLinhas();
    }

    // No toque as linhas ficam mais altas (alvo de 48 dp) enquanto houver espaco ate a borda de baixo da tela; sem toque, como antes.
    private void calcularLinhas() {
        botoes.clear();
        if (jogo.modoToque()) {
            float bordaDeBaixo = MissaoMarteGame.ALTURA / 2f - jogo.viewport().getWorldHeight() / 2f;
            float maximo = (410f - (bordaDeBaixo + 120f)) / (OPCOES - 0.5f);
            espacoEntreLinhas = Math.max(ESPACO_PADRAO, Math.min(jogo.alvoMinimo() + 4f, Math.min(maximo, 110f)));
            primeiraLinhaY = 410f;
        } else {
            espacoEntreLinhas = ESPACO_PADRAO;
            primeiraLinhaY = 380f;
        }
        for (int i = 1; i <= OPCOES; i++) {
            botoes.add(new BotaoVirtual(String.valueOf(i), "", 250f, linhaY(i) - espacoEntreLinhas + 12f, 340f, espacoEntreLinhas - 4f));
        }
    }

    @Override
    protected void aoRedimensionar() {
        calcularLinhas();
    }

    private float linhaY(int opcao) {
        return primeiraLinhaY - (opcao - 1) * espacoEntreLinhas;
    }

    // Texto no meio da altura do botao (com o espaco padrao o deslocamento e zero).
    private float textoY(int opcao) {
        return linhaY(opcao) - (espacoEntreLinhas - ESPACO_PADRAO) / 2f;
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

    @Override
    protected boolean aoTocar(String id, int ponteiro) {
        return executar(Integer.parseInt(id));
    }

    private boolean tratarTecla(int keycode) {
        if (keycode == Keys.BACK) {
            Gdx.app.exit();
            return true;
        }
        if (keycode >= Keys.NUM_1 && keycode <= Keys.NUM_9) {
            return executar(keycode - Keys.NUM_1 + 1);
        }
        if (keycode >= Keys.NUMPAD_1 && keycode <= Keys.NUMPAD_9) {
            return executar(keycode - Keys.NUMPAD_1 + 1);
        }
        return false;
    }

    /** Executa a opcao do menu (1 a 6); devolve false para numeros sem opcao. */
    private boolean executar(int opcao) {
        switch (opcao) {
            case 1:
                jogo.sons().tocar(Som.SELECIONAR);
                jogo.setScreen(new NovaPartidaScreen(jogo));
                return true;
            case 2:
                jogo.sons().tocar(Som.SELECIONAR);
                jogo.setScreen(new RankingScreen(jogo, null, false));
                return true;
            case 3:
                jogo.sons().tocar(Som.SELECIONAR);
                if (logado()) {
                    jogo.setScreen(new HistoricoScreen(jogo));
                } else {
                    jogo.rankingService().limpar();
                    mensagem = "Ranking local limpo.";
                }
                return true;
            case 4:
                jogo.sons().tocar(Som.SELECIONAR);
                if (logado()) {
                    jogo.sair();
                    mensagem = "Você saiu da conta.";
                } else {
                    jogo.setScreen(new LoginScreen(jogo));
                }
                return true;
            case 5:
                Gdx.app.exit();
                return true;
            case 6:
                jogo.sons().tocar(Som.SELECIONAR);
                jogo.setScreen(new ConfigServidorScreen(jogo, () -> new MenuScreen(jogo)));
                return true;
            default:
                return false;
        }
    }

    private boolean logado() {
        return jogo.sessao() != null;
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
        boolean logado = logado();
        texto("1) Jogar", 280f, textoY(1));
        texto(logado ? "2) Ver ranking global" : "2) Ver ranking local", 280f, textoY(2));
        texto(logado ? "3) Meu histórico" : "3) Limpar ranking local", 280f, textoY(3));
        texto(logado ? "4) Sair da conta" : "4) Entrar ou criar conta", 280f, textoY(4));
        texto("5) Sair", 280f, textoY(5));
        texto("6) Servidor", 280f, textoY(6));

        escala(1f);
        cor(Color.LIGHT_GRAY);
        texto(logado ? "Piloto: " + jogo.sessao().getNome() + "   (ranking global)"
                : "Modo convidado   (ranking local)", 280f, jogo.modoToque() ? 470f : 440f);
        float baseDasLinhas = linhaY(OPCOES) - espacoEntreLinhas + 12f;
        float yRodape = Math.min(150f, baseDasLinhas - 8f);
        texto("Servidor: " + jogo.endereco().urlBase(), 280f, yRodape);
        cor(DESTAQUE);
        texto(mensagem, 280f, yRodape - 28f);

        cor(Color.LIGHT_GRAY);
        textoCentralizado(jogo.modoToque() ? "Toque na opção   |   M: ligar ou desligar o som"
                : "Pressione o número da opção   |   M: ligar ou desligar o som", MissaoMarteGame.LARGURA / 2f,
                jogo.modoToque() ? yRodape - 62f : 60f);

        jogo.batch().end();
    }
}
