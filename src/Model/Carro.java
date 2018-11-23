package Model;

/**
 * @author Robson de Jesus
 */
public class Carro {
    
     long chassi;
     String nome;
     String cor;
     double valor;
     //test
     double quantidade;

    public Carro() {
    }

    public Carro(long chassi, String nome, String cor, double valor, double quantidade) {
        this.chassi = chassi;
        this.nome = nome;
        this.cor = cor;
        this.valor= valor;
        //teste
        this.quantidade = quantidade;
    }
    
    public long getChassi() {
        return chassi;
    }

    public void setChassi(long chassi) {
        this.chassi = chassi;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor= valor;
    }

    //teste
    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }
    
    

    @Override
    public String toString() {
        return "Chassi: " + chassi + ", Nome: " + nome + ", cor: " + 
                cor + ", Valor: " + valor + ", Quantidade: "+ quantidade + ".";
    }
    
    public String mostrar(){
        return toString().replaceAll(", ", "\n");
    }
    
}
