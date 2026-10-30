package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.servidor.Resposta;
import br.edu.unifor.missaomarte.application.servidor.Sessao;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;

/**
 * Tela de entrada e cadastro. A senha e mascarada e so vai ao servidor no momento de confirmar.
 * <p>Teclas: digitar, TAB ou setas trocam de campo, F2 alterna entre entrar e criar conta, ENTER confirma, ESC volta.
 * <p>Nao usa o atalho global M, porque a letra M faz parte do texto digitado.
 */
public class LoginScreen extends TelaBase {
    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);
    private static final Color ERRO = new Color(1f, 0.4f, 0.4f, 1f);

    private final FormularioDeLogin formulario = new FormularioDeLogin();
    private String mensagem = "";
    private boolean mensagemDeErro;
    private boolean enviando;

    public LoginScreen(MissaoMarteGame jogo) {
        super(jogo);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return tratarTecla(keycode);
            }

            @Override
            public boolean keyTyped(char caractere) {
                return !enviando && formulario.digitar(caractere);
            }
        });
    }

    private boolean tratarTecla(int keycode) {
        if (enviando) {
            return true;
        }
        switch (keycode) {
            case Keys.BACKSPACE:
                formulario.apagar();
                return true;
            case Keys.TAB:
            case Keys.UP:
            case Keys.DOWN:
                formulario.alternarCampo();
                return true;
            case Keys.F2:
                formulario.alternarModo();
                mensagem = "";
                return true;
            case Keys.ENTER:
            case Keys.NUMPAD_ENTER:
                confirmar();
                return true;
            case Keys.ESCAPE:
                jogo.setScreen(new MenuScreen(jogo));
                return true;
            default:
                return false;
        }
    }

    private void confirmar() {
        String problema = formulario.validar();
        if (problema != null) {
            exibir(problema, true);
            return;
        }
        enviando = true;
        String nome = formulario.getNome();
        String senha = formulario.getSenha();
        if (formulario.getModo() == FormularioDeLogin.Modo.CADASTRAR) {
            exibir("Criando a conta...", false);
            jogo.servidor().cadastrar(nome, senha, resposta -> {
                if (resposta.isSucesso()) {
                    entrar(nome, senha);
                } else {
                    falhar(resposta.getMensagem());
                }
            });
        } else {
            exibir("Conectando...", false);
            entrar(nome, senha);
        }
    }

    private void entrar(String nome, String senha) {
        jogo.servidor().entrar(nome, senha, (Resposta<Sessao> resposta) -> {
            if (resposta.isSucesso()) {
                enviando = false;
                formulario.limparSenha();
                jogo.entrar(resposta.getValor());
                jogo.sons().tocar(Som.SELECIONAR);
                jogo.setScreen(new MenuScreen(jogo));
            } else {
                falhar(resposta.getMensagem());
            }
        });
    }

    private void falhar(String texto) {
        enviando = false;
        exibir(texto, true);
    }

    private void exibir(String texto, boolean erro) {
        mensagem = texto;
        mensagemDeErro = erro;
    }

    @Override
    protected void desenhar(float delta) {
        boolean cadastro = formulario.getModo() == FormularioDeLogin.Modo.CADASTRAR;
        boolean noNome = formulario.getCampo() == FormularioDeLogin.Campo.NOME;
        String cursor = ((int) (tempo * 2f)) % 2 == 0 ? "_" : " ";

        jogo.batch().begin();

        escala(2f);
        cor(DESTAQUE);
        textoCentralizado(cadastro ? "CRIAR CONTA" : "ENTRAR", MissaoMarteGame.LARGURA / 2f, 500f);

        escala(1.5f);
        cor(Color.WHITE);
        texto((noNome ? "> " : "  ") + "Nome:  " + formulario.getNome() + (noNome && !enviando ? cursor : ""), 180f, 400f);
        texto((noNome ? "  " : "> ") + "Senha: " + formulario.getSenhaMascarada() + (!noNome && !enviando ? cursor : ""), 180f, 350f);

        escala(1f);
        cor(Color.LIGHT_GRAY);
        texto(cadastro ? "Nome: 3 a 20 letras, números, _ ou -.   Senha: 8 a 72 caracteres."
                : "Ainda não tem conta? Pressione F2 para criar uma.", 180f, 300f);

        cor(mensagemDeErro ? ERRO : DESTAQUE);
        texto(mensagem, 180f, 250f);

        cor(Color.LIGHT_GRAY);
        textoCentralizado("Tab: trocar de campo   |   F2: entrar ou criar conta   |   Enter: confirmar   |   Esc: voltar",
                MissaoMarteGame.LARGURA / 2f, 60f);

        jogo.batch().end();
    }
}
