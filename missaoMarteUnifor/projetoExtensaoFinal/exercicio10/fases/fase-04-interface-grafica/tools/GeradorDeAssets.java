import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.function.BiConsumer;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Gera as imagens (PNG) e os sons (WAV) do jogo por codigo, sem arquivos externos.
 * Uso, na pasta da fase: java tools/GeradorDeAssets.java [pasta-de-destino]   (padrao: core/assets)
 * As dimensoes e a quantidade de quadros devem coincidir com o enum Imagem do modulo core
 * (um teste confere). Para usar arte propria, basta substituir os arquivos mantendo nome e dimensoes.
 */
public class GeradorDeAssets {
    private static final int T = 48;
    private static final float TAXA = 22050f;

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        Path destino = Paths.get(args.length > 0 ? args[0] : "core/assets");
        Path imagens = destino.resolve("imagens");
        Path sons = destino.resolve("sons");
        Files.createDirectories(imagens);
        Files.createDirectories(sons);
        gerarImagens(imagens);
        gerarSons(sons);
        System.out.println("Assets gerados em " + destino.toAbsolutePath());
    }

    // ---------------------------------------------------------------- imagens

    private static void gerarImagens(Path pasta) throws IOException {
        salvar(fundo(), pasta.resolve("fundo.png"));
        salvar(terreno(), pasta.resolve("terreno.png"));
        salvar(faixa(1, T, T, (g, q) -> nave(g)), pasta.resolve("nave.png"));
        salvar(faixa(4, T, T, GeradorDeAssets::propulsor), pasta.resolve("propulsor.png"));
        salvar(faixa(4, T, T, GeradorDeAssets::base), pasta.resolve("base.png"));
        salvar(faixa(1, T, T, (g, q) -> asteroide(g)), pasta.resolve("asteroide.png"));
        salvar(faixa(2, T, T, GeradorDeAssets::inimigo), pasta.resolve("inimigo.png"));
        salvar(faixa(1, T, T, (g, q) -> passageiro(g, 0)), pasta.resolve("passageiro_professor.png"));
        salvar(faixa(1, T, T, (g, q) -> passageiro(g, 1)), pasta.resolve("passageiro_engenheiro.png"));
        salvar(faixa(1, T, T, (g, q) -> passageiro(g, 2)), pasta.resolve("passageiro_astronauta.png"));
        salvar(faixa(1, T, T, (g, q) -> desconhecido(g)), pasta.resolve("desconhecido.png"));
        salvar(faixa(6, T, T, GeradorDeAssets::explosao), pasta.resolve("explosao.png"));
        salvar(faixa(5, T, T, GeradorDeAssets::brilho), pasta.resolve("brilho.png"));
        salvar(faixa(1, 24, 24, (g, q) -> coracao(g, true)), pasta.resolve("icone_vida.png"));
        salvar(faixa(1, 24, 24, (g, q) -> coracao(g, false)), pasta.resolve("icone_vida_vazia.png"));
        salvar(faixa(1, 24, 24, (g, q) -> iconePassageiro(g, true)), pasta.resolve("icone_passageiro.png"));
        salvar(faixa(1, 24, 24, (g, q) -> iconePassageiro(g, false)), pasta.resolve("icone_vaga.png"));
    }

    private static BufferedImage nova(int largura, int altura) {
        return new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
    }

    private static Graphics2D grafico(BufferedImage imagem) {
        Graphics2D g = imagem.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return g;
    }

    private static void salvar(BufferedImage imagem, Path arquivo) throws IOException {
        ImageIO.write(imagem, "png", arquivo.toFile());
    }

    /** Faixa horizontal com varios quadros do mesmo tamanho, lado a lado. */
    private static BufferedImage faixa(int quadros, int largura, int altura, BiConsumer<Graphics2D, Integer> desenho) {
        BufferedImage imagem = nova(largura * quadros, altura);
        for (int q = 0; q < quadros; q++) {
            Graphics2D g = grafico(imagem);
            g.translate(q * largura, 0);
            g.setClip(0, 0, largura, altura);
            desenho.accept(g, q);
            g.dispose();
        }
        return imagem;
    }

    private static int limitar(int v) {
        return Math.max(0, Math.min(255, v));
    }

    private static BufferedImage fundo() {
        BufferedImage imagem = nova(800, 600);
        Graphics2D g = grafico(imagem);
        g.setPaint(new GradientPaint(0, 0, new Color(6, 6, 24), 0, 600, new Color(26, 8, 22)));
        g.fillRect(0, 0, 800, 600);
        nebulosa(g, 150, 480, 280, new Color(150, 40, 60, 70));
        nebulosa(g, 660, 110, 230, new Color(70, 50, 150, 60));
        Random r = new Random(42);
        for (int i = 0; i < 260; i++) {
            int x = r.nextInt(800);
            int y = r.nextInt(600);
            int brilho = 120 + r.nextInt(135);
            int tamanho = r.nextInt(10) == 0 ? 3 : (r.nextInt(3) == 0 ? 2 : 1);
            g.setColor(new Color(brilho, brilho, Math.min(255, brilho + 20), 80 + r.nextInt(150)));
            g.fillOval(x, y, tamanho, tamanho);
        }
        g.dispose();
        return imagem;
    }

    private static void nebulosa(Graphics2D g, int cx, int cy, int raio, Color cor) {
        Color transparente = new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 0);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(cx, cy), raio, new float[] {0f, 1f}, new Color[] {cor, transparente}));
        g.fill(new Ellipse2D.Double(cx - raio, cy - raio, raio * 2, raio * 2));
    }

    private static BufferedImage terreno() {
        BufferedImage imagem = nova(T, T);
        Graphics2D g = grafico(imagem);
        g.setColor(new Color(74, 36, 28));
        g.fillRect(0, 0, T, T);
        Random r = new Random(7);
        for (int i = 0; i < 70; i++) {
            int v = r.nextInt(30) - 15;
            g.setColor(new Color(limitar(74 + v), limitar(36 + v / 2), limitar(28 + v / 2)));
            g.fillRect(r.nextInt(T), r.nextInt(T), 1 + r.nextInt(2), 1 + r.nextInt(2));
        }
        g.setColor(new Color(25, 10, 8, 170));
        g.drawRect(0, 0, T - 1, T - 1);
        g.dispose();
        return imagem;
    }

    private static void nave(Graphics2D g) {
        Path2D asaEsquerda = new Path2D.Double();
        asaEsquerda.moveTo(18, 26);
        asaEsquerda.lineTo(5, 41);
        asaEsquerda.lineTo(5, 45);
        asaEsquerda.lineTo(18, 39);
        asaEsquerda.closePath();
        Path2D asaDireita = new Path2D.Double();
        asaDireita.moveTo(30, 26);
        asaDireita.lineTo(43, 41);
        asaDireita.lineTo(43, 45);
        asaDireita.lineTo(30, 39);
        asaDireita.closePath();
        g.setColor(new Color(205, 70, 60));
        g.fill(asaEsquerda);
        g.fill(asaDireita);
        g.setColor(new Color(60, 20, 20));
        g.setStroke(new BasicStroke(1.2f));
        g.draw(asaEsquerda);
        g.draw(asaDireita);

        Path2D corpo = new Path2D.Double();
        corpo.moveTo(24, 3);
        corpo.lineTo(32, 18);
        corpo.lineTo(32, 38);
        corpo.lineTo(16, 38);
        corpo.lineTo(16, 18);
        corpo.closePath();
        g.setPaint(new GradientPaint(16, 0, new Color(240, 242, 248), 32, 0, new Color(140, 150, 175)));
        g.fill(corpo);
        g.setColor(new Color(40, 45, 60));
        g.draw(corpo);

        g.setColor(new Color(60, 200, 240));
        g.fill(new Ellipse2D.Double(20, 12, 8, 12));
        g.setColor(new Color(255, 255, 255, 170));
        g.fill(new Ellipse2D.Double(21.5, 14, 2.5, 4));
        g.setColor(new Color(60, 60, 72));
        g.fillRect(20, 38, 8, 4);
    }

    private static void propulsor(Graphics2D g, int quadro) {
        int comprimento = new int[] {3, 5, 4, 6}[quadro];
        Path2D externa = new Path2D.Double();
        externa.moveTo(20, 42);
        externa.lineTo(28, 42);
        externa.lineTo(24, 42 + comprimento);
        externa.closePath();
        g.setPaint(new GradientPaint(0, 42, new Color(255, 230, 110), 0, 48, new Color(255, 90, 20)));
        g.fill(externa);
        Path2D interna = new Path2D.Double();
        interna.moveTo(22, 42);
        interna.lineTo(26, 42);
        interna.lineTo(24, 42 + comprimento * 0.6);
        interna.closePath();
        g.setColor(new Color(255, 255, 220));
        g.fill(interna);
    }

    private static void base(Graphics2D g, int quadro) {
        g.setColor(new Color(72, 78, 94));
        g.fill(new Ellipse2D.Double(3, 3, 42, 42));
        g.setColor(new Color(38, 42, 54));
        g.fill(new Ellipse2D.Double(8, 8, 32, 32));
        double pulso = 0.5 + 0.5 * Math.sin(quadro * Math.PI / 2);
        g.setColor(new Color(90, 220, 140, (int) (110 + 145 * pulso)));
        g.setStroke(new BasicStroke(2.2f));
        g.draw(new Ellipse2D.Double(8, 8, 32, 32));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        FontMetrics metricas = g.getFontMetrics();
        g.setColor(new Color(235, 240, 250));
        g.drawString("B", 24 - metricas.stringWidth("B") / 2, 24 + metricas.getAscent() / 2 - 2);
        int[][] luzes = {{24, 5}, {43, 24}, {24, 43}, {5, 24}};
        for (int i = 0; i < luzes.length; i++) {
            g.setColor(i == quadro ? new Color(255, 230, 90) : new Color(110, 100, 50));
            g.fill(new Ellipse2D.Double(luzes[i][0] - 2.5, luzes[i][1] - 2.5, 5, 5));
        }
    }

    private static void asteroide(Graphics2D g) {
        Random r = new Random(11);
        Path2D rocha = new Path2D.Double();
        int pontos = 11;
        for (int i = 0; i < pontos; i++) {
            double angulo = 2 * Math.PI * i / pontos;
            double raio = 16 + r.nextInt(7) - 2;
            double x = 24 + Math.cos(angulo) * raio;
            double y = 24 + Math.sin(angulo) * raio;
            if (i == 0) {
                rocha.moveTo(x, y);
            } else {
                rocha.lineTo(x, y);
            }
        }
        rocha.closePath();
        g.setPaint(new GradientPaint(10, 8, new Color(170, 160, 150), 38, 40, new Color(85, 78, 74)));
        g.fill(rocha);
        g.setColor(new Color(50, 45, 42));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(rocha);
        g.setColor(new Color(70, 64, 60, 200));
        g.fill(new Ellipse2D.Double(16, 15, 9, 8));
        g.fill(new Ellipse2D.Double(28, 26, 7, 6));
        g.fill(new Ellipse2D.Double(18, 31, 6, 5));
    }

    private static void inimigo(Graphics2D g, int quadro) {
        g.setPaint(new GradientPaint(0, 18, new Color(120, 230, 150), 0, 32, new Color(60, 150, 110)));
        g.fill(new Ellipse2D.Double(14, 8, 20, 20));
        g.setColor(new Color(20, 60, 40));
        g.fill(new Ellipse2D.Double(19, 15, 4, 5));
        g.fill(new Ellipse2D.Double(26, 15, 4, 5));
        g.setPaint(new GradientPaint(0, 24, new Color(230, 60, 90), 0, 38, new Color(120, 20, 60)));
        g.fill(new Ellipse2D.Double(4, 24, 40, 14));
        g.setColor(new Color(50, 10, 30));
        g.setStroke(new BasicStroke(1.3f));
        g.draw(new Ellipse2D.Double(4, 24, 40, 14));
        for (int i = 0; i < 5; i++) {
            boolean acesa = (i + quadro) % 2 == 0;
            g.setColor(acesa ? new Color(255, 235, 110) : new Color(110, 60, 60));
            g.fill(new Ellipse2D.Double(9 + i * 7, 29, 4, 4));
        }
    }

    private static void passageiro(Graphics2D g, int tipo) {
        Color traje = new Color[] {new Color(60, 100, 200), new Color(235, 130, 40), new Color(240, 240, 245)}[tipo];
        Color contorno = new Color(30, 30, 40);
        g.setStroke(new BasicStroke(1.3f));
        g.setColor(new Color(50, 50, 70));
        g.fill(new RoundRectangle2D.Double(17, 38, 5, 8, 3, 3));
        g.fill(new RoundRectangle2D.Double(26, 38, 5, 8, 3, 3));
        g.setColor(traje);
        g.fill(new RoundRectangle2D.Double(11, 25, 6, 13, 4, 4));
        g.fill(new RoundRectangle2D.Double(31, 25, 6, 13, 4, 4));
        g.fill(new RoundRectangle2D.Double(15, 23, 18, 18, 6, 6));
        g.setColor(contorno);
        g.draw(new RoundRectangle2D.Double(15, 23, 18, 18, 6, 6));

        if (tipo == 2) {
            g.setColor(new Color(245, 245, 250));
            g.fill(new Ellipse2D.Double(14, 5, 20, 20));
            g.setColor(contorno);
            g.draw(new Ellipse2D.Double(14, 5, 20, 20));
            g.setPaint(new GradientPaint(0, 9, new Color(120, 200, 255), 0, 21, new Color(30, 80, 160)));
            g.fill(new Ellipse2D.Double(18, 9, 12, 11));
            return;
        }
        g.setColor(new Color(240, 200, 160));
        g.fill(new Ellipse2D.Double(17, 9, 14, 14));
        g.setColor(contorno);
        g.draw(new Ellipse2D.Double(17, 9, 14, 14));
        if (tipo == 0) {
            g.setColor(new Color(25, 25, 30));
            g.fill(new RoundRectangle2D.Double(12, 5, 24, 5, 2, 2));
            g.fillRect(19, 9, 10, 3);
            g.setColor(new Color(20, 20, 20));
            g.draw(new Ellipse2D.Double(19, 14, 4, 4));
            g.draw(new Ellipse2D.Double(25, 14, 4, 4));
        } else {
            g.setColor(new Color(250, 215, 40));
            g.fill(new Arc2D.Double(15, 4, 18, 16, 0, 180, Arc2D.CHORD));
            g.fillRect(13, 12, 22, 3);
            g.setColor(contorno);
            g.draw(new Arc2D.Double(15, 4, 18, 16, 0, 180, Arc2D.CHORD));
        }
    }

    private static void desconhecido(Graphics2D g) {
        g.setColor(new Color(220, 40, 200));
        g.fill(new RoundRectangle2D.Double(6, 6, 36, 36, 8, 8));
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        FontMetrics metricas = g.getFontMetrics();
        g.drawString("?", 24 - metricas.stringWidth("?") / 2, 24 + metricas.getAscent() / 2 - 3);
    }

    private static void explosao(Graphics2D g, int quadro) {
        float t = quadro / 5f;
        float raio = 7 + 17 * t;
        int alfa = (int) (255 * (1 - 0.75 * t));
        g.setPaint(new RadialGradientPaint(new Point2D.Float(24, 24), raio,
                new float[] {0f, 0.45f, 1f},
                new Color[] {new Color(255, 250, 200, alfa), new Color(255, 140, 30, alfa), new Color(200, 30, 10, 0)}));
        g.fill(new Ellipse2D.Double(24 - raio, 24 - raio, raio * 2, raio * 2));
        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(255, 210, 90, (int) (230 * (1 - t))));
        for (int i = 0; i < 8; i++) {
            double angulo = Math.PI * i / 4 + 0.3;
            double interno = raio * 0.7;
            double externo = Math.min(23, raio + 5 + 4 * t);
            g.draw(new Line2D.Double(24 + Math.cos(angulo) * interno, 24 + Math.sin(angulo) * interno,
                    24 + Math.cos(angulo) * externo, 24 + Math.sin(angulo) * externo));
        }
    }

    private static void brilho(Graphics2D g, int quadro) {
        float t = quadro / 4f;
        int alfa = (int) (255 * (1 - 0.85 * t));
        g.setStroke(new BasicStroke(3f - 2f * t));
        g.setColor(new Color(130, 235, 255, alfa));
        double raio = 5 + 17 * t;
        g.draw(new Ellipse2D.Double(24 - raio, 24 - raio, raio * 2, raio * 2));
        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(255, 255, 255, alfa));
        double meio = 6 + 14 * (1 - t);
        g.draw(new Line2D.Double(24 - meio, 24, 24 + meio, 24));
        g.draw(new Line2D.Double(24, 24 - meio, 24, 24 + meio));
    }

    private static void coracao(Graphics2D g, boolean cheio) {
        Path2D coracao = new Path2D.Double();
        coracao.moveTo(12, 21);
        coracao.curveTo(-2, 11, 3, 1, 12, 7);
        coracao.curveTo(21, 1, 26, 11, 12, 21);
        coracao.closePath();
        if (cheio) {
            g.setPaint(new GradientPaint(0, 2, new Color(255, 110, 120), 0, 22, new Color(200, 20, 50)));
            g.fill(coracao);
            g.setColor(new Color(90, 10, 25));
        } else {
            g.setColor(new Color(60, 40, 55, 180));
            g.fill(coracao);
            g.setColor(new Color(130, 110, 125));
        }
        g.setStroke(new BasicStroke(1.4f));
        g.draw(coracao);
    }

    private static void iconePassageiro(Graphics2D g, boolean cheio) {
        Color cor = cheio ? new Color(250, 215, 70) : new Color(125, 115, 90, 190);
        g.setColor(cor);
        g.fill(new Ellipse2D.Double(8, 2, 8, 8));
        g.fill(new RoundRectangle2D.Double(5, 11, 14, 11, 6, 6));
        g.setColor(cheio ? new Color(90, 70, 10) : new Color(70, 62, 48));
        g.setStroke(new BasicStroke(1.2f));
        g.draw(new Ellipse2D.Double(8, 2, 8, 8));
        g.draw(new RoundRectangle2D.Double(5, 11, 14, 11, 6, 6));
    }

    // ------------------------------------------------------------------ sons

    private static void gerarSons(Path pasta) throws IOException {
        Random r = new Random(5);

        double[] mover = vazio(0.07);
        tom(mover, 0, 420, 0.07, 0.22, 0, 4);
        salvarWav(pasta.resolve("mover.wav"), mover);

        double[] selecionar = vazio(0.1);
        tom(selecionar, 0, 660, 0.09, 0.3, 2, 3);
        salvarWav(pasta.resolve("selecionar.wav"), selecionar);

        double[] embarcar = vazio(0.3);
        tom(embarcar, 0, 523, 0.12, 0.35, 2, 2);
        tom(embarcar, 0.1, 784, 0.18, 0.35, 2, 3);
        salvarWav(pasta.resolve("embarcar.wav"), embarcar);

        double[] entregar = vazio(0.6);
        tom(entregar, 0, 523, 0.12, 0.3, 2, 2);
        tom(entregar, 0.1, 659, 0.12, 0.3, 2, 2);
        tom(entregar, 0.2, 784, 0.12, 0.3, 2, 2);
        tom(entregar, 0.3, 1047, 0.3, 0.34, 2, 3);
        salvarWav(pasta.resolve("entregar.wav"), entregar);

        double[] colisao = vazio(0.45);
        ruido(colisao, 0, 0.4, 0.55, r);
        tom(colisao, 0, 90, 0.4, 0.55, 0, 5);
        salvarWav(pasta.resolve("colisao.wav"), colisao);

        double[] vitoria = vazio(1.3);
        tom(vitoria, 0.0, 523, 0.18, 0.3, 2, 1.5);
        tom(vitoria, 0.18, 659, 0.18, 0.3, 2, 1.5);
        tom(vitoria, 0.36, 784, 0.18, 0.3, 2, 1.5);
        tom(vitoria, 0.54, 1047, 0.75, 0.3, 2, 2);
        tom(vitoria, 0.54, 784, 0.75, 0.2, 2, 2);
        tom(vitoria, 0.54, 523, 0.75, 0.2, 2, 2);
        salvarWav(pasta.resolve("vitoria.wav"), vitoria);

        double[] derrota = vazio(1.1);
        double[] notas = {392, 330, 262, 196};
        for (int i = 0; i < notas.length; i++) {
            tom(derrota, i * 0.25, notas[i], 0.3, 0.3, 3, 1.5);
        }
        salvarWav(pasta.resolve("derrota.wav"), derrota);

        salvarWav(pasta.resolve("musica.wav"), musica());
    }

    private static double[] musica() {
        double[] buffer = vazio(8.0);
        double[][] acordes = {
            {110.00, 220.00, 261.63, 329.63},
            {87.31, 174.61, 220.00, 261.63},
            {130.81, 261.63, 329.63, 392.00},
            {98.00, 246.94, 293.66, 392.00}
        };
        for (int c = 0; c < acordes.length; c++) {
            double inicio = c * 2.0;
            tom(buffer, inicio, acordes[c][0], 1.95, 0.16, 0, 0.8);
            for (int n = 0; n < 8; n++) {
                tom(buffer, inicio + n * 0.25, acordes[c][1 + n % 3], 0.3, 0.1, 2, 3);
            }
        }
        int borda = (int) (0.02 * TAXA);
        for (int i = 0; i < borda; i++) {
            double fator = i / (double) borda;
            buffer[i] *= fator;
            buffer[buffer.length - 1 - i] *= fator;
        }
        return buffer;
    }

    private static double[] vazio(double segundos) {
        return new double[(int) (TAXA * segundos)];
    }

    /** Forma: 0 senoide, 1 quadrada, 2 triangular, 3 dente de serra. */
    private static void tom(double[] buffer, double inicio, double frequencia, double duracao, double volume, int forma, double decaimento) {
        int primeiro = (int) (inicio * TAXA);
        int amostras = (int) (duracao * TAXA);
        for (int i = 0; i < amostras && primeiro + i < buffer.length; i++) {
            double t = i / (double) TAXA;
            double fase = 2 * Math.PI * frequencia * t;
            double s;
            switch (forma) {
                case 1:
                    s = Math.signum(Math.sin(fase)) * 0.6;
                    break;
                case 2:
                    s = 2 / Math.PI * Math.asin(Math.sin(fase));
                    break;
                case 3:
                    s = ((frequencia * t) % 1) * 2 - 1;
                    s *= 0.6;
                    break;
                default:
                    s = Math.sin(fase);
            }
            double ataque = Math.min(1, i / (0.004 * TAXA));
            double liberacao = Math.min(1, (amostras - i) / (0.015 * TAXA));
            double envelope = ataque * liberacao * Math.exp(-decaimento * t / duracao);
            buffer[primeiro + i] += s * volume * envelope;
        }
    }

    private static void ruido(double[] buffer, double inicio, double duracao, double volume, Random r) {
        int primeiro = (int) (inicio * TAXA);
        int amostras = (int) (duracao * TAXA);
        for (int i = 0; i < amostras && primeiro + i < buffer.length; i++) {
            double t = i / (double) TAXA;
            double envelope = Math.exp(-6 * t / duracao);
            buffer[primeiro + i] += (r.nextDouble() * 2 - 1) * volume * envelope;
        }
    }

    private static void salvarWav(Path arquivo, double[] amostras) throws IOException {
        byte[] bytes = new byte[amostras.length * 2];
        for (int i = 0; i < amostras.length; i++) {
            int valor = (int) Math.max(-32768, Math.min(32767, Math.round(amostras[i] * 32767)));
            bytes[2 * i] = (byte) (valor & 0xFF);
            bytes[2 * i + 1] = (byte) ((valor >> 8) & 0xFF);
        }
        AudioFormat formato = new AudioFormat(TAXA, 16, 1, true, false);
        try (AudioInputStream fluxo = new AudioInputStream(new ByteArrayInputStream(bytes), formato, amostras.length)) {
            AudioSystem.write(fluxo, AudioFileFormat.Type.WAVE, arquivo.toFile());
        }
    }
}
