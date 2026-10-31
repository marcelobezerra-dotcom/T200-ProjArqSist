package br.edu.unifor.missaomarte.servidor.seguranca;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/** Valores de application.properties com prefixo missaomarte.seguranca. */
@ConfigurationProperties(prefix = "missaomarte.seguranca")
public record PropriedadesDeSeguranca(
        @DefaultValue("") String segredo,
        @DefaultValue("2h") Duration validade) {
}
