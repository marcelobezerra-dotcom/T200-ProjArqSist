package br.edu.unifor.missaomarte.domain;

/**
 * Contrato minimo de tudo que aparece no mapa: posicao e simbolo.
 * <p>SOLID: ISP (2 metodos), OCP (novo elemento aparece sem alterar o renderer), DIP (o renderer depende desta abstracao).
 * <p>GRASP: Polymorphism, Protected Variations (a forma de desenhar muda sem tocar no dominio).
 */
public interface ObjetoRenderizavel {
    Posicao getPosicao();

    char getSimbolo();
}
