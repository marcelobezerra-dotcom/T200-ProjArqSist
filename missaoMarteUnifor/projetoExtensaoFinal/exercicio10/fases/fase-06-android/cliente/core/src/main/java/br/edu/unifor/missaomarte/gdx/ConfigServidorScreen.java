package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.servidor.ConfiguracaoDoServidor;
import br.edu.unifor.missaomarte.application.servidor.ConfiguracaoInvalidaException;
import br.edu.unifor.missaomarte.gdx.entrada.BotaoVirtual;
import br.edu.unifor.missaomarte.gdx.plataforma.Plataforma.TipoDeTexto;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import java.util.function.Supplier;

/**
 * Configura o servidor de ranking: endereco (IP ou nome), porta e HTTPS. Permite testar a conexao antes de salvar.
 * <p>Teclas: digitar, TAB troca de campo, F2 liga ou desliga o HTTPS, F5 testa, ENTER salva, F6 restaura o padrao, ESC volta.
 * <p>O que e salvo fica nas Preferences (so endereco, porta e HTTPS; nunca senha ou token). Trocar de servidor encerra a sessao,
 * porque o token vale so para o servidor que o emitiu. HTTP fora da rede local mostra um aviso: senha e token iriam sem criptografia.
 * <p>Nao usa o atalho global M, porque a letra M pode fazer parte do endereco.
 */
