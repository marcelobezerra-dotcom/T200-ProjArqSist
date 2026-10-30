package br.edu.unifor.missaomarte;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.console.ConsoleGame;
import br.edu.unifor.missaomarte.console.ConsoleRenderer;
import br.edu.unifor.missaomarte.infrastructure.ranking.FileRankingRepository;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

/**
 * Composition root: unico lugar que conhece as implementacoes concretas e monta o grafo de objetos.
 * <p>Padrao: Injecao de Dependencia manual (construtor). SOLID: DIP. Nao contem regra de jogo.
 */
public class Main {
    public static void main(String[] args) {
        Path rankingPath = Paths.get("ranking.json");
        ConsoleGame game = new ConsoleGame(
                new Scanner(System.in),
                new Random(),
                new ConsoleRenderer(),
                new MissaoFactory(),
                new FileRankingRepository(rankingPath)
        );
        game.run();
    }
}
