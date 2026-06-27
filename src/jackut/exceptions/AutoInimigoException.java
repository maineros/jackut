package jackut.exceptions;

/**
 * Lançada quando o usuário tenta declarar a si mesmo como inimigo.
 */
public class AutoInimigoException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public AutoInimigoException() {
        super("Usuário não pode ser inimigo de si mesmo.");
    }
}
