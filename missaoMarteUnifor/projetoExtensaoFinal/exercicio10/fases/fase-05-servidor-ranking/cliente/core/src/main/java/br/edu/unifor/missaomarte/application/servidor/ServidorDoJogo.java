package br.edu.unifor.missaomarte.application.servidor;

import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import java.util.List;

/**
 * Porta para o servidor de ranking (Port and Adapter): a aplicacao so conhece esta interface, nao HTTP nem libGDX.
 * <p>Todas as chamadas sao assincronas; o resultado chega ao {@link Retorno} na thread de desenho do jogo.
 * <p>SOLID: DIP (as telas dependem da porta) e ISP (so o que o jogo usa).
 */
public interface ServidorDoJogo {
    /** Cria a conta. Nao entra automaticamente: a tela chama {@link #entrar} em seguida. */
    void cadastrar(String nome, String senha, Retorno<Void> retorno);

    void entrar(String nome, String senha, Retorno<Sessao> retorno);

    void enviarPartida(Sessao sessao, PartidaConcluida partida, Retorno<Void> retorno);

    void carregarRanking(Sessao sessao, Retorno<List<RankingEntry>> retorno);

    void carregarHistorico(Sessao sessao, Retorno<List<PartidaRegistrada>> retorno);
}
