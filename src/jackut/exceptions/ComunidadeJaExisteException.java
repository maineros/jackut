package jackut.exceptions;

/**
 * Lançada quando já existe uma comunidade com o nome informado.
 *
 * <p>Disparada pelo {@code Controlador} ao tentar criar uma comunidade
 * com um nome que já está presente no sistema.</p>
 */

public class ComunidadeJaExisteException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public ComunidadeJaExisteException() {
        super("Comunidade com esse nome já existe.");
    }
}
