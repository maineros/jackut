package exceptions;

// lancada quando o login ou a senha nao correspondem a nenhuma conta
public class LoginOuSenhaInvalidosException extends JackutException {
    private static final long serialVersionUID = 1L;
    public LoginOuSenhaInvalidosException() {
        super("Login ou senha inv\u00E1lidos.");
    }
}
