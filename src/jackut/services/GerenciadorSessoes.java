package jackut.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Responsável exclusivamente pelo ciclo de vida das sessões ativas do sistema.
 *
 * <p>Extraída do {@code Controlador} para isolar uma responsabilidade que não
 * é regra de negócio de domínio (amizade, perfil, recados, etc.), e sim
 * controle de acesso: abrir uma sessão e resolver qual login está autenticado
 * por um determinado ID. Concentrar essa lógica aqui reduz os motivos de
 * mudança do {@code Controlador}.</p>
 *
 * <p>Sessões são inerentemente voláteis: não fazem sentido persistidos em
 * disco, pois um ID de sessão só tem significado durante a execução em que
 * foi gerado. Por isso esta classe não implementa {@code Serializable} e o
 * {@code Controlador} mantém sua referência fora do que é gravado pelo
 * {@code Repositorio}.</p>
 */
public class GerenciadorSessoes {

    /** Mapa de sessões ativas, indexado pelo ID de sessão (UUID), com o login associado. */
    private final Map<String, String> sessoes;

    /**
     * Inicializa o gerenciador sem nenhuma sessão ativa.
     */
    public GerenciadorSessoes() {
        this.sessoes = new HashMap<>();
    }

    /**
     * Abre uma nova sessão para o login informado e retorna seu identificador único.
     *
     * @param login login do usuário já autenticado pelo chamador
     * @return identificador único (UUID) da sessão aberta
     */
    public String abrir(String login) {
        String idSessao = UUID.randomUUID().toString();
        this.sessoes.put(idSessao, login);
        return idSessao;
    }

    /**
     * Verifica se o ID informado corresponde a uma sessão ativa.
     *
     * @param id identificador de sessão a verificar
     * @return {@code true} se {@code id} não for nulo/vazio e houver sessão ativa correspondente
     */
    public boolean estaAtiva(String id) {
        return id != null && !id.isEmpty() && this.sessoes.containsKey(id);
    }

    /**
     * Retorna o login associado à sessão ativa identificada por {@code id}.
     *
     * @param id identificador da sessão ativa
     * @return login do usuário autenticado na sessão, ou {@code null} se a sessão não existir
     */
    public String getLogin(String id) {
        return this.sessoes.get(id);
    }

    /**
     * Remove todas as sessões ativas.
     */
    public void limpar() {
        this.sessoes.clear();
    }

    /**
     * Remove a sessão identificada por {@code id}, invalidando-a.
     *
     * @param id identificador da sessão a invalidar
     */
    public void invalidar(String id) {
        this.sessoes.remove(id);
    }
}
