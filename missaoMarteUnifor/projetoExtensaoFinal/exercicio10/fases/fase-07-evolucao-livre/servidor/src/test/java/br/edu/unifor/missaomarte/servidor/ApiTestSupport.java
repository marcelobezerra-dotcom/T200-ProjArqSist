package br.edu.unifor.missaomarte.servidor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/** Base dos testes de API: sobe o contexto inteiro com banco H2 em memoria e desfaz cada teste ao final. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class ApiTestSupport {
    protected static final String SENHA = "senha-segura-1";

    @Autowired
    protected MockMvc mvc;

    protected ResultActions cadastrar(String nome, String senha) throws Exception {
        return mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"%s\",\"senha\":\"%s\"}".formatted(nome, senha)));
    }

    protected ResultActions login(String nome, String senha) throws Exception {
        return mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"%s\",\"senha\":\"%s\"}".formatted(nome, senha)));
    }

    /** Cadastra o usuario, entra e devolve o valor do cabecalho Authorization. */
    protected String cadastrarEEntrar(String nome) throws Exception {
        cadastrar(nome, SENHA).andExpect(status().isCreated());
        String corpo = login(nome, SENHA).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.<String>read(corpo, "$.token");
    }

    protected ResultActions enviarPartida(String autorizacao, String resultado, int pontuacao, long duracao) throws Exception {
        return mvc.perform(post("/api/partidas").header("Authorization", autorizacao)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"dificuldade":"FACIL","resultado":"%s","pontuacao":%d,"duracaoSegundos":%d,"movimentos":20}
                        """.formatted(resultado, pontuacao, duracao)));
    }

    protected ResultActions ranking(String autorizacao) throws Exception {
        return mvc.perform(get("/api/ranking").header("Authorization", autorizacao));
    }
}
