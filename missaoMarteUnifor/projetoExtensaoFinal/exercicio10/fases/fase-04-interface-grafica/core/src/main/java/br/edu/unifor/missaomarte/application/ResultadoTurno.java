package br.edu.unifor.missaomarte.application;

import java.util.Collections;
import java.util.List;

/**
 * Resultado de um turno: estado da partida, mensagens para o jogador e eventos ocorridos.
 * <p>Padrao: Parameter Object/DTO imutavel. A regra devolve dados e nao imprime (SRP, Low Coupling com a interface).
 * <p>GRASP: Pure Fabrication. Os eventos sao aditivos: o construtor de dois argumentos continua valido (sem eventos).
 */
public class ResultadoTurno {
    private final EstadoPartida estado;
    private final List<String> mensagens;
    private final List<EventoJogo> eventos;

    public ResultadoTurno(EstadoPartida estado, List<String> mensagens) {
        this(estado, mensagens, Collections.emptyList());
    }

    public ResultadoTurno(EstadoPartida estado, List<String> mensagens, List<EventoJogo> eventos) {
        this.estado = estado;
        this.mensagens = mensagens;
        this.eventos = eventos;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public List<String> getMensagens() {
        return Collections.unmodifiableList(mensagens);
    }

    public List<EventoJogo> getEventos() {
        return Collections.unmodifiableList(eventos);
    }
}
