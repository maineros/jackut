package jackut.controllers;

import jackut.entities.Comunidade;
import jackut.entities.Usuario;
import jackut.exceptions.*;
import jackut.repositories.Repositorio;
import jackut.services.GerenciadorSessoes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Orquestra as operações de negócio do sistema Jackut.
 *
 * <p>Responsável por coordenar entidades, resolver sessões (delegado a
 * {@link GerenciadorSessoes}) e acionar a persistência (delegada a
 * {@link Repositorio}). Não contém detalhes de I/O nem replica regras
 * que já pertencem às entidades — seguindo o princípio <em>Tell, Don't
 * Ask</em>, o Controlador ordena ações às entidades e deixa que elas
 * mesmas validem seu estado e lancem as exceções adequadas.</p>
 */
public class Controlador {

    private final Repositorio repositorio;
    private final GerenciadorSessoes sessoes;
    private Map<String, Usuario> usuarios;
    private Map<String, Comunidade> comunidades;

    /**
     * Inicializa o Controlador carregando o estado persistido em disco.
     */
    public Controlador() {
        this.repositorio = new Repositorio();
        this.sessoes     = new GerenciadorSessoes();
        this.usuarios    = repositorio.carregar();
        this.comunidades = repositorio.carregarComunidades();
    }

    // =========================================================================
    // Sistema
    // =========================================================================

    /**
     * Remove todo o estado em memória e apaga os arquivos persistidos em disco.
     */
    public void zerarSistema() {
        this.usuarios.clear();
        this.comunidades.clear();
        this.sessoes.limpar();
        repositorio.apagar();
    }

    /**
     * Persiste em disco o estado atual de usuários e comunidades.
     */
    public void encerrarSistema() {
        repositorio.salvar(this.usuarios);
        repositorio.salvarComunidades(this.comunidades);
    }
    
