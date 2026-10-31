package br.edu.unifor.missaomarte.servidor.seguranca;

import java.nio.charset.StandardCharsets;

/** O BCrypt so usa os 72 primeiros bytes da senha; acima disso a senha seria cortada em silencio. */
public final class Senhas {
    public static final int MAXIMO_BYTES = 72;

    private Senhas() {
    }

    public static boolean cabeNoBcrypt(String senha) {
        return senha.getBytes(StandardCharsets.UTF_8).length <= MAXIMO_BYTES;
    }
}
