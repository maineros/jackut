package jackut;

import jackut.controllers.Controlador;
import jackut.exceptions.*;

/**
 * É a única porta de entrada do sistema Jackut para o framework EasyAccept.
 *
 * <p>Não contém nenhuma lógica de negócio, validação ou instanciação
 * direta de entidades de domínio: atua exclusivamente como despachante
 * (delegator), repassando cada chamada ao {@link Controlador}.</p>
 */
public class Facade {

    private final Controlador controlador;

    /**
     * Inicializa a Facade e o {@link Controlador} subjacente, que por sua
     * vez carrega o estado persistido em disco (usuários e comunidades).
     */
    public Facade() {
        this.controlador = new Controlador();
    }

    // User Story 1 - Conta

    /** Remove todo o estado do sistema e apaga os dados persistidos em disco. */
    public void zerarSistema() { controlador.zerarSistema(); }

    /** Persiste em disco o estado atual do sistema. */
    public void encerrarSistema() { controlador.encerrarSistema(); }

    /**
     * Cria um novo usuário no sistema.
     *
     * @param login login único do novo usuário
     * @param senha senha de acesso do novo usuário
     * @param nome  nome de exibição do novo usuário
     * @throws LoginInvalidoException se o login for nulo ou vazio
     * @throws SenhaInvalidaException se a senha for nula ou vazia
     * @throws ContaJaExisteException se já existir uma conta com o login informado
     */
    public void criarUsuario(String login, String senha, String nome)
            throws LoginInvalidoException, SenhaInvalidaException, ContaJaExisteException {
        controlador.criarUsuario(login, senha, nome);
    }

    /**
     * Abre uma sessão para o usuário cujas credenciais são informadas.
     *
     * @param login login do usuário
     * @param senha senha do usuário
     * @return identificador único (UUID) da sessão aberta
     * @throws LoginOuSenhaInvalidosException se o login ou a senha forem inválidos
     */
    public String abrirSessao(String login, String senha)
            throws LoginOuSenhaInvalidosException {
        return controlador.abrirSessao(login, senha);
    }

    // User Story 2 - Perfil

    /**
     * Retorna o valor de um atributo de perfil do usuário.
     *
     * @param login    login do usuário
     * @param atributo nome do atributo de perfil consultado
     * @return valor do atributo
     * @throws UsuarioNaoCadastradoException  se o login não corresponder a um usuário existente
     * @throws AtributoNaoPreenchidoException se o atributo não tiver sido definido
     */
    public String getAtributoUsuario(String login, String atributo)
            throws UsuarioNaoCadastradoException, AtributoNaoPreenchidoException {
        return controlador.getAtributoUsuario(login, atributo);
    }

    /**
     * Define ou atualiza um atributo de perfil do usuário autenticado pela sessão.
     *
     * @param id       identificador da sessão do usuário
     * @param atributo nome do atributo de perfil a definir
     * @param valor    novo valor do atributo
     * @throws UsuarioNaoCadastradoException se a sessão informada não estiver ativa
     */
    public void editarPerfil(String id, String atributo, String valor)
            throws UsuarioNaoCadastradoException {
        controlador.editarPerfil(id, atributo, valor);
    }

    // User Story 3 - Amigos

    /**
     * Envia um convite de amizade ou confirma uma amizade pendente.
     *
     * @param id    identificador da sessão do usuário remetente
     * @param amigo login do usuário a ser adicionado como amigo
     * @throws UsuarioNaoCadastradoException se a sessão ou o login do amigo forem inválidos
     * @throws AutoAmizadeException          se o usuário tentar adicionar a si mesmo
     * @throws UsuarioJaAmigoException       se os usuários já forem amigos
     * @throws ConvitePendenteException      se já houver um convite pendente
     * @throws InimigoException              se o destinatário tiver declarado o remetente como inimigo
     */
    public void adicionarAmigo(String id, String amigo)
            throws UsuarioNaoCadastradoException, AutoAmizadeException,
                   UsuarioJaAmigoException, ConvitePendenteException, InimigoException {
        controlador.adicionarAmigo(id, amigo);
    }

    /**
     * Verifica se dois usuários são amigos confirmados.
     *
     * @param login login do usuário consultado
     * @param amigo login do possível amigo
     * @return {@code true} se forem amigos; {@code false} caso contrário
     */
    public boolean ehAmigo(String login, String amigo) {
        return controlador.ehAmigo(login, amigo);
    }

    /**
     * Retorna a lista de amigos do usuário no formato {@code {amigo1,amigo2,...}}.
     *
     * @param login login do usuário
     * @return lista de amigos em ordem de confirmação
     * @throws UsuarioNaoCadastradoException se o login não corresponder a um usuário existente
     */
    public String getAmigos(String login) throws UsuarioNaoCadastradoException {
        return controlador.getAmigos(login);
    }

    // User Story 4 - Recados

