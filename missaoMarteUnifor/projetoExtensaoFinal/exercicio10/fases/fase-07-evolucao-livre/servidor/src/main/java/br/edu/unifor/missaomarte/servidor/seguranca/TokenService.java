package br.edu.unifor.missaomarte.servidor.seguranca;

import br.edu.unifor.missaomarte.servidor.usuario.Usuario;
import java.time.Instant;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/** Emite o token assinado (HS256). O id do usuario vai no campo "sub"; a senha nunca entra no token. */
@Service
public class TokenService {
    private final JwtEncoder encoder;
    private final PropriedadesDeSeguranca propriedades;

    public TokenService(JwtEncoder encoder, PropriedadesDeSeguranca propriedades) {
        this.encoder = encoder;
        this.propriedades = propriedades;
    }

    public TokenEmitido emitir(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiraEm = agora.plus(propriedades.validade());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("missao-marte")
                .subject(String.valueOf(usuario.getId()))
                .issuedAt(agora)
                .expiresAt(expiraEm)
                .claim("nome", usuario.getNome())
                .build();
        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(cabecalho, claims)).getTokenValue();
        return new TokenEmitido(token, expiraEm);
    }

    public record TokenEmitido(String token, Instant expiraEm) {
    }
}
