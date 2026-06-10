package exceptions;

/**
 * Lan\u00E7ada quando o usu\u00E1rio tenta ler um recado mas sua fila est\u00E1 vazia.
 *
 * <p>Disparada diretamente pelo modelo {@link entities.Usuario},
 * que protege o pr\u00F3prio estado sem expor o m\u00E9todo {@code isEmpty()}
 * para chamadores externos.</p>
 */

public class SemRecadosException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constr\u00F3i a exce\u00E7\u00E3o com a mensagem padr\u00E3o exigida pelos testes de aceita\u00E7\u00E3o.
     */
    public SemRecadosException() {
        super("N\u00E3o h\u00E1 recados.");
    }
}
