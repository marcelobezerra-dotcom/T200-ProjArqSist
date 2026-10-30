package br.edu.unifor.missaomarte.gdx.visual;

import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Acompanha a {@link PosicaoVisual} de cada objeto do mapa sem alterar o dominio: compara por identidade a cada quadro.
 * <p>Objetos que deixam de existir (embarcados ou destruidos) saem do mapa na proxima sincronizacao.
 */
public final class PosicoesVisuais {
    private final Map<ObjetoRenderizavel, PosicaoVisual> posicoes = new IdentityHashMap<>();
    private final float velocidade;

    public PosicoesVisuais(float velocidade) {
        this.velocidade = velocidade;
    }

    /** Chame uma vez por quadro com todos os objetos atuais. */
    public void sincronizar(Collection<? extends ObjetoRenderizavel> atuais, float delta) {
        Map<ObjetoRenderizavel, Boolean> presentes = new IdentityHashMap<>();
        for (ObjetoRenderizavel objeto : atuais) {
            presentes.put(objeto, Boolean.TRUE);
            float x = objeto.getPosicao().getX();
            float y = objeto.getPosicao().getY();
            PosicaoVisual visual = posicoes.computeIfAbsent(objeto, o -> new PosicaoVisual(x, y));
            visual.irPara(x, y);
            visual.atualizar(delta, velocidade);
        }
        posicoes.keySet().removeIf(objeto -> !presentes.containsKey(objeto));
    }

    public PosicaoVisual de(ObjetoRenderizavel objeto) {
        PosicaoVisual visual = posicoes.get(objeto);
        if (visual == null) {
            visual = new PosicaoVisual(objeto.getPosicao().getX(), objeto.getPosicao().getY());
            posicoes.put(objeto, visual);
        }
        return visual;
    }

    public int quantidade() {
        return posicoes.size();
    }
}