public class ConfigServidorScreen extends TelaBase {
    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);
    private static final Color ERRO = new Color(1f, 0.4f, 0.4f, 1f);
    private static final Color OK = new Color(0.45f, 0.9f, 0.55f, 1f);
    private static final Color AVISO = new Color(1f, 0.65f, 0.2f, 1f);

    private final Supplier<Screen> destino;
    private FormularioDeServidor formulario;
    private String mensagem = "";
    private boolean mensagemDeErro;
    private boolean testando;
    private String chaveDoAviso = "";
    private boolean exibirAviso;

    /** O destino cria a tela para onde voltar (menu ou login), ja que cada uma guarda seu proprio estado. */
    public ConfigServidorScreen(MissaoMarteGame jogo, Supplier<Screen> destino) {
        super(jogo);
        this.destino = destino;
        this.formulario = new FormularioDeServidor(jogo.endereco().atual());
        botoes.add(new BotaoVirtual("endereco", "", 170f, 396f, 520f, 38f));
        botoes.add(new BotaoVirtual("porta", "", 170f, 346f, 520f, 38f));
        botoes.add(new BotaoVirtual("https", "", 170f, 296f, 520f, 38f));
        botoes.add(new BotaoVirtual("testar", "Testar conexão", 40f, 110f, 190f, 48f));
        botoes.add(new BotaoVirtual("salvar", "Salvar", 245f, 110f, 130f, 48f));
        botoes.add(new BotaoVirtual("padrao", "Restaurar padrão", 390f, 110f, 190f, 48f));
        botoes.add(new BotaoVirtual("voltar", "Voltar", 595f, 110f, 140f, 48f));
    }

    @Override
    public void show() {
        super.show();
        definirEntradaDeTexto(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return tratarTecla(keycode);
            }

            @Override
            public boolean keyTyped(char caractere) {
                return formulario.digitar(caractere);
            }
        });
    }

    private boolean tratarTecla(int keycode) {
        if (ehVoltar(keycode)) {
            voltar();
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
                formulario.alternarHttps();
                return true;
            case Keys.F5:
                testar();
                return true;
            case Keys.F6:
                restaurarPadrao();
                return true;
            case Keys.ENTER:
            case Keys.NUMPAD_ENTER:
                salvar();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected boolean aoTocar(String id, int ponteiro) {
        switch (id) {
            case "endereco":
                formulario.selecionarCampo(FormularioDeServidor.Campo.ENDERECO);
                jogo.plataforma().pedirTexto("Endereço do servidor", formulario.getEndereco(), TipoDeTexto.NORMAL,
                        texto -> formulario.definirTexto(FormularioDeServidor.Campo.ENDERECO, texto));
                return true;
            case "porta":
                formulario.selecionarCampo(FormularioDeServidor.Campo.PORTA);
                jogo.plataforma().pedirTexto("Porta", formulario.getPorta(), TipoDeTexto.NUMERO,
                        texto -> formulario.definirTexto(FormularioDeServidor.Campo.PORTA, texto));
                return true;
            case "https":
                formulario.alternarHttps();
                return true;
            case "testar":
                testar();
                return true;
            case "salvar":
                salvar();
                return true;
            case "padrao":
                restaurarPadrao();
                return true;
            case "voltar":
                voltar();
                return true;
            default:
                return false;
        }
    }

    private void testar() {
        if (testando) {
            return;
        }
        ConfiguracaoDoServidor configuracao = montar();
        if (configuracao == null) {
            return;
        }
        testando = true;
        exibir("Testando " + configuracao.urlBase() + "...", false);
        jogo.servidor().verificarConexao(configuracao, resposta -> {
            testando = false;
            if (resposta.isSucesso()) {
                exibir("Conexão com o servidor OK.", false);
            } else {
                exibir(resposta.getMensagem(), true);
            }
        });
    }

    private void salvar() {
        ConfiguracaoDoServidor configuracao = montar();
        if (configuracao == null) {
            return;
        }
        jogo.sons().tocar(Som.SELECIONAR);
        boolean mudou = jogo.endereco().salvar(configuracao);
        if (mudou && jogo.sessao() != null) {
            jogo.sair();
            exibir("Servidor salvo. Você saiu da conta, pois o servidor mudou.", false);
        } else {
            exibir("Servidor salvo: " + configuracao.urlBase(), false);
        }
    }

    private void restaurarPadrao() {
        boolean mudou = jogo.endereco().restaurarPadrao();
        formulario = new FormularioDeServidor(jogo.endereco().padrao());
        if (mudou && jogo.sessao() != null) {
            jogo.sair();
            exibir("Padrão restaurado. Você saiu da conta, pois o servidor mudou.", false);
        } else {
            exibir("Padrão restaurado: " + jogo.endereco().urlBase(), false);
        }
    }

    private void voltar() {
        jogo.setScreen(destino.get());
    }

    /** Valida o formulario; em caso de problema mostra a mensagem e devolve null. */
    private ConfiguracaoDoServidor montar() {
        try {
            return formulario.montar();
        } catch (ConfiguracaoInvalidaException e) {
            exibir(e.getMessage(), true);
            return null;
        }
    }

    private void exibir(String texto, boolean erro) {
        mensagem = texto;
        mensagemDeErro = erro;
    }

    /** Recalcula o aviso de HTTP inseguro so quando o formulario muda (evita criar excecoes a cada quadro). */
    private void atualizarAviso() {
        String chave = formulario.getEndereco() + "|" + formulario.getPorta() + "|" + formulario.isHttps();
        if (chave.equals(chaveDoAviso)) {
            return;
        }
        chaveDoAviso = chave;
        try {
            exibirAviso = formulario.montar().exigeAviso();
        } catch (ConfiguracaoInvalidaException e) {
            exibirAviso = false;
        }
    }

    @Override
    protected void desenhar(float delta) {
        atualizarAviso();
        boolean noEndereco = formulario.getCampo() == FormularioDeServidor.Campo.ENDERECO;
        String cursor = ((int) (tempo * 2f)) % 2 == 0 ? "_" : " ";

        jogo.batch().begin();

        escala(2f);
        cor(DESTAQUE);
        textoCentralizado("SERVIDOR", MissaoMarteGame.LARGURA / 2f, 520f);

        escala(1.5f);
        cor(Color.WHITE);
        texto((noEndereco ? "> " : "  ") + "Endereço: " + formulario.getEndereco() + (noEndereco ? cursor : ""), 180f, 430f);
        texto((noEndereco ? "  " : "> ") + "Porta:    " + formulario.getPorta() + (noEndereco ? "" : cursor), 180f, 380f);
        texto("   Conexão segura (HTTPS): " + (formulario.isHttps() ? "sim" : "não"), 180f, 330f);

        escala(1f);
        if (exibirAviso) {
            cor(AVISO);
            textoQuebrado("Atenção: HTTP fora da rede local envia senha e token sem criptografia. Use só para teste e prefira HTTPS.",
                    180f, 286f, 520f);
        }
        cor(Color.LIGHT_GRAY);
        texto("Em uso: " + jogo.endereco().urlBase() + "     Padrão: " + jogo.endereco().padrao().urlBase(), 180f, 238f);
        texto("Celular: no emulador use 10.0.2.2; com o PC na mesma rede Wi-Fi, o IP do PC.", 180f, 218f);

        cor(mensagemDeErro ? ERRO : OK);
        texto(mensagem, 180f, 188f);

        cor(Color.LIGHT_GRAY);
        textoCentralizado("Tab: campo  |  F2: HTTPS  |  F5: testar  |  Enter: salvar  |  F6: padrão  |  Esc: voltar",
                MissaoMarteGame.LARGURA / 2f, 50f);

        jogo.batch().end();
    }
}
