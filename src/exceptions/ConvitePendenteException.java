package exceptions;

/**
 * Lan\u00E7ada quando o usu\u00E1rio tenta enviar um convite de amizade para algu\u00E9m
 * para quem j\u00E1 enviou um convite que ainda aguarda aceita\u00E7\u00E3o.
 */

public class ConvitePendenteException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constr\u00F3i a exce\u00E7\u00E3o com a mensagem padr\u00E3o exigida pelos testes de aceita\u00E7\u00E3o.
     */
    public ConvitePendenteException() {
        super("Usu\u00E1rio j\u00E1 est\u00E1 adicionado como amigo, esperando aceita\u00E7\u00E3o do convite.");
    }
}
