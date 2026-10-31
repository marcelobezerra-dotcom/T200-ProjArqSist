package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Missao;
import java.util.Random;
import org.junit.jupiter.api.Test;

class MissaoFactoryDinamicoTest {
    private final MissaoFactory fabrica = new MissaoFactory();

    private static long perseguidores(Missao m) {
        return m.getInimigos().stream().filter(Inimigo::persegue).count();
    }

    @Test
    void modoClassicoNaoTemPerseguidoresNemMunicao() {
        for (Dificuldade d : Dificuldade.values()) {
            Missao m = fabrica.criarMissao(d, -5, 5, new Random(3));

            assertEquals(0, perseguidores(m));
            assertEquals(0, m.getNave().getMunicao());
        }
    }

    @Test
    void modoClassicoFazOsMesmosSorteiosDeAntes() {
        Missao antiga = fabrica.criarMissao(Dificuldade.DIFICIL, -5, 5, new Random(9));
        Missao explicita = fabrica.criarMissao(Dificuldade.DIFICIL, -5, 5, new Random(9), ModoDeJogo.CLASSICO);

        assertEquals(antiga.getInimigos().size(), explicita.getInimigos().size());
        for (int i = 0; i < antiga.getInimigos().size(); i++) {
            assertEquals(antiga.getInimigos().get(i).getPosicao(), explicita.getInimigos().get(i).getPosicao());
        }
        for (int i = 0; i < antiga.getPassageiros().size(); i++) {
            assertEquals(antiga.getPassageiros().get(i).getPosicao(), explicita.getPassageiros().get(i).getPosicao());
        }
    }

    @Test
    void modoDinamicoAcrescentaPerseguidoresDaDificuldadeEMunicao() {
        assertEquals(0, perseguidores(fabrica.criarMissao(Dificuldade.FACIL, -5, 5, new Random(1), ModoDeJogo.DINAMICO)));
        assertEquals(1, perseguidores(fabrica.criarMissao(Dificuldade.MEDIO, -5, 5, new Random(1), ModoDeJogo.DINAMICO)));
        assertEquals(2, perseguidores(fabrica.criarMissao(Dificuldade.DIFICIL, -5, 5, new Random(1), ModoDeJogo.DINAMICO)));
        Missao m = fabrica.criarMissao(Dificuldade.MEDIO, -5, 5, new Random(1), ModoDeJogo.DINAMICO);
        assertEquals(3, m.getNave().getMunicao());
    }

    @Test
    void perseguidoresNascemLongeDaNaveEForaDeOutrosObjetos() {
        for (long semente = 0; semente < 50; semente++) {
            Missao m = fabrica.criarMissao(Dificuldade.DIFICIL, -5, 5, new Random(semente), ModoDeJogo.DINAMICO);
            for (Inimigo i : m.getInimigos()) {
                if (!i.persegue()) {
                    continue;
                }
                int x = i.getPosicao().getX();
                int y = i.getPosicao().getY();
                assertTrue(Math.abs(x) > 2 || Math.abs(y) > 2, "perseguidor perto da nave: " + x + "," + y);
                for (Inimigo outro : m.getInimigos()) {
                    assertFalse(outro != i && outro.getPosicao().equals(i.getPosicao()));
                }
            }
        }
    }

    @Test
    void osObjetosComunsSaoOsMesmosNosDoisModos() {
        Missao classica = fabrica.criarMissao(Dificuldade.MEDIO, -5, 5, new Random(5));
        Missao dinamica = fabrica.criarMissao(Dificuldade.MEDIO, -5, 5, new Random(5), ModoDeJogo.DINAMICO);

        for (int i = 0; i < classica.getAsteroides().size(); i++) {
            assertEquals(classica.getAsteroides().get(i).getPosicao(), dinamica.getAsteroides().get(i).getPosicao());
        }
        assertEquals(classica.getInimigos().size() + 1, dinamica.getInimigos().size());
    }

    @Test
    void velocidadesCrescemComADificuldade() {
        ConfiguracaoDeTempoReal facil = RegrasDeTempoReal.obter(Dificuldade.FACIL);
        ConfiguracaoDeTempoReal medio = RegrasDeTempoReal.obter(Dificuldade.MEDIO);
        ConfiguracaoDeTempoReal dificil = RegrasDeTempoReal.obter(Dificuldade.DIFICIL);

        assertTrue(facil.getIntervaloDosInimigos() > medio.getIntervaloDosInimigos());
        assertTrue(medio.getIntervaloDosInimigos() > dificil.getIntervaloDosInimigos());
        assertTrue(facil.getIntervaloDosPerseguidores() > dificil.getIntervaloDosPerseguidores());
        assertTrue(dificil.getIntervaloDosPerseguidores() > dificil.getIntervaloDaNave());
        assertTrue(facil.getQuantidadeDePerseguidores() < dificil.getQuantidadeDePerseguidores());
    }
}
