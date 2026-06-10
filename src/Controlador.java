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

/**
 * Orquestra as opera\u00E7\u00F5es de neg\u00F3cio do sistema Jackut.
 *
 * <p>Respons\u00E1vel por coordenar entidades, gerenciar sess\u00F5es ativas e aplicar
 * as regras de fluxo. N\u00E3o conhece detalhes de persist\u00EAncia — delega essa
 * responsabilidade ao {@link Repositorio}.</p>
 *
 * <p>Sess\u00F5es s\u00E3o vol\u00E1teis ({@code transient}) e n\u00E3o sobrevivem entre execu\u00E7\u00F5es.
 * O estado dos usu\u00E1rios \u00E9 recarregado do disco a cada instancia\u00E7\u00E3o.</p>
*/

public class Controlador {

    /** Reposit\u00F3rio respons\u00E1vel pela persist\u00EAncia em disco. */
    private final Repositorio repositorio;

    /** Mapa de usu\u00E1rios cadastrados, indexado por login. */
    private Map<String, Usuario> usuarios;

    /**
     * Mapa de sess\u00F5es ativas, indexado pelo ID de sess\u00E3o (UUID).
     * Marcado como {@code transient}: sess\u00F5es n\u00E3o s\u00E3o persistidas.
     */
    private transient Map<String, String> sessoes;

    /**
     * Inicializa o controlador carregando os usu\u00E1rios persistidos em disco.
     * Sess\u00F5es come\u00E7am sempre vazias.
     */
    public Controlador() {
        this.repositorio = new Repositorio();
        this.sessoes     = new HashMap<>();
        this.usuarios    = repositorio.carregar();
    }

    // SISTEMA

    /**
     * Apaga todos os usu\u00E1rios e sess\u00F5es em mem\u00F3ria e remove o arquivo de dados.
     */
    public void zerarSistema() {
        this.usuarios.clear();
        this.sessoes.clear();
        repositorio.apagar();
    }

    /**
     * Persiste o estado atual dos usu\u00E1rios em disco e encerra o sistema.
     */
    public void encerrarSistema() {
        repositorio.salvar(this.usuarios);
    }

    // USER STORY 1 - CONTA

    /**
     * Cadastra um novo usu\u00E1rio no sistema.
     *
     * <p>A valida\u00E7\u00E3o de login e senha inv\u00E1lidos \u00E9 feita pelo construtor de
     * {@link Usuario}. Este m\u00E9todo verifica apenas a unicidade do login.</p>
     *
     * @param login login \u00FAnico desejado
     * @param senha senha de acesso
     * @param nome  nome de exibi\u00E7\u00E3o na rede
     * @throws exceptions.LoginInvalidoException se o login for nulo ou vazio
     * @throws exceptions.SenhaInvalidaException se a senha for nula ou vazia
     * @throws ContaJaExisteException se j\u00E1 existir uma conta com o mesmo login
     */
    public void criarUsuario(String login, String senha, String nome) {
        if (this.usuarios.containsKey(login))
            throw new ContaJaExisteException();
        this.usuarios.put(login, new Usuario(login, senha, nome));
    }

    /**
     * Autentica o usu\u00E1rio e abre uma sess\u00E3o ativa.
     *
     * <p>Por seguran\u00E7a, a mesma exce\u00E7\u00E3o \u00E9 lan\u00E7ada tanto para login inexistente
     * quanto para senha incorreta, impedindo que um atacante identifique
     * qual dos dois est\u00E1 errado.</p>
     *
     * @param login login do usu\u00E1rio
     * @param senha senha do usu\u00E1rio
     * @return identificador \u00FAnico (UUID) da sess\u00E3o aberta
     * @throws LoginOuSenhaInvalidosException se login ou senha forem inv\u00E1lidos ou incorretos
     */
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

    // USER STORY 2 - PERFIL

    /**
     * Retorna o valor de um atributo do perfil de um usu\u00E1rio.
     *
     * @param login    login do usu\u00E1rio
     * @param atributo nome do atributo desejado
     * @return valor do atributo
     * @throws UsuarioNaoCadastradoException    se o login n\u00E3o existir
     * @throws exceptions.AtributoNaoPreenchidoException se o atributo n\u00E3o tiver sido preenchido
     */
    public String getAtributoUsuario(String login, String atributo) {
        return buscarUsuario(login).getAtributo(atributo);
    }

    /**
     * Edita ou cria um atributo no perfil do usu\u00E1rio da sess\u00E3o informada.
     *
     * @param id       identificador da sess\u00E3o ativa
     * @param atributo nome do atributo a definir ou atualizar
     * @param valor    novo valor do atributo
     * @throws UsuarioNaoCadastradoException se o ID de sess\u00E3o for inv\u00E1lido
     */
    public void editarPerfil(String id, String atributo, String valor) {
        getUsuarioDaSessao(id).setAtributo(atributo, valor);
    }

