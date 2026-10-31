package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AcaoTest {

    @Test
    void traduzOsComandosDoJogador() {
        assertEquals(Acao.ESQUERDA, Acao.fromInput("a"));
        assertEquals(Acao.DIREITA, Acao.fromInput("d"));
        assertEquals(Acao.CIMA, Acao.fromInput("w"));
        assertEquals(Acao.BAIXO, Acao.fromInput("s"));
        assertEquals(Acao.EMBARCAR, Acao.fromInput("e"));
        assertEquals(Acao.SAIR, Acao.fromInput("q"));
    }

    @Test
    void ignoraMaiusculasEEspacos() {
        assertEquals(Acao.DIREITA, Acao.fromInput("  D "));
    }

    @Test
    void entradaInvalidaViraAcaoInvalida() {
        assertEquals(Acao.INVALIDA, Acao.fromInput("x"));
        assertEquals(Acao.INVALIDA, Acao.fromInput(""));
        assertEquals(Acao.INVALIDA, Acao.fromInput("   "));
        assertEquals(Acao.INVALIDA, Acao.fromInput(null));
    }
}
