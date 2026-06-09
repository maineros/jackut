package exceptions;

// lancada quando o usuario tenta enviar um recado para si mesmo
public class AutoRecadoException extends JackutException {
    private static final long serialVersionUID = 1L;
    public AutoRecadoException() {
        super("Usu\u00E1rio n\u00E3o pode enviar recado para si mesmo.");
    }
}
