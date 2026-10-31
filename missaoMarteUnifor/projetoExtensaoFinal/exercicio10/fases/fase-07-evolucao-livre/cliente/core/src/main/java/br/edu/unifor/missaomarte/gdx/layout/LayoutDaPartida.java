package br.edu.unifor.missaomarte.gdx.layout;

/**
 * Divide a tela da partida em areas (barras, mapa, painel de informacoes e controles) a partir do tamanho do mundo.
 * <p>Paisagem: {@code [mapa][painel][controles]} na horizontal. Retrato: barra, mapa, faixa de informacoes e controles, de cima
 * para baixo. O mapa e quadrado e ocupa o maior espaco que sobra; a celula acompanha o tamanho do mapa.
 * <p>Sem toque (desktop em 800x600) o resultado e o mesmo layout fixo das fases anteriores: origem (24, 36), celula 48 e painel em x=570.
 * <p>Objeto de valor sem libGDX, para ser testado em varias proporcoes de tela.
 */
public final class LayoutDaPartida {
    public enum Orientacao { PAISAGEM, RETRATO }

    public static final float BARRA = 36f;
    public static final float PAINEL_LARGURA_DO_TEXTO = 200f;
    public static final float PAINEL_LARGURA = 230f;
    public static final float CONTROLES_LARGURA = 260f;
    public static final float FAIXA_DE_INFORMACOES = 96f;
    public static final float CONTROLES_ALTURA_MINIMA = 270f;
    public static final float CELULA_MINIMA = 14f;

    private static final float MARGEM = 24f;
    private static final float MARGEM_DIREITA = 20f;
    private static final float ESPACO = 28f;
    private static final float MARGEM_RETRATO = 12f;
    private static final float FOLGA = 4f;

    private final Orientacao orientacao;
    private final float larguraDoMundo;
    private final float alturaDoMundo;
    private final int celulas;
    private final RetanguloDeTela barraSuperior;
    private final RetanguloDeTela barraInferior;
    private final RetanguloDeTela mapa;
    private final RetanguloDeTela painel;
    private final RetanguloDeTela controles;

    private LayoutDaPartida(Orientacao orientacao, float larguraDoMundo, float alturaDoMundo, int celulas,
            RetanguloDeTela barraSuperior, RetanguloDeTela barraInferior, RetanguloDeTela mapa, RetanguloDeTela painel,
            RetanguloDeTela controles) {
        this.orientacao = orientacao;
        this.larguraDoMundo = larguraDoMundo;
        this.alturaDoMundo = alturaDoMundo;
        this.celulas = celulas;
        this.barraSuperior = barraSuperior;
        this.barraInferior = barraInferior;
        this.mapa = mapa;
        this.painel = painel;
        this.controles = controles;
    }

    /**
     * @param celulas quantidade de celulas por lado do mapa (11)
     * @param toque se os controles virtuais ocupam espaco
     */
    public static LayoutDaPartida calcular(float larguraDoMundo, float alturaDoMundo, int celulas, boolean toque,
            MargensSeguras margens) {
        RetanguloDeTela barraSuperior = new RetanguloDeTela(0f, alturaDoMundo - BARRA - margens.cima(), larguraDoMundo,
                BARRA + margens.cima());
        RetanguloDeTela barraInferior = new RetanguloDeTela(0f, 0f, larguraDoMundo, BARRA + margens.baixo());
        float yMin = barraInferior.topo();
        float yMax = barraSuperior.y();
        float alturaUtil = yMax - yMin;
        float ladoMinimo = CELULA_MINIMA * celulas;

        if (larguraDoMundo >= alturaDoMundo) {
            float margemEsquerda = MARGEM + margens.esquerda();
            float margemDireita = MARGEM_DIREITA + margens.direita();
            float larguraDosControles = toque ? CONTROLES_LARGURA : 0f;
            float maximoPelaLargura = larguraDoMundo - margemEsquerda - margemDireita - larguraDosControles
                    - PAINEL_LARGURA_DO_TEXTO - ESPACO;
            float lado = Math.max(ladoMinimo, Math.min(alturaUtil, maximoPelaLargura));
            float origemY = yMin + (alturaUtil - lado) / 2f;
            RetanguloDeTela mapa = new RetanguloDeTela(margemEsquerda, origemY, lado, lado);
            RetanguloDeTela painel = new RetanguloDeTela(mapa.direita() + ESPACO - 10f, yMin, PAINEL_LARGURA, alturaUtil);
            RetanguloDeTela controles = toque
                    ? new RetanguloDeTela(larguraDoMundo - margens.direita() - larguraDosControles, yMin, larguraDosControles, alturaUtil)
                    : new RetanguloDeTela(larguraDoMundo, yMin, 0f, alturaUtil);
            return new LayoutDaPartida(Orientacao.PAISAGEM, larguraDoMundo, alturaDoMundo, celulas, barraSuperior,
                    barraInferior, mapa, painel, controles);
        }

        float esquerda = MARGEM_RETRATO + margens.esquerda();
        float direita = MARGEM_RETRATO + margens.direita();
        float larguraUtil = larguraDoMundo - esquerda - direita;
        float alturaDosControles = toque ? CONTROLES_ALTURA_MINIMA : 0f;
        float maximoPelaAltura = alturaUtil - FAIXA_DE_INFORMACOES - alturaDosControles - 3 * FOLGA;
        float lado = Math.max(ladoMinimo, Math.min(larguraUtil, maximoPelaAltura));
        float origemY = yMax - FOLGA - lado;
        RetanguloDeTela mapa = new RetanguloDeTela(esquerda + (larguraUtil - lado) / 2f, origemY, lado, lado);
        RetanguloDeTela painel = new RetanguloDeTela(esquerda, origemY - FOLGA - FAIXA_DE_INFORMACOES, larguraUtil,
                FAIXA_DE_INFORMACOES);
        RetanguloDeTela controles = toque
                ? new RetanguloDeTela(margens.esquerda(), yMin, larguraDoMundo - margens.esquerda() - margens.direita(),
                        painel.y() - FOLGA - yMin)
                : new RetanguloDeTela(0f, yMin, larguraDoMundo, 0f);
        return new LayoutDaPartida(Orientacao.RETRATO, larguraDoMundo, alturaDoMundo, celulas, barraSuperior, barraInferior,
                mapa, painel, controles);
    }

    public Orientacao orientacao() {
        return orientacao;
    }

    public boolean emRetrato() {
        return orientacao == Orientacao.RETRATO;
    }

    public float larguraDoMundo() {
        return larguraDoMundo;
    }

    public float alturaDoMundo() {
        return alturaDoMundo;
    }

    public RetanguloDeTela barraSuperior() {
        return barraSuperior;
    }

    public RetanguloDeTela barraInferior() {
        return barraInferior;
    }

    public RetanguloDeTela mapa() {
        return mapa;
    }

    /** Painel lateral (paisagem) ou faixa de informacoes (retrato). */
    public RetanguloDeTela painel() {
        return painel;
    }

    /** Area dos controles virtuais; tem largura ou altura zero quando nao ha toque. */
    public RetanguloDeTela controles() {
        return controles;
    }

    public float celula() {
        return mapa.largura() / celulas;
    }

    public float origemX() {
        return mapa.x();
    }

    public float origemY() {
        return mapa.y();
    }
}
