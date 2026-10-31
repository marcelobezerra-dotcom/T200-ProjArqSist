package br.edu.unifor.missaomarte.application.servidor;

/** Guarda pares chave/valor de texto (porta de persistencia pequena). No jogo, e implementada com as Preferences do libGDX. */
public interface ArmazemDeTexto {
    /** Devolve o valor ou null se a chave nao existe. */
    String ler(String chave);

    void gravar(String chave, String valor);

    void apagar(String chave);
}
