package br.edu.unifor.missaomarte.application.servidor;

/** Jogador autenticado: nome e token recebido no login. Fica so na memoria; o token nao e gravado em disco. */
public final class Sessao {
    private final String nome;
    private final String token;

    public Sessao(String nome, String token) {
        this.nome = nome;
        this.token = token;
    }

    public String getNome() {
        return nome;
    }

    public String getToken() {
        return token;
    }

    /** O token e um segredo: fica fora de logs e de mensagens. */
    @Override
    public String toString() {
        return "Sessao[nome=" + nome + "]";
    }
}