    /**
     * Envia um recado de um usuário para outro.
     *
     * @param id           identificador da sessão do usuário remetente
     * @param destinatario login do usuário destinatário
     * @param recado       texto do recado
     * @throws UsuarioNaoCadastradoException se a sessão ou o destinatário forem inválidos
     * @throws AutoRecadoException           se o usuário tentar enviar recado para si mesmo
     * @throws InimigoException              se o destinatário tiver declarado o remetente como inimigo
     */
    public void enviarRecado(String id, String destinatario, String recado)
            throws UsuarioNaoCadastradoException, AutoRecadoException, InimigoException {
        controlador.enviarRecado(id, destinatario, recado);
    }

    /**
     * Lê e remove o recado mais antigo da fila do usuário autenticado pela sessão.
     *
     * @param id identificador da sessão do usuário
     * @return texto do recado mais antigo na fila
     * @throws UsuarioNaoCadastradoException se a sessão informada não estiver ativa
     * @throws SemRecadosException           se não houver recados na fila
     */
    public String lerRecado(String id)
            throws UsuarioNaoCadastradoException, SemRecadosException {
        return controlador.lerRecado(id);
    }

    // User Story 5 - Comunidades

    /**
     * Cria uma nova comunidade, tornando o usuário autenticado seu dono e primeiro membro.
     *
     * @param id        identificador da sessão do usuário criador
     * @param nome      nome único da comunidade
     * @param descricao descrição da comunidade
     * @throws UsuarioNaoCadastradoException se a sessão informada não estiver ativa
     * @throws ComunidadeJaExisteException   se já existir uma comunidade com o nome informado
     */
    public void criarComunidade(String id, String nome, String descricao)
            throws UsuarioNaoCadastradoException, ComunidadeJaExisteException {
        controlador.criarComunidade(id, nome, descricao);
    }

    /**
     * Retorna a descrição de uma comunidade.
     *
     * @param nome nome da comunidade
     * @return descrição da comunidade
     * @throws ComunidadeNaoExisteException se não existir comunidade com o nome informado
     */
    public String getDescricaoComunidade(String nome) throws ComunidadeNaoExisteException {
        return controlador.getDescricaoComunidade(nome);
    }

    /**
     * Retorna o login do dono de uma comunidade.
     *
     * @param nome nome da comunidade
     * @return login do dono
     * @throws ComunidadeNaoExisteException se não existir comunidade com o nome informado
     */
    public String getDonoComunidade(String nome) throws ComunidadeNaoExisteException {
        return controlador.getDonoComunidade(nome);
    }

    /**
     * Retorna os membros de uma comunidade no formato {@code {membro1,membro2,...}}.
     *
     * @param nome nome da comunidade
     * @return lista de membros em ordem de entrada
     * @throws ComunidadeNaoExisteException se não existir comunidade com o nome informado
     */
    public String getMembrosComunidade(String nome) throws ComunidadeNaoExisteException {
        return controlador.getMembrosComunidade(nome);
    }

    /**
     * Retorna as comunidades do usuário no formato {@code {c1,c2,...}}.
     *
     * @param login login do usuário
     * @return lista de comunidades em ordem de entrada
     * @throws UsuarioNaoCadastradoException se o login não corresponder a um usuário existente
     */
    public String getComunidades(String login) throws UsuarioNaoCadastradoException {
        return controlador.getComunidades(login);
    }

    // User Story 6 - Adicionar membro

    /**
     * Adiciona o usuário autenticado pela sessão como membro de uma comunidade existente.
     *
     * @param id   identificador da sessão do usuário
     * @param nome nome da comunidade a ingressar
     * @throws UsuarioNaoCadastradoException se a sessão informada não estiver ativa
     * @throws ComunidadeNaoExisteException  se não existir comunidade com o nome informado
     * @throws UsuarioJaMembroException      se o usuário já for membro da comunidade
     */
    public void adicionarComunidade(String id, String nome)
            throws UsuarioNaoCadastradoException, ComunidadeNaoExisteException, UsuarioJaMembroException {
        controlador.adicionarComunidade(id, nome);
    }

    // User Story 7 - Mensagens de Comunidade

    /**
     * Envia uma mensagem para todos os membros de uma comunidade.
     *
     * @param id         identificador da sessão do usuário remetente
     * @param comunidade nome da comunidade destinatária
     * @param mensagem   texto da mensagem
     * @throws UsuarioNaoCadastradoException se a sessão informada não estiver ativa
     * @throws ComunidadeNaoExisteException  se não existir comunidade com o nome informado
     */
    public void enviarMensagem(String id, String comunidade, String mensagem)
            throws UsuarioNaoCadastradoException, ComunidadeNaoExisteException {
        controlador.enviarMensagem(id, comunidade, mensagem);
    }

