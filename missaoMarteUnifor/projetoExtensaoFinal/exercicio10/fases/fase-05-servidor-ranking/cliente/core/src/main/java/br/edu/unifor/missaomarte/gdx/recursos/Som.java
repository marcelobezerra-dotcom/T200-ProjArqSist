package br.edu.unifor.missaomarte.gdx.recursos;

/** Catalogo dos sons do jogo; a musica de fundo e carregada como streaming (Music), os demais como Sound. */
public enum Som {
    MOVER("mover.wav", 0.45f, false),
    SELECIONAR("selecionar.wav", 0.7f, false),
    EMBARCAR("embarcar.wav", 0.8f, false),
    ENTREGAR("entregar.wav", 0.8f, false),
    COLISAO("colisao.wav", 0.9f, false),
    VITORIA("vitoria.wav", 0.9f, false),
    DERROTA("derrota.wav", 0.9f, false),
    MUSICA("musica.wav", 0.3f, true);

    private static final String PASTA = "sons/";

    private final String arquivo;
    private final float volume;
    private final boolean musica;

    Som(String arquivo, float volume, boolean musica) {
        this.arquivo = arquivo;
        this.volume = volume;
        this.musica = musica;
    }

    /** Caminho relativo a pasta de assets (core/assets). */
    public String caminho() {
        return PASTA + arquivo;
    }

    public float volume() {
        return volume;
    }

    public boolean musica() {
        return musica;
    }
}
