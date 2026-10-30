package br.edu.unifor.missaomarte.gdx.rede;

import br.edu.unifor.missaomarte.application.ranking.RankingEntry;
import br.edu.unifor.missaomarte.application.servidor.PartidaConcluida;
import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import br.edu.unifor.missaomarte.application.servidor.Resposta;
import br.edu.unifor.missaomarte.application.servidor.Retorno;
import br.edu.unifor.missaomarte.application.servidor.Sessao;
import br.edu.unifor.missaomarte.application.servidor.ServidorDoJogo;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import java.util.List;
import java.util.function.Function;

/**
 * Adaptador HTTP da porta {@link ServidorDoJogo}, feito com {@code Gdx.net} (funciona no desktop e no Android).
 * <p>O libGDX chama o listener numa thread de rede; cada resposta e repassada ao jogo com {@code Gdx.app.postRunnable},
 * porque o desenho e as telas so podem ser tocados na thread principal.
 * <p>Seguranca: o token vai so no cabecalho Authorization; a senha e o token nunca sao escritos no log.
 */
public class ServidorHttp implements ServidorDoJogo {
    private static final int TEMPO_LIMITE_MS = 5000;

    private final String urlBase;

    public ServidorHttp(String urlBase) {
        this.urlBase = urlBase.endsWith("/") ? urlBase.substring(0, urlBase.length() - 1) : urlBase;
    }

    @Override
    public void cadastrar(String nome, String senha, Retorno<Void> retorno) {
        HttpRequest pedido = novoPedido(HttpMethods.POST, "/api/usuarios", null);
        pedido.setContent(JsonDoServidor.corpoDeCredenciais(nome, senha));
        enviar(pedido, retorno, corpo -> null);
    }

    @Override
    public void entrar(String nome, String senha, Retorno<Sessao> retorno) {
        HttpRequest pedido = novoPedido(HttpMethods.POST, "/api/auth/login", null);
        pedido.setContent(JsonDoServidor.corpoDeCredenciais(nome, senha));
        enviar(pedido, retorno, corpo -> new Sessao(nome, JsonDoServidor.lerToken(corpo)));
    }

    @Override
    public void enviarPartida(Sessao sessao, PartidaConcluida partida, Retorno<Void> retorno) {
        HttpRequest pedido = novoPedido(HttpMethods.POST, "/api/partidas", sessao);
        pedido.setContent(JsonDoServidor.corpoDePartida(partida));
        enviar(pedido, retorno, corpo -> null);
    }

    @Override
    public void carregarRanking(Sessao sessao, Retorno<List<RankingEntry>> retorno) {
        enviar(novoPedido(HttpMethods.GET, "/api/ranking", sessao), retorno, JsonDoServidor::lerRanking);
    }

    @Override
    public void carregarHistorico(Sessao sessao, Retorno<List<PartidaRegistrada>> retorno) {
        enviar(novoPedido(HttpMethods.GET, "/api/partidas/minhas?pagina=0&tamanho=10", sessao), retorno,
                JsonDoServidor::lerHistorico);
    }

    private HttpRequest novoPedido(String metodo, String caminho, Sessao sessao) {
        HttpRequest pedido = new HttpRequest(metodo);
        pedido.setUrl(urlBase + caminho);
        pedido.setTimeOut(TEMPO_LIMITE_MS);
        pedido.setHeader("Accept", "application/json");
        if (HttpMethods.POST.equals(metodo)) {
            pedido.setHeader("Content-Type", "application/json");
        }
        if (sessao != null) {
            pedido.setHeader("Authorization", "Bearer " + sessao.getToken());
        }
        return pedido;
    }

    private <T> void enviar(HttpRequest pedido, Retorno<T> retorno, Function<String, T> conversor) {
        Gdx.net.sendHttpRequest(pedido, new HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse resposta) {
                int status = resposta.getStatus().getStatusCode();
                String corpo = resposta.getResultAsString();
                entregar(retorno, interpretar(status, corpo, conversor));
            }

            @Override
            public void failed(Throwable t) {
                entregar(retorno, Resposta.<T>erro("Não foi possível conectar ao servidor (" + urlBase + ")."));
            }

            @Override
            public void cancelled() {
                entregar(retorno, Resposta.<T>erro("Chamada ao servidor cancelada."));
            }
        });
    }

    private <T> Resposta<T> interpretar(int status, String corpo, Function<String, T> conversor) {
        if (status >= 200 && status < 300) {
            try {
                return Resposta.sucesso(conversor.apply(corpo));
            } catch (RuntimeException e) {
                return Resposta.erro("Resposta inesperada do servidor.");
            }
        }
        String mensagem = JsonDoServidor.mensagemDeErro(status, corpo);
        // 401 em chamada autenticada = token expirado; no login significa senha errada (a tela decide pela mensagem).
        return status == 401 ? Resposta.<T>erroNaoAutorizado(mensagem) : Resposta.<T>erro(mensagem);
    }

    private static <T> void entregar(Retorno<T> retorno, Resposta<T> resposta) {
        Gdx.app.postRunnable(() -> retorno.concluido(resposta));
    }
}
