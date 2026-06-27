package jackut.entities;

import jackut.exceptions.UsuarioJaMembroException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * É a entidade do domínio Jackut que representa uma comunidade da rede social.
 *
 * <p>Uma comunidade possui um nome único (chave primária), uma descrição,
 * um dono (o usuário que a criou) e uma lista de membros. O dono é
 * automaticamente o primeiro membro ao criar a comunidade.</p>
 *
 * <p>Implementa {@link Serializable} para suportar a persistência
 * do estado em disco entre execuções.</p>
 */
public class Comunidade implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Nome único da comunidade — funciona como chave primária. */
    private final String nome;

    /** Descrição da comunidade. */
    private final String descricao;

    /** Login do usuário que criou e é dono da comunidade. */
    private final String loginDono;

    /** Lista de logins dos membros, em ordem de entrada. O dono é sempre o primeiro. */
    private final List<String> membros;

    /**
     * Cria uma comunidade com nome, descrição e dono informados.
     * O dono é adicionado automaticamente à lista de membros.
     *
     * @param nome      nome único da comunidade
     * @param descricao descrição da comunidade
     * @param loginDono login do usuário criador e dono da comunidade
     */
    public Comunidade(String nome, String descricao, String loginDono) {
        this.nome      = nome;
        this.descricao = descricao;
        this.loginDono = loginDono;
        this.membros   = new ArrayList<>();
        this.membros.add(loginDono);
    }

    /**
     * Retorna o nome único da comunidade.
     *
     * @return nome da comunidade
     */
    public String getNome() { return nome; }

    /**
     * Retorna a descrição da comunidade.
     *
     * @return descrição da comunidade
     */
    public String getDescricao() { return descricao; }

    /**
     * Retorna o login do dono da comunidade.
     *
     * @return login do dono
     */
    public String getLoginDono() { return loginDono; }

    /**
     * Verifica se o login informado pertence ao dono da comunidade.
     *
     * @param login login a verificar
     * @return {@code true} se for o dono
     */
    public boolean ehDono(String login) { return this.loginDono.equals(login); }

    /**
     * Retorna uma visão não-modificável da lista de membros.
     *
     * @return lista imutável com os logins dos membros em ordem de entrada
     */
    public List<String> getMembros() { return Collections.unmodifiableList(membros); }

    /**
     * Verifica se o usuário com o login informado é membro da comunidade.
     *
     * @param login login do usuário
     * @return {@code true} se for membro; {@code false} caso contrário
     */
    public boolean ehMembro(String login) { return membros.contains(login); }

    /**
     * Adiciona o usuário à lista de membros, validando duplicidade.
     *
     * <p>A validação de duplicidade é responsabilidade da própria entidade:
     * o chamador não precisa verificar {@link #ehMembro(String)} antes de
     * chamar este método.</p>
     *
     * @param login login do usuário a ser adicionado
     * @throws UsuarioJaMembroException se o usuário já for membro da comunidade
     */
    public void adicionarMembro(String login) throws UsuarioJaMembroException {
        if (membros.contains(login))
            throw new UsuarioJaMembroException();
        membros.add(login);
    }

    /**
     * Remove o membro com o login informado da lista de membros.
     *
     * @param login login do membro a remover
     */
    public void removerMembro(String login) { membros.remove(login); }

    /**
     * Notifica todos os membros desta comunidade de que ela foi dissolvida,
     * removendo a referência a ela de cada um.
     *
     * <p>Usado na remoção em cascata quando o dono encerra sua conta (US9).
     * A comunidade conhece seus membros e sabe como limpá-los — o
     * {@link jackut.controllers.Controlador} não precisa navegar pela lista
     * de membros externamente.</p>
     *
     * @param todosUsuarios mapa completo de usuários do sistema, indexado por login
     * @param loginDono     login do dono (que será removido pelo chamador e não precisa
     *                      ser notificado aqui)
     */
    public void dissolver(Map<String, Usuario> todosUsuarios, String loginDono) {
        for (String loginMembro : membros) {
            if (loginMembro.equals(loginDono)) continue;
            Usuario membro = todosUsuarios.get(loginMembro);
            if (membro != null)
                membro.removerComunidade(this.nome);
        }
    }
}