package br.edu.unifor.missaomarte.servidor.saude;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.unifor.missaomarte.servidor.ApiTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SaudeApiTest extends ApiTestSupport {

    @Test
    @DisplayName("a rota de saúde é pública e responde ok")
    void saudePublica() throws Exception {
        mvc.perform(get("/api/saude"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    @DisplayName("a rota de saúde só aceita GET e não libera outras rotas")
    void saudeNaoLiberaOutrasRotas() throws Exception {
        mvc.perform(post("/api/saude")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/ranking")).andExpect(status().isUnauthorized());
    }
}
