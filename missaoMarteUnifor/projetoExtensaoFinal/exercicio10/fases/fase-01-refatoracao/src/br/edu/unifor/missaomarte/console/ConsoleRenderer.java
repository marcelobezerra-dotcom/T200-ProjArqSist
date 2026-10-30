package br.edu.unifor.missaomarte.console;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;
import br.edu.unifor.missaomarte.infrastructure.ranking.RankingEntry;
import java.util.ArrayList;
import java.util.List;

public class ConsoleRenderer {
    private static final char SIMBOLO_VAZIO = '.';

    public String legenda(Missao missao) {
        char simboloNave = missao.getNave().getSimbolo();
        char simboloBase = missao.getBase().getSimbolo();
        char simboloProfessor = new Professor("", 0, 0).getSimbolo();
        char simboloEngenheiro = new Engenheiro("", 0, 0).getSimbolo();
        char simboloAstronauta = new Astronauta("", 0, 0).getSimbolo();
        char simboloAsteroide = new Asteroide(0, 0).getSimbolo();
        char simboloInimigo = new Inimigo(0, 0).getSimbolo();

        return "Legenda: "
                + simboloNave + "=nave, "
                + simboloBase + "=base, "
                + simboloProfessor + "/" + simboloEngenheiro + "/" + simboloAstronauta + "=passageiros, "
                + simboloAsteroide + "=asteroide, "
                + simboloInimigo + "=inimigo";
    }

    public void desenharMapa(GameSession session, int min, int max) {
        Missao missao = session.getMissao();
        List<ObjetoRenderizavel> objetos = coletarObjetosRenderizaveis(missao);
        int aBordo = missao.getNave().getPassageiros().size();
        int capacidade = missao.getNave().getCapacidade();
        int entreguesNaBase = session.getResgatados();
        int total = session.getTotalPassageiros();
        int restantes = missao.getPassageiros().size();

        for (int y = min; y <= max; y++) {
            StringBuilder linha = new StringBuilder();
            for (int x = min; x <= max; x++) {
                char c = simboloNaPosicao(x, y, objetos);
                linha.append(c).append(' ');
            }
            System.out.println(linha);
        }

        System.out.println(
                "Vidas: " + missao.getNave().getVidas()
                        + " | Pontuacao: " + session.getPontuacao()
                    + " | A bordo: " + aBordo + "/" + capacidade
                    + " | Entregues na base: " + entreguesNaBase + "/" + total
                + " | Restantes no mapa: " + restantes
        );
    }

    private List<ObjetoRenderizavel> coletarObjetosRenderizaveis(Missao missao) {
        List<ObjetoRenderizavel> objetos = new ArrayList<>();
        objetos.add(missao.getBase());
        objetos.addAll(missao.getAsteroides());
        objetos.addAll(missao.getInimigos());
        objetos.addAll(missao.getPassageiros());
        objetos.add(missao.getNave());
        return objetos;
    }

    private char simboloNaPosicao(int x, int y, List<ObjetoRenderizavel> objetos) {
        Posicao alvo = new Posicao(x, y);
        char simbolo = SIMBOLO_VAZIO;

        for (ObjetoRenderizavel objeto : objetos) {
            if (objeto.getPosicao().equals(alvo)) {
                simbolo = objeto.getSimbolo();
            }
        }

        return simbolo;
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
