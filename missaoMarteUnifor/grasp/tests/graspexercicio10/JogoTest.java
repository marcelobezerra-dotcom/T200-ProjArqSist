package graspexercicio10;

import graspexercicio10.controller.GameController;
import graspexercicio10.model.*;
import graspexercicio10.presentation.ConsoleJogo;
import graspexercicio10.presentation.MapaRenderer;
import graspexercicio10.repository.IRankingRepository;
import graspexercicio10.repository.RankingEntry;
import graspexercicio10.repository.RankingRepository;
import graspexercicio10.service.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/** Testes de comportamento sem framework, janela, entrada interativa ou ranking real. */
public class JogoTest {
    public static void main(String[] args) throws Exception {
        configuracaoECriacao();
        limitesESnapshot();
        embarquePolimorfico();
        capacidadeDaNave();
        vitoriaERankingUnico();
        derrotaPorVidas();
        colisaoComCombustivelEsgotado();
        abortoEReinicio();
        rankingTopCinco();
        persistencia();
        falhaAoSalvarVitoria();
        consoleSemEntrada();
        extensaoSemAlterarRenderer();
        movimentoDosElementos();
        System.out.println("14 cenários de comportamento passaram.");
    }

    private static void configuracaoECriacao() {
        JogoService jogo = new JogoService();
        verificar(jogo.consultarEstado().status == StatusPartida.NAO_INICIADA, "estado inicial");
        verificar(jogo.executar(AcaoJogo.CIMA) == EventoJogo.PARTIDA_INATIVA, "ação antes de iniciar");
        verificar(jogo.consultarEstado().tempoSegundos == 0, "tempo antes de iniciar");
        for (int raio : new int[]{-1, 0, 1, 21}) {
            esperar(IllegalArgumentException.class, () -> jogo.iniciarPartida("P", Dificuldade.FACIL, raio));
        }
        FabricaMissao fabrica = new FabricaMissao();
        // Até uma fonte aleatória degenerada termina: não há repetição de tentativas.
        Random constante = new Random(0) {
            @Override public int nextInt(int limite) { return 0; }
        };
        for (Dificuldade dificuldade : Dificuldade.values()) {
            Missao missao = fabrica.criar(dificuldade, -2, 2, -2, 2, constante);
            verificar(missao.getPassageiros().size() == dificuldade.getQuantidadePassageiros(), "quantidade de passageiros");
            verificar(missao.getPerigos().size() == dificuldade.getQuantidadeAsteroides()
                    + dificuldade.getQuantidadeInimigos(), "quantidade de perigos");
            Set<String> ocupadas = new HashSet<>();
            ocupadas.add("0,0");
            for (Passageiro p : missao.getPassageiros()) verificar(ocupadas.add(p.getX() + "," + p.getY()), "posição livre");
            for (Perigo p : missao.getPerigos()) verificar(ocupadas.add(p.getX() + "," + p.getY()), "posição livre");
            verificar(missao.posicaoOcupada(0, 0), "nave ocupa a base");
        }
        esperar(IllegalArgumentException.class, () -> fabrica.criar(Dificuldade.DIFICIL, -1, 1, -1, 1, constante));
        jogo.iniciarPartida(" ", Dificuldade.FACIL, 2);
        verificar(jogo.consultarEstado().piloto.equals("Piloto Anônimo"), "nome padrão");
        esperar(IllegalStateException.class, () -> jogo.iniciarPartida("Outro", Dificuldade.MEDIO, 2));
    }

