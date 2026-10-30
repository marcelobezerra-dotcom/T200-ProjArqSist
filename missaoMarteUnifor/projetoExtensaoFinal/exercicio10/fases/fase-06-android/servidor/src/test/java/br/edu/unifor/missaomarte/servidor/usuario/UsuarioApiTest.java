package br.edu.unifor.missaomarte.servidor.usuario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.unifor.missaomarte.servidor.ApiTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UsuarioApiTest extends ApiTestSupport {

    @Autowired
    private UsuarioRepository repositorio;

    @Test
    @DisplayName("cadastra e devolve so id e nome, sem senha nem hash")
    void cadastraSemExporSenha() throws Exception {
        cadastrar("Neo", SENHA)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Neo"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.senha").doesNotExist())
                .andExpect(jsonPath("$.senhaHash").doesNotExist());
    }

    @Test
    @DisplayName("o banco guarda hash BCrypt, nunca a senha")
    void guardaHashBcrypt() throws Exception {
        cadastrar("Trinity", SENHA).andExpect(status().isCreated());

        Usuario salvo = repositorio.findByNomeNormalizado("trinity").orElseThrow();
        assertThat(salvo.getSenhaHash()).isNotEqualTo(SENHA).startsWith("$2");
    }

    @Test
    @DisplayName("dois cadastros com a mesma senha geram hashes diferentes (sal)")
    void hashesComSalDiferente() throws Exception {
        cadastrar("Uno", SENHA).andExpect(status().isCreated());
        cadastrar("Dois", SENHA).andExpect(status().isCreated());

        String hashUm = repositorio.findByNomeNormalizado("uno").orElseThrow().getSenhaHash();
        String hashDois = repositorio.findByNomeNormalizado("dois").orElseThrow().getSenhaHash();
        assertThat(hashUm).isNotEqualTo(hashDois);
    }

    @Test
    @DisplayName("nome repetido, mesmo com outra caixa, devolve 409")
    void nomeDuplicado() throws Exception {
        cadastrar("Morpheus", SENHA).andExpect(status().isCreated());

        cadastrar("MORPHEUS", SENHA)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Nome de usuário já cadastrado."));
    }

    @Test
    @DisplayName("nome invalido devolve 400 com o campo")
    void nomeInvalido() throws Exception {
        cadastrar("a b", SENHA)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").exists());
        cadastrar("ab", SENHA).andExpect(status().isBadRequest());
        cadastrar("nome-com-mais-de-vinte-caracteres", SENHA).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("senha curta devolve 400")
    void senhaCurta() throws Exception {
        cadastrar("Cypher", "1234567")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.senha").exists());
    }

    @Test
    @DisplayName("senha acima de 72 bytes devolve 400 (limite do BCrypt)")
    void senhaLonga() throws Exception {
        cadastrar("Tank", "x".repeat(73)).andExpect(status().isBadRequest());
        cadastrar("Tank", "x".repeat(72)).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("corpo sem campos devolve 400")
    void corpoVazio() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/usuarios")
                        .contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
    }
}
