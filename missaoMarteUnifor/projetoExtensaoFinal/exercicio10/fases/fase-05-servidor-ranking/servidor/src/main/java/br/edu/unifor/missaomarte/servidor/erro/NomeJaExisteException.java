package br.edu.unifor.missaomarte.servidor.erro;

public class NomeJaExisteException extends RuntimeException {
    public NomeJaExisteException() {
        super("Nome de usuário já cadastrado.");
    }
}
