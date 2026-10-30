package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.ConfiguracaoDificuldade;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.RegrasDificuldade;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;

/**
 * Pede nome e dificuldade e cria a {@link GameSession} usando a fabrica e as regras ja existentes.
 * <p>O nome aceita so ASCII simples: evita caracteres fora da faixa de glifos da fonte padrao (ate Latin-1) e simplifica a validacao.
 * <p>Teclas: digitar o nome, BACKSPACE apaga, SETAS esquerda/direita mudam a dificuldade, ENTER inicia, ESC volta.
 * <p>Esta tela nao usa o atalho global M, porque a letra M faz parte do texto digitado.
 */
public class NovaPartidaScreen extends TelaBase {
    private static final int TAMANHO_MAXIMO_NOME = 12;
    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);

    private final Dificuldade[] dificuldades = Dificuldade.values();
    private final StringBuilder nome = new StringBuilder();
    private int indice;

    public NovaPartidaScreen(MissaoMarteGame jogo) {
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

            @Override
            public boolean keyTyped(char caractere) {
                if (nome.length() < TAMANHO_MAXIMO_NOME && caractereValido(caractere)) {
                    nome.append(caractere);
                    return true;
                }
                return false;
            }
        });
    }

    private static boolean caractereValido(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == ' ' || c == '-' || c == '_';
    }

    private boolean tratarTecla(int keycode) {
        switch (keycode) {
            case Keys.BACKSPACE:
                if (nome.length() > 0) {
                    nome.deleteCharAt(nome.length() - 1);
                }
                return true;
            case Keys.LEFT:
                indice = (indice + dificuldades.length - 1) % dificuldades.length;
                jogo.sons().tocar(Som.SELECIONAR);
                return true;
            case Keys.RIGHT:
                indice = (indice + 1) % dificuldades.length;
                jogo.sons().tocar(Som.SELECIONAR);
                return true;
            case Keys.ENTER:
            case Keys.NUMPAD_ENTER:
                iniciarPartida();
                return true;
            case Keys.ESCAPE:
                jogo.setScreen(new MenuScreen(jogo));
                return true;
            default:
                return false;
        }
    }

    private void iniciarPartida() {
        String piloto = nome.toString().trim();
        if (piloto.isEmpty()) {
            piloto = "Piloto";
        }
        Dificuldade dificuldade = dificuldades[indice];
        MissaoFactory fabrica = jogo.missaoFactory();
        Missao missao = fabrica.criarMissao(dificuldade, MissaoMarteGame.MIN, MissaoMarteGame.MAX, jogo.random());
        GameSession session = new GameSession(
                piloto, dificuldade, missao, fabrica.pontuacaoInicial(dificuldade),
                MissaoMarteGame.MIN, MissaoMarteGame.MAX, jogo.random());
        jogo.sons().tocar(Som.SELECIONAR);
        jogo.setScreen(new GameScreen(jogo, session));
    }

    @Override
    protected void desenhar(float delta) {
        Dificuldade dificuldade = dificuldades[indice];
        ConfiguracaoDificuldade cfg = RegrasDificuldade.obter(dificuldade);
        String cursor = ((int) (tempo * 2f)) % 2 == 0 ? "_" : " ";

        jogo.batch().begin();

        escala(2f);
        cor(DESTAQUE);
        textoCentralizado("NOVA PARTIDA", MissaoMarteGame.LARGURA / 2f, 500f);

        escala(1.5f);
        cor(Color.WHITE);
        texto("Nome do piloto: " + nome + cursor, 200f, 380f);
        texto("Dificuldade: < " + dificuldade + " >", 200f, 320f);

        escala(1f);
        cor(Color.LIGHT_GRAY);
        texto("Passageiros: " + cfg.getQuantidadePassageiros()
                + "   Asteroides: " + cfg.getQuantidadeAsteroides()
                + "   Inimigos: " + cfg.getQuantidadeInimigos()
                + "   Pontuação inicial: " + cfg.getPontuacaoInicial(), 200f, 270f);

        textoCentralizado("Digite o nome   |   setas esquerda/direita: dificuldade   |   Enter: decolar   |   Esc: voltar",
                MissaoMarteGame.LARGURA / 2f, 60f);

        jogo.batch().end();
    }
}
