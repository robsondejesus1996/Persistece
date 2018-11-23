package Persistencia;

/**
 * Objeto auxiliar que guarda apenas a posição no arquivo, posicao da tupla
 * à esquerda, posição tupla à direita e se 
 * @author Robson de Jesus
 */
public class ItemArvore {
    
   
     long endereco;
    
     long esquerda;
    
     long direita;
    
     boolean visto;

    public ItemArvore() {
    }

    public long getEndereco() {
        return endereco;
    }

    public void setEndereco(long endereco) {
        this.endereco = endereco;
    }

    public long getEsquerda() {
        return esquerda;
    }

    public void setEsquerda(long esquerda) {
        this.esquerda = esquerda;
    }

    public long getDireita() {
        return direita;
    }

    public void setDireita(long direita) {
        this.direita = direita;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }
       
}
