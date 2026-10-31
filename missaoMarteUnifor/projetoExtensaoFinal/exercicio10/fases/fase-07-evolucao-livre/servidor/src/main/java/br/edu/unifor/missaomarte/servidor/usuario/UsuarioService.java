package br.edu.unifor.missaomarte.servidor.usuario;

import br.edu.unifor.missaomarte.servidor.erro.CredenciaisInvalidasException;
import br.edu.unifor.missaomarte.servidor.erro.NomeJaExisteException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Regras de cadastro de usuarios. */
@Service
public class UsuarioService {
    private final UsuarioRepository repositorio;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repositorio, PasswordEncoder encoder) {
        this.repositorio = repositorio;
        this.encoder = encoder;
    }

    @Transactional
    public Usuario cadastrar(String nome, String senha) {
        if (repositorio.existsByNomeNormalizado(Usuario.normalizar(nome))) {
            throw new NomeJaExisteException();
        }
        // saveAndFlush: se dois cadastros iguais chegarem juntos, o indice unico do banco acusa o conflito aqui.
        return repositorio.saveAndFlush(new Usuario(nome, encoder.encode(senha)));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return repositorio.findById(id).orElseThrow(CredenciaisInvalidasException::new);
    }
}
