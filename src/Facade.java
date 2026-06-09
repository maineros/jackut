public class Facade {

    private final Controlador controlador;

    public Facade() {
        this.controlador = new Controlador();
    }

    // user story 1
    public void zerarSistema()                                              { controlador.zerarSistema(); }
    public void encerrarSistema()                                           { controlador.encerrarSistema(); }
    public void criarUsuario(String login, String senha, String nome)       { controlador.criarUsuario(login, senha, nome); }
    public String abrirSessao(String login, String senha)                   { return controlador.abrirSessao(login, senha); }

    // user story 2
    public String getAtributoUsuario(String login, String atributo)         { return controlador.getAtributoUsuario(login, atributo); }
    public void editarPerfil(String id, String atributo, String valor)      { controlador.editarPerfil(id, atributo, valor); }

    // user story 3
    public void adicionarAmigo(String id, String amigo)                     { controlador.adicionarAmigo(id, amigo); }
    public boolean ehAmigo(String login, String amigo)                      { return controlador.ehAmigo(login, amigo); }
    public String getAmigos(String login)                                   { return controlador.getAmigos(login); }

    // user story 4
    public void enviarRecado(String id, String destinatario, String recado) { controlador.enviarRecado(id, destinatario, recado); }
    public String lerRecado(String id)                                      { return controlador.lerRecado(id); }
}
