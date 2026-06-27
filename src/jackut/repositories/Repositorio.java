package jackut.repositories;

import jackut.entities.Comunidade;
import jackut.entities.Usuario;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Respons\u00E1vel exclusivamente pela persist\u00EAncia do estado do sistema Jackut.
 *
 * <p>Isola o mecanismo de armazenamento (serializa\u00E7\u00E3o Java em disco) do
 * restante da aplica\u00E7\u00E3o. Se a tecnologia de persist\u00EAncia precisar ser trocada
 * (ex: banco de dados, JSON, XML), apenas esta classe precisa ser alterada,
 * sem impacto no {@link Controlador} nem nas entidades.</p>
 *
 * <p>Os dados s\u00E3o gravados em {@code data/dados.dat}. A pasta {@code data/}
 * \u00E9 criada automaticamente na primeira grava\u00E7\u00E3o.</p>
 */

public class Repositorio {

    /** Caminho da pasta onde o arquivo de dados \u00E9 armazenado. */
    private static final String PASTA   = "data";

    /** Caminho completo do arquivo de dados serializado. */
    private static final String ARQUIVO = PASTA + File.separator + "dados.dat";

    /** Caminho completo do arquivo de comunidades serializado. */
    private static final String ARQUIVO_COMUNIDADES = PASTA + File.separator + "comunidades.dat";

    /**
     * Carrega o mapa de usu\u00E1rios persistido em disco.
     *
     * <p>Se o arquivo n\u00E3o existir (primeira execu\u00E7\u00E3o) ou estiver corrompido,
     * retorna um mapa vazio sem lan\u00E7ar exce\u00E7\u00E3o.</p>
     *
     * @return mapa de usu\u00E1rios indexado por login, ou mapa vazio se n\u00E3o houver dados
     */
    @SuppressWarnings("unchecked")
    public Map<String, Usuario> carregar() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return new LinkedHashMap<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (Map<String, Usuario>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new LinkedHashMap<>();
        }
    }

    /**
     * Serializa e grava o mapa de usu\u00E1rios em disco.
     *
     * <p>Cria a pasta {@code data/} automaticamente caso ainda n\u00E3o exista.</p>
     *
     * @param usuarios mapa de usu\u00E1rios a ser persistido, indexado por login
     * @throws PersistenciaException se ocorrer erro de I/O durante a grava\u00E7\u00E3o
     */
    public void salvar(Map<String, Usuario> usuarios) {
        new File(PASTA).mkdirs();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            out.writeObject(usuarios);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao salvar dados: " + e.getMessage(), e);
        }
    }

    /**
     * Remove do disco os arquivos de dados de usuários e de comunidades.
     *
     * <p>Chamado por {@link Controlador#zerarSistema()} para garantir que
     * uma execução seguinte comece sem estado anterior.</p>
     */
    public void apagar() {
        new File(ARQUIVO).delete();
        new File(ARQUIVO_COMUNIDADES).delete();
    }

    /**
     * Carrega o mapa de comunidades persistido em disco.
     *
     * <p>Se o arquivo não existir ou estiver corrompido, retorna um mapa vazio.</p>
     *
     * @return mapa de comunidades indexado por nome, ou mapa vazio se não houver dados
     */
    @SuppressWarnings("unchecked")
    public Map<String, Comunidade> carregarComunidades() {
        File arquivo = new File(ARQUIVO_COMUNIDADES);
        if (!arquivo.exists()) return new LinkedHashMap<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (Map<String, Comunidade>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new LinkedHashMap<>();
        }
    }

    /**
     * Serializa e grava o mapa de comunidades em disco.
     *
     * @param comunidades mapa de comunidades a ser persistido
     * @throws PersistenciaException se ocorrer erro de I/O durante a gravação
     */
    public void salvarComunidades(Map<String, Comunidade> comunidades) {
        new File(PASTA).mkdirs();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARQUIVO_COMUNIDADES))) {
            out.writeObject(comunidades);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao salvar comunidades: " + e.getMessage(), e);
        }
    }
}
