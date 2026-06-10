package exceptions;

/**
 * Lan\u00E7ada quando n\u00E3o existe usu\u00E1rio cadastrado com o login informado,
 * ou quando o ID de sess\u00E3o n\u00E3o corresponde a nenhuma sess\u00E3o ativa.
 */
public class UsuarioNaoCadastradoException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constr\u00F3i a exce\u00E7\u00E3o com a mensagem padr\u00E3o exigida pelos testes de aceita\u00E7\u00E3o.
     */
    public UsuarioNaoCadastradoException() {
        super("Usu\u00E1rio n\u00E3o cadastrado.");
    }
}
