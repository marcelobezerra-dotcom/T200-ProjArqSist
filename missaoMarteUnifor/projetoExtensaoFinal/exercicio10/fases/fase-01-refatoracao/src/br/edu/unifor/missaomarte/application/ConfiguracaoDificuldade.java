package br.edu.unifor.missaomarte.application;

public class ConfiguracaoDificuldade {
    private final int pontuacaoInicial;
    private final int quantidadePassageiros;
    private final int quantidadeAsteroides;
    private final int quantidadeInimigos;

    public ConfiguracaoDificuldade(int pontuacaoInicial, int quantidadePassageiros, int quantidadeAsteroides, int quantidadeInimigos) {
        this.pontuacaoInicial = pontuacaoInicial;
        this.quantidadePassageiros = quantidadePassageiros;
        this.quantidadeAsteroides = quantidadeAsteroides;
        this.quantidadeInimigos = quantidadeInimigos;
    }

    public int getPontuacaoInicial() {
        return pontuacaoInicial;
    }

    public int getQuantidadePassageiros() {
        return quantidadePassageiros;
    }

    public int getQuantidadeAsteroides() {
        return quantidadeAsteroides;
    }

    public int getQuantidadeInimigos() {
        return quantidadeInimigos;
    }
}
