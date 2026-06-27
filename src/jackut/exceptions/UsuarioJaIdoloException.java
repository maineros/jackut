package jackut.exceptions;

/**
 * Lançada quando o usuário tenta adicionar como ídolo alguém que já
 * está em sua lista de ídolos.
 */
public class UsuarioJaIdoloException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public UsuarioJaIdoloException() {
        super("Usuário já está adicionado como ídolo.");
    }
}
