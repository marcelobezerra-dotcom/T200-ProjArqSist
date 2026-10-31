package br.edu.unifor.missaomarte.gdx.recursos;

/**
 * Catalogo das imagens do jogo: arquivo e tamanho de cada quadro (a imagem e uma faixa horizontal de quadros iguais).
 * <p>Sem dependencia do libGDX: serve de contrato entre o gerador de assets, o carregador e os testes.
 */
public enum Imagem {
    FUNDO("fundo.png", 800, 600, 1),
    TERRENO("terreno.png", 48, 48, 1),
    NAVE("nave.png", 48, 48, 1),
    PROPULSOR("propulsor.png", 48, 48, 4),
    BASE("base.png", 48, 48, 4),
    ASTEROIDE("asteroide.png", 48, 48, 1),
    INIMIGO("inimigo.png", 48, 48, 2),
    PERSEGUIDOR("perseguidor.png", 48, 48, 2),
    PASSAGEIRO_PROFESSOR("passageiro_professor.png", 48, 48, 1),
    PASSAGEIRO_ENGENHEIRO("passageiro_engenheiro.png", 48, 48, 1),
    PASSAGEIRO_ASTRONAUTA("passageiro_astronauta.png", 48, 48, 1),
    DESCONHECIDO("desconhecido.png", 48, 48, 1),
    EXPLOSAO("explosao.png", 48, 48, 6),
    BRILHO("brilho.png", 48, 48, 5),
    ICONE_VIDA("icone_vida.png", 24, 24, 1),
    ICONE_VIDA_VAZIA("icone_vida_vazia.png", 24, 24, 1),
    ICONE_PASSAGEIRO("icone_passageiro.png", 24, 24, 1),
    ICONE_VAGA("icone_vaga.png", 24, 24, 1);

    private static final String PASTA = "imagens/";

    private final String arquivo;
    private final int larguraQuadro;
    private final int alturaQuadro;
    private final int quadros;

    Imagem(String arquivo, int larguraQuadro, int alturaQuadro, int quadros) {
        this.arquivo = arquivo;
        this.larguraQuadro = larguraQuadro;
        this.alturaQuadro = alturaQuadro;
        this.quadros = quadros;
    }

    /** Caminho relativo a pasta de assets (core/assets). */
    public String caminho() {
        return PASTA + arquivo;
    }

    public int larguraQuadro() {
        return larguraQuadro;
    }

    public int alturaQuadro() {
        return alturaQuadro;
    }

    public int quadros() {
        return quadros;
    }
}
