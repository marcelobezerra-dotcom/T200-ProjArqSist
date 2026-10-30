package br.edu.unifor.missaomarte.infrastructure.ranking;

import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import br.edu.unifor.missaomarte.application.ranking.RankingRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacao de {@link RankingRepository} em arquivo JSON, com leitura e escrita manuais.
 * <p>SOLID: SRP (so persistencia), LSP (substitui a interface sem quebrar quem a usa).
 * <p>Limite: falhas de IO sao ignoradas em silencio.
 */
public class FileRankingRepository implements RankingRepository {
    private final Path arquivo;

    public FileRankingRepository(Path arquivo) {
        this.arquivo = arquivo;
    }

    @Override
    public List<RankingEntry> carregarTop() {
        if (!Files.exists(arquivo)) {
            return new ArrayList<>();
        }
        try {
            String json = Files.readString(arquivo, StandardCharsets.UTF_8).trim();
            if (json.isEmpty()) {
                return new ArrayList<>();
            }
            return parseRankingJson(json);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void salvarTop(List<RankingEntry> ranking) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < ranking.size(); i++) {
            RankingEntry entry = ranking.get(i);
            sb.append("  {");
            sb.append("\"nome\":\"").append(escapeJson(entry.getNome())).append("\",");
            sb.append("\"pontuacao\":").append(entry.getPontuacao()).append(",");
            sb.append("\"duracaoSegundos\":").append(entry.getDuracaoSegundos()).append(",");
            sb.append("\"movimentos\":").append(entry.getMovimentos());
            sb.append("}");
            if (i < ranking.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("]\n");
        try {
            Files.writeString(arquivo, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static List<RankingEntry> parseRankingJson(String json) {
        List<RankingEntry> list = new ArrayList<>();
        String trimmed = json.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            return list;
        }

        int i = 1;
        int n = trimmed.length() - 1;
        while (i < n) {
            while (i < n && (Character.isWhitespace(trimmed.charAt(i)) || trimmed.charAt(i) == ',')) {
                i++;
            }
            if (i >= n) {
                break;
            }
            if (trimmed.charAt(i) != '{') {
                break;
            }
            int start = i;
            int depth = 0;
            boolean inString = false;
            boolean escape = false;
            while (i < n) {
                char ch = trimmed.charAt(i);
                if (inString) {
                    if (escape) {
                        escape = false;
                    } else if (ch == '\\') {
                        escape = true;
                    } else if (ch == '"') {
                        inString = false;
                    }
                } else {
                    if (ch == '"') {
                        inString = true;
                    } else if (ch == '{') {
                        depth++;
                    } else if (ch == '}') {
                        depth--;
                        if (depth == 0) {
                            i++;
                            break;
                        }
                    }
                }
                i++;
            }
            String object = trimmed.substring(start, i);
            RankingEntry entry = parseObject(object);
            if (entry != null) {
                list.add(entry);
            }
        }
        return list;
    }

    private static RankingEntry parseObject(String object) {
        String nome = readStringField(object, "nome");
        Integer pontuacao = readIntField(object, "pontuacao");
        Long duracao = readLongField(object, "duracaoSegundos");
        Integer movimentos = readIntField(object, "movimentos");

        if (nome == null || pontuacao == null || duracao == null || movimentos == null) {
            return null;
        }
        return new RankingEntry(nome, pontuacao, duracao, movimentos);
    }

    private static String readStringField(String json, String key) {
        String pattern = "\"" + key + "\"";
        int idx = json.indexOf(pattern);
        if (idx < 0) {
            return null;
        }
        int colon = json.indexOf(':', idx + pattern.length());
        if (colon < 0) {
            return null;
        }
        int firstQuote = json.indexOf('"', colon + 1);
        if (firstQuote < 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean escape = false;
        for (int i = firstQuote + 1; i < json.length(); i++) {
            char ch = json.charAt(i);
            if (escape) {
                if (ch == '"' || ch == '\\' || ch == '/') {
                    sb.append(ch);
                } else if (ch == 'n') {
                    sb.append('\n');
                } else if (ch == 't') {
                    sb.append('\t');
                } else if (ch == 'r') {
                    sb.append('\r');
                } else {
                    sb.append(ch);
                }
                escape = false;
            } else if (ch == '\\') {
                escape = true;
            } else if (ch == '"') {
                return sb.toString();
            } else {
                sb.append(ch);
            }
        }
        return null;
    }

    private static Integer readIntField(String json, String key) {
        Long value = readLongField(json, key);
        return value == null ? null : value.intValue();
    }

    private static Long readLongField(String json, String key) {
        String pattern = "\"" + key + "\"";
        int idx = json.indexOf(pattern);
        if (idx < 0) {
            return null;
        }
        int colon = json.indexOf(':', idx + pattern.length());
        if (colon < 0) {
            return null;
        }
        int i = colon + 1;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }
        int start = i;
        if (i < json.length() && (json.charAt(i) == '-' || Character.isDigit(json.charAt(i)))) {
            i++;
            while (i < json.length() && Character.isDigit(json.charAt(i))) {
                i++;
            }
            try {
                return Long.parseLong(json.substring(start, i));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
