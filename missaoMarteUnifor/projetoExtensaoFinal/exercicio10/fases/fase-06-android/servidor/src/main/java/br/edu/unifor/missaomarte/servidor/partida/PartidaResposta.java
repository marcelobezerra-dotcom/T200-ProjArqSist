package br.edu.unifor.missaomarte.servidor.partida;

import java.time.Instant;

public record PartidaResposta(
        Long id,
        Dificuldade dificuldade,
        Resultado resultado,
        int pontuacao,
        long duracaoSegundos,
        int movimentos,
        Instant jogadaEm) {

    public static PartidaResposta de(Partida p) {
        return new PartidaResposta(p.getId(), p.getDificuldade(), p.getResultado(), p.getPontuacao(),
                p.getDuracaoSegundos(), p.getMovimentos(), p.getJogadaEm());
    }
}
