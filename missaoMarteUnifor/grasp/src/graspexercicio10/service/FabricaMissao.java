package graspexercicio10.service;

import graspexercicio10.model.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Creator: reúne os dados de configuração necessários à criação da missão.
 * Pure Fabrication: separa a montagem do cenário da execução dos turnos.
 */
public class FabricaMissao {
    public Missao criar(Dificuldade dificuldade, int minX, int maxX, int minY, int maxY,
                        Random random) {
        if (minX > 0 || maxX < 0 || minY > 0 || maxY < 0
                || minX < -20 || maxX > 20 || minY < -20 || maxY > 20) {
            throw new IllegalArgumentException("O mapa deve conter a base e ter limites entre -20 e 20.");
        }
        List<int[]> livres = new ArrayList<>();
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (x != 0 || y != 0) livres.add(new int[]{x, y});
            }
        }
        int passageiros = dificuldade.getQuantidadePassageiros();
        int asteroides = dificuldade.getQuantidadeAsteroides();
        int inimigos = dificuldade.getQuantidadeInimigos();
        if (livres.size() < passageiros + asteroides + inimigos) {
            throw new IllegalArgumentException("Mapa sem espaço para os elementos da missão.");
        }
        // Sem tentativas aleatórias repetidas: a criação sempre termina.
        Collections.shuffle(livres, random);
        Missao missao = new Missao(new Nave("A-1", 5));
        int indice = 0;
        for (int i = 0; i < passageiros; i++) {
            int[] p = livres.get(indice++);
            missao.addPassageiro(criarPassageiro(i, p[0], p[1]));
        }
        for (int i = 0; i < asteroides; i++) {
            int[] p = livres.get(indice++);
            missao.addPerigo(new Asteroide(p[0], p[1]));
        }
        for (int i = 0; i < inimigos; i++) {
            int[] p = livres.get(indice++);
            missao.addPerigo(new Inimigo(p[0], p[1]));
        }
        return missao;
    }

    private Passageiro criarPassageiro(int indice, int x, int y) {
        switch (indice % 5) {
            case 0: return new Professor("Dr. Silva", x, y);
            case 1: return new Engenheiro("Eng. Rosa", x, y);
            case 2: return new Professor("Dr. Lima", x, y);
            case 3: return new Engenheiro("Eng. Carlos", x, y);
            default: return new Astronauta("Ast. Maria", x, y);
        }
    }
}
