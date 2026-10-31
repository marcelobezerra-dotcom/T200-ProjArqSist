package br.edu.unifor.missaomarte.servidor.ranking;

import br.edu.unifor.missaomarte.servidor.partida.Dificuldade;
import br.edu.unifor.missaomarte.servidor.partida.Partida;
import java.time.Instant;

/** Uma linha do Top 10. Sem id nem dados sensiveis do usuario. */
public record RankingResposta(
        String nome,
        int pontuacao,
        long duracaoSegundos,
        int movimentos,
        Dificuldade dificuldade,
        Instant jogadaEm) {

    public static RankingResposta de(Partida p) {
        return new RankingResposta(p.getUsuario().getNome(), p.getPontuacao(), p.getDuracaoSegundos(),
                p.getMovimentos(), p.getDificuldade(), p.getJogadaEm());
    }
}
