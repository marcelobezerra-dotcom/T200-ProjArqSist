package br.edu.unifor.missaomarte.gdx.rede;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import br.edu.unifor.missaomarte.application.servidor.PartidaConcluida;
import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.List;
import org.junit.jupiter.api.Test;

class JsonDoServidorTest {

    @Test
    void credenciaisViramJsonValido() {
        JsonValue json = new JsonReader().parse(JsonDoServidor.corpoDeCredenciais("neo", "senha-123"));

        assertEquals("neo", json.getString("nome"));
        assertEquals("senha-123", json.getString("senha"));
    }

    @Test
    void aspasEBarrasNaSenhaNaoQuebramOJson() {
        String senhaPerigosa = "a\"b\\c\",\"nome\":\"hacker";

        JsonValue json = new JsonReader().parse(JsonDoServidor.corpoDeCredenciais("neo", senhaPerigosa));

        assertEquals(senhaPerigosa, json.getString("senha"));
        assertEquals("neo", json.getString("nome"));
        assertEquals(2, json.size);
    }

    @Test
    void partidaUsaNumerosSemAspas() {
        PartidaConcluida partida = new PartidaConcluida("MEDIO", "VITORIA", 123, 45L, 30);

        String corpo = JsonDoServidor.corpoDePartida(partida);
        JsonValue json = new JsonReader().parse(corpo);

        assertEquals("MEDIO", json.getString("dificuldade"));
        assertEquals("VITORIA", json.getString("resultado"));
        assertEquals(123, json.getInt("pontuacao"));
        assertEquals(45L, json.getLong("duracaoSegundos"));
        assertEquals(30, json.getInt("movimentos"));
        assertTrue(corpo.contains("\"pontuacao\":123"), corpo);
    }

    @Test
    void pontuacaoNegativaContinuaNumero() {
        String corpo = JsonDoServidor.corpoDePartida(new PartidaConcluida("FACIL", "DERROTA", -40, 10L, 5));

        assertEquals(-40, new JsonReader().parse(corpo).getInt("pontuacao"));
    }

    @Test
    void leOTokenDoLogin() {
        String json = "{\"token\":\"abc.def.ghi\",\"tipo\":\"Bearer\",\"expiraEm\":\"2026-10-30T20:00:00Z\"}";

        assertEquals("abc.def.ghi", JsonDoServidor.lerToken(json));
    }

    @Test
    void loginSemTokenEUmErro() {
        assertThrows(IllegalArgumentException.class, () -> JsonDoServidor.lerToken("{\"tipo\":\"Bearer\"}"));
    }

    @Test
    void leORankingNaOrdemRecebida() {
        String json = "[{\"nome\":\"Neo\",\"pontuacao\":300,\"duracaoSegundos\":40,\"movimentos\":22,\"dificuldade\":\"FACIL\",\"jogadaEm\":\"2026-10-30T20:00:00Z\"},"
                + "{\"nome\":\"Trinity\",\"pontuacao\":250,\"duracaoSegundos\":60,\"movimentos\":31,\"dificuldade\":\"MEDIO\",\"jogadaEm\":\"2026-10-30T20:01:00Z\"}]";

        List<RankingEntry> ranking = JsonDoServidor.lerRanking(json);

        assertEquals(2, ranking.size());
        assertEquals("Neo", ranking.get(0).getNome());
        assertEquals(300, ranking.get(0).getPontuacao());
        assertEquals(40L, ranking.get(0).getDuracaoSegundos());
        assertEquals(22, ranking.get(0).getMovimentos());
        assertEquals("Trinity", ranking.get(1).getNome());
    }

    @Test
    void rankingVazioViraListaVazia() {
        assertTrue(JsonDoServidor.lerRanking("[]").isEmpty());
    }

    @Test
    void leOHistorico() {
        String json = "[{\"id\":7,\"dificuldade\":\"FACIL\",\"resultado\":\"DERROTA\",\"pontuacao\":-20,\"duracaoSegundos\":12,\"movimentos\":9,\"jogadaEm\":\"2026-10-30T21:05:07.123Z\"}]";

        List<PartidaRegistrada> historico = JsonDoServidor.lerHistorico(json);

        assertEquals(1, historico.size());
        PartidaRegistrada p = historico.get(0);
        assertEquals("DERROTA", p.getResultado());
        assertEquals(-20, p.getPontuacao());
        assertEquals(12L, p.getDuracaoSegundos());
        assertEquals("2026-10-30T21:05:07.123Z", p.getJogadaEm());
    }

    @Test
    void mensagemDeErroPrefereOErroDeCampo() {
        String corpo = "{\"status\":400,\"detail\":\"Dados inválidos.\",\"erros\":{\"senha\":\"A senha deve ter pelo menos 8 caracteres.\"}}";

        assertEquals("A senha deve ter pelo menos 8 caracteres.", JsonDoServidor.mensagemDeErro(400, corpo));
    }

    @Test
    void mensagemDeErroUsaODetail() {
        String corpo = "{\"status\":409,\"detail\":\"Nome de usuário já cadastrado.\"}";

        assertEquals("Nome de usuário já cadastrado.", JsonDoServidor.mensagemDeErro(409, corpo));
    }

    @Test
    void semCorpoUsaTextoPeloCodigo() {
        assertEquals("Sessão expirada. Entre novamente.", JsonDoServidor.mensagemDeErro(401, ""));
        assertEquals("Erro no servidor (503).", JsonDoServidor.mensagemDeErro(503, null));
        assertEquals("Erro 404 ao falar com o servidor.", JsonDoServidor.mensagemDeErro(404, "   "));
    }

    @Test
    void corpoQueNaoEJsonNaoVazaParaOJogador() {
        String mensagem = JsonDoServidor.mensagemDeErro(500, "<html>stack trace secreto</html>");

        assertEquals("Erro no servidor (500).", mensagem);
    }
}
