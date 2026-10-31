package br.edu.unifor.missaomarte.application;

/** Tempos (em segundos) e quantidades do modo dinamico para uma dificuldade. Objeto de valor imutavel. */
public final class ConfiguracaoDeTempoReal {
    private final float intervaloDaNave;
    private final float intervaloDosInimigos;
    private final float intervaloDosPerseguidores;
    private final float intervaloDaPontuacao;
    private final float recargaDoTiro;
    private final float contagemInicial;
    private final float deltaMaximo;
    private final int quantidadeDePerseguidores;
    private final int municaoInicial;
    private final int custoDoTiro;

    public ConfiguracaoDeTempoReal(float intervaloDaNave, float intervaloDosInimigos, float intervaloDosPerseguidores,
            float intervaloDaPontuacao, float recargaDoTiro, float contagemInicial, float deltaMaximo,
            int quantidadeDePerseguidores, int municaoInicial, int custoDoTiro) {
        this.intervaloDaNave = intervaloDaNave;
        this.intervaloDosInimigos = intervaloDosInimigos;
        this.intervaloDosPerseguidores = intervaloDosPerseguidores;
        this.intervaloDaPontuacao = intervaloDaPontuacao;
        this.recargaDoTiro = recargaDoTiro;
        this.contagemInicial = contagemInicial;
        this.deltaMaximo = deltaMaximo;
        this.quantidadeDePerseguidores = quantidadeDePerseguidores;
        this.municaoInicial = municaoInicial;
        this.custoDoTiro = custoDoTiro;
    }

    /** Tempo minimo entre dois passos da nave enquanto a direcao esta pressionada. */
    public float getIntervaloDaNave() {
        return intervaloDaNave;
    }

    public float getIntervaloDosInimigos() {
        return intervaloDosInimigos;
    }

    public float getIntervaloDosPerseguidores() {
        return intervaloDosPerseguidores;
    }

    /** A cada intervalo a pontuacao perde 1 ponto. */
    public float getIntervaloDaPontuacao() {
        return intervaloDaPontuacao;
    }

    public float getRecargaDoTiro() {
        return recargaDoTiro;
    }

    public float getContagemInicial() {
        return contagemInicial;
    }

    /** Maior avanco de tempo aceito por quadro: um quadro lento nao vira um salto no jogo. */
    public float getDeltaMaximo() {
        return deltaMaximo;
    }

    public int getQuantidadeDePerseguidores() {
        return quantidadeDePerseguidores;
    }

    public int getMunicaoInicial() {
        return municaoInicial;
    }

    public int getCustoDoTiro() {
        return custoDoTiro;
    }
}
