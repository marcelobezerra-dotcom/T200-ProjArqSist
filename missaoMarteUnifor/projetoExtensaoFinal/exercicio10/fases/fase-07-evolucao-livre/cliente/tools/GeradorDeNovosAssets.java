import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Gera so os assets novos da Fase 7, sem mexer nos que ja existem:
 * imagens/perseguidor.png (o inimigo com as cores trocadas) e sons/tiro.wav (varredura de frequencia descendente).
 * Uso, a partir da pasta cliente: java tools/GeradorDeNovosAssets.java
 */
public class GeradorDeNovosAssets {
    private static final float TAXA = 22050f;

    public static void main(String[] args) throws IOException {
        Path raiz = Paths.get(args.length > 0 ? args[0] : ".").resolve("core/assets");
        gerarPerseguidor(raiz.resolve("imagens"));
        gerarTiro(raiz.resolve("sons"));
        System.out.println("Assets novos gerados em " + raiz.toAbsolutePath().normalize());
    }

    // Gira os canais de cor (R, G, B) -> (G, B, R): o disco vermelho vira azul e o alienigena verde vira pessego.
    private static void gerarPerseguidor(Path pasta) throws IOException {
        BufferedImage inimigo = ImageIO.read(pasta.resolve("inimigo.png").toFile());
        BufferedImage saida = new BufferedImage(inimigo.getWidth(), inimigo.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < inimigo.getHeight(); y++) {
            for (int x = 0; x < inimigo.getWidth(); x++) {
                int argb = inimigo.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                saida.setRGB(x, y, (a << 24) | (g << 16) | (b << 8) | r);
            }
        }
        ImageIO.write(saida, "png", pasta.resolve("perseguidor.png").toFile());
    }

    private static void gerarTiro(Path pasta) throws IOException {
        double duracao = 0.2;
        int amostras = (int) (TAXA * duracao);
        double[] buffer = new double[amostras];
        double fase = 0;
        for (int i = 0; i < amostras; i++) {
            double t = i / (double) amostras;
            double frequencia = 1500 - 1200 * t;
            fase += 2 * Math.PI * frequencia / TAXA;
            double onda = Math.signum(Math.sin(fase)) * 0.5 + Math.sin(fase) * 0.5;
            double ataque = Math.min(1, i / (0.003 * TAXA));
            double envelope = ataque * Math.exp(-4 * t);
            buffer[i] = onda * 0.35 * envelope;
        }
        byte[] bytes = new byte[amostras * 2];
        for (int i = 0; i < amostras; i++) {
            int valor = (int) Math.max(-32768, Math.min(32767, Math.round(buffer[i] * 32767)));
            bytes[2 * i] = (byte) (valor & 0xFF);
            bytes[2 * i + 1] = (byte) ((valor >> 8) & 0xFF);
        }
        AudioFormat formato = new AudioFormat(TAXA, 16, 1, true, false);
        try (AudioInputStream fluxo = new AudioInputStream(new ByteArrayInputStream(bytes), formato, amostras)) {
            AudioSystem.write(fluxo, AudioFileFormat.Type.WAVE, pasta.resolve("tiro.wav").toFile());
        }
    }
}