    // USER STORY 3 - AMIGOS

    /**
     * Envia um convite de amizade ou confirma uma amizade pendente.
     *
     * <p>Se o destinat\u00E1rio j\u00E1 tiver enviado um convite para o remetente,
     * a amizade \u00E9 confirmada imediatamente nos dois lados. Caso contr\u00E1rio,
     * o convite fica pendente at\u00E9 o destinat\u00E1rio retribuir.</p>
     *
     * @param id         identificador da sess\u00E3o do usu\u00E1rio que envia o convite
     * @param loginAmigo login do usu\u00E1rio a ser adicionado
     * @throws UsuarioNaoCadastradoException se o ID de sess\u00E3o ou o login do amigo forem inv\u00E1lidos
     * @throws AutoAmizadeException          se o usu\u00E1rio tentar adicionar a si mesmo
     * @throws UsuarioJaAmigoException       se a amizade j\u00E1 estiver confirmada
     * @throws ConvitePendenteException      se j\u00E1 houver um convite aguardando aceita\u00E7\u00E3o
     */
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

    /**
     * Verifica se dois usu\u00E1rios s\u00E3o amigos confirmados.
     *
     * @param login      login do primeiro usu\u00E1rio
     * @param loginAmigo login do segundo usu\u00E1rio
     * @return {@code true} se forem amigos confirmados; {@code false} caso contr\u00E1rio
     *         ou se algum dos logins n\u00E3o existir
     */
    public boolean ehAmigo(String login, String loginAmigo) {
        Usuario usuario = this.usuarios.get(login);
        if (usuario == null) return false;
        return usuario.ehAmigo(loginAmigo);
    }

    /**
     * Retorna a lista de amigos do usu\u00E1rio no formato {@code {login1,login2,...}}.
     *
     * @param login login do usu\u00E1rio
     * @return string formatada com os logins dos amigos confirmados
     * @throws UsuarioNaoCadastradoException se o login n\u00E3o existir
     */
    public String getAmigos(String login) {
        return "{" + String.join(",", buscarUsuario(login).getAmigos()) + "}";
    }

    // USER STORY 4 - RECADOS

    /**
     * Envia um recado ao usu\u00E1rio destinat\u00E1rio.
     *
     * @param id           identificador da sess\u00E3o do remetente
     * @param loginDestino login do usu\u00E1rio que receber\u00E1 o recado
     * @param recado       texto da mensagem
     * @throws UsuarioNaoCadastradoException se o ID de sess\u00E3o ou o login do destinat\u00E1rio forem inv\u00E1lidos
     * @throws AutoRecadoException           se o remetente tentar enviar um recado para si mesmo
     */
    public void enviarRecado(String id, String loginDestino, String recado) {
        Usuario remetente = getUsuarioDaSessao(id);
        if (remetente.getLogin().equals(loginDestino))
            throw new AutoRecadoException();
        buscarUsuario(loginDestino).receberRecado(recado);
    }

    /**
     * L\u00EA e remove o pr\u00F3ximo recado da fila do usu\u00E1rio da sess\u00E3o informada.
     *
     * @param id identificador da sess\u00E3o ativa
     * @return texto do pr\u00F3ximo recado n\u00E3o lido
     * @throws UsuarioNaoCadastradoException se o ID de sess\u00E3o for inv\u00E1lido
     * @throws exceptions.SemRecadosException se n\u00E3o houver recados na fila
     */
    public String lerRecado(String id) {
        return getUsuarioDaSessao(id).lerProximoRecado();
    }

    // AUXILIARES PRIVADOS

    /**
     * Busca e retorna o usu\u00E1rio pelo login.
     *
     * @param login login do usu\u00E1rio a buscar
     * @return inst\u00E2ncia do usu\u00E1rio correspondente
     * @throws UsuarioNaoCadastradoException se n\u00E3o existir usu\u00E1rio com o login informado
     */
    private Usuario buscarUsuario(String login) {
        Usuario usuario = this.usuarios.get(login);
        if (usuario == null)
            throw new UsuarioNaoCadastradoException();
        return usuario;
    }

    /**
     * Retorna o usu\u00E1rio dono da sess\u00E3o identificada pelo ID fornecido.
     *
     * @param id identificador da sess\u00E3o ativa
     * @return usu\u00E1rio autenticado na sess\u00E3o
     * @throws UsuarioNaoCadastradoException se o ID for nulo, vazio ou n\u00E3o corresponder
     *                                       a nenhuma sess\u00E3o ativa
     */
    private Usuario getUsuarioDaSessao(String id) {
        if (id == null || id.isEmpty() || !this.sessoes.containsKey(id))
            throw new UsuarioNaoCadastradoException();
        return this.usuarios.get(this.sessoes.get(id));
    }
}
