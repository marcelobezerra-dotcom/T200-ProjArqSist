package br.edu.unifor.missaomarte.servidor.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.unifor.missaomarte.servidor.ApiTestSupport;
import java.time.Instant;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

class AuthApiTest extends ApiTestSupport {

    @Autowired
    private JwtEncoder encoder;

    @Autowired
    private SecretKey chaveDoToken;

    @Test
    @DisplayName("login correto devolve token Bearer com expiracao")
    void loginCorreto() throws Exception {
        cadastrar("Neo", SENHA).andExpect(status().isCreated());

        login("Neo", SENHA)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.expiraEm").exists());
    }

    @Test
    @DisplayName("o login nao diferencia maiusculas de minusculas no nome")
    void loginSemDiferenciarCaixa() throws Exception {
        cadastrar("Neo", SENHA).andExpect(status().isCreated());

        login("nEO", SENHA).andExpect(status().isOk());
    }

    @Test
    @DisplayName("senha errada e usuario inexistente devolvem exatamente a mesma resposta 401")
    void erroGenerico() throws Exception {
        cadastrar("Neo", SENHA).andExpect(status().isCreated());

        String senhaErrada = login("Neo", "outra-senha-qualquer").andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();
        String semUsuario = login("Fantasma", SENHA).andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertThat(semUsuario).isEqualTo(senhaErrada).contains("Usuário ou senha inválidos.");
    }

    @Test
    @DisplayName("a resposta do login nunca contem a senha")
    void semSenhaNaResposta() throws Exception {
        cadastrar("Neo", SENHA).andExpect(status().isCreated());

        String corpo = login("Neo", SENHA).andReturn().getResponse().getContentAsString();

        assertThat(corpo).doesNotContain(SENHA);
    }

    @Test
    @DisplayName("sem token as rotas protegidas devolvem 401")
    void semToken() throws Exception {
        mvc.perform(get("/api/ranking")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/partidas/minhas")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("token invalido devolve 401")
    void tokenInvalido() throws Exception {
        ranking("Bearer isto-nao-e-um-token").andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("token expirado devolve 401")
    void tokenExpirado() throws Exception {
        cadastrar("Neo", SENHA).andExpect(status().isCreated());
        Instant ontem = Instant.now().minusSeconds(86_400);
        JwtClaimsSet claims = JwtClaimsSet.builder().subject("1").issuedAt(ontem.minusSeconds(60))
                .expiresAt(ontem).build();
        String expirado = encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();

        ranking("Bearer " + expirado).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("token valido libera a rota protegida")
    void tokenValido() throws Exception {
        String autorizacao = cadastrarEEntrar("Neo");

        ranking(autorizacao).andExpect(status().isOk());
        assertThat(chaveDoToken.getEncoded()).hasSizeGreaterThanOrEqualTo(32);
    }

    @Test
    @DisplayName("login sem campos devolve 400")
    void loginSemCampos() throws Exception {
        login("", "").andExpect(status().isBadRequest());
    }
}