    private static void limitesESnapshot() {
        Missao missao = cenario();
        final int[] atualizacoes = {0};
        missao.addPerigo(new Perigo(2, -2) {
            public boolean colideCom(Nave nave) { return false; }
            public char getSimbolo() { return '!'; }
            public void atualizar(Random r, int minX, int maxX, int minY, int maxY) { atualizacoes[0]++; }
        });
        JogoService jogo = iniciar(missao, new Relogio());
        EstadoJogo anterior = jogo.consultarEstado();
        esperar(UnsupportedOperationException.class, () -> anterior.entidades.clear());
        esperar(UnsupportedOperationException.class, () -> missao.getPassageiros().clear());
        jogo.executar(AcaoJogo.DIREITA);
        jogo.executar(AcaoJogo.DIREITA);
        EstadoJogo limite = jogo.consultarEstado();
        verificar(jogo.executar(AcaoJogo.DIREITA) == EventoJogo.LIMITE_MAPA, "limite do tabuleiro");
        verificar(jogo.consultarEstado().pontos == limite.pontos && jogo.consultarEstado().movimentos == 2,
                "movimento bloqueado não consome turno");
        verificar(atualizacoes[0] == 2, "perigos recebem apenas turnos válidos via polimorfismo");
        verificar(anterior.entidades.get(0).x == 0 && jogo.consultarEstado().entidades.get(0).x == 2,
                "snapshot anterior permanece estável");
        verificar(jogo.executar(AcaoJogo.EMBARCAR) == EventoJogo.NENHUM_PASSAGEIRO, "embarque vazio");
    }

    private static void embarquePolimorfico() {
        Missao missao = new Missao(new Nave("Teste", 5));
        missao.addPassageiro(new Professor("P", 1, 0));
        missao.addPassageiro(new Engenheiro("E", 2, 0));
        missao.addPassageiro(new Astronauta("A", 2, 1));
        JogoService jogo = iniciar(missao, new Relogio());
        jogo.executar(AcaoJogo.DIREITA);
        verificar(jogo.executar(AcaoJogo.EMBARCAR) == EventoJogo.EMBARCADO, "embarque professor");
        verificar(jogo.consultarEstado().pontos == 24, "professor vale 10");
        jogo.executar(AcaoJogo.DIREITA);
        jogo.executar(AcaoJogo.EMBARCAR);
        verificar(jogo.consultarEstado().pontos == 38, "engenheiro vale 15");
        jogo.executar(AcaoJogo.BAIXO);
        verificar(jogo.executar(AcaoJogo.EMBARCAR) == EventoJogo.RETORNE_BASE, "objetivo após último embarque");
        verificar(jogo.consultarEstado().pontos == 57 && jogo.consultarEstado().embarcados == 3,
                "astronauta vale 20 e passageiros são transferidos");
        verificar(jogo.consultarEstado().status == StatusPartida.EM_ANDAMENTO, "vitória exige retorno");
    }

    private static void capacidadeDaNave() {
        Missao missao = new Missao(new Nave("Pequena", 1));
        missao.addPassageiro(new Professor("P", 1, 0));
        missao.addPassageiro(new Engenheiro("E", 2, 0));
        JogoService jogo = iniciar(missao, new Relogio());
        jogo.executar(AcaoJogo.DIREITA);
        jogo.executar(AcaoJogo.EMBARCAR);
        jogo.executar(AcaoJogo.DIREITA);
        int pontos = jogo.consultarEstado().pontos;
        verificar(jogo.executar(AcaoJogo.EMBARCAR) == EventoJogo.NAVE_CHEIA, "capacidade respeitada");
        verificar(jogo.consultarEstado().restantes == 1 && jogo.consultarEstado().pontos == pontos,
                "embarque recusado preserva passageiro e pontuação");
    }

    private static void vitoriaERankingUnico() {
        Relogio clock = new Relogio();
        MemoriaRanking repo = new MemoriaRanking();
        GameController controller = controller(missaoCurta(), clock, repo);
        controller.iniciarPartida("Piloto", Dificuldade.FACIL, 2);
        controller.executar(AcaoJogo.DIREITA);
        controller.executar(AcaoJogo.EMBARCAR);
        clock.avancar(7000);
        verificar(controller.executar(AcaoJogo.ESQUERDA) == EventoJogo.VITORIA, "retorno à base vence");
        verificar(repo.salvamentos == 1 && repo.entradas.get(0).score == 23, "vitória salva uma vez");
        clock.avancar(20000);
        verificar(controller.consultarEstado().tempoSegundos == 7, "tempo final congelado");
        verificar(controller.executar(AcaoJogo.DIREITA) == EventoJogo.PARTIDA_INATIVA, "ação após fim ignorada");
        verificar(repo.salvamentos == 1 && controller.consultarEstado().movimentos == 2, "sem duplicar ranking nem alterar partida");
    }

