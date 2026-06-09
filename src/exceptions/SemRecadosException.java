package exceptions;

// lancada quando nao ha recados na fila do usuario
public class SemRecadosException extends JackutException {
    private static final long serialVersionUID = 1L;
    public SemRecadosException() {
        super("N\u00E3o h\u00E1 recados.");
    }
}
