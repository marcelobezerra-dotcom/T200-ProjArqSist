package br.edu.unifor.missaomarte.application.servidor;

/** Callback de uma chamada assincrona. Quem implementa a porta garante que e chamado na thread de desenho do jogo. */
@FunctionalInterface
public interface Retorno<T> {
    void concluido(Resposta<T> resposta);
}
