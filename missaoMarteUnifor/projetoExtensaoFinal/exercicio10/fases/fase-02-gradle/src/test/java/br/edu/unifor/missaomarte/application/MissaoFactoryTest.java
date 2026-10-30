package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.domain.Posicao;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MissaoFactoryTest {
    private static final int MIN = -5;
    private static final int MAX = 5;

    private final MissaoFactory factory = new MissaoFactory();

    @Test
    void criaAsQuantidadesDeCadaDificuldade() {
        for (Dificuldade dificuldade : Dificuldade.values()) {
            ConfiguracaoDificuldade cfg = RegrasDificuldade.obter(dificuldade);

            Missao missao = factory.criarMissao(dificuldade, MIN, MAX, new Random(1));

            assertEquals(cfg.getQuantidadePassageiros(), missao.getPassageiros().size());
            assertEquals(cfg.getQuantidadeAsteroides(), missao.getAsteroides().size());
            assertEquals(cfg.getQuantidadeInimigos(), missao.getInimigos().size());
            assertEquals(cfg.getPontuacaoInicial(), factory.pontuacaoInicial(dificuldade));
        }
    }

    @Test
    void capacidadeDaNaveAcompanhaAQuantidadeDePassageiros() {
        for (Dificuldade dificuldade : Dificuldade.values()) {
            Missao missao = factory.criarMissao(dificuldade, MIN, MAX, new Random(1));

            assertEquals(missao.getPassageiros().size(), missao.getNave().getCapacidade());
        }
    }

    @Test
    void naveComecaNaBase() {
        Missao missao = factory.criarMissao(Dificuldade.FACIL, MIN, MAX, new Random(1));

        assertEquals(missao.getBase().getPosicao(), missao.getNave().getPosicao());
    }

    @Test
    void nenhumElementoNasceSobrepostoNemSobreABase() {
        for (Dificuldade dificuldade : Dificuldade.values()) {
            for (long semente = 0; semente < 50; semente++) {
                Missao missao = factory.criarMissao(dificuldade, MIN, MAX, new Random(semente));

                List<ObjetoRenderizavel> elementos = new ArrayList<>();
                elementos.addAll(missao.getPassageiros());
                elementos.addAll(missao.getAsteroides());
                elementos.addAll(missao.getInimigos());

                Set<Posicao> unicas = new HashSet<>();
                for (ObjetoRenderizavel elemento : elementos) {
                    unicas.add(elemento.getPosicao());
                }

                assertEquals(elementos.size(), unicas.size(), "posicoes repetidas, semente " + semente);
                assertTrue(!unicas.contains(missao.getBase().getPosicao()), "elemento sobre a base");
            }
        }
    }

    @Test
    void dificuldadesDiferentesTemPontuacaoInicialDiferente() {
        assertNotEquals(
                factory.pontuacaoInicial(Dificuldade.FACIL),
                factory.pontuacaoInicial(Dificuldade.DIFICIL));
    }
}