    /**
     * Lê e remove a mensagem de comunidade mais antiga da fila do usuário autenticado.
     *
     * @param id identificador da sessão do usuário
     * @return texto da mensagem mais antiga na fila
     * @throws UsuarioNaoCadastradoException se a sessão informada não estiver ativa
     * @throws SemMensagensException         se não houver mensagens na fila
     */
    public String lerMensagem(String id)
            throws UsuarioNaoCadastradoException, SemMensagensException {
        return controlador.lerMensagem(id);
    }

    // User Story 8 - Novos Relacionamentos

    /**
     * Adiciona um usuário como ídolo do usuário autenticado pela sessão.
     *
     * @param id    identificador da sessão do usuário fã
     * @param idolo login do usuário a ser adicionado como ídolo
     * @throws UsuarioNaoCadastradoException se a sessão ou o login do ídolo forem inválidos
     * @throws AutoIdoloException            se o usuário tentar adicionar a si mesmo como ídolo
     * @throws UsuarioJaIdoloException       se o usuário já for fã do ídolo informado
     * @throws InimigoException              se o ídolo tiver declarado o fã como inimigo
     */
    public void adicionarIdolo(String id, String idolo)
            throws UsuarioNaoCadastradoException, AutoIdoloException,
                   UsuarioJaIdoloException, InimigoException {
        controlador.adicionarIdolo(id, idolo);
    }

    /**
     * Verifica se um usuário é fã de outro.
     *
     * @param login login do possível fã
     * @param idolo login do possível ídolo
     * @return {@code true} se {@code login} for fã de {@code idolo}
     */
    public boolean ehFa(String login, String idolo) {
        return controlador.ehFa(login, idolo);
    }

    /**
     * Retorna os fãs de um usuário no formato {@code {fa1,fa2,...}}.
     *
     * @param login login do ídolo consultado
     * @return lista de fãs na ordem em que adicionaram o ídolo
     * @throws UsuarioNaoCadastradoException se o login não corresponder a um usuário existente
     */
    public String getFas(String login) throws UsuarioNaoCadastradoException {
        return controlador.getFas(login);
    }

    /**
     * Adiciona um usuário como paquera do usuário autenticado pela sessão.
     *
     * @param id      identificador da sessão do usuário
     * @param paquera login do usuário a ser adicionado como paquera
     * @throws UsuarioNaoCadastradoException se a sessão ou o login da paquera forem inválidos
     * @throws AutoPaqueraException          se o usuário tentar adicionar a si mesmo como paquera
     * @throws UsuarioJaPaqueraException     se o usuário já tiver o alvo como paquera
     * @throws InimigoException              se o alvo tiver declarado o usuário como inimigo
     */
    public void adicionarPaquera(String id, String paquera)
            throws UsuarioNaoCadastradoException, AutoPaqueraException,
                   UsuarioJaPaqueraException, InimigoException {
        controlador.adicionarPaquera(id, paquera);
    }

    /**
     * Verifica se o usuário autenticado pela sessão tem outro usuário como paquera.
     *
     * @param id      identificador da sessão do usuário
     * @param paquera login do possível paquera
     * @return {@code true} se o usuário tiver {@code paquera} em sua lista
     * @throws UsuarioNaoCadastradoException se a sessão informada não estiver ativa
     */
    public boolean ehPaquera(String id, String paquera)
            throws UsuarioNaoCadastradoException {
        return controlador.ehPaquera(id, paquera);
    }

    /**
     * Retorna as paqueras do usuário autenticado no formato {@code {p1,p2,...}}.
     *
     * @param id identificador da sessão do usuário
     * @return lista de paqueras em ordem de adição
     * @throws UsuarioNaoCadastradoException se a sessão informada não estiver ativa
     */
    public String getPaqueras(String id) throws UsuarioNaoCadastradoException {
        return controlador.getPaqueras(id);
    }

    /**
     * Declara um usuário como inimigo do usuário autenticado pela sessão.
     *
     * @param id      identificador da sessão do usuário
     * @param inimigo login do usuário a ser declarado como inimigo
     * @throws UsuarioNaoCadastradoException se a sessão ou o login do inimigo forem inválidos
     * @throws AutoInimigoException          se o usuário tentar declarar a si mesmo como inimigo
     * @throws UsuarioJaInimigoException     se o usuário já tiver o alvo como inimigo
     */
    public void adicionarInimigo(String id, String inimigo)
            throws UsuarioNaoCadastradoException, AutoInimigoException, UsuarioJaInimigoException {
        controlador.adicionarInimigo(id, inimigo);
    }

    // User Story 9 - Remover Usuário

    /**
     * Remove permanentemente o usuário autenticado pela sessão do sistema.
     *
     * @param id identificador da sessão do usuário a remover
     * @throws UsuarioNaoCadastradoException se a sessão informada não estiver ativa
     */
    public void removerUsuario(String id) throws UsuarioNaoCadastradoException {
        controlador.removerUsuario(id);
    }
}
