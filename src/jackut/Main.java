package jackut;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Ponto de entrada da aplicação. Executa todos os testes de aceitação
 * do EasyAccept em cascata via subprocessos independentes.
 *
 * <p>O EasyAccept chama {@code System.exit()} ao finalizar cada script,
 * o que encerraria a JVM principal se os testes fossem chamados diretamente.
 * Para contornar isso, cada script é executado em um subprocesso Java
 * independente via {@link ProcessBuilder}, cuja saída é capturada e
 * exibida no console principal.</p>
 *
 * <p>Os pares {@code _1}/{@code _2} de cada User Story compartilham o
 * arquivo de persistência em disco. O arquivo é apagado antes de cada
 * par para garantir isolamento entre User Stories.</p>
 */
public class Main {

    /** Nome completo (com pacote) da classe Facade, usada como argumento do EasyAccept. */
    private static final String FACADE = "jackut.Facade";

    /** Caminho relativo do arquivo de persistência de usuários. */
    private static final String DADOS = "data" + File.separator + "dados.dat";

    /** Caminho relativo do arquivo de persistência de comunidades. */
    private static final String DADOS_COMUNIDADES = "data" + File.separator + "comunidades.dat";

    /**
     * Scripts de teste agrupados por User Story.
     * Cada par {@code {_1, _2}} representa os testes funcionais e de persistência.
     */
    private static final String[][] TESTES = {
        {"testes/us1_1.txt", "testes/us1_2.txt"},
        {"testes/us2_1.txt", "testes/us2_2.txt"},
        {"testes/us3_1.txt", "testes/us3_2.txt"},
        {"testes/us4_1.txt", "testes/us4_2.txt"},
        {"testes/us5_1.txt", "testes/us5_2.txt"},
        {"testes/us6_1.txt", "testes/us6_2.txt"},
        {"testes/us7_1.txt", "testes/us7_2.txt"},
        {"testes/us8_1.txt", "testes/us8_2.txt"},
        {"testes/us9_1.txt", "testes/us9_2.txt"},
    };

    /**
     * Método principal. Itera sobre os pares de scripts, apagando os arquivos
     * de persistência antes de cada par e executando cada script em subprocesso.
     *
     * @param args argumentos de linha de comando (não utilizados)
     * @throws Exception se ocorrer erro ao criar ou aguardar algum subprocesso
     */
    public static void main(String[] args) throws Exception {
        File raiz = new File(System.getProperty("user.dir"));
        for (String[] par : TESTES) {
            new File(raiz, DADOS).delete();
            new File(raiz, DADOS_COMUNIDADES).delete();
            for (String script : par) {
                rodar(raiz, script);
            }
        }
    }

    /**
     * Executa um único script de teste em um subprocesso Java separado,
     * capturando e imprimindo sua saída no console principal.
     *
     * @param raiz   diretório raiz do projeto, usado como diretório de trabalho do subprocesso
     * @param script caminho relativo ao script de teste ({@code .txt})
     * @throws Exception se ocorrer erro ao iniciar o processo ou ler sua saída
     */
    private static void rodar(File raiz, String script) throws Exception {
        String cp = "bin" + File.pathSeparator
                  + "lib" + File.separator + "easyaccept.jar";

        List<String> cmd = new ArrayList<>();
        cmd.add("java");
        cmd.add("-Dfile.encoding=ISO-8859-1");
        cmd.add("-cp");
        cmd.add(cp);
        cmd.add("easyaccept.EasyAccept");
        cmd.add(FACADE);
        cmd.add(new File(raiz, script).getAbsolutePath());

        Process p = new ProcessBuilder(cmd)
            .directory(raiz)
            .redirectErrorStream(true)
            .start();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                System.out.println(linha);
            }
        }
        p.waitFor();
    }
}
