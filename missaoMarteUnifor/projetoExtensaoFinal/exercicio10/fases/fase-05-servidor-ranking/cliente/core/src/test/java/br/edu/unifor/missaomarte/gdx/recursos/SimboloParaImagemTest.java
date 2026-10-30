package br.edu.unifor.missaomarte.gdx.recursos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SimboloParaImagemTest {

    @Test
    void cadaSimboloDoDominioTemSuaImagem() {
        assertEquals(Imagem.NAVE, SimboloParaImagem.de('N'));
        assertEquals(Imagem.BASE, SimboloParaImagem.de('B'));
        assertEquals(Imagem.ASTEROIDE, SimboloParaImagem.de('*'));
        assertEquals(Imagem.INIMIGO, SimboloParaImagem.de('X'));
        assertEquals(Imagem.PASSAGEIRO_PROFESSOR, SimboloParaImagem.de('P'));
        assertEquals(Imagem.PASSAGEIRO_ENGENHEIRO, SimboloParaImagem.de('E'));
        assertEquals(Imagem.PASSAGEIRO_ASTRONAUTA, SimboloParaImagem.de('A'));
    }

    @Test
    void simboloDesconhecidoUsaImagemGenerica() {
        assertEquals(Imagem.DESCONHECIDO, SimboloParaImagem.de('?'));
        assertEquals(Imagem.DESCONHECIDO, SimboloParaImagem.de('Z'));
    }
}
