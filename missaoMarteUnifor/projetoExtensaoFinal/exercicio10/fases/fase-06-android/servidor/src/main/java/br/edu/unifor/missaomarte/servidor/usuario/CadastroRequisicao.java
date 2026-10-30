package br.edu.unifor.missaomarte.servidor.usuario;

import br.edu.unifor.missaomarte.servidor.seguranca.Senhas;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Corpo de POST /api/usuarios. O toString esconde a senha para ela nao aparecer em logs. */
public record CadastroRequisicao(
        @NotNull(message = "Informe o nome.")
        @Pattern(regexp = "[A-Za-z0-9_-]{3,20}", message = "O nome deve ter de 3 a 20 caracteres: letras, números, _ ou -.")
        String nome,

        @NotNull(message = "Informe a senha.")
        @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres.")
        String senha) {

    @AssertTrue(message = "A senha deve ter no máximo 72 bytes.")
    public boolean isSenhaCabeNoBcrypt() {
        return senha == null || Senhas.cabeNoBcrypt(senha);
    }

    @Override
    public String toString() {
        return "CadastroRequisicao[nome=" + nome + ", senha=***]";
    }
}
