package exceptions;

// excecao base do sistema jackut
// todas as excessoes de negocio herdam desta classe, garantindo um tipo comum para captura na facade e no easyaccept; por ser unchecked, nao obriga try/catch nos chamadores
public class JackutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JackutException(String mensagem) {
        super(mensagem);
    }
}
