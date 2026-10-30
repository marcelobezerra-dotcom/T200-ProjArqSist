package br.edu.unifor.missaomarte.servidor.partida;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PartidaRepository extends JpaRepository<Partida, Long> {

    /** Historico do jogador, da partida mais recente para a mais antiga. */
    List<Partida> findByUsuarioIdOrderByJogadaEmDescIdDesc(Long usuarioId, Pageable pagina);

    /**
     * Uma linha por jogador: a melhor vitoria com pontuacao positiva (maior pontuacao, depois menor duracao, depois a mais antiga).
     * A lista inteira e ordenada pelas mesmas regras; o limite do Top 10 vem do Pageable.
     */
    @Query("""
            select p from Partida p join fetch p.usuario
            where p.resultado = br.edu.unifor.missaomarte.servidor.partida.Resultado.VITORIA
              and p.pontuacao > 0
              and p.id = (
                  select p2.id from Partida p2
                  where p2.usuario = p.usuario
                    and p2.resultado = br.edu.unifor.missaomarte.servidor.partida.Resultado.VITORIA
                    and p2.pontuacao > 0
                  order by p2.pontuacao desc, p2.duracaoSegundos asc, p2.id asc
                  limit 1)
            order by p.pontuacao desc, p.duracaoSegundos asc, p.id asc
            """)
    List<Partida> melhoresPartidasPorJogador(Pageable pagina);
}
