package Persistencia;

/**
 * Objeto auxiliar que guarda apenas a posição no arquivo, posicao da tupla
 * à esquerda, posição tupla à direita e se 
 * @author Robson de Jesus
 */
public class ItemArvore {
    
    /**
     * Endereço da tupla.
     */
     long endereco;
    /**
     * Endereço da tupla à esquerda.
     */
     long esquerda;
    /**
     * Endereço da tupla à direita.
     */
     long direita;
    /**
     * Se a tupla já foi visto.
     */
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
