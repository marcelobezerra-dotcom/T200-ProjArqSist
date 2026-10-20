package graspexercicio10.presentation;

import graspexercicio10.model.EventoJogo;
import graspexercicio10.repository.RankingEntry;
import graspexercicio10.service.EntidadeEstado;
import graspexercicio10.service.EstadoJogo;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

/** Pure Fabrication / High Cohesion: traduz o estado e os eventos em saída textual. */
public class MapaRenderer {
    private final PrintStream saida;

    public MapaRenderer(PrintStream saida) {
        this.saida = saida;
    }

    public void exibirBoasVindas() {
        saida.println("=== MISSÃO MARTE UNIFOR — GRASP ===");
        saida.println("Salve os passageiros e retorne à base!");
    }

    public void exibirMenu() {
        saida.println("\n1. Nova missão | 2. Ranking Top 5 | 3. Limpar ranking | 4. Sair");
    }

    public void mensagem(String texto) { saida.println(texto); }
    public void solicitar(String texto) { saida.print(texto); }

    public void desenharMapa(EstadoJogo estado) {
        saida.printf("%nPiloto: %s | Pontos: %d | Vidas: %d | A bordo: %d/%d | Restantes: %d%n",
                estado.piloto, estado.pontos, estado.vidas, estado.embarcados, estado.capacidade, estado.restantes);
        int raio = estado.raioMapa;
        saida.print("    ");
        for (int x = -raio; x <= raio; x++) saida.printf("%3d", x);
        saida.println();
        for (int y = -raio; y <= raio; y++) {
            saida.printf("%3d|", y);
            for (int x = -raio; x <= raio; x++) saida.printf("%3c", resolverSimbolo(estado, x, y));
            saida.println();
        }
        saida.println(estado.entidades.stream()
                .map(e -> e.simbolo + "=" + e.descricao).distinct().collect(Collectors.joining(" ")));
        saida.println("w/s/a/d: mover | c: embarcar | q: abortar");
        for (EntidadeEstado e : estado.entidades) {
            if (e.resgatavel) {
                saida.printf(" - %s em (%d,%d)%n", e.nome, e.x, e.y);
            }
        }
    }

    public void exibirEvento(EventoJogo evento) {
        switch (evento) {
            case MOVIMENTO: break;
            case LIMITE_MAPA: mensagem("Limite do mapa: movimento não realizado."); break;
            case EMBARCADO: mensagem("Passageiro embarcado! Pontos adicionados."); break;
            case NENHUM_PASSAGEIRO: mensagem("Nenhum passageiro nesta posição."); break;
            case NAVE_CHEIA: mensagem("Nave cheia!"); break;
            case COLISAO: mensagem("Colisão! A nave perdeu uma vida."); break;
            case NAVE_DESTRUIDA: mensagem("GAME OVER! A nave foi destruída."); break;
            case SEM_COMBUSTIVEL: mensagem("Combustível esgotado! Missão perdida."); break;
            case RETORNE_BASE: mensagem("Todos embarcados! Retorne à base (0,0)."); break;
            case VITORIA: mensagem("MISSÃO CUMPRIDA! Todos resgatados e nave na base."); break;
            case ABORTADA: mensagem("Missão abortada pelo piloto."); break;
            case PARTIDA_INATIVA: mensagem("Não há partida em andamento."); break;
            default: throw new IllegalArgumentException("Evento sem apresentação: " + evento);
        }
    }

    public void exibirEstatisticas(EstadoJogo estado) {
        saida.printf("Resultado: %s | Pontos: %d | Movimentos: %d | Tempo: %ds | Embarcados: %d%n",
                estado.status, estado.pontos, estado.movimentos, estado.tempoSegundos, estado.embarcados);
    }

    public void exibirRanking(List<RankingEntry> ranking) {
        mensagem("\n====== RANKING TOP 5 ======");
        if (ranking.isEmpty()) mensagem("Nenhum registro. Seja o primeiro!");
        int posicao = 1;
        for (RankingEntry e : ranking) {
            saida.printf("%d. %s | %d pts | %s | %d passageiros | %ds | %s%n",
                    posicao++, e.nome, e.score, e.dificuldade, e.passageirosColetados, e.tempoJogo, e.dataHora);
        }
    }

    private char resolverSimbolo(EstadoJogo estado, int x, int y) {
        // A ordem do snapshot dá prioridade à nave quando há entidades na mesma célula.
        for (EntidadeEstado e : estado.entidades) {
            if (e.x == x && e.y == y) return e.simbolo;
        }
        return '.';
    }

}
