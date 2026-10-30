package br.edu.unifor.missaomarte.android;

import android.os.Bundle;
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
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import java.io.File;
import java.util.Random;

/**
 * Composition root do Android: monta as mesmas dependencias do desktop, com o modo toque e a caixa de texto nativa ligados.
 * <p>O endereco padrao do servidor vem do build ({@code -PservidorPadrao=...}); o que o jogador salvar na tela Servidor vale mais.
 * O ranking local fica na pasta privada do app.
 */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConfiguracaoDoServidor padrao;
        try {
            padrao = ConfiguracaoDoServidor.deUrl(BuildConfig.SERVIDOR_PADRAO);
        } catch (ConfiguracaoInvalidaException e) {
            padrao = ConfiguracaoDoServidor.PADRAO;
        }
        EnderecoDoServidor endereco = new EnderecoDoServidor(
                new RepositorioDeConfiguracaoDoServidor(new PreferenciasComoArmazem("missaomarte")), padrao);

        MissaoMarteGame jogo = new MissaoMarteGame(
                new MissaoFactory(),
                new RankingService(new FileRankingRepository(new File(getFilesDir(), "ranking.json").toPath())),
                new ServidorHttp(endereco),
                endereco,
                new Plataforma(true, true),
                new Random());

        AndroidApplicationConfiguration configuracao = new AndroidApplicationConfiguration();
        configuracao.useAccelerometer = false;
        configuracao.useCompass = false;
        configuracao.useImmersiveMode = true;
        initialize(jogo, configuracao);
    }
}
