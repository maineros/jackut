import entities.Usuario;
import exceptions.JackutException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Repositorio {

    private static final String PASTA       = "data";
    private static final String ARQUIVO     = PASTA + File.separator + "dados.dat";

    @SuppressWarnings("unchecked")
    public Map<String, Usuario> carregar() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return new HashMap<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (Map<String, Usuario>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    public void salvar(Map<String, Usuario> usuarios) {
        new File(PASTA).mkdirs();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            out.writeObject(usuarios);
        } catch (IOException e) {
            throw new JackutException("Erro ao salvar dados: " + e.getMessage());
        }
    }

    public void apagar() {
        new File(ARQUIVO).delete();
    }
}
