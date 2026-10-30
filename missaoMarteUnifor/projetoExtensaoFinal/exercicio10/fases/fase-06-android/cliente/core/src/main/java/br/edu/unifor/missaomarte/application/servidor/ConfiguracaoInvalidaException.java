package br.edu.unifor.missaomarte.application.servidor;

/** Endereco ou porta do servidor fora das regras. A mensagem e escrita para o jogador. */
public class ConfiguracaoInvalidaException extends IllegalArgumentException {
    public ConfiguracaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
