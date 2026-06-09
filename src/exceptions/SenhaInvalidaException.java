package exceptions;

// lancada quando a senha informada eh nula ou vazia
public class SenhaInvalidaException extends JackutException {
    private static final long serialVersionUID = 1L;
    public SenhaInvalidaException() {
        super("Senha inv\u00E1lida.");
    }
}
