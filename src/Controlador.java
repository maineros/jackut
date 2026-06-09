import entities.Usuario;
import exceptions.AutoAmizadeException;
import exceptions.AutoRecadoException;
import exceptions.ContaJaExisteException;
import exceptions.ConvitePendenteException;
import exceptions.LoginOuSenhaInvalidosException;
import exceptions.UsuarioJaAmigoException;
import exceptions.UsuarioNaoCadastradoException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Controlador {

    private final Repositorio repositorio;
    private Map<String, Usuario> usuarios;
    private transient Map<String, String> sessoes;

    public Controlador() {
        this.repositorio = new Repositorio();
        this.sessoes     = new HashMap<>();
        this.usuarios    = repositorio.carregar();
    }

    // sistema
    public void zerarSistema() {
        this.usuarios.clear();
        this.sessoes.clear();
        repositorio.apagar();
    }

    public void encerrarSistema() {
        repositorio.salvar(this.usuarios);
    }

    // user story 1 - conta
    public void criarUsuario(String login, String senha, String nome) {
        if (this.usuarios.containsKey(login))
            throw new ContaJaExisteException();
        this.usuarios.put(login, new Usuario(login, senha, nome));
    }

    public String abrirSessao(String login, String senha) {
        if (login == null || login.isEmpty() || senha == null || senha.isEmpty())
            throw new LoginOuSenhaInvalidosException();
        Usuario usuario = this.usuarios.get(login);
        if (usuario == null || !usuario.verificarSenha(senha))
            throw new LoginOuSenhaInvalidosException();
        String idSessao = UUID.randomUUID().toString();
        this.sessoes.put(idSessao, login);
        return idSessao;
    }

    // user story 2 - perfil
    public String getAtributoUsuario(String login, String atributo) {
        return buscarUsuario(login).getAtributo(atributo);
    }

    public void editarPerfil(String id, String atributo, String valor) {
        getUsuarioDaSessao(id).setAtributo(atributo, valor);
    }

    // user story 3 - amigos
    public void adicionarAmigo(String id, String loginAmigo) {
        Usuario remetente = getUsuarioDaSessao(id);

        if (remetente.getLogin().equals(loginAmigo))
            throw new AutoAmizadeException();

        Usuario destino = buscarUsuario(loginAmigo);

        if (remetente.ehAmigo(loginAmigo))
            throw new UsuarioJaAmigoException();

        if (remetente.temConvitePara(loginAmigo))
            throw new ConvitePendenteException();

        if (destino.temConvitePara(remetente.getLogin())) {
            remetente.confirmarAmizade(loginAmigo);
            destino.confirmarAmizade(remetente.getLogin());
        } else {
            remetente.enviarConvite(loginAmigo);
        }
    }

    public boolean ehAmigo(String login, String loginAmigo) {
        Usuario usuario = this.usuarios.get(login);
        if (usuario == null) return false;
        return usuario.ehAmigo(loginAmigo);
    }

    public String getAmigos(String login) {
        return "{" + String.join(",", buscarUsuario(login).getAmigos()) + "}";
    }

    // user story 4 - recados
    public void enviarRecado(String id, String loginDestino, String recado) {
        Usuario remetente = getUsuarioDaSessao(id);
        if (remetente.getLogin().equals(loginDestino))
            throw new AutoRecadoException();
        buscarUsuario(loginDestino).receberRecado(recado);
    }

    public String lerRecado(String id) {
        return getUsuarioDaSessao(id).lerProximoRecado();
    }

    // auxiliares privados
    private Usuario buscarUsuario(String login) {
        Usuario usuario = this.usuarios.get(login);
        if (usuario == null)
            throw new UsuarioNaoCadastradoException();
        return usuario;
    }

    private Usuario getUsuarioDaSessao(String id) {
        if (id == null || id.isEmpty() || !this.sessoes.containsKey(id))
            throw new UsuarioNaoCadastradoException();
        return this.usuarios.get(this.sessoes.get(id));
    }
}
