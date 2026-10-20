package graspexercicio10.model;

/** A base também é um elemento do mapa. */
public class Base extends ElementoMapa {
    public Base() { super(0, 0); }
    @Override public char getSimbolo() { return 'L'; }
}
