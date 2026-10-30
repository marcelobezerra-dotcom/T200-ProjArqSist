package br.edu.unifor.missaomarte.gdx.rede;

import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import br.edu.unifor.missaomarte.application.servidor.PartidaConcluida;
import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Converte objetos do jogo em JSON e o JSON do servidor em objetos do jogo, com {@link JsonWriter} e {@link JsonReader} da libGDX.
 * <p>Nao usa rede nem OpenGL, entao e testavel sem abrir janela. A escrita passa pelo JsonWriter, que escapa as aspas
 * e as barras do texto: o conteudo digitado pelo jogador nunca vira parte da estrutura do JSON.
 */
public final class JsonDoServidor {
    private JsonDoServidor() {
    }

    public static String corpoDeCredenciais(String nome, String senha) {
        return escrever(w -> w.object().set("nome", nome).set("senha", senha).pop());
    }

    public static String corpoDePartida(PartidaConcluida p) {
        return escrever(w -> w.object()
                .set("dificuldade", p.getDificuldade())
                .set("resultado", p.getResultado())
                .set("pontuacao", p.getPontuacao())
                .set("duracaoSegundos", p.getDuracaoSegundos())
                .set("movimentos", p.getMovimentos())
                .pop());
    }

    public static String lerToken(String json) {
        String token = ler(json).getString("token", "");
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Resposta de login sem token.");
        }
        return token;
    }

    public static List<RankingEntry> lerRanking(String json) {
        List<RankingEntry> lista = new ArrayList<>();
        for (JsonValue item : ler(json)) {
            lista.add(new RankingEntry(item.getString("nome"), item.getInt("pontuacao"),
                    item.getLong("duracaoSegundos"), item.getInt("movimentos")));
        }
        return lista;
    }

    public static List<PartidaRegistrada> lerHistorico(String json) {
        List<PartidaRegistrada> lista = new ArrayList<>();
        for (JsonValue item : ler(json)) {
            lista.add(new PartidaRegistrada(item.getString("dificuldade"), item.getString("resultado"),
                    item.getInt("pontuacao"), item.getLong("duracaoSegundos"), item.getInt("movimentos"),
                    item.getString("jogadaEm", "")));
        }
        return lista;
    }

    /**
     * Escolhe a mensagem de erro para o jogador: o primeiro erro de campo ("erros"), senao o "detail" do ProblemDetail,
     * senao um texto pelo codigo HTTP. Nunca devolve o corpo bruto.
     */
    public static String mensagemDeErro(int status, String corpo) {
        if (corpo != null && !corpo.isBlank()) {
            try {
                JsonValue raiz = new JsonReader().parse(corpo);
                JsonValue erros = raiz.get("erros");
                if (erros != null && erros.child != null) {
                    return erros.child.asString();
                }
                String detalhe = raiz.getString("detail", "");
                if (!detalhe.isEmpty()) {
                    return detalhe;
                }
            } catch (RuntimeException ignorado) {
                // corpo que nao e JSON: cai no texto padrao abaixo
            }
        }
        if (status == 401) {
            return "Sessão expirada. Entre novamente.";
        }
        if (status >= 500) {
            return "Erro no servidor (" + status + ").";
        }
        return "Erro " + status + " ao falar com o servidor.";
    }

    private static JsonValue ler(String json) {
        return new JsonReader().parse(json);
    }

    private interface Escritor {
        void escrever(JsonWriter writer) throws IOException;
    }

    private static String escrever(Escritor escritor) {
        StringWriter saida = new StringWriter();
        JsonWriter writer = new JsonWriter(saida);
        writer.setOutputType(OutputType.json);
        try {
            escritor.escrever(writer);
            writer.close();
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao montar o JSON.", e);
        }
        return saida.toString();
    }
}
