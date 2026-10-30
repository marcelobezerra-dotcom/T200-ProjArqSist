package br.edu.unifor.missaomarte.infrastructure.ranking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileRankingRepositoryTest {

    @TempDir
    Path pasta;

    @Test
    void arquivoInexistenteDevolveRankingVazio() {
        FileRankingRepository repositorio = new FileRankingRepository(pasta.resolve("ranking.json"));

        assertTrue(repositorio.carregarTop().isEmpty());
    }

    @Test
    void salvaELeOsMesmosDados() {
        FileRankingRepository repositorio = new FileRankingRepository(pasta.resolve("ranking.json"));
        List<RankingEntry> entradas = new ArrayList<>();
        entradas.add(new RankingEntry("Ana", 120, 45L, 30));
        entradas.add(new RankingEntry("Bruno \"Estrela\" \\ Silva", 90, 80L, 55));

        repositorio.salvarTop(entradas);
        List<RankingEntry> lidas = repositorio.carregarTop();

        assertEquals(2, lidas.size());
        assertEquals("Ana", lidas.get(0).getNome());
        assertEquals(120, lidas.get(0).getPontuacao());
        assertEquals(45L, lidas.get(0).getDuracaoSegundos());
        assertEquals(30, lidas.get(0).getMovimentos());
        assertEquals("Bruno \"Estrela\" \\ Silva", lidas.get(1).getNome());
        assertEquals(90, lidas.get(1).getPontuacao());
    }

    @Test
    void limparRankingGravaListaVazia() {
        FileRankingRepository repositorio = new FileRankingRepository(pasta.resolve("ranking.json"));
        repositorio.salvarTop(List.of(new RankingEntry("Ana", 120, 45L, 30)));

        repositorio.salvarTop(new ArrayList<>());

        assertTrue(repositorio.carregarTop().isEmpty());
    }

    @Test
    void conteudoInvalidoDevolveRankingVazio() throws IOException {
        Path arquivo = pasta.resolve("ranking.json");
        Files.writeString(arquivo, "isto nao e json");

        assertTrue(new FileRankingRepository(arquivo).carregarTop().isEmpty());
    }
}
