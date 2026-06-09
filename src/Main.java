import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String FACADE = "Facade";
    private static final String DADOS  = "data" + File.separator + "dados.dat";

    private static final String[][] TESTES = {
        {"testes/us1_1.txt", "testes/us1_2.txt"},
        {"testes/us2_1.txt", "testes/us2_2.txt"},
        {"testes/us3_1.txt", "testes/us3_2.txt"},
        {"testes/us4_1.txt", "testes/us4_2.txt"},
    };

    public static void main(String[] args) throws Exception {
        // Diretorio raiz do projeto = onde este processo foi iniciado
        File raiz = new File(System.getProperty("user.dir"));

        for (String[] par : TESTES) {
            new File(raiz, DADOS).delete();
            for (String script : par) {
                rodar(raiz, script);
            }
        }
    }

    private static void rodar(File raiz, String script) throws Exception {
        String cp = "bin" + File.pathSeparator
                  + "lib" + File.separator + "easyaccept.jar";

        // Caminho absoluto para o script de teste
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
            .directory(raiz)           // subprocesso roda na raiz do projeto
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
