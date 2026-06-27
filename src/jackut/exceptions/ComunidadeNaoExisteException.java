package jackut.exceptions;

/**
 * Lançada quando uma comunidade com o nome informado não existe no sistema.
 *
 * <p>Disparada pelo {@code Controlador} ao consultar ou ingressar em
 * uma comunidade inexistente.</p>
 */

public class ComunidadeNaoExisteException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public ComunidadeNaoExisteException() {
        super("Comunidade não existe.");
    }
}