    // User Story 1 - Conta

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
        if (this.usuarios.containsKey(login))
            throw new ContaJaExisteException();
        this.usuarios.put(login, new Usuario(login, senha, nome));
    }

    /**
     * Autentica um usuário e abre uma nova sessão para ele.
     *
     * @param login login do usuário
     * @param senha senha do usuário
     * @return identificador único (UUID) da sessão aberta
     * @throws LoginOuSenhaInvalidosException se as credenciais forem inválidas
     */
    public String abrirSessao(String login, String senha) throws LoginOuSenhaInvalidosException {
        if (login == null || login.isEmpty() || senha == null || senha.isEmpty())
            throw new LoginOuSenhaInvalidosException();
        Usuario usuario = this.usuarios.get(login);
        if (usuario == null || !usuario.verificarSenha(senha))
            throw new LoginOuSenhaInvalidosException();
        return this.sessoes.abrir(login);
    }

    // User Story 2 - Perfil

    /**
     * Retorna o valor de um atributo de perfil do usuário.
     *
     * @param login    login do usuário
     * @param atributo nome do atributo de perfil consultado
     * @return valor do atributo
     * @throws UsuarioNaoCadastradoException  se o login não existir
     * @throws AtributoNaoPreenchidoException se o atributo não tiver sido definido
     */
    public String getAtributoUsuario(String login, String atributo)
            throws UsuarioNaoCadastradoException, AtributoNaoPreenchidoException {
        return buscarUsuario(login).getAtributo(atributo);
    }

    /**
     * Define ou atualiza um atributo de perfil do usuário autenticado pela sessão.
     *
     * @param id       identificador da sessão do usuário
     * @param atributo nome do atributo de perfil a definir
     * @param valor    novo valor do atributo
     * @throws UsuarioNaoCadastradoException se a sessão não estiver ativa
     */
    public void editarPerfil(String id, String atributo, String valor)
            throws UsuarioNaoCadastradoException {
        getUsuarioDaSessao(id).setAtributo(atributo, valor);
    }

    // User Story 3 - Amigos

    /**
     * Envia um convite de amizade ou confirma amizade pendente.
     * Toda a lógica de validação e estado é delegada a {@link Usuario#tentarAdicionarAmigo}.
     *
     * @param id         identificador da sessão do usuário remetente
     * @param loginAmigo login do usuário a ser adicionado como amigo
     * @throws UsuarioNaoCadastradoException se a sessão ou o login forem inválidos
     * @throws AutoAmizadeException          se o usuário tentar adicionar a si mesmo
     * @throws InimigoException              se o destinatário tiver o remetente como inimigo
     * @throws UsuarioJaAmigoException       se já forem amigos confirmados
     * @throws ConvitePendenteException      se já houver convite pendente
     */
    public void adicionarAmigo(String id, String loginAmigo)
            throws UsuarioNaoCadastradoException, AutoAmizadeException,
                   UsuarioJaAmigoException, ConvitePendenteException, InimigoException {
        Usuario remetente = getUsuarioDaSessao(id);
        Usuario destino   = buscarUsuario(loginAmigo);
        remetente.tentarAdicionarAmigo(destino);
    }

    /**
     * Verifica se dois usuários são amigos confirmados.
     *
     * @param login      login do usuário consultado
     * @param loginAmigo login do possível amigo
     * @return {@code true} se forem amigos; {@code false} caso contrário
     */
    public boolean ehAmigo(String login, String loginAmigo) {
        Usuario usuario = this.usuarios.get(login);
        return usuario != null && usuario.ehAmigo(loginAmigo);
    }

    /**
     * Retorna a lista de amigos do usuário no formato {@code {a1,a2,...}}.
     *
     * @param login login do usuário
     * @return amigos em ordem de confirmação
     * @throws UsuarioNaoCadastradoException se o login não existir
     */
    public String getAmigos(String login) throws UsuarioNaoCadastradoException {
        return "{" + String.join(",", buscarUsuario(login).getAmigos()) + "}";
    }

    // User Story 4 - Recados

    /**
     * Envia um recado do usuário autenticado para outro usuário.
     *
     * @param id           identificador da sessão do remetente
     * @param loginDestino login do destinatário
     * @param recado       texto do recado
     * @throws UsuarioNaoCadastradoException se a sessão ou o destinatário forem inválidos
     * @throws AutoRecadoException           se o usuário tentar enviar recado a si mesmo
     * @throws InimigoException              se o destinatário tiver o remetente como inimigo
     */
    public void enviarRecado(String id, String loginDestino, String recado)
            throws UsuarioNaoCadastradoException, AutoRecadoException, InimigoException {
        Usuario remetente = getUsuarioDaSessao(id);
        if (remetente.getLogin().equals(loginDestino))
            throw new AutoRecadoException();
        Usuario destino = buscarUsuario(loginDestino);
        if (destino.ehInimigoDe(remetente.getLogin()))
            throw new InimigoException(destino.getAtributoSemExcecao("nome"));
        destino.receberRecado(remetente.getLogin(), recado);
    }

    /**
     * Lê e remove o recado mais antigo da fila do usuário autenticado.
     *
     * @param id identificador da sessão do usuário
     * @return texto do recado mais antigo
     * @throws UsuarioNaoCadastradoException se a sessão não estiver ativa
     * @throws SemRecadosException           se não houver recados na fila
     */
    public String lerRecado(String id) throws UsuarioNaoCadastradoException, SemRecadosException {
        return getUsuarioDaSessao(id).lerProximoRecado();
    }

    // User Story 5 - Criação de Comunidades

    /**
     * Cria uma nova comunidade, tornando o usuário autenticado seu dono e primeiro membro.
     *
     * @param id        identificador da sessão do usuário criador
     * @param nome      nome único da comunidade
     * @param descricao descrição da comunidade
     * @throws UsuarioNaoCadastradoException se a sessão não estiver ativa
     * @throws ComunidadeJaExisteException   se já existir comunidade com o nome informado
     */
    public void criarComunidade(String id, String nome, String descricao)
            throws UsuarioNaoCadastradoException, ComunidadeJaExisteException {
        Usuario dono = getUsuarioDaSessao(id);
        if (this.comunidades.containsKey(nome))
            throw new ComunidadeJaExisteException();
        this.comunidades.put(nome, new Comunidade(nome, descricao, dono.getLogin()));
        dono.adicionarComunidade(nome);
    }

    /**
     * Retorna a descrição de uma comunidade.
     *
     * @param nome nome da comunidade
     * @return descrição
     * @throws ComunidadeNaoExisteException se a comunidade não existir
     */
    public String getDescricaoComunidade(String nome) throws ComunidadeNaoExisteException {
        return buscarComunidade(nome).getDescricao();
    }

    /**
     * Retorna o login do dono de uma comunidade.
     *
     * @param nome nome da comunidade
     * @return login do dono
     * @throws ComunidadeNaoExisteException se a comunidade não existir
     */
    public String getDonoComunidade(String nome) throws ComunidadeNaoExisteException {
        return buscarComunidade(nome).getLoginDono();
    }

    /**
     * Retorna os membros de uma comunidade no formato {@code {m1,m2,...}}.
     *
     * @param nome nome da comunidade
     * @return membros em ordem de entrada
     * @throws ComunidadeNaoExisteException se a comunidade não existir
     */
    public String getMembrosComunidade(String nome) throws ComunidadeNaoExisteException {
        return "{" + String.join(",", buscarComunidade(nome).getMembros()) + "}";
    }

    /**
     * Retorna as comunidades do usuário no formato {@code {c1,c2,...}}.
     *
     * @param login login do usuário
     * @return comunidades em ordem de entrada
     * @throws UsuarioNaoCadastradoException se o login não existir
     */
    public String getComunidades(String login) throws UsuarioNaoCadastradoException {
        return "{" + String.join(",", buscarUsuario(login).getComunidades()) + "}";
    }

    // User Story 6 - Adição a Comunidades

    /**
     * Adiciona o usuário autenticado como membro de uma comunidade existente.
     * A validação de duplicidade é delegada a {@link Comunidade#adicionarMembro}.
     *
     * @param id   identificador da sessão do usuário
     * @param nome nome da comunidade a ingressar
     * @throws UsuarioNaoCadastradoException se a sessão não estiver ativa
     * @throws ComunidadeNaoExisteException  se a comunidade não existir
     * @throws UsuarioJaMembroException      se o usuário já for membro
     */
    public void adicionarComunidade(String id, String nome)
            throws UsuarioNaoCadastradoException, ComunidadeNaoExisteException, UsuarioJaMembroException {
        Usuario usuario   = getUsuarioDaSessao(id);
        Comunidade comunidade = buscarComunidade(nome);
        comunidade.adicionarMembro(usuario.getLogin()); // lança UsuarioJaMembroException se duplicado
        usuario.adicionarComunidade(nome);
    }

    // User Story 7 - Mensagens de Comunidade

    /**
     * Envia uma mensagem para todos os membros de uma comunidade.
     *
     * @param id             identificador da sessão do remetente
     * @param nomeComunidade nome da comunidade destinatária
     * @param mensagem       texto da mensagem
     * @throws UsuarioNaoCadastradoException se a sessão não estiver ativa
     * @throws ComunidadeNaoExisteException  se a comunidade não existir
     */
    public void enviarMensagem(String id, String nomeComunidade, String mensagem)
            throws UsuarioNaoCadastradoException, ComunidadeNaoExisteException {
        getUsuarioDaSessao(id); // valida sessão
        Comunidade comunidade = buscarComunidade(nomeComunidade);
        for (String loginMembro : comunidade.getMembros()) {
            Usuario membro = this.usuarios.get(loginMembro);
            if (membro != null) membro.receberMensagem(mensagem);
        }
    }

    /**
     * Lê e remove a mensagem de comunidade mais antiga da fila do usuário autenticado.
     *
     * @param id identificador da sessão do usuário
     * @return texto da mensagem mais antiga
     * @throws UsuarioNaoCadastradoException se a sessão não estiver ativa
     * @throws SemMensagensException         se não houver mensagens na fila
     */
    public String lerMensagem(String id) throws UsuarioNaoCadastradoException, SemMensagensException {
        return getUsuarioDaSessao(id).lerProximaMensagem();
    }

    // User Story 8 - Novos Relacionamentos

    /**
     * Adiciona um ídolo ao usuário autenticado.
     * Toda a lógica de validação é delegada a {@link Usuario#tentarAdicionarIdolo}.
     *
     * @param id         identificador da sessão do fã
     * @param loginIdolo login do ídolo
     * @throws UsuarioNaoCadastradoException se a sessão ou o login forem inválidos
     * @throws AutoIdoloException            se tentar adicionar a si mesmo
     * @throws InimigoException              se o ídolo tiver o fã como inimigo
     * @throws UsuarioJaIdoloException       se o ídolo já estiver na lista
     */
    public void adicionarIdolo(String id, String loginIdolo)
            throws UsuarioNaoCadastradoException, AutoIdoloException,
                   UsuarioJaIdoloException, InimigoException {
        Usuario fa    = getUsuarioDaSessao(id);
        Usuario idolo = buscarUsuario(loginIdolo);
        fa.tentarAdicionarIdolo(idolo);
    }

    /**
     * Verifica se um usuário é fã de outro.
     *
     * @param loginFa    login do possível fã
     * @param loginIdolo login do possível ídolo
     * @return {@code true} se for fã
     */
    public boolean ehFa(String loginFa, String loginIdolo) {
        Usuario fa = this.usuarios.get(loginFa);
        return fa != null && fa.ehFaDe(loginIdolo);
    }

    /**
     * Retorna os fãs de um usuário no formato {@code {f1,f2,...}}.
     *
     * @param login login do ídolo consultado
     * @return fãs na ordem de criação dos usuários
     * @throws UsuarioNaoCadastradoException se o login não existir
     */
    public String getFas(String login) throws UsuarioNaoCadastradoException {
        buscarUsuario(login);
        List<String> fas = new ArrayList<>();
        for (Usuario u : this.usuarios.values()) {
            if (u.ehFaDe(login)) fas.add(u.getLogin());
        }
        return "{" + String.join(",", fas) + "}";
    }

    /**
     * Adiciona uma paquera ao usuário autenticado.
     * Toda a lógica de validação e match mútuo é delegada a
     * {@link Usuario#tentarAdicionarPaquera}.
     *
     * @param id           identificador da sessão do usuário
     * @param loginPaquera login da paquera
     * @throws UsuarioNaoCadastradoException se a sessão ou o login forem inválidos
     * @throws AutoPaqueraException          se tentar adicionar a si mesmo
     * @throws InimigoException              se o alvo tiver o usuário como inimigo
     * @throws UsuarioJaPaqueraException     se a paquera já estiver na lista
     */
    public void adicionarPaquera(String id, String loginPaquera)
            throws UsuarioNaoCadastradoException, AutoPaqueraException,
                   UsuarioJaPaqueraException, InimigoException {
        Usuario usuario = getUsuarioDaSessao(id);
        Usuario alvo    = buscarUsuario(loginPaquera);
        usuario.tentarAdicionarPaquera(alvo);
    }

    /**
     * Verifica se o usuário autenticado tem outro como paquera.
     *
     * @param id           identificador da sessão do usuário
     * @param loginPaquera login do possível paquera
     * @return {@code true} se for paquera
     * @throws UsuarioNaoCadastradoException se a sessão não estiver ativa
     */
    public boolean ehPaquera(String id, String loginPaquera) throws UsuarioNaoCadastradoException {
        return getUsuarioDaSessao(id).ehPaqueraDe(loginPaquera);
    }

    /**
     * Retorna as paqueras do usuário autenticado no formato {@code {p1,p2,...}}.
     *
     * @param id identificador da sessão do usuário
     * @return paqueras em ordem de adição
     * @throws UsuarioNaoCadastradoException se a sessão não estiver ativa
     */
    public String getPaqueras(String id) throws UsuarioNaoCadastradoException {
        return "{" + String.join(",", getUsuarioDaSessao(id).getPaqueras()) + "}";
    }

    /**
     * Declara um usuário como inimigo do usuário autenticado.
     * Toda a lógica de validação é delegada a {@link Usuario#tentarAdicionarInimigo}.
     *
     * @param id           identificador da sessão do usuário
     * @param loginInimigo login do inimigo
     * @throws UsuarioNaoCadastradoException se a sessão ou o login forem inválidos
     * @throws AutoInimigoException          se tentar declarar a si mesmo como inimigo
     * @throws UsuarioJaInimigoException     se o alvo já estiver na lista de inimigos
     */
    public void adicionarInimigo(String id, String loginInimigo)
            throws UsuarioNaoCadastradoException, AutoInimigoException, UsuarioJaInimigoException {
        buscarUsuario(loginInimigo); // valida existência
        getUsuarioDaSessao(id).tentarAdicionarInimigo(loginInimigo);
    }

    // User Story 9 - Remoção de Conta

    /**
     * Remove permanentemente o usuário autenticado do sistema em cascata:
     * dissolve as comunidades das quais é dono (notificando os membros via
     * {@link Comunidade#dissolver}), sai das demais comunidades, remove todos
     * os relacionamentos que outros usuários têm com ele e descarta os recados
     * não lidos que ele enviou.
     *
     * @param id identificador da sessão do usuário a remover
     * @throws UsuarioNaoCadastradoException se a sessão não estiver ativa
     */
    public void removerUsuario(String id) throws UsuarioNaoCadastradoException {
        Usuario usuario = getUsuarioDaSessao(id);
        String login    = usuario.getLogin();

        // Dissolver comunidades das quais é dono e sair das demais
        List<String> comunidadesParaRemover = new ArrayList<>();
        for (Map.Entry<String, Comunidade> entry : this.comunidades.entrySet()) {
            if (entry.getValue().ehDono(login))
                comunidadesParaRemover.add(entry.getKey());
        }
        for (String nomeCom : comunidadesParaRemover) {
            Comunidade com = this.comunidades.remove(nomeCom);
            com.dissolver(this.usuarios, login); // Comunidade notifica os membros
        }
        for (String nomeCom : new ArrayList<>(usuario.getComunidades())) {
            Comunidade com = this.comunidades.get(nomeCom);
            if (com != null) com.removerMembro(login);
        }

        // Limpar referências nos outros usuários
        for (Usuario outro : this.usuarios.values()) {
            if (!outro.getLogin().equals(login)) {
                outro.removerRelacionamento(login);
                outro.removerRecadosDe(login);
            }
        }

        this.sessoes.invalidar(id);
        this.usuarios.remove(login);
    }

    // Auxiliares Privados

    /**
     * Busca um usuário pelo login.
     *
     * @param login login do usuário
     * @return entidade {@link Usuario} correspondente
     * @throws UsuarioNaoCadastradoException se não existir usuário com o login informado
     */
    private Usuario buscarUsuario(String login) throws UsuarioNaoCadastradoException {
        Usuario usuario = this.usuarios.get(login);
        if (usuario == null) throw new UsuarioNaoCadastradoException();
        return usuario;
    }

    /**
     * Busca uma comunidade pelo nome.
     *
     * @param nome nome da comunidade
     * @return entidade {@link Comunidade} correspondente
     * @throws ComunidadeNaoExisteException se não existir comunidade com o nome informado
     */
    private Comunidade buscarComunidade(String nome) throws ComunidadeNaoExisteException {
        Comunidade comunidade = this.comunidades.get(nome);
        if (comunidade == null) throw new ComunidadeNaoExisteException();
        return comunidade;
    }

    /**
     * Resolve o usuário autenticado em uma sessão ativa.
     *
     * @param id identificador da sessão
     * @return entidade {@link Usuario} autenticada na sessão
     * @throws UsuarioNaoCadastradoException se a sessão não estiver ativa
     */
    private Usuario getUsuarioDaSessao(String id) throws UsuarioNaoCadastradoException {
        if (!this.sessoes.estaAtiva(id))
            throw new UsuarioNaoCadastradoException();
        return this.usuarios.get(this.sessoes.getLogin(id));
    }
}