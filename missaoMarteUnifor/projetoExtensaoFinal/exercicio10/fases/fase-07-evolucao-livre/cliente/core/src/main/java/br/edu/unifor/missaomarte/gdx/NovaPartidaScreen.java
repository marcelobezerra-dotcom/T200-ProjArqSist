package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.ConfiguracaoDificuldade;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.ModoDeJogo;
import br.edu.unifor.missaomarte.application.RegrasDeTempoReal;
import br.edu.unifor.missaomarte.application.RegrasDificuldade;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.gdx.entrada.BotaoVirtual;
import br.edu.unifor.missaomarte.gdx.plataforma.Plataforma.TipoDeTexto;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;

/**
 * Pede nome, dificuldade e modo de jogo e cria a {@link GameSession} usando a fabrica e as regras ja existentes.
 * <p>Logado: o piloto e o nome da conta. Convidado: o nome e digitado.
 * <p>O nome aceita so ASCII simples: evita caracteres fora da faixa de glifos da fonte padrao (ate Latin-1) e simplifica a validacao.
 * <p>Modo: dinamico (tempo real, padrao) ou classico (por turnos, como nas fases anteriores).
 * <p>Teclas: digitar o nome, BACKSPACE apaga, SETAS esquerda/direita mudam a dificuldade, CIMA/BAIXO mudam o modo, ENTER inicia,
 * ESC (ou Voltar) volta. Modo toque: botoes < e > para dificuldade e modo, Decolar e Voltar; tocar no nome abre a caixa de texto nativa.
 * <p>Esta tela nao usa o atalho global M, porque a letra M faz parte do texto digitado.
 */
