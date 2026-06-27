package jackut.exceptions;

/**
 * Lançada quando o usuário tenta adicionar como paquera alguém que já
 * está em sua lista de paqueras.
 */
public class UsuarioJaPaqueraException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public UsuarioJaPaqueraException() {
        super("Usuário já está adicionado como paquera.");
    }
}
