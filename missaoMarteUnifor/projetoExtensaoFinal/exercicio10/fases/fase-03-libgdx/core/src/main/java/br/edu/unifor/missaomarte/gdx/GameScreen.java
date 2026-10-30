package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.Acao;
import br.edu.unifor.missaomarte.application.EstadoPartida;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.ResultadoTurno;
import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Base;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.ObjetoRenderizavel;
import br.edu.unifor.missaomarte.domain.Professor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela da partida: desenha o mapa com formas e simbolos e converte teclas em {@link Acao} para a {@link GameSession}.
 * <p>GRASP: Controller de interface (nao tem regra de jogo; so traduz entrada, chama a sessao e desenha o estado).
 * <p>O jogo e por turnos: cada tecla valida processa um turno; render() so redesenha o estado atual.
 * <p>Limite: o desenho usa cores por categoria (base, perigos, passageiros, nave); sprites e animacoes ficam para a Fase 4.
 */
public class GameScreen extends TelaBase {
    private static final float CELULA = 48f;
    private static final float PAINEL_X = 580f;
    private static final float PAINEL_LARGURA = 200f;
    private static final float CENTRO_MAPA_X = 24f + 11 * CELULA / 2f;
    private static final float CENTRO_MAPA_Y = 36f + 11 * CELULA / 2f;

    private static final Color FUNDO_CELULA = new Color(0.10f, 0.10f, 0.16f, 1f);
    private static final Color GRADE = new Color(0.25f, 0.25f, 0.35f, 1f);
    private static final Color COR_BASE = new Color(0.25f, 0.75f, 0.45f, 1f);
    private static final Color COR_ASTEROIDE = new Color(0.65f, 0.65f, 0.65f, 1f);
    private static final Color COR_INIMIGO = new Color(0.90f, 0.25f, 0.25f, 1f);
    private static final Color COR_PASSAGEIRO = new Color(0.95f, 0.85f, 0.20f, 1f);
    private static final Color COR_NAVE = new Color(0.30f, 0.85f, 0.95f, 1f);
    private static final Color DESTAQUE = new Color(1f, 0.85f, 0.3f, 1f);
    private static final Color CAIXA = new Color(0.05f, 0.05f, 0.12f, 1f);

    private record Camada(List<? extends ObjetoRenderizavel> objetos, Color cor) {
    }

    private final GameSession session;
    private final MapaGeometria mapa = new MapaGeometria(MissaoMarteGame.MIN, MissaoMarteGame.MAX, CELULA, 24f, 36f);
    private final String legenda = montarLegenda();

    private List<String> mensagens = new ArrayList<>();
    private boolean novoRegistro;

    public GameScreen(MissaoMarteGame jogo, GameSession session) {
        super(jogo);
        this.session = session;
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                return tratarTecla(keycode);
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

        ResultadoTurno resultado = session.processarAcao(acao);
        mensagens = new ArrayList<>(resultado.getMensagens());
        aplicarEstado(resultado.getEstado());
        return true;
    }

