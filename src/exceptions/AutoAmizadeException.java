package exceptions;

// lancada quando o usuario tenta adicionar a si mesmo como amigo
public class AutoAmizadeException extends JackutException {
    private static final long serialVersionUID = 1L;
    public AutoAmizadeException() {
        super("Usu\u00E1rio n\u00E3o pode adicionar a si mesmo como amigo.");
    }
}
