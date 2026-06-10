package entities;

import exceptions.AtributoNaoPreenchidoException;
import exceptions.LoginInvalidoException;
import exceptions.SenhaInvalidaException;
import exceptions.SemRecadosException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Entidade rica do dom\u00EDnio Jackut que representa um usu\u00E1rio da rede social.
 *
 * <p>Adota o conceito de <em>Rich Domain Model</em>: \u00E9 respons\u00E1vel por
 * garantir a pr\u00F3pria integridade, validando credenciais na constru\u00E7\u00E3o,
 * encapsulando suas cole\u00E7\u00F5es internas e lan\u00E7ando as exce\u00E7\u00F5es adequadas
 * ao detectar estados inv\u00E1lidos.</p>
 *
 * <p>Implementa {@link Serializable} para suportar a persist\u00EAncia
 * do estado em disco entre execu\u00E7\u00F5es.</p>
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Login \u00FAnico e imut\u00E1vel do usu\u00E1rio. */
    private final String login;

    /** Senha de acesso, mantida privada e nunca exposta por getter. */
    private final String senha;

    /**
     * Atributos livres do perfil (ex: nome, descricao, cidadeNatal).
     * O atributo {@code "nome"} \u00E9 inserido na constru\u00E7\u00E3o.
     */
    private final Map<String, String> perfil;

    /** Logins dos amigos confirmados, em ordem de confirma\u00E7\u00E3o. */
    private final List<String> amigos;

    /**
     * Logins para os quais este usu\u00E1rio enviou convite de amizade,
     * aguardando aceita\u00E7\u00E3o. Usa {@link LinkedHashSet} para manter
     * ordem de inser\u00E7\u00E3o e garantir busca em O(1).
     */
    private final Set<String> convitesEnviados;

    /** Fila FIFO de recados recebidos ainda n\u00E3o lidos. */
    private final Queue<String> recados;

    // CONSTRUTOR

    /**
     * Cria um usu\u00E1rio com as credenciais e o nome fornecidos.
     *
     * <p>O pr\u00F3prio modelo valida os dados de entrada: se o login ou a senha
     * forem nulos ou vazios, a exce\u00E7\u00E3o \u00E9 lan\u00E7ada antes de o objeto existir
     * com estado inv\u00E1lido.</p>
     *
     * @param login login \u00FAnico desejado; n\u00E3o pode ser nulo nem vazio
     * @param senha senha de acesso; n\u00E3o pode ser nula nem vazia
     * @param nome  nome de exibi\u00E7\u00E3o na rede; pode ser vazio
     * @throws LoginInvalidoException se {@code login} for nulo ou vazio
     * @throws SenhaInvalidaException se {@code senha} for nula ou vazia
     */
    public Usuario(String login, String senha, String nome) {
        if (login == null || login.trim().isEmpty())
            throw new LoginInvalidoException();
        if (senha == null || senha.trim().isEmpty())
            throw new SenhaInvalidaException();

        this.login = login;
        this.senha = senha;
        this.perfil = new HashMap<>();
        this.perfil.put("nome", nome);
        this.amigos = new ArrayList<>();
        this.convitesEnviados = new LinkedHashSet<>();
        this.recados = new LinkedList<>();
    }

    // CREDENCIAIS

    /**
     * Retorna o login do usu\u00E1rio.
     *
     * @return login \u00FAnico e imut\u00E1vel
     */
    public String getLogin() {
        return login;
    }

    /**
     * Verifica se a senha informada corresponde \u00E0 senha cadastrada.
     *
     * @param senha senha a ser verificada
     * @return {@code true} se a senha estiver correta; {@code false} caso contr\u00E1rio
     */
    public boolean verificarSenha(String senha) {
        return this.senha.equals(senha);
    }

    // PERFIL

    /**
     * Retorna o valor do atributo de perfil solicitado.
     *
     * <p>O modelo garante que {@code null} nunca seja retornado:
     * se o atributo n\u00E3o existir, uma exce\u00E7\u00E3o \u00E9 lan\u00E7ada.</p>
     *
     * @param atributo nome do atributo (ex: {@code "nome"}, {@code "descricao"})
     * @return valor associado ao atributo
     * @throws AtributoNaoPreenchidoException se o atributo n\u00E3o tiver sido preenchido
     */
    public String getAtributo(String atributo) {
        if (!this.perfil.containsKey(atributo))
            throw new AtributoNaoPreenchidoException();
        return this.perfil.get(atributo);
    }

    /**
     * Define ou atualiza o valor de um atributo do perfil.
     *
     * <p>Novos atributos s\u00E3o criados dinamicamente; atributos existentes
     * s\u00E3o sobrescritos. O atributo {@code "nome"} pode ser alterado por
     * este m\u00E9todo.</p>
     *
     * @param atributo nome do atributo a definir ou atualizar
     * @param valor    novo valor do atributo
     */
    public void setAtributo(String atributo, String valor) {
        this.perfil.put(atributo, valor);
    }

    // AMIZADES

    /**
     * Verifica se o usu\u00E1rio com o login informado \u00E9 amigo confirmado.
     *
     * @param login login do candidato a amigo
     * @return {@code true} se a amizade estiver confirmada; {@code false} caso contr\u00E1rio
     */
    public boolean ehAmigo(String login) {
        return this.amigos.contains(login);
    }

    /**
     * Verifica se j\u00E1 existe um convite enviado para o login informado
     * que ainda aguarda aceita\u00E7\u00E3o.
     *
     * @param login login do destinat\u00E1rio do convite
     * @return {@code true} se houver convite pendente; {@code false} caso contr\u00E1rio
     */
    public boolean temConvitePara(String login) {
        return this.convitesEnviados.contains(login);
    }

    /**
     * Registra que este usu\u00E1rio enviou um convite de amizade para o login informado.
     *
     * @param login login do destinat\u00E1rio do convite
     */
    public void enviarConvite(String login) {
        this.convitesEnviados.add(login);
    }

    /**
     * Confirma a amizade com o usu\u00E1rio do login informado.
     *
     * <p>Remove o convite pendente (se existir) e adiciona o login \u00E0
     * lista de amigos, caso ainda n\u00E3o esteja presente.</p>
     *
     * @param login login do usu\u00E1rio cuja amizade ser\u00E1 confirmada
     */
    public void confirmarAmizade(String login) {
        this.convitesEnviados.remove(login);
        if (!this.amigos.contains(login))
            this.amigos.add(login);
    }

    /**
     * Retorna uma vis\u00E3o n\u00E3o-modific\u00E1vel da lista de amigos confirmados.
     *
     * <p>A ordem reflete a sequ\u00EAncia de confirma\u00E7\u00E3o das amizades,
     * garantindo sa\u00EDda determin\u00EDstica nos testes de aceita\u00E7\u00E3o.</p>
     *
     * @return lista imut\u00E1vel com os logins dos amigos confirmados
     */
    public List<String> getAmigos() {
        return Collections.unmodifiableList(this.amigos);
    }

    // RECADOS

    /**
     * Adiciona um recado ao fim da fila de recados do usu\u00E1rio.
     *
     * @param recado texto do recado recebido
     */
    public void receberRecado(String recado) {
        this.recados.add(recado);
    }

    /**
     * Remove e retorna o primeiro recado da fila (ordem FIFO).
     *
     * <p>O modelo \u00E9 respons\u00E1vel por proteger seu pr\u00F3prio estado:
     * lan\u00E7a exce\u00E7\u00E3o se a fila estiver vazia, evitando que chamadores
     * externos precisem verificar o estado interno antes de chamar.</p>
     *
     * @return texto do pr\u00F3ximo recado n\u00E3o lido
     * @throws SemRecadosException se n\u00E3o houver recados na fila
     */
    public String lerProximoRecado() {
        if (this.recados.isEmpty())
            throw new SemRecadosException();
        return this.recados.poll();
    }
}