    private void aplicarEstado(EstadoPartida estado) {
        if (estado == EstadoPartida.ABORTADA) {
            jogo.setScreen(new MenuScreen(jogo));
        } else if (estado == EstadoPartida.VITORIA) {
            novoRegistro = jogo.rankingService().registrarVitoria(session);
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
        List<Camada> camadas = camadas(session.getMissao());

        desenharCelulasEObjetos(camadas);

        jogo.batch().begin();
        desenharSimbolos(camadas);
        desenharPainel();
        cor(Color.LIGHT_GRAY);
        texto("W/A/S/D ou setas: mover | E: embarcar | Q ou ESC: abortar", 24f, 24f);
        jogo.batch().end();

        if (session.getEstado() != EstadoPartida.EM_ANDAMENTO) {
            desenharFimDePartida();
        }
    }

    // Ordem de desenho: a nave por ultimo, para prevalecer quando divide a celula com outro objeto.
    private List<Camada> camadas(Missao missao) {
        List<Camada> camadas = new ArrayList<>();
        camadas.add(new Camada(List.of(missao.getBase()), COR_BASE));
        camadas.add(new Camada(missao.getAsteroides(), COR_ASTEROIDE));
        camadas.add(new Camada(missao.getInimigos(), COR_INIMIGO));
        camadas.add(new Camada(missao.getPassageiros(), COR_PASSAGEIRO));
        camadas.add(new Camada(List.of(missao.getNave()), COR_NAVE));
        return camadas;
    }

    private void desenharCelulasEObjetos(List<Camada> camadas) {
        ShapeRenderer formas = jogo.shapes();

        formas.begin(ShapeType.Filled);
        formas.setColor(FUNDO_CELULA);
        for (int y = MissaoMarteGame.MIN; y <= MissaoMarteGame.MAX; y++) {
            for (int x = MissaoMarteGame.MIN; x <= MissaoMarteGame.MAX; x++) {
                formas.rect(mapa.xCanto(x), mapa.yCanto(y), CELULA, CELULA);
            }
        }
        for (Camada camada : camadas) {
            formas.setColor(camada.cor());
            for (ObjetoRenderizavel objeto : camada.objetos()) {
                formas.rect(mapa.xCanto(objeto.getPosicao().getX()) + 4f, mapa.yCanto(objeto.getPosicao().getY()) + 4f,
                        CELULA - 8f, CELULA - 8f);
            }
        }
        formas.end();

        formas.begin(ShapeType.Line);
        formas.setColor(GRADE);
        for (int y = MissaoMarteGame.MIN; y <= MissaoMarteGame.MAX; y++) {
            for (int x = MissaoMarteGame.MIN; x <= MissaoMarteGame.MAX; x++) {
                formas.rect(mapa.xCanto(x), mapa.yCanto(y), CELULA, CELULA);
            }
        }
        formas.end();
    }

    private void desenharSimbolos(List<Camada> camadas) {
        cor(Color.BLACK);
        escala(1.3f);
        for (Camada camada : camadas) {
            for (ObjetoRenderizavel objeto : camada.objetos()) {
                textoCentralizado(String.valueOf(objeto.getSimbolo()),
                        mapa.xCentro(objeto.getPosicao().getX()), mapa.yCentro(objeto.getPosicao().getY()));
            }
        }
        escala(1f);
    }

    private void desenharPainel() {
        Missao missao = session.getMissao();
        Nave nave = missao.getNave();
        float y = 584f;

        escala(1.3f);
        cor(DESTAQUE);
        texto("MISSAO MARTE", PAINEL_X, y);
        escala(1f);
        cor(Color.WHITE);
        y -= 34f;
        texto("Piloto: " + session.getPiloto(), PAINEL_X, y);
        y -= 22f;
        texto("Dificuldade: " + session.getDificuldade(), PAINEL_X, y);
        y -= 30f;
        texto("Vidas: " + nave.getVidas(), PAINEL_X, y);
        y -= 22f;
        texto("Pontuacao: " + session.getPontuacao(), PAINEL_X, y);
        y -= 22f;
        texto("A bordo: " + nave.getPassageiros().size() + "/" + nave.getCapacidade(), PAINEL_X, y);
        y -= 22f;
        texto("Entregues: " + session.getResgatados() + "/" + session.getTotalPassageiros(), PAINEL_X, y);
        y -= 22f;
        texto("Restantes no mapa: " + missao.getPassageiros().size(), PAINEL_X, y);
        y -= 22f;
        texto("Movimentos: " + session.getMovimentos(), PAINEL_X, y);

        y -= 40f;
        if (!mensagens.isEmpty()) {
            cor(DESTAQUE);
            textoQuebrado(String.join("\n", mensagens), PAINEL_X, y, PAINEL_LARGURA);
        }

        cor(Color.LIGHT_GRAY);
        textoQuebrado(legenda, PAINEL_X, 130f, PAINEL_LARGURA);
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
        cor(vitoria ? COR_BASE : COR_INIMIGO);
        textoCentralizado(vitoria ? "MISSAO CUMPRIDA!" : "GAME OVER", CENTRO_MAPA_X, CENTRO_MAPA_Y + 35f);
        escala(1f);
        cor(Color.WHITE);
        textoCentralizado("Pontuacao: " + session.getPontuacao(), CENTRO_MAPA_X, CENTRO_MAPA_Y);
        textoCentralizado(vitoria ? "ENTER para ver o ranking" : "ENTER para voltar ao menu", CENTRO_MAPA_X, CENTRO_MAPA_Y - 30f);
        jogo.batch().end();
    }

    // Os simbolos vem das proprias classes de dominio; limite herdado da Fase 1: usa objetos de exemplo.
    private static String montarLegenda() {
        return "Legenda: "
                + new Nave(0, 0, 0, 0).getSimbolo() + "=nave, "
                + new Base(0, 0).getSimbolo() + "=base, "
                + new Professor("", 0, 0).getSimbolo() + "/"
                + new Engenheiro("", 0, 0).getSimbolo() + "/"
                + new Astronauta("", 0, 0).getSimbolo() + "=passageiros, "
                + new Asteroide(0, 0).getSimbolo() + "=asteroide, "
                + new Inimigo(0, 0).getSimbolo() + "=inimigo";
    }
}
