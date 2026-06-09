package exceptions;

// lancada quando ja existe uma conta com o login informado
public class ContaJaExisteException extends JackutException {
    private static final long serialVersionUID = 1L;
    public ContaJaExisteException() {
        super("Conta com esse nome j\u00E1 existe.");
    }
}
