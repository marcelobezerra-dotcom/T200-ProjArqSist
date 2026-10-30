package br.edu.unifor.missaomarte.gdx;

/**
 * Estado e regras do formulario de entrada/cadastro (sem libGDX, para poder ser testado).
 * <p>Os caracteres aceitos seguem as regras do servidor: nome com letras, numeros, _ e - (3 a 20); senha ASCII imprimivel (8 a 72).
 * A senha so e exposta mascarada para desenhar na tela.
 */
public class FormularioDeLogin {
    public enum Modo { ENTRAR, CADASTRAR }

    public enum Campo { NOME, SENHA }

    public static final int NOME_MIN = 3;
    public static final int NOME_MAX = 20;
    public static final int SENHA_MIN = 8;
    public static final int SENHA_MAX = 72;

    private final StringBuilder nome = new StringBuilder();
    private final StringBuilder senha = new StringBuilder();
    private Modo modo = Modo.ENTRAR;
    private Campo campo = Campo.NOME;

    /** Devolve true se o caractere foi aceito no campo atual. */
    public boolean digitar(char c) {
        if (campo == Campo.NOME) {
            if (nome.length() < NOME_MAX && nomeValido(c)) {
                nome.append(c);
                return true;
            }
            return false;
        }
        if (senha.length() < SENHA_MAX && c >= 32 && c <= 126) {
            senha.append(c);
            return true;
        }
        return false;
    }

    private static boolean nomeValido(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_' || c == '-';
    }

    public void apagar() {
        StringBuilder alvo = campo == Campo.NOME ? nome : senha;
        if (alvo.length() > 0) {
            alvo.deleteCharAt(alvo.length() - 1);
        }
    }

    public void alternarCampo() {
        campo = campo == Campo.NOME ? Campo.SENHA : Campo.NOME;
    }

    public void alternarModo() {
        modo = modo == Modo.ENTRAR ? Modo.CADASTRAR : Modo.ENTRAR;
    }

    public void limparSenha() {
        senha.setLength(0);
    }

    /** Devolve a mensagem do primeiro problema encontrado ou null se o formulario pode ser enviado. */
    public String validar() {
        if (modo == Modo.ENTRAR) {
            if (nome.length() == 0) {
                return "Informe o nome.";
            }
            if (senha.length() == 0) {
                return "Informe a senha.";
            }
            return null;
        }
        if (nome.length() < NOME_MIN) {
            return "O nome deve ter de " + NOME_MIN + " a " + NOME_MAX + " caracteres.";
        }
        if (senha.length() < SENHA_MIN) {
            return "A senha deve ter pelo menos " + SENHA_MIN + " caracteres.";
        }
        return null;
    }

    public String getNome() {
        return nome.toString();
    }

    public String getSenha() {
        return senha.toString();
    }

    public String getSenhaMascarada() {
        return "*".repeat(senha.length());
    }

    public Modo getModo() {
        return modo;
    }

    public Campo getCampo() {
        return campo;
    }
}
