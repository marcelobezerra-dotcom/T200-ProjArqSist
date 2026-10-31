package br.edu.unifor.missaomarte.application.servidor;

import java.util.Optional;

/**
 * Le e grava o endereco do servidor escolhido pelo jogador. Guarda so endereco, porta e esquema: nunca senha nem token.
 * <p>Valor guardado que nao passa na validacao (arquivo editado a mao, por exemplo) e ignorado.
 */
public class RepositorioDeConfiguracaoDoServidor {
    static final String CHAVE_HOST = "servidor.host";
    static final String CHAVE_PORTA = "servidor.porta";
    static final String CHAVE_HTTPS = "servidor.https";

    private final ArmazemDeTexto armazem;

    public RepositorioDeConfiguracaoDoServidor(ArmazemDeTexto armazem) {
        this.armazem = armazem;
    }

    public Optional<ConfiguracaoDoServidor> carregar() {
        String host = armazem.ler(CHAVE_HOST);
        String porta = armazem.ler(CHAVE_PORTA);
        if (host == null || porta == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(ConfiguracaoDoServidor.criar(host, porta, "true".equals(armazem.ler(CHAVE_HTTPS))));
        } catch (ConfiguracaoInvalidaException e) {
            return Optional.empty();
        }
    }

    public void salvar(ConfiguracaoDoServidor configuracao) {
        armazem.gravar(CHAVE_HOST, configuracao.getHost());
        armazem.gravar(CHAVE_PORTA, String.valueOf(configuracao.getPorta()));
        armazem.gravar(CHAVE_HTTPS, String.valueOf(configuracao.isHttps()));
    }

    public void limpar() {
        armazem.apagar(CHAVE_HOST);
        armazem.apagar(CHAVE_PORTA);
        armazem.apagar(CHAVE_HTTPS);
    }
}
