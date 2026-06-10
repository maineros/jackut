package exceptions;

/**
 * Lan\u00E7ada quando o login informado \u00E9 nulo ou vazio ao criar um usu\u00E1rio.
 *
 * <p>A valida\u00E7\u00E3o ocorre no construtor de {@link entities.Usuario},
 * que \u00E9 respons\u00E1vel por garantir o pr\u00F3prio estado v\u00E1lido.</p>
 */

public class LoginInvalidoException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constr\u00F3i a exce\u00E7\u00E3o com a mensagem padr\u00E3o exigida pelos testes de aceita\u00E7\u00E3o.
     */
    public LoginInvalidoException() {
        super("Login inv\u00E1lido.");
    }
}
