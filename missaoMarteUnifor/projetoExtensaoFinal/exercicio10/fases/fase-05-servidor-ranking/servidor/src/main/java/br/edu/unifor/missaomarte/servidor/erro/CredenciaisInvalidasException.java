package br.edu.unifor.missaomarte.servidor.erro;

/** Mesma mensagem para usuario inexistente e senha errada, para nao revelar quais nomes existem. */
public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() {
        super("Usuário ou senha inválidos.");
    }
}
