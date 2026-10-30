package br.edu.unifor.missaomarte.servidor.auth;

import br.edu.unifor.missaomarte.servidor.erro.CredenciaisInvalidasException;
import br.edu.unifor.missaomarte.servidor.seguranca.TokenService;
import br.edu.unifor.missaomarte.servidor.seguranca.TokenService.TokenEmitido;
import br.edu.unifor.missaomarte.servidor.usuario.Usuario;
import br.edu.unifor.missaomarte.servidor.usuario.UsuarioRepository;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Confere nome e senha e emite o token. */
@Service
public class AutenticacaoService {
    private final UsuarioRepository repositorio;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;
    /** Hash usado quando o nome nao existe, para o tempo de resposta ser parecido com o de uma senha errada. */
    private final String hashFalso;

    public AutenticacaoService(UsuarioRepository repositorio, PasswordEncoder encoder, TokenService tokenService) {
        this.repositorio = repositorio;
        this.encoder = encoder;
        this.tokenService = tokenService;
        this.hashFalso = encoder.encode("senha-que-nao-pertence-a-ninguem");
    }

    public TokenEmitido entrar(String nome, String senha) {
        Optional<Usuario> usuario = repositorio.findByNomeNormalizado(Usuario.normalizar(nome));
        String hash = usuario.map(Usuario::getSenhaHash).orElse(hashFalso);
        boolean senhaCorreta = encoder.matches(senha, hash);
        if (usuario.isEmpty() || !senhaCorreta) {
            throw new CredenciaisInvalidasException();
        }
        return tokenService.emitir(usuario.get());
    }
}
