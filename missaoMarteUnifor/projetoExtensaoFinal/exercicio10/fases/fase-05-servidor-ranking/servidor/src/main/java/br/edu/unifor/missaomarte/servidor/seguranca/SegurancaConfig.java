package br.edu.unifor.missaomarte.servidor.seguranca;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Regras de seguranca: tudo exige token, exceto cadastro e login.
 * <p>API sem estado (stateless): nao ha sessao nem cookie, entao CSRF nao se aplica e e desligado.
 */
@Configuration
@EnableConfigurationProperties(PropriedadesDeSeguranca.class)
public class SegurancaConfig {
    private static final Logger LOG = LoggerFactory.getLogger(SegurancaConfig.class);
    private static final int TAMANHO_MINIMO_SEGREDO = 32;

    @Bean
    SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.POST, "/api/usuarios", "/api/auth/login").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        return http.build();
    }

    /** BCrypt: hash lento e com sal proprio. So considera os 72 primeiros bytes da senha. */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecretKey chaveDoToken(PropriedadesDeSeguranca propriedades) {
        byte[] bytes;
        if (propriedades.segredo() == null || propriedades.segredo().isBlank()) {
            bytes = new byte[TAMANHO_MINIMO_SEGREDO];
            new SecureRandom().nextBytes(bytes);
            LOG.warn("missaomarte.seguranca.segredo nao definido: usando segredo aleatorio; os tokens deixam de valer ao reiniciar.");
        } else {
            bytes = propriedades.segredo().getBytes(StandardCharsets.UTF_8);
            if (bytes.length < TAMANHO_MINIMO_SEGREDO) {
                throw new IllegalStateException("O segredo do token precisa ter pelo menos " + TAMANHO_MINIMO_SEGREDO + " bytes.");
            }
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey chaveDoToken) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chaveDoToken));
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey chaveDoToken) {
        return NimbusJwtDecoder.withSecretKey(chaveDoToken).macAlgorithm(MacAlgorithm.HS256).build();
    }
}
