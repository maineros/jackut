package exceptions;

// lancada quando nao existe um usuario com o login informado
public class UsuarioNaoCadastradoException extends JackutException {
    private static final long serialVersionUID = 1L;
    public UsuarioNaoCadastradoException() {
        super("Usu\u00E1rio n\u00E3o cadastrado.");
    }
}
