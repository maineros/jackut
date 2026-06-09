package exceptions;

// lancada quando a amizade ja foi confirmada entre os dois usuarios
public class UsuarioJaAmigoException extends JackutException {
    private static final long serialVersionUID = 1L;
    public UsuarioJaAmigoException() {
        super("Usu\u00E1rio j\u00E1 est\u00E1 adicionado como amigo.");
    }
}
