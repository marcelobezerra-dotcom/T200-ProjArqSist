package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import br.edu.unifor.missaomarte.application.ranking.RankingRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class RankingServiceTest {

    private static class RepositorioEmMemoria implements RankingRepository {
        private List<RankingEntry> dados = new ArrayList<>();

        @Override
        public List<RankingEntry> carregarTop() {
            return new ArrayList<>(dados);
        }

        @Override
        public void salvarTop(List<RankingEntry> ranking) {
            dados = new ArrayList<>(ranking);
        }
    }

    private final RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
    private final RankingService servico = new RankingService(repositorio);

    private static RankingEntry entrada(String nome, int pontuacao) {
        return new RankingEntry(nome, pontuacao, 10, 20);
    }

    private void preencherDezEntradas() {
        for (int i = 1; i <= 10; i++) {
            servico.registrar(entrada("J" + i, i * 10));
        }
    }

    @Test
    void pontuacaoZeroOuNegativaNaoEntra() {
        assertFalse(servico.registrar(entrada("Zero", 0)));
        assertFalse(servico.registrar(entrada("Negativo", -5)));
        assertTrue(servico.top().isEmpty());
    }

    @Test
    void ordenaPorPontuacaoDecrescente() {
        servico.registrar(entrada("B", 50));
        servico.registrar(entrada("A", 80));
        servico.registrar(entrada("C", 65));

        List<RankingEntry> top = servico.top();
        assertEquals("A", top.get(0).getNome());
        assertEquals("C", top.get(1).getNome());
        assertEquals("B", top.get(2).getNome());
    }

    @Test
    void mantemNoMaximoDezEntradas() {
        preencherDezEntradas();

        assertTrue(servico.registrar(entrada("Novo", 55)));

        List<RankingEntry> top = servico.top();
        assertEquals(RankingService.TAMANHO_MAXIMO, top.size());
        assertEquals(100, top.get(0).getPontuacao());
        assertEquals("Novo", top.get(5).getNome());
        assertEquals(20, top.get(9).getPontuacao());
    }

    @Test
    void rankingCheioNaoAceitaPontuacaoIgualOuMenorQueAPior() {
        preencherDezEntradas();

        assertFalse(servico.registrar(entrada("Igual", 10)));
        assertFalse(servico.registrar(entrada("Menor", 5)));
        assertEquals(10, servico.top().get(9).getPontuacao());
    }

    @Test
    void limparEsvaziaORanking() {
        servico.registrar(entrada("A", 10));

        servico.limpar();

        assertTrue(servico.top().isEmpty());
    }
}
