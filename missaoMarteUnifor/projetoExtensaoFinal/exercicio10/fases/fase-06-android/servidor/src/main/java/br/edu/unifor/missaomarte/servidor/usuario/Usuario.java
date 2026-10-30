package br.edu.unifor.missaomarte.servidor.usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Locale;

/** Jogador cadastrado. Guarda somente o hash BCrypt da senha, nunca a senha. */
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String nome;

    /** Nome em minusculas: garante nome unico sem diferenciar maiusculas de minusculas. */
    @Column(name = "nome_normalizado", nullable = false, unique = true, length = 20)
    private String nomeNormalizado;

    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected Usuario() {
    }

    public Usuario(String nome, String senhaHash) {
        this.nome = nome;
        this.nomeNormalizado = normalizar(nome);
        this.senhaHash = senhaHash;
        this.criadoEm = Instant.now();
    }

    public static String normalizar(String nome) {
        return nome.toLowerCase(Locale.ROOT);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
