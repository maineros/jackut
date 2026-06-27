package jackut.exceptions;

/**
 * Lançada quando uma operação é bloqueada porque o usuário-alvo declarou
 * o solicitante como inimigo (ex.: adicionar amigo, enviar recado, virar
 * fã ou paquera de alguém que o considera inimigo).
 */
public class InimigoException extends JackutException {

    private static final long serialVersionUID = 1L;

    /**
     * Constrói a exceção com o nome do inimigo incluído na mensagem.
     *
     * @param nomeInimigo nome de exibição do usuário que declarou a inimizade
     */
    public InimigoException(String nomeInimigo) {
        super("Função inválida: " + nomeInimigo + " é seu inimigo.");
    }
}
