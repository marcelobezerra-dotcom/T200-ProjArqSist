package br.edu.unifor.missaomarte.servidor.usuario;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNomeNormalizado(String nomeNormalizado);

    boolean existsByNomeNormalizado(String nomeNormalizado);
}
