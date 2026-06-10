package exceptions;

/**
 * Exce\u00E7\u00E3o base do dom\u00EDnio Jackut.
 *
 * <p>Todas as exce\u00E7\u00F5es de regra de neg\u00F3cio do sistema herdam desta classe,
 * garantindo um tipo comum para identifica\u00E7\u00E3o e captura pelo EasyAccept.
 * Por estender {@link RuntimeException}, n\u00E3o obriga {@code try/catch}
 * nos chamadores.</p>
 */

public class JackutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constr\u00F3i a exce\u00E7\u00E3o com a mensagem de erro fornecida.
     *
     * @param mensagem texto descritivo do erro, exibido pelo EasyAccept
     */
    public JackutException(String mensagem) {
        super(mensagem);
    }
}
