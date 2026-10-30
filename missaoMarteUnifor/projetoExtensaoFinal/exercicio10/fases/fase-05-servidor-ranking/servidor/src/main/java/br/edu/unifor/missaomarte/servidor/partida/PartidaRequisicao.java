package br.edu.unifor.missaomarte.servidor.partida;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Corpo de POST /api/partidas. O usuario NAO vem no corpo: sai do token.
 * Os limites abaixo so barram valores absurdos; o servidor nao tem como provar que a pontuacao e verdadeira.
 */
public record PartidaRequisicao(
        @NotNull(message = "Informe a dificuldade.")
        Dificuldade dificuldade,

        @NotNull(message = "Informe o resultado.")
        Resultado resultado,

        @NotNull(message = "Informe a pontuação.")
        @Min(value = -100_000, message = "Pontuação fora da faixa.")
        @Max(value = 100_000, message = "Pontuação fora da faixa.")
        Integer pontuacao,

        @NotNull(message = "Informe a duração.")
        @Min(value = 0, message = "Duração inválida.")
        @Max(value = 86_400, message = "Duração inválida.")
        Long duracaoSegundos,

        @NotNull(message = "Informe os movimentos.")
        @Min(value = 0, message = "Número de movimentos inválido.")
        @Max(value = 100_000, message = "Número de movimentos inválido.")
        Integer movimentos) {
}
