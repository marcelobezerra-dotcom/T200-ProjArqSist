package br.edu.unifor.missaomarte.gdx.recursos;

/**
 * Escolhe a imagem de um objeto pelo simbolo que ele proprio informa em {@code ObjetoRenderizavel#getSimbolo()}.
 * <p>OCP: um novo tipo de objeto so precisa de um novo simbolo e uma nova imagem, sem instanceof; simbolo desconhecido usa a imagem generica.
 */
public final class SimboloParaImagem {
    private SimboloParaImagem() {
    }

    public static Imagem de(char simbolo) {
        switch (simbolo) {
            case 'N':
                return Imagem.NAVE;
            case 'B':
                return Imagem.BASE;
            case '*':
                return Imagem.ASTEROIDE;
            case 'X':
                return Imagem.INIMIGO;
            case 'C':
                return Imagem.PERSEGUIDOR;
            case 'P':
                return Imagem.PASSAGEIRO_PROFESSOR;
            case 'E':
                return Imagem.PASSAGEIRO_ENGENHEIRO;
            case 'A':
                return Imagem.PASSAGEIRO_ASTRONAUTA;
            default:
                return Imagem.DESCONHECIDO;
        }
    }
}
