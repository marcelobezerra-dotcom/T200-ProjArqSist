package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HistoricoScreenTest {

    @Test
    void formataDataEHoraDoServidor() {
        assertEquals("2026-10-30 21:05", HistoricoScreen.formatarData("2026-10-30T21:05:07.123Z"));
    }

    @Test
    void dataAusenteOuCurtaViraTextoVazio() {
        assertEquals("", HistoricoScreen.formatarData(null));
        assertEquals("", HistoricoScreen.formatarData("2026-10-30"));
    }
}
