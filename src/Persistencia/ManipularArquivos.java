package Persistencia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Robson de Jesus
 */
public class ManipularArquivos {
    
    /**
     * Caminho do arquivo aonde são salvos os comandos digitados.
     */
    public final static String CAMINHO_ARQUIVO_COMANDOS = "historico_comandos.txt";
    
    /**
     * Caminho do arquivo auxiliar utilizado na geração do aquivo em pdf.
     */
    public final static String CAMINNHO_ARQUIVO_GRAFO = "grafo.dot";
    
    /**
     * Grava no final do arquivo a String passada nos parametros.
     * 
     * @param arquivo Caminho da arquivo aonde deve ser gravado.
     * @param s String que deve ser gravada.
     * 
     * @return true se a operação ocorrei, false caso contrário.
     */
    public static boolean gravar(String arquivo, String s){
        boolean sucesso = true;
        try {
            
            String aux;            
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(arquivo), true));
            
            bw.write(s);
            bw.newLine();
            
            bw.close();
        } catch (IOException ex) {
            System.out.println("Erro salvar em arquivo\n");
            sucesso = false;
        }
        return sucesso;
    }
    
    /**
     * Limpa arquivo, ou seja, remove tudo o que foi escrito até então.
     * 
     * @param arquivo Caminho do arquivo.
     */
    public static void limparArquivo(String arquivo){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(arquivo), false));
            bw.write("");
            bw.close();
        } catch (IOException ex) {
            System.out.println("Erro ao limpar arquivo\n");
        }
    }
}
