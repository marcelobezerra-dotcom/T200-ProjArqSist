package br.edu.unifor.missaomarte.application;

import java.util.Collections;
import java.util.List;

/**
 * Resultado de um turno: estado da partida e mensagens para o jogador.
 * <p>Padrao: Parameter Object/DTO imutavel. A regra devolve dados e nao imprime (SRP, Low Coupling com o console).
 * <p>GRASP: Pure Fabrication.
 */
public class ResultadoTurno {
    private final EstadoPartida estado;
    private final List<String> mensagens;

    public ResultadoTurno(EstadoPartida estado, List<String> mensagens) {
        this.estado = estado;
        this.mensagens = mensagens;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public List<String> getMensagens() {
        return Collections.unmodifiableList(mensagens);
    }
}
