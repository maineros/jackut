package jackut.repositories;

/**
 * Lan\u00E7ada quando ocorre uma falha de I/O ao persistir ou carregar o estado
 * do sistema em disco.
 *
 * <p>Deliberadamente <strong>n\u00E3o</strong> herda de {@code exceptions.JackutException}:
 * aquela hierarquia representa exclusivamente viola\u00E7\u00F5es de regras de neg\u00F3cio
 * do dom\u00EDnio, com mensagens exatas exigidas pelos testes de aceita\u00E7\u00E3o do
 * EasyAccept. Uma falha de disco \u00E9 um erro de infraestrutura, n\u00E3o uma regra
 * de neg\u00F3cio violada, e por isso pertence a uma hierarquia separada.</p>
 *
 * <p>Estende {@link RuntimeException} porque \u00E9 uma condi\u00E7\u00E3o excepcional e
 * n\u00E3o recuper\u00E1vel pelo fluxo normal do {@link Controlador}: n\u00E3o h\u00E1 regra de
 * neg\u00F3cio que trate "disco cheio" ou "permiss\u00E3o negada" como um caminho
 * alternativo esperado.</p>
 */
public class PersistenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constr\u00F3i a exce\u00E7\u00E3o com a mensagem e a causa origin\u00E1ria do erro de I/O.
     *
     * @param mensagem texto descritivo do erro
     * @param causa    exce\u00E7\u00E3o de I/O que originou esta falha
     */
    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
