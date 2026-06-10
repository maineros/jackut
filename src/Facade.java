/**
 * \u00DAnica porta de entrada do sistema Jackut para o framework EasyAccept.
 *
 * <p>Implementa o padr\u00E3o <em>Facade</em>: n\u00E3o cont\u00E9m l\u00F3gica de neg\u00F3cio,
 * valida\u00E7\u00F5es nem instancia\u00E7\u00E3o direta de modelos. Atua exclusivamente
 * como despachante (<em>delegator</em>), repassando cada chamada ao
 * {@link Controlador} respons\u00E1vel.</p>
 *
 * <p>Todas as exce\u00E7\u00F5es lan\u00E7adas pelas camadas internas propagam
 * livremente at\u00E9 aqui para que o EasyAccept possa capturar as
 * mensagens de erro exatas.</p>
 */

public class Facade {

    /** Controlador que concentra toda a l\u00F3gica de neg\u00F3cio do sistema. */
    private final Controlador controlador;

    /**
     * Inicializa a Facade criando o {@link Controlador}, que por sua vez
     * recarrega o estado persistido em disco.
     */
    public Facade() {
        this.controlador = new Controlador();
    }

    // USER STORY 1 - CONTA

    /**
     * Apaga todos os dados do sistema em mem\u00F3ria e em disco.
     */
    public void zerarSistema() {
        controlador.zerarSistema();
    }

    /**
     * Persiste o estado atual e encerra o sistema.
     */
    public void encerrarSistema() {
        controlador.encerrarSistema();
    }

    /**
     * Cadastra um novo usu\u00E1rio no sistema.
     *
     * @param login login \u00FAnico desejado
     * @param senha senha de acesso
     * @param nome  nome de exibi\u00E7\u00E3o na rede
     * @throws exceptions.LoginInvalidoException  se o login for nulo ou vazio
     * @throws exceptions.SenhaInvalidaException  se a senha for nula ou vazia
     * @throws exceptions.ContaJaExisteException  se j\u00E1 existir uma conta com o login
     */
    public void criarUsuario(String login, String senha, String nome) {
        controlador.criarUsuario(login, senha, nome);
    }

    /**
     * Autentica o usu\u00E1rio e retorna o ID da sess\u00E3o aberta.
     *
     * @param login login do usu\u00E1rio
     * @param senha senha do usu\u00E1rio
     * @return identificador \u00FAnico (UUID) da sess\u00E3o
     * @throws exceptions.LoginOuSenhaInvalidosException se as credenciais forem inv\u00E1lidas
     */
    public String abrirSessao(String login, String senha) {
        return controlador.abrirSessao(login, senha);
    }

    // USER STORY 2 - PERFIL

    /**
     * Retorna o valor de um atributo do perfil de um usu\u00E1rio.
     *
     * @param login    login do usu\u00E1rio
     * @param atributo nome do atributo
     * @return valor do atributo
     * @throws exceptions.UsuarioNaoCadastradoException    se o login n\u00E3o existir
     * @throws exceptions.AtributoNaoPreenchidoException se o atributo n\u00E3o tiver sido preenchido
     */
    public String getAtributoUsuario(String login, String atributo) {
        return controlador.getAtributoUsuario(login, atributo);
    }

    /**
     * Edita ou cria um atributo no perfil do usu\u00E1rio da sess\u00E3o informada.
     *
     * @param id       identificador da sess\u00E3o ativa
     * @param atributo nome do atributo a definir ou atualizar
     * @param valor    novo valor do atributo
     * @throws exceptions.UsuarioNaoCadastradoException se o ID de sess\u00E3o for inv\u00E1lido
     */
    public void editarPerfil(String id, String atributo, String valor) {
        controlador.editarPerfil(id, atributo, valor);
    }

    // USER STORY 3 - AMIGOS

    /**
     * Envia um convite de amizade ou confirma uma amizade pendente.
     *
     * @param id    identificador da sess\u00E3o do usu\u00E1rio que envia o convite
     * @param amigo login do usu\u00E1rio a ser adicionado
     * @throws exceptions.UsuarioNaoCadastradoException se a sess\u00E3o ou o login forem inv\u00E1lidos
     * @throws exceptions.AutoAmizadeException          se o usu\u00E1rio tentar adicionar a si mesmo
     * @throws exceptions.UsuarioJaAmigoException       se a amizade j\u00E1 estiver confirmada
     * @throws exceptions.ConvitePendenteException      se j\u00E1 houver convite pendente
     */
    public void adicionarAmigo(String id, String amigo) {
        controlador.adicionarAmigo(id, amigo);
    }

    /**
     * Verifica se dois usu\u00E1rios s\u00E3o amigos confirmados.
     *
     * @param login login do primeiro usu\u00E1rio
     * @param amigo login do segundo usu\u00E1rio
     * @return {@code true} se forem amigos confirmados; {@code false} caso contr\u00E1rio
     */
    public boolean ehAmigo(String login, String amigo) {
        return controlador.ehAmigo(login, amigo);
    }

    /**
     * Retorna a lista de amigos do usu\u00E1rio no formato {@code {login1,login2,...}}.
     *
     * @param login login do usu\u00E1rio
     * @return string formatada com os logins dos amigos
     * @throws exceptions.UsuarioNaoCadastradoException se o login n\u00E3o existir
     */
    public String getAmigos(String login) {
        return controlador.getAmigos(login);
    }

    // USER STORY 4 - RECADOS

    /**
     * Envia um recado ao usu\u00E1rio destinat\u00E1rio.
     *
     * @param id           identificador da sess\u00E3o do remetente
     * @param destinatario login do destinat\u00E1rio
     * @param recado       texto da mensagem
     * @throws exceptions.UsuarioNaoCadastradoException se a sess\u00E3o ou o destinat\u00E1rio forem inv\u00E1lidos
     * @throws exceptions.AutoRecadoException           se o remetente tentar enviar recado a si mesmo
     */
    public void enviarRecado(String id, String destinatario, String recado) {
        controlador.enviarRecado(id, destinatario, recado);
    }

    /**
     * L\u00EA e remove o pr\u00F3ximo recado da fila do usu\u00E1rio da sess\u00E3o informada.
     *
     * @param id identificador da sess\u00E3o ativa
     * @return texto do pr\u00F3ximo recado n\u00E3o lido
     * @throws exceptions.UsuarioNaoCadastradoException se o ID de sess\u00E3o for inv\u00E1lido
     * @throws exceptions.SemRecadosException           se n\u00E3o houver recados na fila
     */
    public String lerRecado(String id) {
        return controlador.lerRecado(id);
    }
}
