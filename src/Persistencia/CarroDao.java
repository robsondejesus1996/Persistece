package Persistencia;

import Exceptions.IndiceInvalidoException;
import Exceptions.IndiceAtributoInvalido;
import Model.Carro;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Stack;

/**
 * @author Robson de Jesus
 */
public class CarroDao {

    
    private final int TAMANHO_TUPLA = 163;

    
    private final int TAMANHO_NOME = 35;
    
   
    private final int TAMANHO_COR = 30;
    
    
    private final String CAMINHO_ARQUIVO = "usuarios.dat";

    
    private final int POSICAO_CHASSI = 0;
    
    
    private final int POSICAO_NOME = 8;
    
    
    private final int POSICAO_COR = 78;
    
    
    private final int POSICAO_VALOR = 138;
    
   
    private final int POSICAO_STATUS_SALVO = 146;
    
    
    private final int POSICAO_TUPLA_ESQUERDA = 147;
    
   
    private final int POSICAO_TUPLA_DIREITA = 155;

   
    public final int INDICE_CHASSI = 1;
    
    
    public final int INDICE_NOME = 2;
    
    
    public final int INDICE_COR = 3;
    
   
    public final int INDICE_VALOR = 4;
    public final int INDICE_QUANTIDADE = 5;
    
    
    public final int INDICE_STATUS_SALVO = 6;
    
   
    public final int INDICE_TUPLA_ESQUERDA = 7;
    
    
    public final int INDICE_TUPLA_DIREITA = 8;

   
    private final int NUMERO_ATRIBUTOS = 8;

    
    public CarroDao() {
        
        RandomAccessFile arq;
        try {
            arq = new RandomAccessFile(CAMINHO_ARQUIVO, "rw");
            arq.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

    
    public String imprimirArvore() {
        return "Árvore no documento:\n" + percorreArvore(0, 0, false);
    }
    
    
    public void escreverArvore(){
        
        ManipularArquivos.limparArquivo(ManipularArquivos.CAMINNHO_ARQUIVO_GRAFO);
        
        
        ManipularArquivos.gravar(ManipularArquivos.CAMINNHO_ARQUIVO_GRAFO,
                  "digraph g {\n"
                + "label = \"Dados no arquivo\"\n"
                + "node [shape=\"rectangle\"]\n");
        
        
        ManipularArquivos.gravar(ManipularArquivos.CAMINNHO_ARQUIVO_GRAFO,
                percorreArvore(0, 0, true) + "}");
        
        try {
            
            Runtime.getRuntime().exec("dot grafo.dot -T pdf -o grafo.pdf").waitFor();

            
            Runtime.getRuntime().exec("evince grafo.pdf");
        } catch (IOException | InterruptedException  ex) {
            ex.printStackTrace();
        }
        
    }

    private String percorreArvore(long posicao, int nivel, boolean paraGrafo) {
        String s = "";
        
       
        if (tamanhoArquivo() == 0) {
            s += "vazio";
        } else if (posicao != -1) {
            
            if(paraGrafo){
                
                if(tamanhoArquivo() == TAMANHO_TUPLA*1){
                    s += "\"" + lerRegistro(posicao).mostrar() + "\"";
                }
                
                Carro u = lerRegistro(posicao);
                
                
                long l = (long) lerAtributo(posicao, INDICE_TUPLA_ESQUERDA);
                
                
                if( l != -1){
                    s += "\"" + u.mostrar() + "\"";
                    s += " -> ";
                    s += "\"" + lerRegistro(l).mostrar() + "\"";
                    s += " [label=\"ESQ:" + l + "\"]\n";
                }
                
                
                l = (long) lerAtributo(posicao, INDICE_TUPLA_DIREITA);
                
                
                if(l != -1){
                    s += "\"" + u.mostrar() + "\"";
                    s += " -> ";
                    s += "\"" + lerRegistro(l).mostrar() + "\"";
                    s += " [label=\"DIR:" + l + "\"]\n";
                }
            }else{
               
                for (int i = 0; i < nivel; i++) {
                    s += "|  ";
                }
                nivel++;
                
                s += lerRegistroCompleto(posicao);
                s += "\n";
            }
            
            
            s += percorreArvore((long) lerAtributo(posicao, INDICE_TUPLA_DIREITA), nivel, paraGrafo);
            
            s += percorreArvore((long) lerAtributo(posicao, INDICE_TUPLA_ESQUERDA), nivel, paraGrafo);
        } else {
            
            if( !paraGrafo ){
                for (int i = 0; i < nivel; i++) {
                    s += "|  ";
                }
                nivel++;
                s += "vazio\n";
            }
        }
        
        return s;
    }

    
    public boolean inserir(Carro u) {
        long posicaoNovoRegistro;
        boolean salvoComSucesso = true;
        

        
        posicaoNovoRegistro = tamanhoArquivo();

        if (posicaoNovoRegistro == 0) {
           
            escreverRegistro(u, posicaoNovoRegistro);
        } else {
            
            long posicaoAtual = 0;
           
            long posicaoAnterior = 0;
            
            long codigoTupla;
            
            boolean tuplaEsquerda;

            while (true) {
               
                codigoTupla = (long) lerAtributo(posicaoAtual, INDICE_CHASSI);

                
                if ( u.getChassi() == codigoTupla ) {
                    
                    if ( (boolean) lerAtributo(posicaoAtual, INDICE_STATUS_SALVO) ) {
                        
                        salvoComSucesso = false;
                    } else {
                        
                        escreverAtributo(posicaoAtual, INDICE_NOME, u.getNome());
                        escreverAtributo(posicaoAtual, INDICE_COR, u.getCor());
                        escreverAtributo(posicaoAtual, INDICE_VALOR, u.getValor());
                        escreverAtributo(posicaoAtual, INDICE_QUANTIDADE, u.getQuantidade());
                        escreverAtributo(posicaoAtual, INDICE_STATUS_SALVO, true);
                    }
                    
                    break;
                } else if (u.getChassi() < codigoTupla) {
                    
                    posicaoAtual = (long) lerAtributo(posicaoAnterior, INDICE_TUPLA_ESQUERDA);
                    tuplaEsquerda = true;
                } else {
                    
                    posicaoAtual = (long) lerAtributo(posicaoAnterior, INDICE_TUPLA_DIREITA);
                    tuplaEsquerda = false;
                }

                if (posicaoAtual == -1 && tuplaEsquerda) {
                    
                    escreverRegistro(u, posicaoNovoRegistro);
                    escreverAtributo(posicaoAnterior, INDICE_TUPLA_ESQUERDA, posicaoNovoRegistro);
                    break;
                } else if (posicaoAtual == -1 && !tuplaEsquerda) {
                    
                    escreverRegistro(u, posicaoNovoRegistro);
                    escreverAtributo(posicaoAnterior, INDICE_TUPLA_DIREITA, posicaoNovoRegistro);
                    break;
                }

                posicaoAnterior = posicaoAtual;
            }

        }
        return salvoComSucesso;
    }

    
    public Carro ler(long codigoUsuario) {
        Carro usuarioLido = null;
        
        
        long posicaoBusca = pesquisar(codigoUsuario);

        
        if (posicaoBusca != -1) {
            usuarioLido = lerRegistro(posicaoBusca);
        }

        return usuarioLido;
    }

    
    public boolean excluir(long codigoUsuario) {
        boolean excluido = false;

        
        long posicaoBusca = pesquisar(codigoUsuario);

        
        if (posicaoBusca != -1) {
            
            if( (boolean) lerAtributo(posicaoBusca, INDICE_STATUS_SALVO) ){
                escreverAtributo(posicaoBusca, INDICE_STATUS_SALVO, false);
                excluido = true;
            }
        }

        return excluido;
    }

    
    public boolean alterar(Carro u) {
        boolean alterado = false;

        
        long posicaoBusca = pesquisar(u.getChassi());

        
        if ( posicaoBusca != -1 ) {
           
            if ( (boolean) lerAtributo(posicaoBusca, INDICE_STATUS_SALVO) ) {
                escreverAtributo(posicaoBusca, INDICE_NOME, u.getNome());
                escreverAtributo(posicaoBusca, INDICE_COR, u.getCor());
                escreverAtributo(posicaoBusca, INDICE_VALOR, u.getValor());
                alterado = true;
            }
        }

        return alterado;
    }
    
    
    public boolean alterar(Carro u, int[] indicesAtributos) {
        boolean alterado = false;

        
        long posicaoBusca = pesquisar(u.getChassi());

        
        if ( posicaoBusca != -1 ) {
          
            if ( (boolean) lerAtributo(posicaoBusca, INDICE_STATUS_SALVO) ) {
               
                for(int i: indicesAtributos){
                    switch(i){
                        case INDICE_NOME:
                            escreverAtributo(posicaoBusca, INDICE_NOME, u.getNome());
                            break;
                        case INDICE_COR:
                            escreverAtributo(posicaoBusca, INDICE_COR, u.getCor());
                            break;
                        case INDICE_VALOR:
                            escreverAtributo(posicaoBusca, INDICE_VALOR, u.getValor());
                            break;
                    }
                }
                alterado = true;
            }
        }

        return alterado;
    }

  
    private long pesquisar(long codigoUsuario) {
      
        if (tamanhoArquivo() == 0) {
            return -1;
        }
        
       
        long posicao = -1;
       
        long posicaoAtual = 0;
       
        long codigoTupla;

        
        while ( posicaoAtual != -1 ) {

           
            codigoTupla = (long) lerAtributo(posicaoAtual, INDICE_CHASSI);

            
            if (codigoTupla == codigoUsuario) {
                posicao = posicaoAtual;
               
                break;
            } else if (codigoUsuario < codigoTupla) {
               
                posicaoAtual = (long) lerAtributo(posicaoAtual, INDICE_TUPLA_ESQUERDA);
            } else {
               
                posicaoAtual = (long) lerAtributo(posicaoAtual, INDICE_TUPLA_DIREITA);
            }
        }

        return posicao;
    }

   
    public ArrayList<Carro> pesquisarPorAtributos(Object atributoUsuario, int indiceAtributo) {
       
        if (tamanhoArquivo() == 0) {
            return new ArrayList<>();
        }

        
        ArrayList<Carro> usuarios = new ArrayList<>();
      
        Stack<ItemArvore> pilha = new Stack();
        long aux;
      
        ItemArvore a;

        a = new ItemArvore();
        a.setEndereco(0);
        a.setEsquerda((long) lerAtributo(0, INDICE_TUPLA_DIREITA));
        a.setDireita((long) lerAtributo(0, INDICE_TUPLA_ESQUERDA));
        a.setVisto(false);
        
       
        pilha.push( a );
        
        
        while (!pilha.isEmpty()) {
            
           
            if( !pilha.peek().isVisto() &&
                    (boolean) lerAtributo(pilha.peek().getEndereco(), INDICE_STATUS_SALVO) ){
                
                
                switch(indiceAtributo){                        
                    case INDICE_NOME:
                       
                        if(lerAtributo(pilha.peek().getEndereco(), INDICE_NOME).equals( atributoUsuario) ){
                            usuarios.add( lerRegistro( pilha.peek().getEndereco() ) );
                        }
                        break;
                    case INDICE_COR:
                       
                        if(lerAtributo(pilha.peek().getEndereco(), INDICE_COR)
                                .equals( atributoUsuario)){
                            
                            usuarios.add( lerRegistro( pilha.peek().getEndereco() ) );
                        }
                        break;
                    case INDICE_VALOR:
                      
                        if( (double) lerAtributo(pilha.peek().getEndereco(), INDICE_VALOR)
                                == (double) atributoUsuario ){
                            usuarios.add( lerRegistro( pilha.peek().getEndereco() ) );
                        }
                        break;
                }
                
            }
            
           
            pilha.peek().setVisto(true);
            
          
            if(pilha.peek().getEsquerda() != -1){
             
                aux = pilha.peek().getEsquerda();
                
                
                a = new ItemArvore();
                a.setEndereco(aux);
                a.setEsquerda((long) lerAtributo(aux, INDICE_TUPLA_DIREITA));
                a.setDireita((long) lerAtributo(aux, INDICE_TUPLA_ESQUERDA));
                a.setVisto(false);
                
              
                pilha.peek().setEsquerda(-1);
                
               
                pilha.push( a );
                
            }else if(pilha.peek().getDireita() != -1){
                
                
               
                aux = pilha.peek().getDireita();
                
               
                a = new ItemArvore();
                a.setEndereco(pilha.peek().getDireita());
                a.setEsquerda((long) lerAtributo(aux, INDICE_TUPLA_DIREITA));
                a.setDireita((long) lerAtributo(aux, INDICE_TUPLA_ESQUERDA));
                a.setVisto(false);
                
              
                pilha.peek().setDireita(-1);
                
               
                pilha.push( a );
                
            }else{
              
                pilha.pop();
            }
            
        }

        return usuarios;
    }

    
    public boolean limparArquivo() {
        RandomAccessFile arq;
        try {
            arq = new RandomAccessFile(CAMINHO_ARQUIVO, "rw");
            arq.setLength(0);
            arq.close();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Arquivo não encontrado !!");
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao operar arquivo");
        }
        return true;
    }

   
    private long tamanhoArquivo() {
        RandomAccessFile arq;
        long tamanho = -1;
        try {
            arq = new RandomAccessFile(CAMINHO_ARQUIVO, "r");
            tamanho = arq.length();
            arq.close();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Arquivo não encontrado !!");
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao operar arquivo");
        }
        return tamanho;
    }

 
    private boolean escreverRegistro(Carro u, long posição) {
      
        if (posição % TAMANHO_TUPLA != 0) {
            throw new IndiceInvalidoException();
        }
        RandomAccessFile arq;
        try {
            arq = new RandomAccessFile(CAMINHO_ARQUIVO, "rw");
           
            arq.seek(posição);
            
            arq.writeLong(u.getChassi());
           
            arq.writeChars(String.format("%1$" + TAMANHO_NOME + "s", u.getNome()));
            
            arq.writeChars(String.format("%1$" + TAMANHO_COR + "s", u.getCor()));
           
            arq.writeDouble(u.getValor());
           
            arq.writeBoolean(true);
            
            arq.writeLong(-1);
           
            arq.writeLong(-1);

            arq.close();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Arquivo não encontrado !!");
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao operar arquivo");
        }
        return true;
    }

    private String lerRegistroCompleto(long posicao) {
        /**
         * Verifica se a posição é um múltiplo do tamanho da tupla para evitar
         * ler de forma errada dados no arquivo.
         */
        if (posicao % TAMANHO_TUPLA != 0) {
            throw new IndiceInvalidoException();
        }
        String s = "", aux;
        RandomAccessFile arq;

        try {
            arq = new RandomAccessFile(CAMINHO_ARQUIVO, "r");
            
            
            arq.seek(posicao);

          
            s += arq.readLong() + ", ";

            
            aux  = "";
            for (int i = 0; i < TAMANHO_NOME; i++) {
                aux += arq.readChar();
            }
            aux = aux.trim();
            s += aux + ", ";

            aux = "";
           
            for (int i = 0; i < TAMANHO_COR; i++) {
                aux += arq.readChar();
            }
            aux = aux.trim();
            s += aux + ", ";

            
            s += arq.readDouble() + ", ";

           
            s += arq.readBoolean() + ", ";

            
            s += arq.readLong() + ", ";

            
            s += arq.readLong() + ", ";

            arq.close();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Arquivo não encontrado !!");
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao operar arquivo");
        }

        return s;
    }

   
    private Carro lerRegistro(long posicao) {
       
        if (posicao % TAMANHO_TUPLA != 0) {
            throw new IndiceInvalidoException();
        }
        Carro u = new Carro();
        String aux;
        RandomAccessFile arq;
        try {
            arq = new RandomAccessFile(CAMINHO_ARQUIVO, "r");
            
            
            arq.seek(posicao);

           
            u.setChassi(arq.readLong());

             
            aux = "";
            for (int i = 0; i < TAMANHO_NOME; i++) {
                aux += arq.readChar();
            }
            aux = aux.trim();
           
            u.setNome(aux);

           
            aux = "";
            for (int i = 0; i < TAMANHO_COR; i++) {
                aux += arq.readChar();
            }
            aux = aux.trim();
            
            u.setCor(aux);

            
            u.setValor(arq.readDouble());

            arq.close();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Arquivo não encontrado !!");
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao operar arquivo");
        }
        return u;
    }

   
    private boolean escreverAtributo(long posicaoTupla, int indiceAtributo, Object dado) {
        
        if (posicaoTupla % TAMANHO_TUPLA != 0) {
            throw new IndiceInvalidoException();
        }
       
        if (indiceAtributo < 0 || indiceAtributo > NUMERO_ATRIBUTOS) {
            throw new IndiceAtributoInvalido();
        }
        RandomAccessFile arq;
        try {
            arq = new RandomAccessFile(CAMINHO_ARQUIVO, "rw");

            
            switch (indiceAtributo) {
                
                case INDICE_CHASSI:
                    
                    arq.seek(posicaoTupla + POSICAO_CHASSI);
                    arq.writeLong((long) dado);
                    break;

                case INDICE_NOME:
                   
                    arq.seek(posicaoTupla + POSICAO_NOME);
                    String nome = (String) dado;
                    nome = String.format("%1$" + TAMANHO_NOME + "s", nome);
                    arq.writeChars(nome);
                    break;

                case INDICE_COR:
                    
                    arq.seek(posicaoTupla + POSICAO_COR);
                    String cor = (String) dado;
                    cor = String.format("%1$" + TAMANHO_COR + "s", cor);
                    arq.writeChars(cor);
                    break;

                case INDICE_VALOR:
                  
                    arq.seek(posicaoTupla + POSICAO_VALOR);
                    arq.writeDouble((double) dado);
                    break;

                case INDICE_STATUS_SALVO:
                   
                    arq.seek(posicaoTupla + POSICAO_STATUS_SALVO);
                    arq.writeBoolean((boolean) dado);
                    break;

                case INDICE_TUPLA_ESQUERDA:
                   
                    arq.seek(posicaoTupla + POSICAO_TUPLA_ESQUERDA);
                    arq.writeLong((long) dado);
                    break;

                case INDICE_TUPLA_DIREITA:
                   
                    arq.seek(posicaoTupla + POSICAO_TUPLA_DIREITA);
                    arq.writeLong((long) dado);
                    break;
                default:
                    break;
            }

            arq.close();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Arquivo não encontrado !!");
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao operar arquivo");
        }
        return true;
    }

   
    private Object lerAtributo(long posicaoTupla, int indiceAtributo) {
       
        if (posicaoTupla % TAMANHO_TUPLA != 0) {
            throw new IndiceInvalidoException();
        }
        
        if (indiceAtributo < 1 || indiceAtributo > NUMERO_ATRIBUTOS) {
            throw new IndiceAtributoInvalido();
        }

        Object objetoLido = null;
        RandomAccessFile arq;
        try {
            arq = new RandomAccessFile(CAMINHO_ARQUIVO, "r");

           
            switch (indiceAtributo) {
                case INDICE_CHASSI:
                   
                    arq.seek(posicaoTupla + POSICAO_CHASSI);
                    objetoLido = arq.readLong();
                    break;

                case INDICE_NOME:
                    
                    arq.seek(posicaoTupla + POSICAO_NOME);
                    String nome = "";
                    for (int i = 0; i < TAMANHO_NOME; i++) {
                        nome += arq.readChar();
                    }
                    objetoLido = nome.trim();
                    break;

                case INDICE_COR:
                    
                    arq.seek(posicaoTupla + POSICAO_COR);
                    String qualificacao = "";
                    for (int i = 0; i < TAMANHO_COR; i++) {
                        qualificacao += arq.readChar();
                    }
                    objetoLido = qualificacao.trim();
                    break;

                case INDICE_VALOR:
                   
                    arq.seek(posicaoTupla + POSICAO_VALOR);
                    objetoLido = arq.readDouble();
                    break;

                case INDICE_STATUS_SALVO:
                   
                    arq.seek(posicaoTupla + POSICAO_STATUS_SALVO);
                    objetoLido = arq.readBoolean();
                    break;

                case INDICE_TUPLA_ESQUERDA:
                    
                    arq.seek(posicaoTupla + POSICAO_TUPLA_ESQUERDA);
                    objetoLido = arq.readLong();
                    break;

                case INDICE_TUPLA_DIREITA:
                   
                    arq.seek(posicaoTupla + POSICAO_TUPLA_DIREITA);
                    objetoLido = arq.readLong();
                    break;
                default:
                    break;
            }

            arq.close();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Arquivo não encontrado !!");
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao operar arquivo");
        }
        return objetoLido;
    }

}
