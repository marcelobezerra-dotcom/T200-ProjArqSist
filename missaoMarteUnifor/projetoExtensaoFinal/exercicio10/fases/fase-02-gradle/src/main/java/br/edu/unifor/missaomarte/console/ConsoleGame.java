package br.edu.unifor.missaomarte.console;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.ResultadoTurno;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.infrastructure.ranking.RankingEntry;
import br.edu.unifor.missaomarte.infrastructure.ranking.RankingRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Fluxo de menu e partida no console: le a entrada, traduz para {@link Acao} e delega a {@link GameSession}.
 * <p>GRASP: Controller (de interface). SOLID: DIP (usa RankingRepository, nao o arquivo), dependencias injetadas pelo construtor.
 * <p>Limites: processarVitoria/deveEntrarNoRanking contem regra de ranking que pertenceria a application,
 * e ConsoleRenderer e MissaoFactory ainda sao classes concretas.
 */
public class ConsoleGame {
    private final Scanner scanner;
    private final Random random;
    private final ConsoleRenderer renderer;
    private final MissaoFactory missaoFactory;
    private final RankingRepository rankingRepository;

    public ConsoleGame(
            Scanner scanner,
            Random random,
            ConsoleRenderer renderer,
            MissaoFactory missaoFactory,
            RankingRepository rankingRepository
    ) {
        this.scanner = scanner;
        this.random = random;
        this.renderer = renderer;
        this.missaoFactory = missaoFactory;
        this.rankingRepository = rankingRepository;
    }

    public void run() {
        while (true) {
            System.out.println("=== MISSAO MARTE (MODO TEXTO) ===");
            System.out.println("1) Jogar");
            System.out.println("2) Ver Ranking");
            System.out.println("3) Limpar Ranking");
            System.out.println("4) Sair");
            System.out.print("> ");

            if (!scanner.hasNextLine()) {
                System.out.println("Entrada encerrada. Encerrando jogo.");
                return;
            }
            String opcao = scanner.nextLine().trim();

            if ("1".equals(opcao)) {
                jogarPartida();
            } else if ("2".equals(opcao)) {
                renderer.imprimirRanking(rankingRepository.carregarTop());
            } else if ("3".equals(opcao)) {
                rankingRepository.salvarTop(new ArrayList<>());
                System.out.println("Ranking limpo.");
            } else if ("4".equals(opcao)) {
                System.out.println("Ate a proxima, comandante!");
                return;
            } else {
                System.out.println("Opcao invalida.");
            }
            System.out.println();
        }
    }

    private void jogarPartida() {
        System.out.print("Nome do piloto: ");
        String nome = lerOuDefault("Piloto");

        System.out.print("Dificuldade (facil/medio/dificil): ");
        Dificuldade dificuldade = Dificuldade.deString(lerOuDefault("facil"));

        int min = -5;
        int max = 5;

        Missao missao = missaoFactory.criarMissao(dificuldade, min, max, random);
        int pontuacaoInicial = missaoFactory.pontuacaoInicial(dificuldade);
        GameSession session = new GameSession(nome, dificuldade, missao, pontuacaoInicial, min, max, random);

        System.out.println();
        System.out.println(renderer.legenda(missao));
        System.out.println("Comandos: W/A/S/D para mover, E para embarcar, Q para sair");
        System.out.println("Pressione Enter para decolar!");
        if (!scanner.hasNextLine()) {
            System.out.println("Entrada encerrada antes do inicio da partida.");
            return;
        }
        scanner.nextLine();

        while (session.getEstado() == EstadoPartida.EM_ANDAMENTO) {
            System.out.println();
            renderer.desenharMapa(session, min, max);
            System.out.print("Comando > ");

            if (!scanner.hasNextLine()) {
                System.out.println("Entrada encerrada durante a partida. Abortando jogo atual.");
                break;
            }

            String entrada = scanner.nextLine();
            if (entrada.trim().isEmpty()) {
                System.out.println("Comando vazio. Digite W/A/S/D, E ou Q.");
                continue;
            }

            Acao acao = Acao.fromInput(entrada);
            ResultadoTurno resultado = session.processarAcao(acao);
            renderer.imprimirMensagens(resultado.getMensagens());
        }

        if (session.getEstado() == EstadoPartida.VITORIA) {
            processarVitoria(session);
        } else if (session.getEstado() == EstadoPartida.DERROTA) {
            System.out.println("GAME OVER");
        }
    }

    private void processarVitoria(GameSession session) {
        renderer.imprimirResumoVitoria(session);

        List<RankingEntry> ranking = rankingRepository.carregarTop();
        RankingEntry atual = new RankingEntry(
                session.getPiloto(),
                session.getPontuacao(),
                session.getDuracaoSegundos(),
                session.getMovimentos()
        );

        if (deveEntrarNoRanking(ranking, atual.getPontuacao())) {
            ranking.add(atual);
            ranking.sort(Comparator.comparingInt(RankingEntry::getPontuacao).reversed());
            while (ranking.size() > 10) {
                ranking.remove(ranking.size() - 1);
            }
            rankingRepository.salvarTop(ranking);
            System.out.println("Novo registro no ranking!");
        }

        renderer.imprimirRanking(rankingRepository.carregarTop());
    }

    private boolean deveEntrarNoRanking(List<RankingEntry> ranking, int pontuacao) {
        if (pontuacao <= 0) {
            return false;
        }
        if (ranking.size() < 10) {
            return true;
        }
        int pior = Integer.MAX_VALUE;
        for (RankingEntry e : ranking) {
            pior = Math.min(pior, e.getPontuacao());
        }
        return pontuacao > pior;
    }

    private String lerOuDefault(String valorPadrao) {
        if (!scanner.hasNextLine()) {
            return valorPadrao;
        }
        String entrada = scanner.nextLine();
        if (entrada == null || entrada.trim().isEmpty()) {
            return valorPadrao;
        }
        return entrada.trim();
    }
}
