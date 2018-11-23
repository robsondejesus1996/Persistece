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
    
    
    public final static String CAMINHO_ARQUIVO_COMANDOS = "historico_comandos.txt";
    
    
    public final static String CAMINNHO_ARQUIVO_GRAFO = "grafo.dot";
    
    
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
