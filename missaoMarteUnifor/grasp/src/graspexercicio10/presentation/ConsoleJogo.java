package graspexercicio10.presentation;

import graspexercicio10.controller.GameController;
import graspexercicio10.model.AcaoJogo;
import graspexercicio10.model.Dificuldade;
import graspexercicio10.model.StatusPartida;
import java.io.UncheckedIOException;
import java.util.Scanner;

/** Adaptador textual: traduz entradas do console em eventos para o GRASP Controller. */
public class ConsoleJogo {
    private final GameController controller;
    private final MapaRenderer renderer;
    private final Scanner scanner;

    public ConsoleJogo(GameController controller, MapaRenderer renderer, Scanner scanner) {
        this.controller = controller;
        this.renderer = renderer;
        this.scanner = scanner;
    }

    public void iniciar() {
        renderer.exibirBoasVindas();
        boolean rodando = true;
        while (rodando) {
            renderer.exibirMenu();
            String opcao = lerLinha("Escolha uma opção: ");
            if (opcao == null) break;
            try {
                switch (opcao) {
                    case "1": jogarPartida(); break;
                    case "2": renderer.exibirRanking(controller.consultarRanking()); break;
                    case "3": resetarRanking(); break;
                    case "4": rodando = false; break;
                    default: renderer.mensagem("Opção inválida.");
                }
            } catch (UncheckedIOException e) {
                renderer.mensagem(e.getMessage());
            }
        }
        renderer.mensagem("Até logo, piloto!");
    }

    private void jogarPartida() {
        String nome = lerLinha("Nome do piloto: ");
        if (nome == null) return;
        String textoDificuldade = lerLinha("Dificuldade (facil/medio/dificil): ");
        if (textoDificuldade == null) return;
        Dificuldade dificuldade = Dificuldade.deString(textoDificuldade);
        String raio = lerLinha("Raio do mapa de 2 a 20 (Enter = 5): ");
        if (raio == null) return;
        try {
            controller.iniciarPartida(nome, dificuldade, raio.isEmpty() ? 5 : Integer.parseInt(raio));
        } catch (IllegalArgumentException e) {
            renderer.mensagem("Configuração inválida: informe um raio inteiro entre 2 e 20.");
            return;
        }
        while (controller.consultarEstado().status == StatusPartida.EM_ANDAMENTO) {
            renderer.desenharMapa(controller.consultarEstado());
            String entrada = lerLinha("Comando: ");
            if (entrada == null) {
                controller.executar(AcaoJogo.ABORTAR);
                break;
            }
            AcaoJogo acao = traduzir(entrada);
            if (acao == null) {
                renderer.mensagem("Comando inválido (use w/s/a/d/c/q).");
                continue;
            }
            try {
                renderer.exibirEvento(controller.executar(acao));
            } catch (UncheckedIOException e) {
                // A vitória continua válida mesmo se o armazenamento falhar.
                renderer.mensagem("Resultado: " + controller.consultarEstado().status + ". " + e.getMessage());
            }
        }
        renderer.exibirEstatisticas(controller.consultarEstado());
    }

    private void resetarRanking() {
        String resposta = lerLinha("Limpar ranking? (s/n): ");
        if (resposta != null && (resposta.equalsIgnoreCase("s") || resposta.equalsIgnoreCase("sim"))) {
            controller.resetarRanking();
            renderer.mensagem("Ranking resetado.");
        }
    }

    private String lerLinha(String prompt) {
        renderer.solicitar(prompt);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }

    private AcaoJogo traduzir(String texto) {
        switch (texto.toLowerCase(java.util.Locale.ROOT)) {
            case "w": return AcaoJogo.CIMA;
            case "s": return AcaoJogo.BAIXO;
            case "a": return AcaoJogo.ESQUERDA;
            case "d": return AcaoJogo.DIREITA;
            case "c": return AcaoJogo.EMBARCAR;
            case "q": return AcaoJogo.ABORTAR;
            default: return null;
        }
    }
}
