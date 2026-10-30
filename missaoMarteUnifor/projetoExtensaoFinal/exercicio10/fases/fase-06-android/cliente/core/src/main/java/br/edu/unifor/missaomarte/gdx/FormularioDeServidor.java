package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.servidor.ConfiguracaoDoServidor;
import br.edu.unifor.missaomarte.application.servidor.ConfiguracaoInvalidaException;

/**
 * Estado do formulario de configuracao do servidor (sem libGDX, para poder ser testado).
 * <p>Aceita so caracteres que podem existir num endereco (letras, numeros, ponto e hifen) e numeros na porta;
 * a validacao completa e feita por {@link ConfiguracaoDoServidor#criar}.
 */
public class FormularioDeServidor {
    public enum Campo { ENDERECO, PORTA }

    public static final int ENDERECO_MAX = 253;
    public static final int PORTA_MAX = 5;

    private final StringBuilder endereco = new StringBuilder();
    private final StringBuilder porta = new StringBuilder();
    private boolean https;
    private Campo campo = Campo.ENDERECO;

    public FormularioDeServidor(ConfiguracaoDoServidor inicial) {
        endereco.append(inicial.getHost());
        porta.append(inicial.getPorta());
        https = inicial.isHttps();
    }

    /** Devolve true se o caractere foi aceito no campo atual. */
    public boolean digitar(char c) {
        if (campo == Campo.ENDERECO) {
            if (endereco.length() < ENDERECO_MAX && enderecoValido(c)) {
                endereco.append(c);
                return true;
            }
            return false;
        }
        if (porta.length() < PORTA_MAX && c >= '0' && c <= '9') {
            porta.append(c);
            return true;
        }
        return false;
    }

    private static boolean enderecoValido(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.' || c == '-';
    }

    public void apagar() {
        StringBuilder alvo = campo == Campo.ENDERECO ? endereco : porta;
        if (alvo.length() > 0) {
            alvo.deleteCharAt(alvo.length() - 1);
        }
    }

    public void alternarCampo() {
        campo = campo == Campo.ENDERECO ? Campo.PORTA : Campo.ENDERECO;
    }

    public void selecionarCampo(Campo novo) {
        campo = novo;
    }

    public void alternarHttps() {
        https = !https;
    }

    /** Substitui o conteudo de um campo; cada caractere passa pelas mesmas regras de {@link #digitar}. */
    public void definirTexto(Campo alvo, String texto) {
        Campo anterior = campo;
        campo = alvo;
        (alvo == Campo.ENDERECO ? endereco : porta).setLength(0);
        if (texto != null) {
            for (int i = 0; i < texto.length(); i++) {
                digitar(texto.charAt(i));
            }
        }
        campo = anterior;
    }

    /** Valida os campos e devolve a configuracao; lanca {@link ConfiguracaoInvalidaException} com a mensagem do problema. */
    public ConfiguracaoDoServidor montar() {
        return ConfiguracaoDoServidor.criar(endereco.toString(), porta.toString(), https);
    }

    public String getEndereco() {
        return endereco.toString();
    }

    public String getPorta() {
        return porta.toString();
    }

    public boolean isHttps() {
        return https;
    }

    public Campo getCampo() {
        return campo;
    }
}