    private static void derrotaPorVidas() {
        Missao missao = cenario();
        missao.addPerigo(new Asteroide(1, 0));
        JogoService jogo = iniciar(missao, new Relogio());
        for (int i = 0; i < 2; i++) {
            verificar(jogo.executar(AcaoJogo.DIREITA) == EventoJogo.COLISAO, "colisão não fatal");
            jogo.executar(AcaoJogo.ESQUERDA);
        }
        verificar(jogo.executar(AcaoJogo.DIREITA) == EventoJogo.NAVE_DESTRUIDA, "terceira colisão fatal");
        verificar(jogo.consultarEstado().status == StatusPartida.DERROTA && jogo.consultarEstado().vidas == 0, "derrota por vidas");
    }

    private static void colisaoComCombustivelEsgotado() {
        Missao missao = cenario();
        missao.addPerigo(new Perigo(1, 0) {
            private int turnos;
            public boolean colideCom(Nave nave) { return turnos == 15; }
            public char getSimbolo() { return '!'; }
            public void atualizar(Random r, int a, int b, int c, int d) { turnos++; }
        });
        JogoService jogo = iniciar(missao, new Relogio());
        EventoJogo evento = null;
        for (int i = 0; i < 15; i++) evento = jogo.executar(i % 2 == 0 ? AcaoJogo.DIREITA : AcaoJogo.ESQUERDA);
        verificar(evento == EventoJogo.SEM_COMBUSTIVEL, "colisão não adia derrota por combustível");
        verificar(jogo.consultarEstado().status == StatusPartida.DERROTA && jogo.consultarEstado().vidas == 2,
                "colisão e consumo aplicados no mesmo turno");
    }

    private static void abortoEReinicio() {
        Relogio clock = new Relogio();
        GameController controller = controller(cenario(), clock, new MemoriaRanking());
        controller.iniciarPartida("P", Dificuldade.FACIL, 2);
        clock.avancar(3000);
        verificar(controller.executar(AcaoJogo.ABORTAR) == EventoJogo.ABORTADA, "aborto real no modelo");
        clock.avancar(3000);
        verificar(controller.consultarEstado().tempoSegundos == 3, "aborto congela tempo");
        controller.iniciarPartida("Outro", Dificuldade.DIFICIL, 2);
        verificar(controller.consultarEstado().pontos == 30 && controller.consultarEstado().movimentos == 0,
                "reinício limpa estado anterior");
    }

    private static void rankingTopCinco() {
        MemoriaRanking repo = new MemoriaRanking();
        RankingService ranking = new RankingService(repo);
        for (int pontos : new int[]{5, 40, 10, 30, 20, 50}) ranking.registrarVitoria(vitoria(pontos));
        verificar(ranking.consultar().size() == 5 && ranking.consultar().get(0).score == 50, "top cinco ordenado");
        verificar(ranking.consultar().get(4).score == 10, "descarta menor pontuação");
        int salvamentos = repo.salvamentos;
        ranking.registrarVitoria(vitoria(10));
        verificar(repo.salvamentos == salvamentos, "empate com último não substitui registro");
        ranking.resetar();
        verificar(ranking.consultar().isEmpty(), "resetar ranking");
    }

