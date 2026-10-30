package br.edu.unifor.missaomarte.gdx.entrada;

import br.edu.unifor.missaomarte.application.Acao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Botoes virtuais da partida: setas, embarcar, som e abortar, numa area a direita do mapa.
 * <p>Os botoes ficam sempre nos 260 pixels finais do mundo; com {@code ExtendViewport} essa area acompanha a largura
 * da tela. Aqui so se calcula o layout e se traduz o botao em {@link Acao}; quem desenha e a tela.
 */
public final class ControleDeToque {
    public static final float LARGURA_DA_AREA = 260f;

    public static final String CIMA = "cima";
    public static final String BAIXO = "baixo";
    public static final String ESQUERDA = "esquerda";
    public static final String DIREITA = "direita";
    public static final String EMBARCAR = "embarcar";
    public static final String SOM = "som";
    public static final String ABORTAR = "abortar";

    private static final float LADO = 76f;
    private static final float FOLGA = 4f;

    private final List<BotaoVirtual> botoes;

    public ControleDeToque(float larguraDoMundo) {
        float cx = larguraDoMundo - LARGURA_DA_AREA / 2f;
        float esquerda = cx - LADO / 2f;
        List<BotaoVirtual> lista = new ArrayList<>();
        lista.add(new BotaoVirtual(CIMA, "^", esquerda, 400f, LADO, LADO));
        lista.add(new BotaoVirtual(ESQUERDA, "<", esquerda - LADO - FOLGA, 320f, LADO, LADO));
        lista.add(new BotaoVirtual(DIREITA, ">", esquerda + LADO + FOLGA, 320f, LADO, LADO));
        lista.add(new BotaoVirtual(BAIXO, "v", esquerda, 240f, LADO, LADO));
        lista.add(new BotaoVirtual(EMBARCAR, "Embarcar", cx - 110f, 150f, 220f, 64f));
        lista.add(new BotaoVirtual(SOM, "Som", cx - 110f, 70f, 104f, 52f));
        lista.add(new BotaoVirtual(ABORTAR, "Abortar", cx + 6f, 70f, 104f, 52f));
        botoes = Collections.unmodifiableList(lista);
    }

    public List<BotaoVirtual> getBotoes() {
        return botoes;
    }

    /** Devolve o botao sob o ponto ou null. */
    public BotaoVirtual em(float x, float y) {
        for (BotaoVirtual botao : botoes) {
            if (botao.contem(x, y)) {
                return botao;
            }
        }
        return null;
    }

    /** Acao do jogo para o botao; {@link Acao#INVALIDA} para botoes que nao sao acao (como o som). */
    public static Acao acaoDe(String id) {
        switch (id) {
            case CIMA:
                return Acao.CIMA;
            case BAIXO:
                return Acao.BAIXO;
            case ESQUERDA:
                return Acao.ESQUERDA;
            case DIREITA:
                return Acao.DIREITA;
            case EMBARCAR:
                return Acao.EMBARCAR;
            case ABORTAR:
                return Acao.SAIR;
            default:
                return Acao.INVALIDA;
        }
    }

    /** Setas repetem enquanto o dedo fica apoiado; os demais botoes agem uma vez por toque. */
    public static boolean repete(String id) {
        return CIMA.equals(id) || BAIXO.equals(id) || ESQUERDA.equals(id) || DIREITA.equals(id);
    }
}
