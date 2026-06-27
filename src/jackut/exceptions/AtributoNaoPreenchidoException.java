package jackut.exceptions;

/**
 * Lan\u00E7ada quando o atributo de perfil solicitado ainda n\u00E3o foi preenchido
 * pelo usu\u00E1rio.
 *
 * <p>Disparada pelo pr\u00F3prio modelo {@link entities.Usuario} ao tentar
 * acessar um atributo inexistente no mapa de perfil.</p>
 */

public class AtributoNaoPreenchidoException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constr\u00F3i a exce\u00E7\u00E3o com a mensagem padr\u00E3o exigida pelos testes de aceita\u00E7\u00E3o.
     */
    public AtributoNaoPreenchidoException() {
        super("Atributo n\u00E3o preenchido.");
    }
}
