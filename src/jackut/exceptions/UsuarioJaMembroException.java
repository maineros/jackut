package jackut.exceptions;

/**
 * Lançada quando o usuário tenta ingressar em uma comunidade da qual já é membro.
 *
 * <p>Disparada pelo {@code Controlador} ao chamar {@code adicionarComunidade}
 * para um usuário que já pertence à comunidade informada.</p>
 */

public class UsuarioJaMembroException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public UsuarioJaMembroException() {
        super("Usuario já faz parte dessa comunidade.");
    }
}
