package graspexercicio10;

import graspexercicio10.controller.GameController;
import graspexercicio10.presentation.ConsoleJogo;
import graspexercicio10.presentation.MapaRenderer;
import graspexercicio10.repository.RankingRepository;
import graspexercicio10.service.JogoService;
import graspexercicio10.service.RankingService;
import java.nio.file.Paths;
import java.util.Scanner;

/** Ponto de composição: escolhe as implementações e conecta as dependências. */
public class Main {
    public static void main(String[] args) {
        RankingService ranking = new RankingService(new RankingRepository(Paths.get("ranking-grasp.json")));
        GameController controller = new GameController(new JogoService(), ranking);
        try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
            new ConsoleJogo(controller, new MapaRenderer(System.out), scanner).iniciar();
        }
    }
}