public class NovaPartidaScreen extends TelaBase {
    private static final int TAMANHO_MAXIMO_NOME = 12;
    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);

    private final Dificuldade[] dificuldades = Dificuldade.values();
    private final ModoDeJogo[] modos = ModoDeJogo.values();
    private final StringBuilder nome = new StringBuilder();
    private int indice;
    private int indiceDoModo = ModoDeJogo.DINAMICO.ordinal();

    public NovaPartidaScreen(MissaoMarteGame jogo) {
        super(jogo);
        if (!logado()) {
            botoes.add(new BotaoVirtual("nome", "", 190f, 376f, 460f, 38f));
        }
        botoes.add(new BotaoVirtual("anterior", "<", 560f, 326f, 56f, 44f));
        botoes.add(new BotaoVirtual("proxima", ">", 626f, 326f, 56f, 44f));
        botoes.add(new BotaoVirtual("modo_anterior", "<", 560f, 276f, 56f, 44f));
        botoes.add(new BotaoVirtual("modo_proximo", ">", 626f, 276f, 56f, 44f));
        botoes.add(new BotaoVirtual("decolar", "Decolar", 300f, 150f, 200f, 56f));
        botoes.add(new BotaoVirtual("voltar", "Voltar", 40f, 60f, 140f, 44f));
    }

    @Override
    public void show() {
        super.show();
        definirEntradaDeTexto(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return tratarTecla(keycode);
            }

            @Override
            public boolean keyTyped(char caractere) {
                if (!logado() && nome.length() < TAMANHO_MAXIMO_NOME && caractereValido(caractere)) {
                    nome.append(caractere);
                    return true;
                }
                return false;
            }
        });
    }

    private boolean logado() {
        return jogo.sessao() != null;
    }

    private static boolean caractereValido(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == ' ' || c == '-' || c == '_';
    }

    @Override
    protected boolean aoTocar(String id, int ponteiro) {
        switch (id) {
            case "nome":
                jogo.plataforma().pedirTexto("Nome do piloto", nome.toString(), TipoDeTexto.NORMAL, this::definirNome);
                return true;
            case "anterior":
                mudarDificuldade(-1);
                return true;
            case "proxima":
                mudarDificuldade(1);
                return true;
            case "modo_anterior":
                mudarModo(-1);
                return true;
            case "modo_proximo":
                mudarModo(1);
                return true;
            case "decolar":
                iniciarPartida();
                return true;
            case "voltar":
                jogo.setScreen(new MenuScreen(jogo));
                return true;
            default:
                return false;
        }
    }

    /** Substitui o nome pelo texto da caixa nativa, descartando o que o nome nao aceita. */
    private void definirNome(String texto) {
        nome.setLength(0);
        for (int i = 0; i < texto.length() && nome.length() < TAMANHO_MAXIMO_NOME; i++) {
            if (caractereValido(texto.charAt(i))) {
                nome.append(texto.charAt(i));
            }
        }
    }

    private void mudarDificuldade(int passo) {
        indice = (indice + passo + dificuldades.length) % dificuldades.length;
        jogo.sons().tocar(Som.SELECIONAR);
    }

    private void mudarModo(int passo) {
        indiceDoModo = (indiceDoModo + passo + modos.length) % modos.length;
        jogo.sons().tocar(Som.SELECIONAR);
    }

    private boolean tratarTecla(int keycode) {
        if (ehVoltar(keycode)) {
            jogo.setScreen(new MenuScreen(jogo));
            return true;
        }
        switch (keycode) {
            case Keys.BACKSPACE:
                if (nome.length() > 0) {
                    nome.deleteCharAt(nome.length() - 1);
                }
                return true;
            case Keys.LEFT:
                mudarDificuldade(-1);
                return true;
            case Keys.RIGHT:
                mudarDificuldade(1);
                return true;
            case Keys.UP:
                mudarModo(-1);
                return true;
            case Keys.DOWN:
            case Keys.TAB:
                mudarModo(1);
                return true;
            case Keys.ENTER:
            case Keys.NUMPAD_ENTER:
                iniciarPartida();
                return true;
            default:
                return false;
        }
    }

    private void iniciarPartida() {
        String piloto = logado() ? jogo.sessao().getNome() : nome.toString().trim();
        if (piloto.isEmpty()) {
            piloto = "Piloto";
        }
        Dificuldade dificuldade = dificuldades[indice];
        ModoDeJogo modo = modos[indiceDoModo];
        MissaoFactory fabrica = jogo.missaoFactory();
        Missao missao = fabrica.criarMissao(dificuldade, MissaoMarteGame.MIN, MissaoMarteGame.MAX, jogo.random(), modo);
        GameSession session = new GameSession(
                piloto, dificuldade, missao, fabrica.pontuacaoInicial(dificuldade),
                MissaoMarteGame.MIN, MissaoMarteGame.MAX, jogo.random(), modo);
        jogo.sons().tocar(Som.SELECIONAR);
        jogo.setScreen(new GameScreen(jogo, session));
    }

    @Override
    protected void desenhar(float delta) {
        Dificuldade dificuldade = dificuldades[indice];
        ModoDeJogo modo = modos[indiceDoModo];
        ConfiguracaoDificuldade cfg = RegrasDificuldade.obter(dificuldade);
        String cursor = ((int) (tempo * 2f)) % 2 == 0 ? "_" : " ";

        jogo.batch().begin();

        escala(2f);
        cor(DESTAQUE);
        textoCentralizado("NOVA PARTIDA", MissaoMarteGame.LARGURA / 2f, 500f);

        escala(1.5f);
        cor(Color.WHITE);
        texto(logado() ? "Piloto: " + jogo.sessao().getNome() : "Nome do piloto: " + nome + cursor, 200f, 410f);
        texto("Dificuldade: < " + dificuldade + " >", 200f, 360f);
        texto("Modo: < " + (modo == ModoDeJogo.DINAMICO ? "Dinâmico" : "Clássico") + " >", 200f, 310f);

        escala(1f);
        cor(Color.LIGHT_GRAY);
        int perseguidores = modo == ModoDeJogo.DINAMICO ? RegrasDeTempoReal.obter(dificuldade).getQuantidadeDePerseguidores() : 0;
        texto("Passageiros: " + cfg.getQuantidadePassageiros()
                + "   Asteroides: " + cfg.getQuantidadeAsteroides()
                + "   Inimigos: " + (cfg.getQuantidadeInimigos() + perseguidores)
                + "   Pontos: " + cfg.getPontuacaoInicial(), 200f, 258f);
        cor(DESTAQUE);
        texto(modo == ModoDeJogo.DINAMICO
                ? "Tempo real: segure a direção. Tem tiro."
                : "Por turnos: o jogo espera a sua jogada.", 200f, 234f);

        cor(Color.LIGHT_GRAY);
        textoCentralizado(jogo.modoToque()
                ? (logado() ? "< >: dificuldade e modo   |   Decolar: iniciar"
                        : "Toque no nome para digitar   |   < >: dificuldade e modo   |   Decolar: iniciar")
                : "Setas: esquerda/direita dificuldade, cima/baixo ou Tab modo   |   Enter: decolar   |   Esc: voltar",
                MissaoMarteGame.LARGURA / 2f, 30f);

        jogo.batch().end();
    }
}
