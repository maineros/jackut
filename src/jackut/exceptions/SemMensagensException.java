package jackut.exceptions;

/**
 * Lançada quando não há mensagens de comunidade na fila do usuário.
 */
public class SemMensagensException extends JackutException {
    private static final long serialVersionUID = 1L;

    /**
     * Constrói a exceção com a mensagem padrão exigida pelos testes de aceitação.
     */
    public SemMensagensException() { super("Não há mensagens."); }
}
