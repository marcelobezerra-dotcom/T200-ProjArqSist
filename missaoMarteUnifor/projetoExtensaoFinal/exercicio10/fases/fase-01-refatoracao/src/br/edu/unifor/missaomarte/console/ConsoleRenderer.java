package br.edu.unifor.missaomarte.console;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.infrastructure.ranking.RankingEntry;
import java.util.List;

public class ConsoleRenderer {
    public void desenharMapa(GameSession session, int min, int max) {
        Missao missao = session.getMissao();
        for (int y = min; y <= max; y++) {
            StringBuilder linha = new StringBuilder();
            for (int x = min; x <= max; x++) {
                char c = '.';
                if (x == 0 && y == 0) {
                    c = 'B';
                }

                if (missao.getNave().getX() == x && missao.getNave().getY() == y) {
                    c = 'N';
                } else {
                    for (Passageiro passageiro : missao.getPassageiros()) {
                        if (passageiro.getX() == x && passageiro.getY() == y) {
                            c = passageiro.getSimbolo();
                            break;
                        }
                    }
                    for (Asteroide asteroide : missao.getAsteroides()) {
                        if (asteroide.getPosicao().getX() == x && asteroide.getPosicao().getY() == y) {
                            c = asteroide.getSimbolo();
                            break;
                        }
                    }
                    for (Inimigo inimigo : missao.getInimigos()) {
                        if (inimigo.getPosicao().getX() == x && inimigo.getPosicao().getY() == y) {
                            c = inimigo.getSimbolo();
                            break;
                        }
                    }
                }
                linha.append(c).append(' ');
            }
            System.out.println(linha);
        }

        System.out.println(
                "Vidas: " + missao.getNave().getVidas()
                        + " | Pontuacao: " + session.getPontuacao()
                        + " | A bordo: " + missao.getNave().getPassageiros().size() + "/" + missao.getNave().getCapacidade()
                        + " | Restantes: " + missao.getPassageiros().size()
        );
    }

    public void imprimirMensagens(List<String> mensagens) {
        for (String msg : mensagens) {
            System.out.println(msg);
        }
    }

    public void imprimirResumoVitoria(GameSession session) {
        System.out.println();
        System.out.println("PARABENS, " + session.getPiloto() + "!");
        System.out.println("Missao concluida com sucesso.");
        System.out.println("Dificuldade: " + session.getDificuldade());
        System.out.println("Pontuacao final: " + session.getPontuacao());
        System.out.println("Movimentos: " + session.getMovimentos());
        System.out.println("Tempo: " + session.getDuracaoSegundos() + "s");
    }

    public void imprimirRanking(List<RankingEntry> ranking) {
        System.out.println();
        System.out.println("=== RANKING TOP 10 ===");
        if (ranking.isEmpty()) {
            System.out.println("Sem registros ainda.");
            return;
        }
        int i = 1;
        for (RankingEntry e : ranking) {
            System.out.println(i + ". " + e.getNome() + " - " + e.getPontuacao() + " pts - " + e.getDuracaoSegundos() + "s - " + e.getMovimentos() + " mov");
            i++;
        }
    }
}