    private static void persistencia() throws Exception {
        Path diretorio = Files.createDirectories(Paths.get("build-grasp"));
        Path arquivo = Files.createTempFile(diretorio, "ranking-teste-", ".json");
        try {
            RankingService ranking = new RankingService(new RankingRepository(arquivo));
            ranking.registrarVitoria(vitoria(25));
            verificar(ranking.consultar().size() == 1 && ranking.consultar().get(0).score == 25, "JSON lê escrita");
            ranking.resetar();
            verificar(!Files.exists(arquivo) && ranking.consultar().isEmpty(), "reset remove apenas arquivo temporário");
        } finally {
            Files.deleteIfExists(arquivo);
        }
    }

    private static void falhaAoSalvarVitoria() {
        MemoriaRanking repo = new MemoriaRanking() {
            @Override public void salvar(List<RankingEntry> entradas) {
                throw new UncheckedIOException(new java.io.IOException("Falha simulada"));
            }
        };
        GameController controller = controller(missaoCurta(), new Relogio(), repo);
        controller.iniciarPartida("P", Dificuldade.FACIL, 2);
        controller.executar(AcaoJogo.DIREITA);
        controller.executar(AcaoJogo.EMBARCAR);
        esperar(UncheckedIOException.class, () -> controller.executar(AcaoJogo.ESQUERDA));
        verificar(controller.consultarEstado().status == StatusPartida.VITORIA, "erro de armazenamento preserva vitória");
    }

    private static void consoleSemEntrada() throws Exception {
        for (String entrada : new String[]{"", "1\nP\nfacil\n2\n", "1\nP\nfacil\n0\n4\n",
                "1\nP\nfacil\n2\ninvalido\nq\n2\n4\n"}) {
            GameController controller = controller(cenario(), new Relogio(), new MemoriaRanking());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (Scanner scanner = new Scanner(entrada); PrintStream saida = new PrintStream(buffer, true, "UTF-8")) {
                new ConsoleJogo(controller, new MapaRenderer(saida), scanner).iniciar();
            }
            verificar(buffer.toString("UTF-8").contains("Até logo"), "console termina com EOF ou saída");
            verificar(controller.consultarEstado().status != StatusPartida.EM_ANDAMENTO, "EOF encerra partida ativa");
        }
    }

