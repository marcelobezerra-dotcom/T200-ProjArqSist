package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.EventoJogo;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.ModoDeJogo;
import br.edu.unifor.missaomarte.application.ResultadoTurno;
import br.edu.unifor.missaomarte.application.servidor.PartidaConcluida;
import br.edu.unifor.missaomarte.application.servidor.Resposta;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.ResultadoDoTiro;
import br.edu.unifor.missaomarte.gdx.entrada.BotaoVirtual;
import br.edu.unifor.missaomarte.gdx.entrada.ControleDeToque;
import br.edu.unifor.missaomarte.gdx.hud.HudRenderer;
import br.edu.unifor.missaomarte.gdx.layout.LayoutDaPartida;
import br.edu.unifor.missaomarte.gdx.layout.RetanguloDeTela;
import br.edu.unifor.missaomarte.gdx.recursos.Imagem;
import br.edu.unifor.missaomarte.gdx.recursos.Som;
import br.edu.unifor.missaomarte.gdx.visual.EfeitosVisuais;
import br.edu.unifor.missaomarte.gdx.visual.MensagensFlutuantes;
import br.edu.unifor.missaomarte.gdx.visual.MundoRenderer;
import br.edu.unifor.missaomarte.gdx.visual.PosicoesVisuais;
import br.edu.unifor.missaomarte.gdx.visual.Tremor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tela da partida: converte teclas e toques em {@link Acao}, chama a {@link GameSession} e reage ao que aconteceu.
 * <p>GRASP: Controller de interface. Nao tem regra de jogo: o resultado (estado, mensagens e eventos) vem da sessao.
 * <p>Os eventos disparam sons, efeitos visuais, tremor e textos flutuantes (Observer simplificado).
 * <p>Fase 5: ao terminar, o jogador logado tem o resultado enviado ao servidor (assincrono); o convidado grava no ranking local.
 * <p>Fase 6: botoes virtuais ({@link ControleDeToque}); Voltar do Android e Esc abortam.
 * <p>Fase 7: dois modos. No <b>classico</b> cada acao e um turno (a tecla segurada repete). No <b>dinamico</b> o jogo corre sozinho:
 * a tela so repassa o tempo de cada quadro a {@link GameSession#avancar(float)} e avisa quando uma direcao e pressionada ou solta.
 * O layout (mapa, painel e controles) vem de {@link LayoutDaPartida} e se adapta a paisagem, retrato e tamanhos de tela.
 */
public class GameScreen extends TelaBase {
    private enum Envio { NAO_ENVIA, ENVIANDO, ENVIADO, FALHOU }

    private static final int CELULAS = MissaoMarteGame.MAX - MissaoMarteGame.MIN + 1;
    private static final float VELOCIDADE_VISUAL = 12f;
    private static final float DURACAO_DO_RAIO = 0.18f;
    private static final float DURACAO_DA_MENSAGEM = 3f;

    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);
    private static final Color VITORIA = new Color(0.45f, 0.9f, 0.55f, 1f);
    private static final Color DERROTA = new Color(1f, 0.4f, 0.4f, 1f);
    private static final Color CAIXA = new Color(0.05f, 0.05f, 0.12f, 1f);

    private final GameSession session;
    private final boolean dinamico;
    private final PosicoesVisuais posicoes = new PosicoesVisuais(VELOCIDADE_VISUAL);
    private final EfeitosVisuais efeitos = new EfeitosVisuais();
    private final MensagensFlutuantes flutuantes = new MensagensFlutuantes();
    private final Tremor tremor = new Tremor();
    private final RepetidorDeTecla repetidor = new RepetidorDeTecla(0.28f, 0.14f);
    private final MundoRenderer mundo;
    private final HudRenderer hud;
    private final Map<Integer, Acao> setasPorDedo = new HashMap<>();

    private LayoutDaPartida layout;
    private MapaGeometria mapa;
    private ControleDeToque controles;
    private RetanguloDeTela areaDosControles;
    private float alvoDosControles = -1f;
    private boolean controlesEmRetrato;

    private List<String> mensagens = new ArrayList<>();
    private float tempoDaMensagem;
    private boolean novoRegistro;
    private Envio envio = Envio.NAO_ENVIA;
    private String erroDeEnvio = "";
    private float anguloNave;
    private float tempoPiscando;
    private float tempoDoRaio;
    private ResultadoDoTiro raio;
    private Acao acaoSegurada;
    private int teclaSegurada = -1;
    private int ponteiroSegurado = -1;
    private int contagemMostrada = -1;

    public GameScreen(MissaoMarteGame jogo, GameSession session) {
        super(jogo);
        this.session = session;
        this.dinamico = session.getModo() == ModoDeJogo.DINAMICO;
        Viewport viewport = jogo.viewport();
        this.layout = layoutAtual(viewport);
        this.mapa = geometriaDe(layout);
        this.mundo = new MundoRenderer(jogo.recursos(), jogo.batch(), jogo.fonte(), mapa);
        this.hud = new HudRenderer(jogo.recursos(), jogo.batch(), jogo.shapes(), jogo.fonte());
    }

    private LayoutDaPartida layoutAtual(Viewport viewport) {
        return LayoutDaPartida.calcular(viewport.getWorldWidth(), viewport.getWorldHeight(), CELULAS, jogo.modoToque(),
                jogo.margensSeguras());
    }

    private static MapaGeometria geometriaDe(LayoutDaPartida l) {
        return new MapaGeometria(MissaoMarteGame.MIN, MissaoMarteGame.MAX, l.celula(), l.origemX(), l.origemY());
    }

    @Override
    public void show() {
        super.show();
        definirEntrada(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return dinamico ? teclaDinamica(keycode) : tratarTecla(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                if (dinamico) {
                    session.soltar(TecladoParaAcao.converter(keycode));
                    return false;
                }
                if (keycode == teclaSegurada) {
                    pararRepeticao();
                }
                return false;
            }
        });
    }

    /** O app foi para segundo plano (ou a tela apagou): o jogo dinamico para de correr. */
    @Override
    public void pause() {
        super.pause();
        if (dinamico) {
            session.pausar();
            setasPorDedo.clear();
        }
    }

    @Override
    protected boolean usaMundoInteiro() {
        return true;
    }

    // ---------------------------------------------------------------- entrada: modo classico

    private boolean tratarTecla(int keycode) {
        if (session.getEstado() != EstadoPartida.EM_ANDAMENTO) {
            return teclaDeFimDePartida(keycode);
        }

        Acao acao = TecladoParaAcao.converter(keycode);
        if (acao == Acao.INVALIDA) {
            return false;
        }

        executar(acao);
        if (ehMovimento(acao) && session.getEstado() == EstadoPartida.EM_ANDAMENTO) {
            acaoSegurada = acao;
            teclaSegurada = keycode;
            repetidor.iniciar();
        }
        return true;
    }

    private boolean teclaDeFimDePartida(int keycode) {
        if (keycode == Keys.R && envio == Envio.FALHOU && jogo.sessao() != null) {
            enviarResultado();
            return true;
        }
        if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Keys.BACK
                || keycode == Keys.BUTTON_A || keycode == Keys.DPAD_CENTER) {
            if (envio != Envio.ENVIANDO) {
                encerrar();
            }
            return true;
        }
        return false;
    }

    // ---------------------------------------------------------------- entrada: modo dinamico

    private boolean teclaDinamica(int keycode) {
        if (session.getEstado() != EstadoPartida.EM_ANDAMENTO) {
            return teclaDeFimDePartida(keycode);
        }
        if (keycode == Keys.P || keycode == Keys.BACK) {
            alternarPausa();
            return true;
        }
        if (session.isPausada()) {
            if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER || keycode == Keys.BUTTON_A) {
                alternarPausa();
                return true;
            }
            if (TecladoParaAcao.converter(keycode) == Acao.SAIR) {
                executar(Acao.SAIR);
                return true;
            }
            return false;
        }
        if (keycode == Keys.F || keycode == Keys.SPACE) {
            atirar();
            return true;
        }
        Acao acao = TecladoParaAcao.converter(keycode);
        if (acao == Acao.SAIR) {
            executar(Acao.SAIR);
            return true;
        }
        if (acao == Acao.EMBARCAR) {
            atirar();
            return true;
        }
        if (ehMovimento(acao)) {
            session.pressionar(acao);
            return true;
        }
        return false;
    }

    private void alternarPausa() {
        session.alternarPausa();
        setasPorDedo.clear();
        log(session.isPausada() ? "pausa" : "continua");
    }

    private void atirar() {
        int antes = session.getPontuacao();
        tratarResultado(session.atirar(), antes);
    }

    // ---------------------------------------------------------------- entrada: toque

    @Override
    protected boolean aoTocar(String id, int ponteiro) {
        if (session.getEstado() != EstadoPartida.EM_ANDAMENTO) {
            if ("reenviar".equals(id) && envio == Envio.FALHOU && jogo.sessao() != null) {
                enviarResultado();
                return true;
            }
            if ("continuar".equals(id)) {
                if (envio != Envio.ENVIANDO) {
                    encerrar();
                }
                return true;
            }
            return false;
        }
        if (dinamico && session.isPausada()) {
            if ("continuar".equals(id)) {
                alternarPausa();
                return true;
            }
            if ("sair".equals(id)) {
                executar(Acao.SAIR);
                return true;
            }
            return false;
        }
        if (ControleDeToque.SOM.equals(id)) {
            log(jogo.sons().alternarMudo() ? "som desligado" : "som ligado");
            return true;
        }
        if (ControleDeToque.PAUSA.equals(id)) {
            alternarPausa();
            return true;
        }
        Acao acao = ControleDeToque.acaoDe(id);
        if (acao == Acao.INVALIDA) {
            return false;
        }
        if (dinamico) {
            return toqueDinamico(acao, ponteiro);
        }
        executar(acao);
        if (ControleDeToque.repete(id) && session.getEstado() == EstadoPartida.EM_ANDAMENTO) {
            acaoSegurada = acao;
            ponteiroSegurado = ponteiro;
            repetidor.iniciar();
        }
        return true;
    }

    private boolean toqueDinamico(Acao acao, int ponteiro) {
        if (acao == Acao.ATIRAR) {
            atirar();
        } else if (acao == Acao.SAIR) {
            executar(Acao.SAIR);
        } else if (ehMovimento(acao)) {
            setasPorDedo.put(ponteiro, acao);
            session.pressionar(acao);
        }
        return true;
    }

    @Override
    protected void aoSoltar(int ponteiro) {
        if (dinamico) {
            Acao solta = setasPorDedo.remove(ponteiro);
            if (solta != null && !setasPorDedo.containsValue(solta)) {
                session.soltar(solta);
            }
            return;
        }
        if (ponteiro == ponteiroSegurado) {
            pararRepeticao();
        }
    }

    /** Monta os botoes do momento: controles na partida; Continuar e Sair na pausa; Continuar (e Tentar de novo) no fim. */
    private void atualizarBotoes() {
        if (!jogo.modoToque()) {
            return;
        }
        botoes.clear();
        RetanguloDeTela m = layout.mapa();
        if (session.getEstado() == EstadoPartida.EM_ANDAMENTO && !(dinamico && session.isPausada())) {
            RetanguloDeTela area = layout.controles();
            float alvo = jogo.alvoMinimo();
            if (controles == null || alvo != alvoDosControles || controlesEmRetrato != layout.emRetrato()
                    || !mesmoRetangulo(area, areaDosControles)) {
                controles = new ControleDeToque(area, layout.emRetrato(), dinamico, alvo);
                areaDosControles = area;
                alvoDosControles = alvo;
                controlesEmRetrato = layout.emRetrato();
            }
            botoes.addAll(controles.getBotoes());
            return;
        }
        float largura = Math.min(220f, m.largura() - 16f);
        float altura = Math.max(52f, jogo.alvoMinimo());
        float x = m.centroX() - largura / 2f;
        float yBase = m.centroY() - 75f - 14f - altura;
        if (session.getEstado() == EstadoPartida.EM_ANDAMENTO) {
            botoes.add(new BotaoVirtual("continuar", "Continuar", x, yBase, largura, altura));
            botoes.add(new BotaoVirtual("sair", "Abortar", x, yBase - 10f - altura, largura, altura));
            return;
        }
        boolean vitoria = session.getEstado() == EstadoPartida.VITORIA;
        botoes.add(new BotaoVirtual("continuar", vitoria ? "Ver ranking" : "Voltar ao menu", x, yBase, largura, altura));
        if (envio == Envio.FALHOU && jogo.sessao() != null) {
            botoes.add(new BotaoVirtual("reenviar", "Tentar de novo", x, yBase - 10f - altura, largura, altura));
        }
    }

    private static boolean mesmoRetangulo(RetanguloDeTela a, RetanguloDeTela b) {
        return b != null && a.x() == b.x() && a.y() == b.y() && a.largura() == b.largura() && a.altura() == b.altura();
    }

    // ---------------------------------------------------------------- regras e reacoes

    private void pararRepeticao() {
        acaoSegurada = null;
        teclaSegurada = -1;
        ponteiroSegurado = -1;
        repetidor.parar();
    }

    private static boolean ehMovimento(Acao acao) {
        return acao == Acao.CIMA || acao == Acao.BAIXO || acao == Acao.ESQUERDA || acao == Acao.DIREITA;
    }

    private static float anguloDe(Acao acao) {
        switch (acao) {
            case DIREITA:
                return -90f;
            case BAIXO:
                return 180f;
            case ESQUERDA:
                return 90f;
            default:
                return 0f;
        }
    }

    private void executar(Acao acao) {
        int pontuacaoAntes = session.getPontuacao();
        ResultadoTurno resultado = session.processarAcao(acao);
        if (ehMovimento(acao)) {
            anguloNave = anguloDe(acao);
        }
        tratarResultado(resultado, pontuacaoAntes);
    }

    private void tratarResultado(ResultadoTurno resultado, int pontuacaoAntes) {
        if (!resultado.getMensagens().isEmpty() || !dinamico) {
            mensagens = new ArrayList<>(resultado.getMensagens());
            tempoDaMensagem = DURACAO_DA_MENSAGEM;
        }
        reagirAosEventos(resultado.getEventos(), session.getPontuacao() - pontuacaoAntes);
        aplicarEstado(resultado.getEstado());
    }

    private void reagirAosEventos(List<EventoJogo> eventos, int variacaoDePontos) {
        Posicao nave = session.getMissao().getNave().getPosicao();
        Posicao base = session.getMissao().getBase().getPosicao();

        for (EventoJogo evento : eventos) {
            if (evento != EventoJogo.MOVIMENTO) {
                log("evento " + evento);
            }
            switch (evento) {
                case MOVIMENTO:
                    jogo.sons().tocar(Som.MOVER);
                    break;
                case EMBARQUE:
                    jogo.sons().tocar(Som.EMBARCAR);
                    efeitos.adicionar(Imagem.BRILHO, nave.getX(), nave.getY(), 0.45f);
                    break;
                case ENTREGA:
                    jogo.sons().tocar(Som.ENTREGAR);
                    efeitos.adicionar(Imagem.BRILHO, base.getX(), base.getY(), 0.5f);
                    flutuantes.adicionar("Entregue!", base.getX(), base.getY() - 0.6f, true);
                    break;
                case COLISAO:
                    jogo.sons().tocar(Som.COLISAO);
                    efeitos.adicionar(Imagem.EXPLOSAO, nave.getX(), nave.getY(), 0.55f);
                    tremor.iniciar(0.35f, 7f);
                    tempoPiscando = 1f;
                    break;
                case TIRO:
                    jogo.sons().tocar(Som.TIRO);
                    raio = session.getUltimoTiro();
                    tempoDoRaio = DURACAO_DO_RAIO;
                    break;
                case PERIGO_DESTRUIDO:
                    if (raio != null) {
                        efeitos.adicionar(Imagem.EXPLOSAO, raio.getDestino().getX(), raio.getDestino().getY(), 0.45f);
                    }
                    jogo.sons().tocar(Som.COLISAO);
                    break;
                case VITORIA:
                    jogo.sons().pausarMusica();
                    jogo.sons().tocar(Som.VITORIA);
                    break;
                case DERROTA:
                    jogo.sons().pausarMusica();
                    jogo.sons().tocar(Som.DERROTA);
                    break;
                default:
                    break;
            }
        }

        if (Math.abs(variacaoDePontos) > 1) {
            String texto = (variacaoDePontos > 0 ? "+" : "") + variacaoDePontos;
            flutuantes.adicionar(texto, nave.getX(), nave.getY() - 0.4f, variacaoDePontos > 0);
        }
    }

    private void aplicarEstado(EstadoPartida estado) {
        if (estado == EstadoPartida.ABORTADA) {
            pararRepeticao();
            session.soltarTudo();
            jogo.setScreen(new MenuScreen(jogo));
        } else if (estado == EstadoPartida.VITORIA) {
            pararRepeticao();
            session.soltarTudo();
            if (jogo.sessao() == null) {
                novoRegistro = jogo.rankingService().registrarVitoria(session);
            } else {
                enviarResultado();
            }
        } else if (estado == EstadoPartida.DERROTA) {
            pararRepeticao();
            session.soltarTudo();
            if (jogo.sessao() != null) {
                enviarResultado();
            }
        }
    }

    private void enviarResultado() {
        envio = Envio.ENVIANDO;
        erroDeEnvio = "";
        log("enviando resultado ao servidor");
        jogo.servidor().enviarPartida(jogo.sessao(), PartidaConcluida.de(session), this::resultadoDoEnvio);
    }

    private void resultadoDoEnvio(Resposta<Void> resposta) {
        if (resposta.isSucesso()) {
            envio = Envio.ENVIADO;
            return;
        }
        envio = Envio.FALHOU;
        erroDeEnvio = resposta.getMensagem();
        log("falha ao enviar: " + erroDeEnvio);
        if (resposta.isNaoAutorizado()) {
            jogo.sair();
        }
    }

    private void encerrar() {
        if (session.getEstado() == EstadoPartida.VITORIA) {
            jogo.setScreen(new RankingScreen(jogo, session, novoRegistro));
        } else {
            jogo.setScreen(new MenuScreen(jogo));
        }
    }

    // ---------------------------------------------------------------- desenho

    @Override
    protected void desenhar(float delta) {
        layout = layoutAtual(jogo.viewport());
        mapa = geometriaDe(layout);
        mundo.definirMapa(mapa);
        atualizarBotoes();

        if (dinamico) {
            avancarTempoReal(delta);
        } else if (acaoSegurada != null && session.getEstado() == EstadoPartida.EM_ANDAMENTO && repetidor.atualizar(delta)) {
            executar(acaoSegurada);
        }
        if (jogo.getScreen() != this) {
            return;
        }

        Missao missao = session.getMissao();
        posicoes.sincronizar(todosOsObjetos(missao), delta);
        efeitos.atualizar(delta);
        flutuantes.atualizar(delta);
        tremor.atualizar(delta);
        tempoPiscando = Math.max(0f, tempoPiscando - delta);
        tempoDoRaio = Math.max(0f, tempoDoRaio - delta);
        if (dinamico && tempoDaMensagem > 0f) {
            tempoDaMensagem -= delta;
            if (tempoDaMensagem <= 0f) {
                mensagens = new ArrayList<>();
            }
        }

        boolean naveVisivel = tempoPiscando <= 0f || ((int) (tempoPiscando * 14f)) % 2 == 0;
        mundo.desenhar(missao, posicoes, efeitos, flutuantes, tempo, anguloNave, naveVisivel,
                tremor.deslocamentoX(), tremor.deslocamentoY());

        desenharRaio();
        desenharFlashDeColisao();

        boolean fim = session.getEstado() != EstadoPartida.EM_ANDAMENTO;
        hud.definirLayout(layout, jogo.margensSeguras(), jogo.modoToque(), jogo.fatorDeTexto());
        hud.desenhar(session, mensagens, jogo.sons().mudo(), session.getDuracaoSegundos());

        if (fim) {
            desenharFimDePartida();
        } else if (dinamico && session.isPausada()) {
            desenharPausa();
        } else if (dinamico && session.getContagemRestante() > 0f) {
            desenharContagem();
        }
    }

    private void avancarTempoReal(float delta) {
        if (session.getEstado() != EstadoPartida.EM_ANDAMENTO) {
            return;
        }
        int pontuacaoAntes = session.getPontuacao();
        Acao mira = session.getMira();
        anguloNave = anguloDe(mira);
        ResultadoTurno resultado = session.avancar(delta);
        if (!resultado.getEventos().isEmpty() || !resultado.getMensagens().isEmpty()) {
            tratarResultado(resultado, pontuacaoAntes);
        }
        int contagem = (int) Math.ceil(session.getContagemRestante());
        if (contagem != contagemMostrada) {
            if (contagem > 0 && contagemMostrada != -1) {
                jogo.sons().tocar(Som.SELECIONAR);
            }
            contagemMostrada = contagem;
        }
    }

    private List<ObjetoRenderizavel> todosOsObjetos(Missao missao) {
        List<ObjetoRenderizavel> objetos = new ArrayList<>();
        objetos.add(missao.getBase());
        objetos.addAll(missao.getAsteroides());
        objetos.addAll(missao.getInimigos());
        objetos.addAll(missao.getPassageiros());
        objetos.add(missao.getNave());
        return objetos;
    }

    private void desenharRaio() {
        if (raio == null || tempoDoRaio <= 0f) {
            return;
        }
        ShapeRenderer formas = jogo.shapes();
        float alfa = Math.min(1f, tempoDoRaio / DURACAO_DO_RAIO + 0.2f);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(1f, 0.55f, 0.2f, alfa * 0.5f);
        formas.rectLine(mapa.xCentro(raio.getOrigem().getX()), mapa.yCentro(raio.getOrigem().getY()),
                mapa.xCentro(raio.getDestino().getX()), mapa.yCentro(raio.getDestino().getY()), mapa.celula() * 0.28f);
        formas.setColor(1f, 0.95f, 0.5f, alfa);
        formas.rectLine(mapa.xCentro(raio.getOrigem().getX()), mapa.yCentro(raio.getOrigem().getY()),
                mapa.xCentro(raio.getDestino().getX()), mapa.yCentro(raio.getDestino().getY()), mapa.celula() * 0.1f);
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void desenharFlashDeColisao() {
        if (!tremor.ativo()) {
            return;
        }
        ShapeRenderer formas = jogo.shapes();
        RetanguloDeTela m = layout.mapa();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(1f, 0.15f, 0.1f, 0.3f * tremor.intensidade());
        formas.rect(m.x(), m.y(), m.largura(), m.altura());
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /** Caixa escura no centro do mapa, usada na pausa e no fim da partida. */
    private RetanguloDeTela desenharCaixa(float larguraDesejada, float altura) {
        RetanguloDeTela m = layout.mapa();
        float largura = Math.min(larguraDesejada, m.largura() - 8f);
        RetanguloDeTela caixa = new RetanguloDeTela(m.centroX() - largura / 2f, m.centroY() - altura / 2f, largura, altura);
        ShapeRenderer formas = jogo.shapes();
        formas.begin(ShapeType.Filled);
        formas.setColor(CAIXA);
        formas.rect(caixa.x(), caixa.y(), caixa.largura(), caixa.altura());
        formas.end();
        formas.begin(ShapeType.Line);
        formas.setColor(DESTAQUE);
        formas.rect(caixa.x(), caixa.y(), caixa.largura(), caixa.altura());
        formas.end();
        return caixa;
    }

    // Reduz o titulo se o mapa for pequeno (telas estreitas ou controles ocupando espaco).
    private float escalaDoTitulo(float base, RetanguloDeTela caixa) {
        float maximo = caixa.largura() / (170f * Math.max(1f, jogo.fatorDeTexto()));
        return Math.max(0.8f, Math.min(base, maximo));
    }

    private void desenharPausa() {
        RetanguloDeTela caixa = desenharCaixa(380f, 150f);
        jogo.batch().begin();
        escala(escalaDoTitulo(1.8f, caixa));
        cor(DESTAQUE);
        textoCentralizado("PAUSADO", caixa.centroX(), caixa.centroY() + 30f);
        escala(1f);
        cor(Color.WHITE);
        if (!jogo.modoToque()) {
            textoCentralizado("P ou Enter: continuar", caixa.centroX(), caixa.centroY() - 10f);
            textoCentralizado("Q ou Esc: abortar", caixa.centroX(), caixa.centroY() - 34f);
        }
        jogo.batch().end();
    }

    private void desenharContagem() {
        RetanguloDeTela m = layout.mapa();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        jogo.shapes().begin(ShapeType.Filled);
        jogo.shapes().setColor(0f, 0f, 0.05f, 0.45f);
        jogo.shapes().rect(m.x(), m.y(), m.largura(), m.altura());
        jogo.shapes().end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        jogo.batch().begin();
        escala(Math.max(2f, Math.min(5f, m.largura() / 110f)));
        cor(DESTAQUE);
        textoCentralizado(String.valueOf(Math.max(1, (int) Math.ceil(session.getContagemRestante()))), m.centroX(), m.centroY() + 12f);
        escala(1.2f);
        cor(Color.WHITE);
        textoCentralizado("Prepare-se!", m.centroX(), m.centroY() - 50f);
        jogo.batch().end();
    }

    private void desenharFimDePartida() {
        RetanguloDeTela caixa = desenharCaixa(460f, 150f);
        float cx = caixa.centroX();
        float cy = caixa.centroY();

        boolean vitoria = session.getEstado() == EstadoPartida.VITORIA;
        jogo.batch().begin();
        escala(escalaDoTitulo(1.8f, caixa));
        cor(vitoria ? VITORIA : DERROTA);
        textoCentralizado(vitoria ? "MISSÃO CUMPRIDA!" : "GAME OVER", cx, cy + 45f);
        escala(1f);
        cor(Color.WHITE);
        textoCentralizado("Pontuação: " + session.getPontuacao(), cx, cy + 12f);
        desenharSituacaoDoEnvio(cx, cy);
        cor(Color.WHITE);
        if (!jogo.modoToque()) {
            textoCentralizado(vitoria ? "Enter para ver o ranking" : "Enter para voltar ao menu", cx, cy - 52f);
        }
        jogo.batch().end();
    }

    private void desenharSituacaoDoEnvio(float cx, float cy) {
        switch (envio) {
            case ENVIANDO:
                cor(Color.LIGHT_GRAY);
                textoCentralizado("Enviando o resultado ao servidor...", cx, cy - 20f);
                break;
            case ENVIADO:
                cor(VITORIA);
                textoCentralizado("Resultado enviado ao servidor.", cx, cy - 20f);
                break;
            case FALHOU:
                cor(DERROTA);
                String resumo = erroDeEnvio.length() > 52 ? erroDeEnvio.substring(0, 49) + "..." : erroDeEnvio;
                textoCentralizado(resumo, cx, cy - 12f);
                if (jogo.sessao() != null && !jogo.modoToque()) {
                    textoCentralizado("R: tentar de novo", cx, cy - 32f);
                }
                break;
            default:
                break;
        }
    }
}
