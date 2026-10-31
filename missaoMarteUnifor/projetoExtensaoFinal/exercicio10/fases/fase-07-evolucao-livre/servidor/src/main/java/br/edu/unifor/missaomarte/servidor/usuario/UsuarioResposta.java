package br.edu.unifor.missaomarte.servidor.usuario;

/** Dados publicos do usuario. Nao tem campo de senha nem de hash. */
public record UsuarioResposta(Long id, String nome) {
    public static UsuarioResposta de(Usuario usuario) {
        return new UsuarioResposta(usuario.getId(), usuario.getNome());
    }
}
