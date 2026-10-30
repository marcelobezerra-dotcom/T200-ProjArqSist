package br.edu.unifor.missaomarte.lwjgl3;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.RankingService;
import br.edu.unifor.missaomarte.gdx.MissaoMarteGame;
import br.edu.unifor.missaomarte.infrastructure.ranking.FileRankingRepository;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Composition root do desktop: monta as dependencias, configura a janela e inicia o jogo.
 * <p>Padrao: Injecao de Dependencia manual (como o antigo Main). Nao contem regra de jogo.
 */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        MissaoMarteGame jogo = new MissaoMarteGame(
                new MissaoFactory(),
                new RankingService(new FileRankingRepository(Paths.get("ranking.json"))),
                new Random());

        Lwjgl3ApplicationConfiguration configuracao = new Lwjgl3ApplicationConfiguration();
        configuracao.setTitle("Missao Marte");
        configuracao.setWindowedMode(800, 600);
        configuracao.useVsync(true);
        configuracao.setForegroundFPS(60);

        new Lwjgl3Application(jogo, configuracao);
    }
}
