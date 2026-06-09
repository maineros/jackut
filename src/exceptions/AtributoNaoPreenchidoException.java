package exceptions;

// lancada quando o atributo solicitado nao foi preenchido no perflil
public class AtributoNaoPreenchidoException extends JackutException {
    private static final long serialVersionUID = 1L;
    public AtributoNaoPreenchidoException() {
        super("Atributo n\u00E3o preenchido.");
    }
}
