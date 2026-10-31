package br.edu.unifor.missaomarte.gdx.plataforma;

import br.edu.unifor.missaomarte.application.servidor.ArmazemDeTexto;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * {@link ArmazemDeTexto} sobre as Preferences do libGDX (arquivo em ~/.prefs no desktop; SharedPreferences no Android).
 * <p>As Preferences so existem depois que o jogo inicia, por isso sao abertas na primeira chamada e nao na criacao.
 */
public class PreferenciasComoArmazem implements ArmazemDeTexto {
    private final String nome;
    private Preferences preferencias;

    public PreferenciasComoArmazem(String nome) {
        this.nome = nome;
    }

    private Preferences preferencias() {
        if (preferencias == null) {
            preferencias = Gdx.app.getPreferences(nome);
        }
        return preferencias;
    }

    @Override
    public String ler(String chave) {
        return preferencias().contains(chave) ? preferencias().getString(chave) : null;
    }

    @Override
    public void gravar(String chave, String valor) {
        preferencias().putString(chave, valor);
        preferencias().flush();
    }

    @Override
    public void apagar(String chave) {
        preferencias().remove(chave);
        preferencias().flush();
    }
}
