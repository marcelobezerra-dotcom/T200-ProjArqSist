package br.edu.unifor.missaomarte.application;

/**
 * O que aconteceu em um turno, para que a interface reaja (efeitos, sons, HUD) sem deduzir nada do estado.
 * <p>Padrao: Domain Event simplificado. Nao altera nenhuma regra: apenas descreve o que a regra ja decidiu.
 */
public enum EventoJogo {
    MOVIMENTO,
    EMBARQUE,
    ENTREGA,
    COLISAO,
    VITORIA,
    DERROTA,
    TIRO,
    PERIGO_DESTRUIDO
}
