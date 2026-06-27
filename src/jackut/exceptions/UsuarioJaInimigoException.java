package jackut.exceptions;

/**
 * Lançada quando o usuário tenta declarar como inimigo alguém que já
 * está em sua lista de inimigos.
 */
public class UsuarioJaInimigoException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public UsuarioJaInimigoException() {
        super("Usuário já está adicionado como inimigo.");
    }
}
