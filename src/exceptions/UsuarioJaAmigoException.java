package exceptions;

/**
 * Lan\u00E7ada quando a amizade entre dois usu\u00E1rios j\u00E1 foi confirmada
 * e um deles tenta adicionar o outro novamente.
 */

public class UsuarioJaAmigoException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constr\u00F3i a exce\u00E7\u00E3o com a mensagem padr\u00E3o exigida pelos testes de aceita\u00E7\u00E3o.
     */
    public UsuarioJaAmigoException() {
        super("Usu\u00E1rio j\u00E1 est\u00E1 adicionado como amigo.");
    }
}
