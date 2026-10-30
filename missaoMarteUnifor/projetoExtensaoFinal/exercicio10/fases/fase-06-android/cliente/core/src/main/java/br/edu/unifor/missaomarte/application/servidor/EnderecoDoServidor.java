package br.edu.unifor.missaomarte.application.servidor;

/**
 * Endereco do servidor em uso. A precedencia e: valor salvo pelo jogador, depois o padrao informado pelo lancador
 * (linha de comando no desktop, configuracao do build no Android).
 * <p>O valor salvo so e lido no primeiro uso, porque as Preferences do libGDX so existem depois que o jogo inicia.
 * A URL e lida a cada chamada de rede, entao uma troca vale na hora, sem recriar o adaptador HTTP.
 */
public final class EnderecoDoServidor {
    private final RepositorioDeConfiguracaoDoServidor repositorio;
    private final ConfiguracaoDoServidor padrao;
    private volatile ConfiguracaoDoServidor atual;

    public EnderecoDoServidor(RepositorioDeConfiguracaoDoServidor repositorio, ConfiguracaoDoServidor padrao) {
        this.repositorio = repositorio;
        this.padrao = padrao;
    }

    public ConfiguracaoDoServidor atual() {
        ConfiguracaoDoServidor valor = atual;
        if (valor == null) {
            synchronized (this) {
                if (atual == null) {
                    atual = repositorio.carregar().orElse(padrao);
                }
                valor = atual;
            }
        }
        return valor;
    }

    public String urlBase() {
        return atual().urlBase();
    }

    public ConfiguracaoDoServidor padrao() {
        return padrao;
    }

    /** Grava e passa a usar o novo endereco. Devolve true se ele e diferente do anterior. */
    public boolean salvar(ConfiguracaoDoServidor nova) {
        boolean mudou = !nova.equals(atual());
        repositorio.salvar(nova);
        atual = nova;
        return mudou;
    }

    /** Apaga o valor salvo e volta ao padrao. Devolve true se o endereco em uso mudou. */
    public boolean restaurarPadrao() {
        boolean mudou = !padrao.equals(atual());
        repositorio.limpar();
        atual = padrao;
        return mudou;
    }
}
