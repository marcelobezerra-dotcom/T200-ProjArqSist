package br.edu.unifor.missaomarte.servidor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Servidor do Missao Marte: cadastro, login, historico de partidas e ranking global. */
@SpringBootApplication
public class ServidorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServidorApplication.class, args);
    }
}
