package exceptions;

// lancada quando ja existe um convite enviado aguardando aceitacao
public class ConvitePendenteException extends JackutException {
    private static final long serialVersionUID = 1L;
    public ConvitePendenteException() {
        super("Usu\u00E1rio j\u00E1 est\u00E1 adicionado como amigo, esperando aceita\u00E7\u00E3o do convite.");
    }
}
