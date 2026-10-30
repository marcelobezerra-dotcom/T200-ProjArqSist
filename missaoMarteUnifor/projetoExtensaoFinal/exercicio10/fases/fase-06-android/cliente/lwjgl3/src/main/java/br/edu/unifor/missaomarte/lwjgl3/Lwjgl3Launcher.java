package br.edu.unifor.missaomarte.lwjgl3;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.RankingService;
import br.edu.unifor.missaomarte.application.servidor.ConfiguracaoDoServidor;
import br.edu.unifor.missaomarte.application.servidor.ConfiguracaoInvalidaException;
import br.edu.unifor.missaomarte.application.servidor.EnderecoDoServidor;
import br.edu.unifor.missaomarte.application.servidor.RepositorioDeConfiguracaoDoServidor;
import br.edu.unifor.missaomarte.gdx.MissaoMarteGame;
import br.edu.unifor.missaomarte.gdx.plataforma.Plataforma;
import br.edu.unifor.missaomarte.gdx.plataforma.PreferenciasComoArmazem;
import br.edu.unifor.missaomarte.gdx.rede.ServidorHttp;
import br.edu.unifor.missaomarte.infrastructure.ranking.FileRankingRepository;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Composition root do desktop: monta as dependencias, configura a janela e inicia o jogo.
 * <p>Padrao: Injecao de Dependencia manual (como o antigo Main). Nao contem regra de jogo.
 * <p>Propriedades: missaomarte.semente (cenario repetivel), missaomarte.servidor (endereco padrao do servidor; o que o jogador
 * salvar na tela Servidor vale mais), missaomarte.semaudio e missaomarte.toque (mouse no lugar do dedo, com botoes virtuais).
 */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        // -Dmissaomarte.semente=123 repete exatamente o mesmo cenario e os mesmos movimentos dos inimigos.
        Long semente = Long.getLong("missaomarte.semente");
        Random random = semente == null ? new Random() : new Random(semente);

        // -Dmissaomarte.servidor=http://host:porta e o endereco padrao; o salvo pelo jogador na tela Servidor prevalece.
        ConfiguracaoDoServidor padrao;
        try {
            padrao = ConfiguracaoDoServidor.deUrl(System.getProperty("missaomarte.servidor", ConfiguracaoDoServidor.PADRAO.urlBase()));
        } catch (ConfiguracaoInvalidaException e) {
            System.err.println("missaomarte.servidor: " + e.getMessage());
            System.exit(1);
            return;
        }
        EnderecoDoServidor endereco = new EnderecoDoServidor(
                new RepositorioDeConfiguracaoDoServidor(new PreferenciasComoArmazem("missaomarte")), padrao);

        // -Dmissaomarte.toque=true liga o modo toque no desktop: o mouse faz o papel do dedo.
        boolean toque = Boolean.getBoolean("missaomarte.toque");

        MissaoMarteGame jogo = new MissaoMarteGame(
                new MissaoFactory(),
                new RankingService(new FileRankingRepository(Paths.get("ranking.json"))),
                new ServidorHttp(endereco),
                endereco,
                new Plataforma(toque, false),
                random);

        Lwjgl3ApplicationConfiguration configuracao = new Lwjgl3ApplicationConfiguration();
        configuracao.setTitle("Missao Marte");
        configuracao.setWindowedMode(toque ? 1200 : 800, toque ? 540 : 600);
        configuracao.useVsync(true);
        configuracao.setForegroundFPS(60);
        configuracao.disableAudio(Boolean.getBoolean("missaomarte.semaudio"));

        new Lwjgl3Application(jogo, configuracao);
    }
}
