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

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String login;
    private final String senha;
    private final Map<String, String> perfil;
    private final List<String> amigos;
    private final Set<String> convitesEnviados;
    private final Queue<String> recados;

    // cria um usuario validando login e senha
    // algumas excecoes de estado invalido sao lancadas aqui, pois o proprio modelo que conhece suas regras mais basicas
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

    // credenciais
    
    public String getLogin() {
        return login;
    }

    public boolean verificarSenha(String senha) {
        return this.senha.equals(senha);
    }

    // perfil

    // retorna o valor do atributo ou lanca a excecao se nao preenchido
    public String getAtributo(String atributo) {
        if (!this.perfil.containsKey(atributo))
            throw new AtributoNaoPreenchidoException();
        return this.perfil.get(atributo);
    }

    public void setAtributo(String atributo, String valor) {
        this.perfil.put(atributo, valor);
    }

    // amizades

    public boolean ehAmigo(String login) {
        return this.amigos.contains(login);
    }

    public boolean temConvitePara(String login) {
        return this.convitesEnviados.contains(login);
    }

    public void enviarConvite(String login) {
        this.convitesEnviados.add(login);
    }

    public void confirmarAmizade(String login) {
        this.convitesEnviados.remove(login);
        if (!this.amigos.contains(login))
            this.amigos.add(login);
    }

    // retorna visao imutavel; o chamados nao pode modificar a lista interna
    public List<String> getAmigos() {
        return Collections.unmodifiableList(this.amigos);
    }

    // recados

    public void receberRecado(String recado) {
        this.recados.add(recado);
    }

    // remove e retorna o proximo recado. o proprio modelo lanca a excecao se a fila estiver vazia, evitando que o controlador precise inspecionar o estado interno
    public String lerProximoRecado() {
        if (this.recados.isEmpty())
            throw new SemRecadosException();
        return this.recados.poll();
    }
}
