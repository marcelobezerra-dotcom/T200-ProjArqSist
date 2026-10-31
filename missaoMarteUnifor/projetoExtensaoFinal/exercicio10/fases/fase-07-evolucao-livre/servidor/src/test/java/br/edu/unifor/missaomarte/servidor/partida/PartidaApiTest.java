package br.edu.unifor.missaomarte.servidor.partida;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.unifor.missaomarte.servidor.ApiTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class PartidaApiTest extends ApiTestSupport {

    @Test
    @DisplayName("registra a partida do usuario do token")
    void registra() throws Exception {
        String autorizacao = cadastrarEEntrar("Neo");

        enviarPartida(autorizacao, "VITORIA", 120, 45)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.resultado").value("VITORIA"))
                .andExpect(jsonPath("$.pontuacao").value(120))
                .andExpect(jsonPath("$.duracaoSegundos").value(45));
    }

    @Test
    @DisplayName("sem token nao registra")
    void semToken() throws Exception {
        mvc.perform(post("/api/partidas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dificuldade\":\"FACIL\",\"resultado\":\"VITORIA\",\"pontuacao\":10,\"duracaoSegundos\":5,\"movimentos\":3}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("dificuldade ou resultado desconhecidos devolvem 400")
    void enumsInvalidos() throws Exception {
        String autorizacao = cadastrarEEntrar("Neo");
        String corpoRuim = "{\"dificuldade\":\"IMPOSSIVEL\",\"resultado\":\"VITORIA\",\"pontuacao\":10,\"duracaoSegundos\":5,\"movimentos\":3}";
        String abortada = "{\"dificuldade\":\"FACIL\",\"resultado\":\"ABORTADA\",\"pontuacao\":10,\"duracaoSegundos\":5,\"movimentos\":3}";

        mvc.perform(post("/api/partidas").header("Authorization", autorizacao)
                .contentType(MediaType.APPLICATION_JSON).content(corpoRuim)).andExpect(status().isBadRequest());
        mvc.perform(post("/api/partidas").header("Authorization", autorizacao)
                .contentType(MediaType.APPLICATION_JSON).content(abortada)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("valores fora da faixa e campos ausentes devolvem 400")
    void valoresForaDaFaixa() throws Exception {
        String autorizacao = cadastrarEEntrar("Neo");

        enviarPartida(autorizacao, "VITORIA", 1_000_000, 10).andExpect(status().isBadRequest());
        enviarPartida(autorizacao, "VITORIA", 10, -1).andExpect(status().isBadRequest());
        mvc.perform(post("/api/partidas").header("Authorization", autorizacao)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"dificuldade\":\"FACIL\",\"resultado\":\"VITORIA\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.pontuacao").exists());
    }

    @Test
    @DisplayName("o usuario enviado no corpo e ignorado: a partida pertence ao dono do token")
    void usuarioDoCorpoIgnorado() throws Exception {
        String neo = cadastrarEEntrar("Neo");
        String trinity = cadastrarEEntrar("Trinity");

        mvc.perform(post("/api/partidas").header("Authorization", neo).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuario\":\"Trinity\",\"dificuldade\":\"FACIL\",\"resultado\":\"VITORIA\",\"pontuacao\":50,\"duracaoSegundos\":9,\"movimentos\":4}"))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/partidas/minhas").header("Authorization", trinity))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/partidas/minhas").header("Authorization", neo))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("historico traz so as partidas do usuario, da mais recente para a mais antiga")
    void historicoDoUsuario() throws Exception {
        String neo = cadastrarEEntrar("Neo");
        String trinity = cadastrarEEntrar("Trinity");
        enviarPartida(neo, "VITORIA", 100, 30).andExpect(status().isCreated());
        enviarPartida(neo, "DERROTA", -20, 12).andExpect(status().isCreated());
        enviarPartida(trinity, "VITORIA", 999, 10).andExpect(status().isCreated());

        mvc.perform(get("/api/partidas/minhas").header("Authorization", neo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].resultado").value("DERROTA"))
                .andExpect(jsonPath("$[1].resultado").value("VITORIA"));
    }

    @Test
    @DisplayName("historico e paginado")
    void historicoPaginado() throws Exception {
        String neo = cadastrarEEntrar("Neo");
        for (int i = 1; i <= 5; i++) {
            enviarPartida(neo, "VITORIA", i * 10, 20).andExpect(status().isCreated());
        }

        mvc.perform(get("/api/partidas/minhas?pagina=0&tamanho=2").header("Authorization", neo))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].pontuacao").value(50));
        mvc.perform(get("/api/partidas/minhas?pagina=2&tamanho=2").header("Authorization", neo))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].pontuacao").value(10));
    }

    @Test
    @DisplayName("tamanho de pagina acima de 50 devolve 400")
    void tamanhoMaximo() throws Exception {
        String neo = cadastrarEEntrar("Neo");

        mvc.perform(get("/api/partidas/minhas?tamanho=51").header("Authorization", neo))
                .andExpect(status().isBadRequest());
    }
}
