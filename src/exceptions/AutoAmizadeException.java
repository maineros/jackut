package exceptions;

/**
 * Lan\u00E7ada quando o usu\u00E1rio tenta adicionar a si mesmo como amigo.
 */

public class AutoAmizadeException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constr\u00F3i a exce\u00E7\u00E3o com a mensagem padr\u00E3o exigida pelos testes de aceita\u00E7\u00E3o.
     */
    public AutoAmizadeException() {
        super("Usu\u00E1rio n\u00E3o pode adicionar a si mesmo como amigo.");
    }
}
