package br.edu.unifor.missaomarte.gdx.entrada;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Botoes virtuais da partida: setas, acao principal (atirar ou embarcar), pausa, som e abortar, dentro da area de controles.
 * <p>O layout se adapta a area recebida: em paisagem os botoes ficam empilhados numa coluna; em retrato as setas ficam a esquerda e
 * as acoes a direita, perto do polegar. O tamanho dos botoes respeita o alvo minimo de toque informado (em unidades do mundo).
 * <p>Aqui so se calcula o layout e se traduz o botao em {@link Acao}; quem desenha e a tela.
 */
public final class ControleDeToque {
    public static final String CIMA = "cima";
    public static final String BAIXO = "baixo";
    public static final String ESQUERDA = "esquerda";
    public static final String DIREITA = "direita";
    public static final String EMBARCAR = "embarcar";
    public static final String ATIRAR = "atirar";
    public static final String PAUSA = "pausa";
    public static final String SOM = "som";
    public static final String ABORTAR = "abortar";

    private static final float LADO_PADRAO = 76f;
    private static final float LADO_MAXIMO = 92f;
    private static final float FOLGA = 4f;
    private static final float MARGEM = 12f;
    private static final float ESPACO_ENTRE_BOTOES = 8f;

    private final List<BotaoVirtual> botoes;

    /**
     * @param dinamico true no modo dinamico (tem tiro e pausa); false no classico (tem embarcar, como na Fase 6)
     * @param alvoMinimo menor lado aceitavel de um botao, em unidades do mundo
     */
    public ControleDeToque(RetanguloDeTela area, boolean emRetrato, boolean dinamico, float alvoMinimo) {
        List<BotaoVirtual> lista = new ArrayList<>();
        if (emRetrato) {
            montarEmRetrato(lista, area, dinamico, alvoMinimo);
        } else {
            montarEmPaisagem(lista, area, dinamico, alvoMinimo);
        }
        botoes = Collections.unmodifiableList(lista);
    }

    private static float ladoDasSetas(float alvoMinimo, float limite) {
        return Math.max(40f, Math.min(Math.max(LADO_PADRAO, Math.min(alvoMinimo, LADO_MAXIMO)), limite));
    }

    private static void montarEmPaisagem(List<BotaoVirtual> lista, RetanguloDeTela area, boolean dinamico, float alvoMinimo) {
        float lado = ladoDasSetas(alvoMinimo, (area.largura() - 24f - 2 * FOLGA) / 3f);
        float cx = area.centroX();
        float esquerda = cx - lado / 2f;
        float topo = area.topo() - 16f;
        float yCima = topo - lado;
        float yMeio = yCima - lado - FOLGA;
        float yBaixo = yMeio - lado - FOLGA;
        lista.add(new BotaoVirtual(CIMA, "^", esquerda, yCima, lado, lado));
        lista.add(new BotaoVirtual(ESQUERDA, "<", esquerda - lado - FOLGA, yMeio, lado, lado));
        lista.add(new BotaoVirtual(DIREITA, ">", esquerda + lado + FOLGA, yMeio, lado, lado));
        lista.add(new BotaoVirtual(BAIXO, "v", esquerda, yBaixo, lado, lado));

        float larguraDaAcao = area.largura() - 2 * MARGEM;
        float alturaDaAcao = Math.max(64f, alvoMinimo);
        float yAcao = yBaixo - 16f - alturaDaAcao;
        lista.add(dinamico
                ? new BotaoVirtual(ATIRAR, "Atirar", area.x() + MARGEM, yAcao, larguraDaAcao, alturaDaAcao)
                : new BotaoVirtual(EMBARCAR, "Embarcar", area.x() + MARGEM, yAcao, larguraDaAcao, alturaDaAcao));

        float alturaPequena = Math.max(52f, alvoMinimo);
        float yPequenos = yAcao - 12f - alturaPequena;
        adicionarPequenos(lista, area.x() + MARGEM, yPequenos, larguraDaAcao, alturaPequena, dinamico);
    }

    private static void montarEmRetrato(List<BotaoVirtual> lista, RetanguloDeTela area, boolean dinamico, float alvoMinimo) {
        float lado = ladoDasSetas(alvoMinimo, (area.altura() - 32f - 2 * FOLGA) / 3f);
        float xSetas = area.x() + MARGEM;
        float yBaixo = area.y() + 16f;
        float yMeio = yBaixo + lado + FOLGA;
        float yCima = yMeio + lado + FOLGA;
        lista.add(new BotaoVirtual(CIMA, "^", xSetas + lado + FOLGA, yCima, lado, lado));
        lista.add(new BotaoVirtual(ESQUERDA, "<", xSetas, yMeio, lado, lado));
        lista.add(new BotaoVirtual(DIREITA, ">", xSetas + 2 * (lado + FOLGA), yMeio, lado, lado));
        lista.add(new BotaoVirtual(BAIXO, "v", xSetas + lado + FOLGA, yBaixo, lado, lado));

        float fimDasSetas = xSetas + 3 * lado + 2 * FOLGA;
        float larguraDaColuna = Math.max(120f, Math.min(300f, area.direita() - MARGEM - fimDasSetas - 24f));
        float xColuna = area.direita() - MARGEM - larguraDaColuna;
        lista.add(dinamico
                ? new BotaoVirtual(ATIRAR, "Atirar", xColuna, yCima, larguraDaColuna, lado)
                : new BotaoVirtual(EMBARCAR, "Embarcar", xColuna, yCima, larguraDaColuna, lado));
        adicionarPequenos(lista, xColuna, yMeio, larguraDaColuna, lado, dinamico);
    }

    private static void adicionarPequenos(List<BotaoVirtual> lista, float x, float y, float largura, float altura, boolean dinamico) {
        int quantidade = dinamico ? 3 : 2;
        float larguraDoBotao = (largura - (quantidade - 1) * ESPACO_ENTRE_BOTOES) / quantidade;
        float posicao = x;
        if (dinamico) {
            lista.add(new BotaoVirtual(PAUSA, "Pausa", posicao, y, larguraDoBotao, altura));
            posicao += larguraDoBotao + ESPACO_ENTRE_BOTOES;
        }
        lista.add(new BotaoVirtual(SOM, "Som", posicao, y, larguraDoBotao, altura));
        posicao += larguraDoBotao + ESPACO_ENTRE_BOTOES;
        lista.add(new BotaoVirtual(ABORTAR, "Abortar", posicao, y, larguraDoBotao, altura));
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

    /** Acao do jogo para o botao; {@link Acao#INVALIDA} para botoes que nao sao acao (som e pausa). */
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
            case ATIRAR:
                return Acao.ATIRAR;
            case ABORTAR:
                return Acao.SAIR;
            default:
                return Acao.INVALIDA;
        }
    }

    /** True para as setas: no modo dinamico a nave anda enquanto o botao esta pressionado. */
    public static boolean ehSeta(String id) {
        return CIMA.equals(id) || BAIXO.equals(id) || ESQUERDA.equals(id) || DIREITA.equals(id);
    }

    /** Setas repetem enquanto o dedo fica apoiado; os demais botoes agem uma vez por toque. */
    public static boolean repete(String id) {
        return ehSeta(id);
    }
}
