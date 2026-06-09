package exceptions;

// lancada quando o login informado eh nulo ou vazio
public class LoginInvalidoException extends JackutException {
    private static final long serialVersionUID = 1L;
    public LoginInvalidoException() {
        super("Login inv\u00E1lido.");
    }
}
