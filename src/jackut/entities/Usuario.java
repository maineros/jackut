package jackut.entities;

import jackut.exceptions.AtributoNaoPreenchidoException;
import jackut.exceptions.AutoAmizadeException;
import jackut.exceptions.AutoIdoloException;
import jackut.exceptions.AutoInimigoException;
import jackut.exceptions.AutoPaqueraException;
import jackut.exceptions.ConvitePendenteException;
import jackut.exceptions.InimigoException;
import jackut.exceptions.LoginInvalidoException;
import jackut.exceptions.SenhaInvalidaException;
import jackut.exceptions.SemMensagensException;
import jackut.exceptions.SemRecadosException;
import jackut.exceptions.UsuarioJaAmigoException;
import jackut.exceptions.UsuarioJaIdoloException;
import jackut.exceptions.UsuarioJaInimigoException;
import jackut.exceptions.UsuarioJaPaqueraException;

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
 * Entidade rica do domínio Jackut que representa um usuário da rede social.
 *
 * <p>Esta classe não é um simples repositório de dados (Anemic Domain
 * Model): ela valida seu próprio estado no construtor e encapsula
 * amizades, convites, recados, comunidades e os demais relacionamentos
 * (US8) em coleções internas privadas, expondo apenas operações
 * semânticas. Coleções devolvidas a chamadores externos são sempre
 * cópias não-modificáveis, protegendo o estado interno de alterações
 * indevidas.</p>
 *
 * <p>Seguindo o princípio <em>Tell, Don't Ask</em>, as operações de
 * relacionamento (amizade, ídolo, paquera, inimigo) incluem suas
 * próprias validações e lançam as exceções adequadas — o
 * {@link jackut.controllers.Controlador} não precisa interrogar o estado
 * interno antes de ordenar uma ação.</p>
 *
 * <p>Implementa {@link Serializable} para suportar a persistência do
 * estado em disco entre execuções.</p>
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String login;
    private final String senha;
    private final Map<String, String> perfil;

    // Amizades
    private final List<String> amigos;
    private final Set<String> convitesEnviados;

    // Recados: guardamos o remetente para remoção em cascata (US9)
    private final Queue<Recado> recados;

    // Mensagens de comunidade
    private final Queue<String> mensagens;

    // Comunidades
    private final List<String> comunidades;

    // Relacionamentos US8
    private final List<String> idolos;
    private final List<String> paqueras;
    private final List<String> inimigos;

    // -------------------------------------------------------------------------
    // Construtor
    // -------------------------------------------------------------------------

    /**
     * Cria um novo usuário, validando login e senha antes que o objeto
     * passe a existir com estado inválido.
     *
     * @param login login único do usuário; não pode ser nulo ou vazio
     * @param senha senha de acesso do usuário; não pode ser nula ou vazia
     * @param nome  nome de exibição do usuário
     * @throws LoginInvalidoException se {@code login} for nulo, vazio ou só espaços
     * @throws SenhaInvalidaException se {@code senha} for nula, vazia ou só espaços
     */
    public Usuario(String login, String senha, String nome)
            throws LoginInvalidoException, SenhaInvalidaException {
        if (login == null || login.trim().isEmpty()) throw new LoginInvalidoException();
        if (senha == null || senha.trim().isEmpty()) throw new SenhaInvalidaException();
        this.login            = login;
        this.senha            = senha;
        this.perfil           = new HashMap<>();
        this.perfil.put("nome", nome);
        this.amigos           = new ArrayList<>();
        this.convitesEnviados = new LinkedHashSet<>();
        this.recados          = new LinkedList<>();
        this.mensagens        = new LinkedList<>();
        this.comunidades      = new ArrayList<>();
        this.idolos           = new ArrayList<>();
        this.paqueras         = new ArrayList<>();
        this.inimigos         = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Credenciais
    // -------------------------------------------------------------------------

    /**
     * Retorna o login deste usuário.
     *
     * @return login do usuário
     */
    public String getLogin() { return login; }

    /**
     * Verifica se a senha informada corresponde à senha do usuário.
     *
     * @param senha senha a verificar
     * @return {@code true} se a senha informada for igual à senha cadastrada
     */
    public boolean verificarSenha(String senha) { return this.senha.equals(senha); }

    // -------------------------------------------------------------------------
    // Perfil
    // -------------------------------------------------------------------------

    /**
     * Retorna o valor de um atributo de perfil.
     *
     * @param atributo nome do atributo de perfil consultado
     * @return valor associado ao atributo
     * @throws AtributoNaoPreenchidoException se o atributo não tiver sido definido
     */
    public String getAtributo(String atributo) throws AtributoNaoPreenchidoException {
        if (!this.perfil.containsKey(atributo))
            throw new AtributoNaoPreenchidoException();
        return this.perfil.get(atributo);
    }

    /**
     * Retorna o valor de um atributo sem lançar exceção checked — útil
     * para compor mensagens de exceção em fluxos onde o atributo é apenas informativo.
     *
     * @param atributo nome do atributo
     * @return valor do atributo, ou string vazia se não estiver definido
     */
    public String getAtributoSemExcecao(String atributo) {
        return this.perfil.getOrDefault(atributo, "");
    }

    /**
     * Define ou atualiza o valor de um atributo de perfil.
     *
     * @param atributo nome do atributo de perfil a definir
     * @param valor    novo valor do atributo
     */
    public void setAtributo(String atributo, String valor) { this.perfil.put(atributo, valor); }

    // -------------------------------------------------------------------------
    // Amizades (US3) — Tell, Don't Ask
    // -------------------------------------------------------------------------

    /**
     * Tenta adicionar {@code destino} como amigo, respeitando as regras
     * de amizade bilateral. Lança a exceção adequada a cada estado inválido:
     * auto-adição, inimizade, já amigo, convite pendente. Se o destino já
     * tiver convite pendente para este usuário, confirma a amizade nos dois
     * lados; caso contrário, registra um convite pendente.
     *
     * <p>Com isso o {@link jackut.controllers.Controlador} não precisa
     * interrogar o estado interno antes de ordenar a ação.</p>
     *
     * @param destino usuário a ser adicionado como amigo
     * @throws AutoAmizadeException     se tentar adicionar a si mesmo
     * @throws InimigoException         se o destino tiver este usuário como inimigo
     * @throws UsuarioJaAmigoException  se já forem amigos confirmados
     * @throws ConvitePendenteException se já houver convite pendente para o destino
     */
    public void tentarAdicionarAmigo(Usuario destino)
            throws AutoAmizadeException, InimigoException,
                   UsuarioJaAmigoException, ConvitePendenteException {
        if (this.login.equals(destino.getLogin()))
            throw new AutoAmizadeException();
        if (destino.ehInimigoDe(this.login))
            throw new InimigoException(destino.getAtributoSemExcecao("nome"));
        if (this.amigos.contains(destino.getLogin()))
            throw new UsuarioJaAmigoException();
        if (this.convitesEnviados.contains(destino.getLogin()))
            throw new ConvitePendenteException();

        if (destino.convitesEnviados.contains(this.login)) {
            // Amizade mútua: confirmar dos dois lados
            this.amigos.add(destino.getLogin());
            destino.convitesEnviados.remove(this.login);
            destino.amigos.add(this.login);
        } else {
            this.convitesEnviados.add(destino.getLogin());
        }
    }

    /**
     * Verifica se o login informado está na lista de amigos confirmados.
     *
     * @param login login a verificar
     * @return {@code true} se for amigo confirmado
     */
    public boolean ehAmigo(String login) { return this.amigos.contains(login); }

    /**
     * Retorna uma visão não-modificável da lista de amigos confirmados.
     *
     * @return lista imutável com os logins dos amigos
     */
    public List<String> getAmigos() { return Collections.unmodifiableList(this.amigos); }

    /**
     * Remove o login informado de todas as listas de relacionamento
     * (amigos, convites, ídolos, paqueras, inimigos). Usado na remoção
     * em cascata quando outro usuário encerra sua conta (US9).
     *
     * @param login login a remover de todos os relacionamentos
     */
    public void removerRelacionamento(String login) {
        this.amigos.remove(login);
        this.convitesEnviados.remove(login);
        this.idolos.remove(login);
        this.paqueras.remove(login);
        this.inimigos.remove(login);
    }

    // -------------------------------------------------------------------------
    // Recados (US4)
    // -------------------------------------------------------------------------

    /**
     * Adiciona um recado ao final da fila deste usuário.
     *
     * @param remetente login de quem enviou o recado
     * @param recado    texto do recado
     */
    public void receberRecado(String remetente, String recado) {
        this.recados.add(new Recado(remetente, recado));
    }

    /**
     * Remove e retorna o texto do recado mais antigo da fila (FIFO).
     *
     * @return texto do recado mais antigo
     * @throws SemRecadosException se a fila de recados estiver vazia
     */
    public String lerProximoRecado() throws SemRecadosException {
        if (this.recados.isEmpty()) throw new SemRecadosException();
        return this.recados.poll().getTexto();
    }

    /**
     * Remove da fila todos os recados cujo remetente seja o login informado.
     * Usado na remoção em cascata quando o remetente encerra sua conta (US9).
     *
     * @param loginRemetente login do remetente cujos recados devem ser descartados
     */
    public void removerRecadosDe(String loginRemetente) {
        this.recados.removeIf(r -> r.getRemetente().equals(loginRemetente));
    }

    // -------------------------------------------------------------------------
    // Mensagens de comunidade (US7)
    // -------------------------------------------------------------------------

    /**
     * Adiciona uma mensagem de comunidade ao final da fila deste usuário.
     *
     * @param mensagem texto da mensagem recebida
     */
    public void receberMensagem(String mensagem) { this.mensagens.add(mensagem); }

    /**
     * Remove e retorna o texto da mensagem de comunidade mais antiga (FIFO).
     *
     * @return texto da mensagem mais antiga
     * @throws SemMensagensException se a fila de mensagens estiver vazia
     */
    public String lerProximaMensagem() throws SemMensagensException {
        if (this.mensagens.isEmpty()) throw new SemMensagensException();
        return this.mensagens.poll();
    }

    // -------------------------------------------------------------------------
    // Comunidades (US5/US6)
    // -------------------------------------------------------------------------

    /**
     * Registra que este usuário passou a ser membro da comunidade informada.
     *
     * @param nomeComunidade nome da comunidade
     */
    public void adicionarComunidade(String nomeComunidade) { this.comunidades.add(nomeComunidade); }

    /**
     * Remove o registro de participação deste usuário na comunidade informada.
     *
     * @param nomeComunidade nome da comunidade a remover
     */
    public void removerComunidade(String nomeComunidade) { this.comunidades.remove(nomeComunidade); }

    /**
     * Retorna uma visão não-modificável das comunidades do usuário.
     *
     * @return lista imutável com os nomes das comunidades
     */
    public List<String> getComunidades() { return Collections.unmodifiableList(this.comunidades); }

    // -------------------------------------------------------------------------
    // Fã/Ídolo (US8) — Tell, Don't Ask
    // -------------------------------------------------------------------------

    /**
     * Registra este usuário como fã do ídolo informado, validando as regras
     * de negócio: auto-adição, inimizade e duplicidade.
     *
     * @param idolo usuário a ser adicionado como ídolo
     * @throws AutoIdoloException      se tentar adicionar a si mesmo
     * @throws InimigoException        se o ídolo tiver este usuário como inimigo
     * @throws UsuarioJaIdoloException se o ídolo já estiver na lista
     */
    public void tentarAdicionarIdolo(Usuario idolo)
            throws AutoIdoloException, InimigoException, UsuarioJaIdoloException {
        if (this.login.equals(idolo.getLogin()))
            throw new AutoIdoloException();
        if (idolo.ehInimigoDe(this.login))
            throw new InimigoException(idolo.getAtributoSemExcecao("nome"));
        if (this.idolos.contains(idolo.getLogin()))
            throw new UsuarioJaIdoloException();
        this.idolos.add(idolo.getLogin());
    }

    /**
     * Verifica se este usuário é fã do ídolo informado.
     *
     * @param loginIdolo login do possível ídolo
     * @return {@code true} se for fã
     */
    public boolean ehFaDe(String loginIdolo) { return this.idolos.contains(loginIdolo); }

    // -------------------------------------------------------------------------
    // Paquera (US8) — Tell, Don't Ask
    // -------------------------------------------------------------------------

    /**
     * Adiciona o alvo como paquera deste usuário, validando as regras de negócio:
     * auto-adição, inimizade e duplicidade. Se o alvo também tiver este usuário
     * como paquera (match mútuo), envia recado automático a ambos.
     *
     * @param alvo usuário a ser adicionado como paquera
     * @throws AutoPaqueraException      se tentar adicionar a si mesmo
     * @throws InimigoException          se o alvo tiver este usuário como inimigo
     * @throws UsuarioJaPaqueraException se o alvo já estiver na lista de paqueras
     */
    public void tentarAdicionarPaquera(Usuario alvo)
            throws AutoPaqueraException, InimigoException, UsuarioJaPaqueraException {
        if (this.login.equals(alvo.getLogin()))
            throw new AutoPaqueraException();
        if (alvo.ehInimigoDe(this.login))
            throw new InimigoException(alvo.getAtributoSemExcecao("nome"));
        if (this.paqueras.contains(alvo.getLogin()))
            throw new UsuarioJaPaqueraException();
        this.paqueras.add(alvo.getLogin());

        // Match mútuo: enviar recado automático a ambos
        if (alvo.paqueras.contains(this.login)) {
            String nomeEste = this.getAtributoSemExcecao("nome");
            String nomeAlvo = alvo.getAtributoSemExcecao("nome");
            this.receberRecado("Jackut", nomeAlvo + " é seu paquera - Recado do Jackut.");
            alvo.receberRecado("Jackut", nomeEste + " é seu paquera - Recado do Jackut.");
        }
    }

    /**
     * Verifica se este usuário tem o login informado como paquera.
     *
     * @param loginPaquera login do possível paquera
     * @return {@code true} se for paquera
     */
    public boolean ehPaqueraDe(String loginPaquera) { return this.paqueras.contains(loginPaquera); }

    /**
     * Retorna uma visão não-modificável da lista de paqueras.
     *
     * @return lista imutável com os logins das paqueras
     */
    public List<String> getPaqueras() { return Collections.unmodifiableList(this.paqueras); }

    // -------------------------------------------------------------------------
    // Inimigo (US8) — Tell, Don't Ask
    // -------------------------------------------------------------------------

    /**
     * Declara o alvo como inimigo deste usuário, validando as regras de negócio:
     * auto-adição e duplicidade.
     *
     * @param loginAlvo login do usuário a declarar como inimigo
     * @throws AutoInimigoException      se tentar declarar a si mesmo como inimigo
     * @throws UsuarioJaInimigoException se o alvo já estiver na lista de inimigos
     */
    public void tentarAdicionarInimigo(String loginAlvo)
            throws AutoInimigoException, UsuarioJaInimigoException {
        if (this.login.equals(loginAlvo))
            throw new AutoInimigoException();
        if (this.inimigos.contains(loginAlvo))
            throw new UsuarioJaInimigoException();
        this.inimigos.add(loginAlvo);
    }

    /**
     * Verifica se este usuário declarou o login informado como inimigo.
     *
     * @param loginInimigo login do possível inimigo
     * @return {@code true} se for inimigo
     */
    public boolean ehInimigoDe(String loginInimigo) { return this.inimigos.contains(loginInimigo); }

    // -------------------------------------------------------------------------
    // Classe interna: Recado
    // -------------------------------------------------------------------------

    /**
     * Representa um recado armazenado junto com o login de quem o enviou.
     *
     * <p>Guardar o remetente permite a remoção em cascata de recados não
     * lidos quando o remetente encerra sua conta (US9), via
     * {@link Usuario#removerRecadosDe(String)}.</p>
     */
    private static class Recado implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String remetente;
        private final String texto;

        /**
         * Cria um recado com o remetente e o texto informados.
         *
         * @param remetente login de quem enviou o recado
         * @param texto     conteúdo do recado
         */
        Recado(String remetente, String texto) {
            this.remetente = remetente;
            this.texto     = texto;
        }

        /** @return login do remetente */
        String getRemetente() { return remetente; }

        /** @return conteúdo do recado */
        String getTexto()     { return texto; }
    }
}