import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Ponto de entrada da aplica\u00E7\u00E3o. Executa todos os testes de aceita\u00E7\u00E3o
 * do EasyAccept em cascata.
 *
 * <p>O EasyAccept chama {@code System.exit()} ao finalizar cada script,
 * o que encerraria a JVM principal se os testes fossem chamados diretamente
 * via {@code EasyAccept.main()}. Para contornar isso, cada script \u00E9
 * executado em um subprocesso Java independente via {@link ProcessBuilder}.
 * A sa\u00EDda de cada subprocesso \u00E9 capturada e exibida no console principal.</p>
 *
 * <p>Os pares {@code _1}/{@code _2} de cada User Story compartilham o arquivo
 * {@code data/dados.dat}: o script {@code _1} termina com {@code encerrarSistema}
 * (que persiste os dados) e o {@code _2} os recarrega na inicializa\u00E7\u00E3o.
 * O arquivo \u00E9 apagado antes de cada par para garantir isolamento entre User Stories.</p>
 */

public class Main {

    /** Nome completo da classe Facade, usada como argumento do EasyAccept. */
    private static final String FACADE = "Facade";

    /** Caminho relativo do arquivo de persist\u00EAncia. */
    private static final String DADOS = "data" + File.separator + "dados.dat";

    /**
     * Scripts de teste agrupados por User Story.
     * Cada par {@code {_1, _2}} representa os testes funcionais e de persist\u00EAncia.
     */
    private static final String[][] TESTES = {
        {"testes/us1_1.txt", "testes/us1_2.txt"},
        {"testes/us2_1.txt", "testes/us2_2.txt"},
        {"testes/us3_1.txt", "testes/us3_2.txt"},
        {"testes/us4_1.txt", "testes/us4_2.txt"},
    };

    /**
     * M\u00E9todo principal. Itera sobre os pares de scripts, apagando o arquivo
     * de persist\u00EAncia antes de cada par e executando cada script em subprocesso.
     *
     * @param args argumentos de linha de comando (n\u00E3o utilizados)
     * @throws Exception se ocorrer erro ao criar ou aguardar algum subprocesso
     */
    public static void main(String[] args) throws Exception {
        File raiz = new File(System.getProperty("user.dir"));

        for (String[] par : TESTES) {
            new File(raiz, DADOS).delete();
            for (String script : par) {
                rodar(raiz, script);
            }
        }
    }

    /**
     * Executa um \u00FAnico script de teste em um subprocesso Java separado,
     * capturando e imprimindo sua sa\u00EDda no console principal.
     *
     * @param raiz   diret\u00F3rio raiz do projeto, usado como diret\u00F3rio de trabalho do subprocesso
     * @param script caminho relativo ao script de teste ({@code .txt})
     * @throws Exception se ocorrer erro ao iniciar o processo ou ler sua sa\u00EDda
     */
    private static void rodar(File raiz, String script) throws Exception {
        String cp = "bin" + File.pathSeparator
                  + "lib" + File.separator + "easyaccept.jar";

        String scriptAbsoluto = new File(raiz, script).getAbsolutePath();

        List<String> cmd = new ArrayList<>();
        cmd.add("java");
        cmd.add("-Dfile.encoding=ISO-8859-1");
        cmd.add("-cp");
        cmd.add(cp);
        cmd.add("easyaccept.EasyAccept");
        cmd.add(FACADE);
        cmd.add(scriptAbsoluto);

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
