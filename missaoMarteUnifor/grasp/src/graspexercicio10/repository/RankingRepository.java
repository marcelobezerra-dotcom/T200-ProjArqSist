package graspexercicio10.repository;

import graspexercicio10.model.Dificuldade;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * GRASP Pure Fabrication: não representa nenhum conceito do domínio do jogo —
 * existe exclusivamente para isolar toda a lógica de I/O de ranking.
 *
 * GRASP Indirection: intermediário entre RankingService e o sistema de arquivos,
 * reduzindo o acoplamento direto entre serviço e infraestrutura de I/O.
 *
 * GRASP High Cohesion: responsabilidade única — persistir e recuperar rankings.
 */
public class RankingRepository implements IRankingRepository {

    private final Path caminho;

    public RankingRepository(Path caminho) {
        this.caminho = caminho;
    }

    @Override
    public List<RankingEntry> carregar() {
        if (!Files.exists(caminho)) return new ArrayList<>();
        try {
            String json = new String(Files.readAllBytes(caminho), StandardCharsets.UTF_8).trim();
            return parseJson(json);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível carregar o ranking.", e);
        }
    }

    @Override
    public void salvar(List<RankingEntry> ranking) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < ranking.size(); i++) {
            RankingEntry e = ranking.get(i);
            sb.append("{\"nome\":\"").append(e.nome.replace("\"", "\\\""))
              .append("\",\"score\":").append(e.score)
              .append(",\"dificuldade\":\"").append(e.dificuldade.name())
              .append(",\"passageirosColetados\":").append(e.passageirosColetados)
              .append(",\"dataHora\":\"").append(e.dataHora)
              .append("\",\"tempoJogo\":").append(e.tempoJogo)
              .append("}");
            if (i < ranking.size() - 1) sb.append(",");
        }
        sb.append("]");
        try {
            Files.write(caminho, sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new UncheckedIOException("Não foi possível salvar o ranking.", ex);
        }
    }

    @Override
    public void resetar() {
        try {
            Files.deleteIfExists(caminho);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível resetar o ranking.", e);
        }
    }

    // ── Parser JSON manual (sem bibliotecas externas) ─────────────────────────

    private List<RankingEntry> parseJson(String json) {
        List<RankingEntry> resultado = new ArrayList<>();
        if (json.isEmpty() || json.equals("[]")) return resultado;

        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]"))   json = json.substring(0, json.length() - 1);

        int idx = 0;
        while (idx < json.length()) {
            int start = json.indexOf('{', idx);
            if (start < 0) break;
            int end = json.indexOf('}', start);
            if (end < 0) break;

            String       nome       = null;
            Integer      score      = null;
            Dificuldade  dif        = Dificuldade.MEDIO;
            int          passageiros = 0;
            String       dataHora   = "";
            long         tempoJogo  = 0;

            for (String part : json.substring(start + 1, end).split(",")) {
                String[] kv = part.split(":", 2);
                if (kv.length != 2) continue;
                String key   = kv[0].trim().replace("\"", "");
                String value = kv[1].trim();
                switch (key) {
                    case "nome":                 nome        = unquote(value); break;
                    case "score":                try { score = Integer.parseInt(value); } catch (NumberFormatException ignored) {} break;
                    case "dificuldade":          dif         = Dificuldade.deString(unquote(value)); break;
                    case "passageirosColetados": try { passageiros = Integer.parseInt(value); } catch (NumberFormatException ignored) {} break;
                    case "dataHora":             dataHora    = unquote(value); break;
                    case "tempoJogo":            try { tempoJogo = Long.parseLong(value); } catch (NumberFormatException ignored) {} break;
                }
            }
            if (nome != null && score != null) {
                resultado.add(new RankingEntry(nome, score, dif, passageiros, dataHora, tempoJogo));
            }
            idx = end + 1;
        }
        return resultado;
    }

    private static String unquote(String s) {
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\""))
            return s.substring(1, s.length() - 1).replace("\\\"", "\"");
        return s;
    }
}
