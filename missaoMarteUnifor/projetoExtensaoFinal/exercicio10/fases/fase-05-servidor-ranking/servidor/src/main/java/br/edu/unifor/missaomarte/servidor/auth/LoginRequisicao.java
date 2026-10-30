package br.edu.unifor.missaomarte.servidor.auth;

import br.edu.unifor.missaomarte.servidor.seguranca.Senhas;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corpo de POST /api/auth/login. O toString esconde a senha. */
public record LoginRequisicao(
        @NotBlank(message = "Informe o nome.")
        @Size(max = 20, message = "Nome muito longo.")
        String nome,

        @NotBlank(message = "Informe a senha.")
        @Size(max = 72, message = "Senha muito longa.")
        String senha) {

    @AssertTrue(message = "Senha muito longa.")
    public boolean isSenhaCabeNoBcrypt() {
        return senha == null || Senhas.cabeNoBcrypt(senha);
    }

    @Override
    public String toString() {
        return "LoginRequisicao[nome=" + nome + ", senha=***]";
    }
}
