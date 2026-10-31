package br.edu.unifor.missaomarte.application.servidor;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Onde esta o servidor: endereco (IP ou nome), porta e se usa HTTPS (objeto de valor imutavel).
 * <p>Valida na criacao, entao nunca existe uma instancia invalida. A URL e montada a partir das partes ja validadas;
 * texto digitado como "http://x/a@b" ou com espacos e barras e recusado, nao interpretado.
 */
public final class ConfiguracaoDoServidor {
    public static final int PORTA_PADRAO = 8080;
    public static final ConfiguracaoDoServidor PADRAO = new ConfiguracaoDoServidor("localhost", PORTA_PADRAO, false);

    private static final int TAMANHO_MAXIMO_DO_ENDERECO = 253;
    private static final Pattern IPV4 = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3}");
    private static final Pattern SO_NUMEROS_E_PONTOS = Pattern.compile("[0-9.]+");
    private static final Pattern ROTULO = Pattern.compile("[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?");
    private static final Pattern URL = Pattern.compile("(https?)://([^/:@\\s]+)(?::(\\d{1,5}))?/?");

    private final String host;
    private final int porta;
    private final boolean https;

    private ConfiguracaoDoServidor(String host, int porta, boolean https) {
        this.host = host;
        this.porta = porta;
        this.https = https;
    }

    /** Cria a partir do que o jogador digitou; lanca {@link ConfiguracaoInvalidaException} com a mensagem do problema. */
    public static ConfiguracaoDoServidor criar(String host, String porta, boolean https) {
        String h = validarHost(host);
        return new ConfiguracaoDoServidor(h, validarPorta(porta), https);
    }

    public static ConfiguracaoDoServidor criar(String host, int porta, boolean https) {
        return criar(host, String.valueOf(porta), https);
    }

    /** Le "http://host:porta" ou "https://host" (usada para o valor padrao vindo da linha de comando ou do build). */
    public static ConfiguracaoDoServidor deUrl(String url) {
        Matcher m = URL.matcher(url == null ? "" : url.trim());
        if (!m.matches()) {
            throw new ConfiguracaoInvalidaException("URL do servidor inválida. Use http://endereço:porta ou https://endereço.");
        }
        boolean seguro = "https".equals(m.group(1));
        String porta = m.group(3) != null ? m.group(3) : (seguro ? "443" : "80");
        return criar(m.group(2), porta, seguro);
    }

    private static String validarHost(String texto) {
        String h = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        if (h.isEmpty()) {
            throw new ConfiguracaoInvalidaException("Informe o endereço do servidor.");
        }
        if (h.length() > TAMANHO_MAXIMO_DO_ENDERECO) {
            throw new ConfiguracaoInvalidaException("Endereço muito longo.");
        }
        if (SO_NUMEROS_E_PONTOS.matcher(h).matches()) {
            if (!IPV4.matcher(h).matches() || !octetosValidos(h)) {
                throw new ConfiguracaoInvalidaException("Endereço IP inválido. Exemplo: 192.168.0.10");
            }
            return h;
        }
        for (String rotulo : h.split("\\.", -1)) {
            if (!ROTULO.matcher(rotulo).matches()) {
                throw new ConfiguracaoInvalidaException("Endereço inválido: use só letras, números, hífen e ponto.");
            }
        }
        return h;
    }

    private static boolean octetosValidos(String ip) {
        for (String parte : ip.split("\\.")) {
            if (Integer.parseInt(parte) > 255) {
                return false;
            }
        }
        return true;
    }

    private static int validarPorta(String texto) {
        String p = texto == null ? "" : texto.trim();
        if (p.isEmpty()) {
            throw new ConfiguracaoInvalidaException("Informe a porta.");
        }
        if (!p.matches("\\d{1,5}")) {
            throw new ConfiguracaoInvalidaException("A porta deve ser um número entre 1 e 65535.");
        }
        int valor = Integer.parseInt(p);
        if (valor < 1 || valor > 65535) {
            throw new ConfiguracaoInvalidaException("A porta deve ser um número entre 1 e 65535.");
        }
        return valor;
    }

    public String getHost() {
        return host;
    }

    public int getPorta() {
        return porta;
    }

    public boolean isHttps() {
        return https;
    }

    /** Ex.: http://192.168.0.10:8080 ou https://meu.servidor.com (a porta padrao do esquema fica de fora). */
    public String urlBase() {
        boolean portaPadraoDoEsquema = https ? porta == 443 : porta == 80;
        return (https ? "https://" : "http://") + host + (portaPadraoDoEsquema ? "" : ":" + porta);
    }

    /** localhost, 127.x, 10.x, 172.16-31.x, 192.168.x, 169.254.x e nomes terminados em .local. */
    public boolean isLocalOuRedePrivada() {
        if ("localhost".equals(host) || host.endsWith(".local")) {
            return true;
        }
        if (!IPV4.matcher(host).matches()) {
            return false;
        }
        String[] partes = host.split("\\.");
        int a = Integer.parseInt(partes[0]);
        int b = Integer.parseInt(partes[1]);
        return a == 127 || a == 10 || (a == 172 && b >= 16 && b <= 31) || (a == 192 && b == 168) || (a == 169 && b == 254);
    }

    /** HTTP para fora da rede local: senha e token viajam sem criptografia. */
    public boolean exigeAviso() {
        return !https && !isLocalOuRedePrivada();
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof ConfiguracaoDoServidor)) {
            return false;
        }
        ConfiguracaoDoServidor c = (ConfiguracaoDoServidor) outro;
        return porta == c.porta && https == c.https && host.equals(c.host);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * host.hashCode() + porta) + (https ? 1 : 0);
    }

    @Override
    public String toString() {
        return urlBase();
    }
}
