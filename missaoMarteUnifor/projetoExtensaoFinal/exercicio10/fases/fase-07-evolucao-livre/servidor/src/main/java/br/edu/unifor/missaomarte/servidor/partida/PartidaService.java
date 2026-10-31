package br.edu.unifor.missaomarte.servidor.partida;

import br.edu.unifor.missaomarte.servidor.usuario.UsuarioService;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Registro e consulta de partidas do usuario autenticado. */
@Service
public class PartidaService {
    private final PartidaRepository repositorio;
    private final UsuarioService usuarioService;

    public PartidaService(PartidaRepository repositorio, UsuarioService usuarioService) {
        this.repositorio = repositorio;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Partida registrar(Long usuarioId, PartidaRequisicao r) {
        var usuario = usuarioService.buscarPorId(usuarioId);
        return repositorio.save(new Partida(usuario, r.dificuldade(), r.resultado(),
                r.pontuacao(), r.duracaoSegundos(), r.movimentos()));
    }

    @Transactional(readOnly = true)
    public List<Partida> historico(Long usuarioId, int pagina, int tamanho) {
        return repositorio.findByUsuarioIdOrderByJogadaEmDescIdDesc(usuarioId, PageRequest.of(pagina, tamanho));
    }
}
