package jackut.exceptions;

/**
 * Lançada quando o usuário tenta adicionar a si mesmo como paquera.
 */
public class AutoPaqueraException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public AutoPaqueraException() {
        super("Usuário não pode ser paquera de si mesmo.");
    }
}