    /** Um tipo definido apenas neste teste deve funcionar com o jogo e o renderer existentes. */
    private static void extensaoSemAlterarRenderer() throws Exception {
        Missao missao = new Missao(new Nave("Teste", 5));
        missao.addPassageiro(new Medico("Dra. Ana", 1, 0));
        JogoService jogo = iniciar(missao, new Relogio());
        EstadoJogo estado = jogo.consultarEstado();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream saida = new PrintStream(buffer, true, "UTF-8")) {
            MapaRenderer renderer = new MapaRenderer(saida);
            renderer.desenharMapa(estado);
            verificar(buffer.toString("UTF-8").contains("Dra. Ana em (1,0)"),
                    "novo passageiro listado pela característica resgatável");
            verificar(buffer.toString("UTF-8").contains("@  M"), "renderer usa símbolo polimórfico sem registro");
            verificar(buffer.toString("UTF-8").contains("M=Médico"), "legenda vem do elemento");
        }
        jogo.executar(AcaoJogo.DIREITA);
        verificar(jogo.executar(AcaoJogo.EMBARCAR) == EventoJogo.RETORNE_BASE, "novo passageiro pode embarcar");
        verificar(jogo.consultarEstado().pontos == 39, "pontuação do novo tipo usa polimorfismo");
        verificar(jogo.executar(AcaoJogo.ESQUERDA) == EventoJogo.VITORIA, "novo passageiro participa do objetivo");
    }

    private static void movimentoDosElementos() {
        for (int direcao = 0; direcao < 4; direcao++) {
            final int sorteio = direcao;
            Random random = new Random(0) {
                @Override public int nextInt(int limite) { return sorteio; }
            };
            Inimigo inimigo = new Inimigo(0, 0);
            inimigo.atualizar(random, -2, 2, -2, 2);
            int dx = direcao == 0 ? 1 : direcao == 1 ? -1 : 0;
            int dy = direcao == 2 ? 1 : direcao == 3 ? -1 : 0;
            verificar(inimigo.ocupa(dx, dy), "inimigo avança na direção sorteada");
            for (int turno = 0; turno < 5; turno++) inimigo.atualizar(random, -2, 2, -2, 2);
            verificar(inimigo.ocupa(2 * dx, 2 * dy), "inimigo respeita limite com posição herdada");
        }
        Missao missao = cenario();
        missao.getNave().mover(AcaoJogo.DIREITA, -2, 2, -2, 2);
        verificar(missao.posicaoOcupada(0, 0), "base permanece no mapa quando nave sai");
        verificar(missao.getBase().getSimbolo() == 'L', "base tem representação própria");
        verificar(missao.getElementos().stream().anyMatch(e -> e == missao.getBase()), "base integra os elementos");
        Asteroide asteroide = new Asteroide(1, 0);
        verificar(asteroide.colideCom(missao.getNave()), "perigos compartilham colisão pela posição");
        asteroide.atualizar(new Random(0), -2, 2, -2, 2);
        verificar(asteroide.ocupa(1, 0), "asteroide permanece imóvel");
        esperar(UnsupportedOperationException.class, () -> missao.getElementos().clear());
    }

    private static class Medico extends Passageiro {
        Medico(String nome, int x, int y) { super(nome, "Médico", x, y); }
        public int getPontuacao() { return 25; }
        public char getSimbolo() { return 'M'; }
    }

    private static Missao cenario() {
        Missao missao = new Missao(new Nave("Teste", 5));
        missao.addPassageiro(new Professor("Distante", 2, 2));
        return missao;
    }

    private static Missao missaoCurta() {
        Missao missao = new Missao(new Nave("Teste", 5));
        missao.addPassageiro(new Professor("P", 1, 0));
        return missao;
    }

    private static FabricaMissao fabrica(Missao missao) {
        return new FabricaMissao() {
            @Override public Missao criar(Dificuldade d, int a, int b, int c, int e, Random r) { return missao; }
        };
    }

    private static JogoService iniciar(Missao missao, Clock clock) {
        JogoService jogo = new JogoService(fabrica(missao), new Random(42), clock);
        jogo.iniciarPartida("P", Dificuldade.FACIL, 2);
        return jogo;
    }

    private static GameController controller(Missao missao, Clock clock, IRankingRepository repo) {
        return new GameController(new JogoService(fabrica(missao), new Random(42), clock), new RankingService(repo, clock));
    }

    private static EstadoJogo vitoria(int pontos) {
        return new EstadoJogo(StatusPartida.VITORIA, "Piloto", Dificuldade.FACIL, 2,
                pontos, 2, 3, 5, 1, 0, 7, new ArrayList<>());
    }

    private static void verificar(boolean condicao, String descricao) {
        if (!condicao) throw new AssertionError(descricao);
    }

    private static void esperar(Class<? extends Throwable> tipo, Runnable operacao) {
        try { operacao.run(); }
        catch (Throwable e) {
            if (tipo.isInstance(e)) return;
            throw new AssertionError("Exceção inesperada", e);
        }
        throw new AssertionError("Esperava " + tipo.getSimpleName());
    }

    private static class MemoriaRanking implements IRankingRepository {
        List<RankingEntry> entradas = new ArrayList<>();
        int salvamentos;
        public List<RankingEntry> carregar() { return new ArrayList<>(entradas); }
        public void salvar(List<RankingEntry> ranking) { entradas = new ArrayList<>(ranking); salvamentos++; }
        public void resetar() { entradas.clear(); }
    }

    private static class Relogio extends Clock {
        private long millis;
        void avancar(long tempo) { millis += tempo; }
        public ZoneId getZone() { return ZoneOffset.UTC; }
        public Clock withZone(ZoneId zona) { return Clock.fixed(instant(), zona); }
        public Instant instant() { return Instant.ofEpochMilli(millis); }
        public long millis() { return millis; }
    }
}
