package br.edu.unifor.missaomarte.servidor.seguranca;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Metadados da documentação (Swagger UI) e o esquema "bearer" usado pelo botão Authorize. */
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Missão Marte - Servidor",
        version = "1.0",
        description = "Cadastro, login, histórico de partidas e ranking global. "
                + "Faça o login em POST /api/auth/login, copie o token e cole em Authorize (sem a palavra Bearer)."))
@SecurityScheme(name = "bearer", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
