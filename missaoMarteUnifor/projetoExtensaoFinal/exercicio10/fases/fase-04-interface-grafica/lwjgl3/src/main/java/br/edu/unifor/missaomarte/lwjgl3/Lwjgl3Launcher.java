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
        // -Dmissaomarte.semente=123 repete exatamente o mesmo cenario e os mesmos movimentos dos inimigos.
        Long semente = Long.getLong("missaomarte.semente");
        Random random = semente == null ? new Random() : new Random(semente);

        MissaoMarteGame jogo = new MissaoMarteGame(
                new MissaoFactory(),
                new RankingService(new FileRankingRepository(Paths.get("ranking.json"))),
                random);

        Lwjgl3ApplicationConfiguration configuracao = new Lwjgl3ApplicationConfiguration();
        configuracao.setTitle("Missao Marte");
        configuracao.setWindowedMode(800, 600);
        configuracao.useVsync(true);
        configuracao.setForegroundFPS(60);
        configuracao.disableAudio(Boolean.getBoolean("missaomarte.semaudio"));

        new Lwjgl3Application(jogo, configuracao);
    }
}
