package graspexercicio10.model;

/**
 * Information Expert: conhece posição e pontuação.
 * Polymorphism: subclasses definem pontuação e símbolo do elemento.
 */
public abstract class Passageiro extends ElementoMapa {
    private final String nome;
    private final String tipo;

    protected Passageiro(String nome, String tipo, int x, int y) {
        super(x, y);
        this.nome = nome;
        this.tipo = tipo;
    }

    @Override public String getNome() { return nome; }
    @Override public String getDescricao() { return tipo; }
    @Override public boolean isResgatavel() { return true; }
    public String getTipo()         { return tipo; }
    public abstract int getPontuacao();
}
