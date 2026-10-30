package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Professor;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Monta a Missao conforme a dificuldade, sem sobrepor elementos no mapa.
 * <p>Padrao: Factory. GRASP: Creator (cria Nave, Passageiros e Perigos), Pure Fabrication, High Cohesion.
 * <p>SOLID: SRP (so cria). A capacidade da nave acompanha a quantidade de passageiros da dificuldade.
 * <p>Limite OCP: criarPassageiroAleatorio escolhe o tipo com if.
 */
public class MissaoFactory {
    public Missao criarMissao(Dificuldade dificuldade, int min, int max, Random random) {
        ConfiguracaoDificuldade cfg = RegrasDificuldade.obter(dificuldade);
        Nave nave = new Nave(0, 0, 3, cfg.getQuantidadePassageiros());
        Missao missao = new Missao(nave);
        Set<String> ocupadas = new HashSet<>();
        ocupadas.add(chave(0, 0));

        for (int i = 0; i < cfg.getQuantidadePassageiros(); i++) {
            int[] pos = novaPosicaoLivre(min, max, random, ocupadas);
            Passageiro passageiro = criarPassageiroAleatorio(i, pos[0], pos[1], random);
            missao.adicionarPassageiro(passageiro);
            ocupadas.add(chave(pos[0], pos[1]));
        }

        for (int i = 0; i < cfg.getQuantidadeAsteroides(); i++) {
            int[] pos = novaPosicaoLivre(min, max, random, ocupadas);
            missao.adicionarAsteroide(new Asteroide(pos[0], pos[1]));
            ocupadas.add(chave(pos[0], pos[1]));
        }

        for (int i = 0; i < cfg.getQuantidadeInimigos(); i++) {
            int[] pos = novaPosicaoLivre(min, max, random, ocupadas);
            missao.adicionarInimigo(new Inimigo(pos[0], pos[1]));
            ocupadas.add(chave(pos[0], pos[1]));
        }

        return missao;
    }

    public int pontuacaoInicial(Dificuldade dificuldade) {
        return RegrasDificuldade.obter(dificuldade).getPontuacaoInicial();
    }

    private Passageiro criarPassageiroAleatorio(int indice, int x, int y, Random random) {
        int tipo = random.nextInt(3);
        String sufixo = String.valueOf(indice + 1);

        if (tipo == 0) {
            return new Professor("Prof. " + sufixo, x, y);
        }
        if (tipo == 1) {
            return new Engenheiro("Eng. " + sufixo, x, y);
        }
        return new Astronauta("Astro " + sufixo, x, y);
    }

    // Sorteia ate achar uma celula fora do Set de ocupadas (base, passageiros e perigos ja criados).
    private int[] novaPosicaoLivre(int min, int max, Random random, Set<String> ocupadas) {
        int tentativasMaximas = (max - min + 1) * (max - min + 1) * 2;
        for (int tentativa = 0; tentativa < tentativasMaximas; tentativa++) {
            int x = random.nextInt(max - min + 1) + min;
            int y = random.nextInt(max - min + 1) + min;
            String key = chave(x, y);
            if (!ocupadas.contains(key)) {
                return new int[]{x, y};
            }
        }
        throw new IllegalStateException("Nao foi possivel encontrar posicao livre no mapa.");
    }

    private String chave(int x, int y) {
        return x + "," + y;
    }
}
