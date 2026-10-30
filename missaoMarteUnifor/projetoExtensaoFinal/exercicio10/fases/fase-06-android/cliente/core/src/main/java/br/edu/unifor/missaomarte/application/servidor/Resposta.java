package br.edu.unifor.missaomarte.application.servidor;

/**
 * Resultado de uma chamada ao servidor: um valor ou uma mensagem de erro (objeto de dados imutavel).
 * <p>{@code naoAutorizado} indica que o servidor recusou o token (sessao expirada), para a tela mandar o jogador entrar de novo.
 */
public final class Resposta<T> {
    private final T valor;
    private final String mensagem;
    private final boolean naoAutorizado;

    private Resposta(T valor, String mensagem, boolean naoAutorizado) {
        this.valor = valor;
        this.mensagem = mensagem;
        this.naoAutorizado = naoAutorizado;
    }

    public static <T> Resposta<T> sucesso(T valor) {
        return new Resposta<>(valor, null, false);
    }

    public static <T> Resposta<T> erro(String mensagem) {
        return new Resposta<>(null, mensagem, false);
    }

    public static <T> Resposta<T> erroNaoAutorizado(String mensagem) {
        return new Resposta<>(null, mensagem, true);
    }

    public boolean isSucesso() {
        return mensagem == null;
    }

    public T getValor() {
        return valor;
    }

    public String getMensagem() {
        return mensagem;
    }

    public boolean isNaoAutorizado() {
        return naoAutorizado;
    }
}
