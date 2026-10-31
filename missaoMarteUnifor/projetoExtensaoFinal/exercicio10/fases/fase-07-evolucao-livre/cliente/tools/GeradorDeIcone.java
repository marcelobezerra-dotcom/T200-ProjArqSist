import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

/**
 * Gera o icone do app Android (mipmap-*) a partir de core/assets/imagens/nave.png.
 * Uso, a partir da pasta cliente: java tools/GeradorDeIcone.java
 * Saida: android/src/main/res/mipmap-mdpi ... mipmap-xxxhdpi/ic_launcher.png
 * Os PNG gerados ficam versionados; so e preciso rodar de novo para mudar o icone.
 */
public class GeradorDeIcone {
    private static final String[] PASTAS = {"mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"};
    private static final int[] TAMANHOS = {48, 72, 96, 144, 192};

    public static void main(String[] args) throws IOException {
        Path raiz = Paths.get(args.length > 0 ? args[0] : ".");
        BufferedImage nave = ImageIO.read(raiz.resolve("core/assets/imagens/nave.png").toFile());
        for (int i = 0; i < PASTAS.length; i++) {
            Path pasta = raiz.resolve("android/src/main/res/mipmap-" + PASTAS[i]);
            Files.createDirectories(pasta);
            ImageIO.write(icone(nave, TAMANHOS[i]), "png", pasta.resolve("ic_launcher.png").toFile());
        }
        System.out.println("Icones gerados em android/src/main/res/mipmap-*");
    }

    private static BufferedImage icone(BufferedImage nave, int lado) {
        BufferedImage saida = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = saida.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int arco = lado / 4;
        g.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, lado, lado, arco, arco));
        g.setPaint(new GradientPaint(0, 0, new Color(8, 8, 34), 0, lado, new Color(40, 14, 40)));
        g.fillRect(0, 0, lado, lado);

        // "Marte": circulo avermelhado no canto inferior direito.
        int marte = (int) (lado * 0.62);
        g.setColor(new Color(196, 84, 52));
        g.fillOval(lado - marte / 2 - lado / 10, lado - marte / 2 - lado / 10, marte, marte);
        g.setColor(new Color(150, 56, 36));
        g.fillOval(lado - marte / 2 + lado / 12, lado - marte / 3, lado / 8, lado / 8);

        // Nave no centro.
        int tamanho = (int) (lado * 0.66);
        int x = (lado - tamanho) / 2 - lado / 12;
        int y = (lado - tamanho) / 2 - lado / 12;
        g.drawImage(nave, x, y, tamanho, tamanho, null);
        g.dispose();
        return saida;
    }
}
