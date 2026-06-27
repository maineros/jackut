package jackut.exceptions;

/**
 * Exce\u00E7\u00E3o base do dom\u00EDnio Jackut.
 *
 * <p>Todas as exce\u00E7\u00F5es de regra de neg\u00F3cio do sistema herdam desta classe,
 * garantindo um tipo comum para identifica\u00E7\u00E3o e captura pelo EasyAccept.
 * Por estender {@link Exception} (checked exception), cada m\u00E9todo que possa
 * lan\u00E7\u00E1-la \u00E9 obrigado a declar\u00E1-la explicitamente em sua assinatura
 * ({@code throws}), tornando os contratos de erro vis\u00EDveis em tempo de
 * compila\u00E7\u00E3o em vez de depender apenas de documenta\u00E7\u00E3o.</p>
 */

public class JackutException extends Exception {

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
