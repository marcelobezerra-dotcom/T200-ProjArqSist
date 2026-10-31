package br.edu.unifor.missaomarte.servidor.auth;

import java.time.Instant;

/** Resposta do login: o cliente envia "Authorization: Bearer {token}" nas chamadas seguintes. */
public record TokenResposta(String token, String tipo, Instant expiraEm) {
}
