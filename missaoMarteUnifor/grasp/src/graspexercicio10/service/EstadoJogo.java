package graspexercicio10.service;

import graspexercicio10.model.Dificuldade;
import graspexercicio10.model.StatusPartida;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Protected Variations: contrato de leitura compartilhado pelo console e futura GUI.
 * Não entrega Missao/Nave mutáveis à apresentação. Cada consulta produz uma cópia.
 */
public final class EstadoJogo {
    public final StatusPartida status;
    public final String piloto;
    public final Dificuldade dificuldade;
    public final int raioMapa;
    public final int pontos;
    public final int movimentos;
    public final int vidas;
    public final int capacidade;
    public final int embarcados;
    public final int restantes;
    public final long tempoSegundos;
    public final List<EntidadeEstado> entidades;

    public EstadoJogo(StatusPartida status, String piloto, Dificuldade dificuldade,
                      int raioMapa, int pontos, int movimentos, int vidas, int capacidade,
                      int embarcados, int restantes, long tempoSegundos,
                      List<EntidadeEstado> entidades) {
        this.status = status;
        this.piloto = piloto;
        this.dificuldade = dificuldade;
        this.raioMapa = raioMapa;
        this.pontos = pontos;
        this.movimentos = movimentos;
        this.vidas = vidas;
        this.capacidade = capacidade;
        this.embarcados = embarcados;
        this.restantes = restantes;
        this.tempoSegundos = tempoSegundos;
        this.entidades = Collections.unmodifiableList(new ArrayList<>(entidades));
    }
}
