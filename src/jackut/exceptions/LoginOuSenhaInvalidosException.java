package jackut.exceptions;

/**
 * Lan\u00E7ada quando as credenciais fornecidas n\u00E3o correspondem a nenhuma conta.
 *
 * <p>Usada intencionalmente para cobrir tanto login inexistente quanto
 * senha incorreta, impedindo que um invasor identifique qual dos dois
 * est\u00E1 errado.</p>
 */

public class LoginOuSenhaInvalidosException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constr\u00F3i a exce\u00E7\u00E3o com a mensagem padr\u00E3o exigida pelos testes de aceita\u00E7\u00E3o.
     */
    public LoginOuSenhaInvalidosException() {
        super("Login ou senha inv\u00E1lidos.");
    }
}
