package br.edu.unifor.missaomarte.servidor.ranking;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.unifor.missaomarte.servidor.ApiTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RankingApiTest extends ApiTestSupport {

    @Test
    @DisplayName("ranking vazio devolve lista vazia")
    void vazio() throws Exception {
        String neo = cadastrarEEntrar("Neo");

        ranking(neo).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("cada jogador aparece uma vez, com a sua maior pontuacao")
    void melhorPartidaDeCadaJogador() throws Exception {
        String neo = cadastrarEEntrar("Neo");
        String trinity = cadastrarEEntrar("Trinity");
        enviarPartida(neo, "VITORIA", 100, 50).andExpect(status().isCreated());
        enviarPartida(neo, "VITORIA", 300, 40).andExpect(status().isCreated());
        enviarPartida(neo, "VITORIA", 200, 30).andExpect(status().isCreated());
        enviarPartida(trinity, "VITORIA", 250, 60).andExpect(status().isCreated());

        ranking(neo).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Neo"))
                .andExpect(jsonPath("$[0].pontuacao").value(300))
                .andExpect(jsonPath("$[0].duracaoSegundos").value(40))
                .andExpect(jsonPath("$[1].nome").value("Trinity"))
                .andExpect(jsonPath("$[1].pontuacao").value(250));
    }

    @Test
    @DisplayName("derrotas e pontuacoes nao positivas ficam fora do ranking")
    void ignoraDerrotasEPontuacaoZero() throws Exception {
        String neo = cadastrarEEntrar("Neo");
        String trinity = cadastrarEEntrar("Trinity");
        enviarPartida(neo, "DERROTA", 900, 20).andExpect(status().isCreated());
        enviarPartida(trinity, "VITORIA", 0, 20).andExpect(status().isCreated());
        enviarPartida(trinity, "VITORIA", -5, 20).andExpect(status().isCreated());

        ranking(neo).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("empate de pontuacao: menor duracao primeiro; se igual, quem chegou antes")
    void desempate() throws Exception {
        String a = cadastrarEEntrar("Alfa");
        String b = cadastrarEEntrar("Beta");
        String c = cadastrarEEntrar("Gama");
        enviarPartida(a, "VITORIA", 100, 50).andExpect(status().isCreated());
        enviarPartida(b, "VITORIA", 100, 30).andExpect(status().isCreated());
        enviarPartida(c, "VITORIA", 100, 30).andExpect(status().isCreated());

        ranking(a).andExpect(jsonPath("$[0].nome").value("Beta"))
                .andExpect(jsonPath("$[1].nome").value("Gama"))
                .andExpect(jsonPath("$[2].nome").value("Alfa"));
    }

    @Test
    @DisplayName("o ranking mostra no maximo 10 jogadores")
    void limiteDeDez() throws Exception {
        String ultimo = null;
        for (int i = 1; i <= 12; i++) {
            ultimo = cadastrarEEntrar("jogador" + i);
            enviarPartida(ultimo, "VITORIA", i * 10, 30).andExpect(status().isCreated());
        }

        ranking(ultimo).andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].nome").value("jogador12"))
                .andExpect(jsonPath("$[0].pontuacao").value(120))
                .andExpect(jsonPath("$[9].nome").value("jogador3"));
    }

    @Test
    @DisplayName("o ranking nao expoe senha, hash nem id")
    void semDadosSensiveis() throws Exception {
        String neo = cadastrarEEntrar("Neo");
        enviarPartida(neo, "VITORIA", 100, 30).andExpect(status().isCreated());

        ranking(neo).andExpect(jsonPath("$[0].senha").doesNotExist())
                .andExpect(jsonPath("$[0].senhaHash").doesNotExist())
                .andExpect(jsonPath("$[0].id").doesNotExist());
    }
}
