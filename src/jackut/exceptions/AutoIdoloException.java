package jackut.exceptions;

/**
 * Lançada quando o usuário tenta adicionar a si mesmo como ídolo.
 */
public class AutoIdoloException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public AutoIdoloException() {
        super("Usuário não pode ser fã de si mesmo.");
    }
}
