package exceptions;

/**
 * Lan\u00E7ada quando j\u00E1 existe uma conta cadastrada com o login informado.
 *
 * <p>Disparada pelo {@code Controlador} ao tentar criar um usu\u00E1rio
 * com um login que j\u00E1 est\u00E1 presente no sistema.</p>
 */

public class ContaJaExisteException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constr\u00F3i a exce\u00E7\u00E3o com a mensagem padr\u00E3o exigida pelos testes de aceita\u00E7\u00E3o.
     */
    public ContaJaExisteException() {
        super("Conta com esse nome j\u00E1 existe.");
    }
}
