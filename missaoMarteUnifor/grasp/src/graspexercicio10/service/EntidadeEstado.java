package graspexercicio10.service;

import graspexercicio10.model.ElementoMapa;

/** Cópia imutável de uma entidade para leitura pela interface. */
public final class EntidadeEstado {
    public final char simbolo;
    public final String descricao;
    public final String nome;
    public final int x;
    public final int y;
    public final boolean resgatavel;

    public EntidadeEstado(ElementoMapa elemento) {
        this.simbolo = elemento.getSimbolo();
        this.descricao = elemento.getDescricao();
        this.nome = elemento.getNome();
        this.x = elemento.getX();
        this.y = elemento.getY();
        this.resgatavel = elemento.isResgatavel();
    }
}
