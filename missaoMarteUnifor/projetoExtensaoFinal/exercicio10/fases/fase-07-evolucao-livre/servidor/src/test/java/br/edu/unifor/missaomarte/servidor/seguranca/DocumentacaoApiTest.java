package br.edu.unifor.missaomarte.servidor.seguranca;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.unifor.missaomarte.servidor.ApiTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class DocumentacaoApiTest extends ApiTestSupport {

    @Test
    @DisplayName("a descrição OpenAPI é pública e lista as rotas e o esquema bearer")
    void descricaoPublica() throws Exception {
        MvcResult resultado = mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/usuarios']").exists())
                .andExpect(jsonPath("$.paths['/api/auth/login']").exists())
                .andExpect(jsonPath("$.paths['/api/partidas']").exists())
                .andExpect(jsonPath("$.paths['/api/partidas/minhas']").exists())
                .andExpect(jsonPath("$.paths['/api/ranking']").exists())
                .andExpect(jsonPath("$.components.securitySchemes.bearer.scheme").value("bearer"))
                .andReturn();

        String corpo = resultado.getResponse().getContentAsString();
        assertThat(corpo).doesNotContain("senhaHash");
    }

    @Test
    @DisplayName("a página do Swagger UI é pública")
    void paginaPublica() throws Exception {
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("ter a documentação pública não libera as rotas da API")
    void apiContinuaProtegida() throws Exception {
        mvc.perform(get("/api/ranking")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/partidas").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/partidas/minhas")).andExpect(status().isUnauthorized());
    }
}
