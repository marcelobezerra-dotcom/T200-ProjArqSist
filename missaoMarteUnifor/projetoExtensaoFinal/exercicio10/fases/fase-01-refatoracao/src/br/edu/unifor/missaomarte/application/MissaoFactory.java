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
import java.util.Random;

public class MissaoFactory {
    public Missao criarMissao(Dificuldade dificuldade, int min, int max, Random random) {
        ConfiguracaoDificuldade cfg = RegrasDificuldade.obter(dificuldade);
        Nave nave = new Nave(0, 0, 3, 3);
        Missao missao = new Missao(nave);

        for (int i = 0; i < cfg.getQuantidadePassageiros(); i++) {
            Passageiro passageiro = criarPassageiroAleatorio(i, min, max, random);
            missao.adicionarPassageiro(passageiro);
        }

        for (int i = 0; i < cfg.getQuantidadeAsteroides(); i++) {
            missao.adicionarAsteroide(new Asteroide(random.nextInt(max - min + 1) + min, random.nextInt(max - min + 1) + min));
        }

        for (int i = 0; i < cfg.getQuantidadeInimigos(); i++) {
            missao.adicionarInimigo(new Inimigo(random.nextInt(max - min + 1) + min, random.nextInt(max - min + 1) + min));
        }

        return missao;
    }

    public int pontuacaoInicial(Dificuldade dificuldade) {
        return RegrasDificuldade.obter(dificuldade).getPontuacaoInicial();
    }

    private Passageiro criarPassageiroAleatorio(int indice, int min, int max, Random random) {
        int tipo = random.nextInt(3);
        int x = random.nextInt(max - min + 1) + min;
        int y = random.nextInt(max - min + 1) + min;
        String sufixo = String.valueOf(indice + 1);

        if (tipo == 0) {
            return new Professor("Prof. " + sufixo, x, y);
        }
        if (tipo == 1) {
            return new Engenheiro("Eng. " + sufixo, x, y);
        }
        return new Astronauta("Astro " + sufixo, x, y);
    }
}
