package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.EventoJogo;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.ResultadoTurno;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.gdx.hud.HudRenderer;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Tela da partida: converte teclas em {@link Acao}, chama a {@link GameSession} e reage ao que aconteceu no turno.
 * <p>GRASP: Controller de interface. Nao tem regra de jogo: o resultado do turno (estado, mensagens e eventos) vem da sessao.
 * <p>O jogo e por turnos e a regra e instantanea; o mundo desliza entre as celulas apenas no desenho ({@link PosicoesVisuais}).
 * <p>Os eventos do turno disparam sons, efeitos visuais, tremor e textos flutuantes (Observer simplificado).
 */
public class GameScreen extends TelaBase {
    private static final float CELULA = 48f;
    private static final float ORIGEM_X = 24f;
    private static final float ORIGEM_Y = 36f;
    private static final float VELOCIDADE_VISUAL = 12f;
    private static final float CENTRO_MAPA_X = ORIGEM_X + 11 * CELULA / 2f;
    private static final float CENTRO_MAPA_Y = ORIGEM_Y + 11 * CELULA / 2f;

    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);
    private static final Color VITORIA = new Color(0.45f, 0.9f, 0.55f, 1f);
    private static final Color DERROTA = new Color(1f, 0.4f, 0.4f, 1f);
    private static final Color CAIXA = new Color(0.05f, 0.05f, 0.12f, 1f);

    private final GameSession session;
    private final MapaGeometria mapa = new MapaGeometria(MissaoMarteGame.MIN, MissaoMarteGame.MAX, CELULA, ORIGEM_X, ORIGEM_Y);
    private final PosicoesVisuais posicoes = new PosicoesVisuais(VELOCIDADE_VISUAL);
    private final EfeitosVisuais efeitos = new EfeitosVisuais();
    private final MensagensFlutuantes flutuantes = new MensagensFlutuantes();
    private final Tremor tremor = new Tremor();
    private final RepetidorDeTecla repetidor = new RepetidorDeTecla(0.28f, 0.14f);
    private final MundoRenderer mundo;
    private final HudRenderer hud;

    private List<String> mensagens = new ArrayList<>();
    private boolean novoRegistro;
    private float anguloNave;
    private float tempoPiscando;
    private Acao acaoSegurada;
    private int teclaSegurada = -1;

    public GameScreen(MissaoMarteGame jogo, GameSession session) {
        super(jogo);
        this.session = session;
        this.mundo = new MundoRenderer(jogo.recursos(), jogo.batch(), jogo.fonte(), mapa);
        this.hud = new HudRenderer(jogo.recursos(), jogo.batch(), jogo.shapes(), jogo.fonte());
    }

    @Override
    public void show() {
        super.show();
        definirEntrada(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return tratarTecla(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == teclaSegurada) {
                    pararRepeticao();
                }
                return false;
            }
        });
    }

    private boolean tratarTecla(int keycode) {
        if (session.getEstado() != EstadoPartida.EM_ANDAMENTO) {
            if (keycode == Keys.ENTER || keycode == Keys.NUMPAD_ENTER) {
                encerrar();
                return true;
            }
            return false;
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

    private void pararRepeticao() {
        acaoSegurada = null;
        teclaSegurada = -1;
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
        mensagens = new ArrayList<>(resultado.getMensagens());
        if (ehMovimento(acao)) {
            anguloNave = anguloDe(acao);
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
            jogo.setScreen(new MenuScreen(jogo));
        } else if (estado == EstadoPartida.VITORIA) {
            pararRepeticao();
            novoRegistro = jogo.rankingService().registrarVitoria(session);
        } else if (estado == EstadoPartida.DERROTA) {
            pararRepeticao();
        }
    }

    private void encerrar() {
        if (session.getEstado() == EstadoPartida.VITORIA) {
            jogo.setScreen(new RankingScreen(jogo, session, novoRegistro));
        } else {
            jogo.setScreen(new MenuScreen(jogo));
        }
    }

    @Override
    protected void desenhar(float delta) {
        if (acaoSegurada != null && session.getEstado() == EstadoPartida.EM_ANDAMENTO && repetidor.atualizar(delta)) {
            executar(acaoSegurada);
            if (jogo.getScreen() != this) {
                return;
            }
        }

        Missao missao = session.getMissao();
        posicoes.sincronizar(todosOsObjetos(missao), delta);
        efeitos.atualizar(delta);
        flutuantes.atualizar(delta);
        tremor.atualizar(delta);
        tempoPiscando = Math.max(0f, tempoPiscando - delta);

        boolean naveVisivel = tempoPiscando <= 0f || ((int) (tempoPiscando * 14f)) % 2 == 0;
        mundo.desenhar(missao, posicoes, efeitos, flutuantes, tempo, anguloNave, naveVisivel,
                tremor.deslocamentoX(), tremor.deslocamentoY());

        desenharFlashDeColisao();

        boolean fim = session.getEstado() != EstadoPartida.EM_ANDAMENTO;
        hud.desenhar(session, mensagens, jogo.sons().mudo(), session.getDuracaoSegundos());

        if (fim) {
            desenharFimDePartida();
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

    private void desenharFlashDeColisao() {
        if (!tremor.ativo()) {
            return;
        }
        ShapeRenderer formas = jogo.shapes();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(1f, 0.15f, 0.1f, 0.3f * tremor.intensidade());
        formas.rect(ORIGEM_X, ORIGEM_Y, 11 * CELULA, 11 * CELULA);
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void desenharFimDePartida() {
        ShapeRenderer formas = jogo.shapes();
        float largura = 380f;
        float altura = 130f;
        float x = CENTRO_MAPA_X - largura / 2f;
        float y = CENTRO_MAPA_Y - altura / 2f;

        formas.begin(ShapeType.Filled);
        formas.setColor(CAIXA);
        formas.rect(x, y, largura, altura);
        formas.end();
        formas.begin(ShapeType.Line);
        formas.setColor(DESTAQUE);
        formas.rect(x, y, largura, altura);
        formas.end();

        boolean vitoria = session.getEstado() == EstadoPartida.VITORIA;
        jogo.batch().begin();
        escala(1.8f);
        cor(vitoria ? VITORIA : DERROTA);
        textoCentralizado(vitoria ? "MISSÃO CUMPRIDA!" : "GAME OVER", CENTRO_MAPA_X, CENTRO_MAPA_Y + 35f);
        escala(1f);
        cor(Color.WHITE);
        textoCentralizado("Pontuação: " + session.getPontuacao(), CENTRO_MAPA_X, CENTRO_MAPA_Y);
        textoCentralizado(vitoria ? "Enter para ver o ranking" : "Enter para voltar ao menu", CENTRO_MAPA_X, CENTRO_MAPA_Y - 30f);
        jogo.batch().end();
    }
}
