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

    /**
     * Contantes que especifica o tamanho da tupla em bytes.
     */
    private final int TAMANHO_TUPLA = 163;

    /**
     * Tamanho máximo da string do nome em número de letras.
     */
    private final int TAMANHO_NOME = 35;
    
    /**
     * Tamanho máximo da String qualificação em número de letras.
     */
    private final int TAMANHO_COR = 30;
    
    /**
     * Endereço do arquivo aonde serão salvos as tuplas.
     */
    private final String CAMINHO_ARQUIVO = "usuarios.dat";

    /**
     * Número de bytes a partir do inicio da tupla 
     * aonde começa o código na tupla.
     */
    private final int POSICAO_CHASSI = 0;
    
    /**
     * Número de bytes a partir do inicio da 
     * tupla aonde começa o nome na tupla.
     */
    private final int POSICAO_NOME = 8;
    
    /**
     * Número de bytes a partir do inicio da
     * tupla aonde começa a qualificação na tupla.
     */
    private final int POSICAO_COR = 78;
    
    /**
     * Número de bytes a partir do inicio da
     * tupla aonde começa o salário na tupla.
     */
    private final int POSICAO_VALOR = 138;
    
    /**
     * Número de bytes a partir do inicio da
     * tupla aonde começa a status de salvamento
     * na tupla.
     */
    private final int POSICAO_STATUS_SALVO = 146;
    
    /**
     * Número de bytes a partir do inicio da
     * tupla aonde começa o ponteiro para a esquerda
     * na tupla.
     */
    private final int POSICAO_TUPLA_ESQUERDA = 147;
    
    /**
     * Número de bytes a partir do inicio da
     * tupla aonde começa o ponteiro para a esquerda
     * na tupla.
     */
    private final int POSICAO_TUPLA_DIREITA = 155;

    /**
     * Indica a ordem de salvamento dos atributos na tupla,
     * no caso, o código é o primeiro atributo;
     */
    public final int INDICE_CHASSI = 1;
    
    /**
     * Indica a ordem de salvamento dos atributos na tupla,
     * no caso, o nome é o segundo atributo;
     */
    public final int INDICE_NOME = 2;
    
    /**
     * Indica a ordem de salvamento dos atributos na tupla,
     * no caso, a qualificação é o terceiro atributo;
     */
    public final int INDICE_COR = 3;
    
    /**
     * Indica a ordem de salvamento dos atributos na tupla,
     * no caso, o salário é o quarto atributo;
     */
    public final int INDICE_VALOR = 4;
    public final int INDICE_QUANTIDADE = 5;
    
    /**
     * Indica a ordem de salvamento dos atributos na tupla,
     * no caso, o status de salvamento é o quinto atributo;
     */
    public final int INDICE_STATUS_SALVO = 6;
    
    /**
     * Indica a ordem de salvamento dos atributos na tupla,
     * no caso, o ponteiro para a tupla à esquerda
     * é o sexto atributo;
     */
    public final int INDICE_TUPLA_ESQUERDA = 7;
    
    /**
     * Indica a ordem de salvamento dos atributos na tupla,
     * no caso, o ponteiro para a tupla à direita
     * é o sétimo atributo;
     */
    public final int INDICE_TUPLA_DIREITA = 8;

    /**
     * Número de atributos que a tupla possui.
     */
    private final int NUMERO_ATRIBUTOS = 8;

    /**
     * Construtor da classe.
     */
    public CarroDao() {
        /**
         * As linhas abaixo garante que ao executar o programa, seja criado
         * o arquivo aonde vai ser salvo as tuplas, caso não exista.
         */
        RandomAccessFile arq;
        try {
            arq = new RandomAccessFile(CAMINHO_ARQUIVO, "rw");
            arq.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

    /**
     * Imprime a árvore binária no arquivo.
     * 
     * @return String com as tuplas da árvora editada.
     */
    public String imprimirArvore() {
        return "Árvore no documento:\n" + percorreArvore(0, 0, false);
    }
    
    /**
     * Escreve e gera árvore binária em pdf.
     */
    public void escreverArvore(){
        /**
         * Limpa arquivo auxiliar na geração do pdf com a árvore.
         */
        ManipularArquivos.limparArquivo(ManipularArquivos.CAMINNHO_ARQUIVO_GRAFO);
        
        /**
         * Escreve as primeiras informações no arquivo auxiliar.
         */
        ManipularArquivos.gravar(ManipularArquivos.CAMINNHO_ARQUIVO_GRAFO,
                  "digraph g {\n"
                + "label = \"Dados no arquivo\"\n"
                + "node [shape=\"rectangle\"]\n");
        
        /**
         * Percorre a árvore e escreve as informações 
         * já editadas no arquivo auxiliar.
         */
        ManipularArquivos.gravar(ManipularArquivos.CAMINNHO_ARQUIVO_GRAFO,
                percorreArvore(0, 0, true) + "}");
        
        try {
            /**
             * Executa comando que compila o pdf com a árvore binária.
             */
            Runtime.getRuntime().exec("dot grafo.dot -T pdf -o grafo.pdf").waitFor();

            /**
             * Executo comando que abre o pdf.
             */
            Runtime.getRuntime().exec("evince grafo.pdf");
        } catch (IOException | InterruptedException  ex) {
            ex.printStackTrace();
        }
        
    }

    /**
     * Percorre toda a árvore criando uma string com as informações das tuplas
     * na árvore. Esse algortimo é recursivo.
     * 
     * @param posicao Posição de aonde deve começar o percorrimento da árvore.
     * @param nivel Indica o nível da arvore. serve apenas para 
     * edição da string da saída.
     * @param paraGrafo se for true, cria string para criação do pdf com árvore, 
     * caso false, cria string para impressão em console.
     * 
     * @return String com as informações das tuplas na árvore.
     */
    private String percorreArvore(long posicao, int nivel, boolean paraGrafo) {
        String s = "";
        
        /**
         * Se o arquivo estiver vazio, apenas retorna a mensagem.
         */
        if (tamanhoArquivo() == 0) {
            s += "vazio";
        } else if (posicao != -1) {
            /**
             * Para garantir que ao ler a posição seja válida e também 
             * critério de parada da recursividade.
             */
            
            /**
             * Se for para criar o grafo edita de uma forma.
             */
            if(paraGrafo){
                /**
                 * Para o caso aonde tem apenas uma tupla no arquivo.
                 */
                if(tamanhoArquivo() == TAMANHO_TUPLA*1){
                    s += "\"" + lerRegistro(posicao).mostrar() + "\"";
                }
                /**
                 * Le o usuário na posição atual.
                 */
                Carro u = lerRegistro(posicao);
                
                /**
                 * Le a posição da tupla à esquerda.
                 */
                long l = (long) lerAtributo(posicao, INDICE_TUPLA_ESQUERDA);
                
                /**
                 * Se a posição da tupla à esquerda não for nulo, edita 
                 * para escrita em arquivo e posterior geração em pdf.
                 */
                if( l != -1){
                    s += "\"" + u.mostrar() + "\"";
                    s += " -> ";
                    s += "\"" + lerRegistro(l).mostrar() + "\"";
                    s += " [label=\"ESQ:" + l + "\"]\n";
                }
                
                /**
                 * Le a posição da tupla à direita.
                 */
                l = (long) lerAtributo(posicao, INDICE_TUPLA_DIREITA);
                
                /**
                 * Se a posição da tupla à direita não for nulo, edita 
                 * para escrita em arquivo e posterior geração em pdf.
                 */
                if(l != -1){
                    s += "\"" + u.mostrar() + "\"";
                    s += " -> ";
                    s += "\"" + lerRegistro(l).mostrar() + "\"";
                    s += " [label=\"DIR:" + l + "\"]\n";
                }
            }else{
                /**
                 * No caso de criação para impressão em console.
                 */
                
                /**
                 * Edita o palpi.
                 */
                for (int i = 0; i < nivel; i++) {
                    s += "|  ";
                }
                nivel++;
                
                s += lerRegistroCompleto(posicao);
                s += "\n";
            }
            
            /**
             * Entra na recusividade na tupla à esquerda.
             */
            s += percorreArvore((long) lerAtributo(posicao, INDICE_TUPLA_DIREITA), nivel, paraGrafo);
            /**
             * Entra na recusividade na tupla à direita.
             */
            s += percorreArvore((long) lerAtributo(posicao, INDICE_TUPLA_ESQUERDA), nivel, paraGrafo);
        } else {
            /**
             * Para o caso da posição for igual a -1 e não for para a geraçao
             * do grafo, edita para mostrar quais ramos da árvore estão vazios.
             */
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

    /**
     * Insere usuário no arquivo de persistencia seguindo 
     * a organização de árvore binária, se o usuário possuir 
     * um código que já existe no arquivo, não será salvo.
     * 
     * @param u Usuário que deve ter seus atributos salvos.
     * 
     * @return true se o usuário foi salvo, false caso contrário
     */
    public boolean inserir(Carro u) {
        long posicaoNovoRegistro;
        boolean salvoComSucesso = true;
        

        //Le quantos bytes possui o arquivo
        posicaoNovoRegistro = tamanhoArquivo();

        if (posicaoNovoRegistro == 0) {
            //Se estiver vazio apenas salva a nova tupla.
            escreverRegistro(u, posicaoNovoRegistro);
        } else {
            //posicao da tupla operada atualmente.
            long posicaoAtual = 0;
            //posicao da tupla pai da tupla operada atualmente.
            long posicaoAnterior = 0;
            //codigo da tupla que esta sendo operada atualmente
            long codigoTupla;
            /**
             * Variável para indicar se é uma tupla a esquerda ou a direita da
             * tupla pai.
             */
            boolean tuplaEsquerda;

            while (true) {
                //le o código da tupla atual
                codigoTupla = (long) lerAtributo(posicaoAtual, INDICE_CHASSI);

                /**
                 * Se o código do usuário para ser inserido for igual ao 
                 * código da tupla operada atualmente,
                 * Aqui vale um observação importante, ao excluir um registro,
                 * ele não é apagado, apenas o atributo status de salvamento é
                 * alterado para false, isso ocorre para não desconfigurar 
                 * a árvore, ou seja, fisicamente a tupla continua escrita,
                 * apenas fica possível a sobreescrita sobre esse registro.
                 */
                if ( u.getChassi() == codigoTupla ) {
                    
                    if ( (boolean) lerAtributo(posicaoAtual, INDICE_STATUS_SALVO) ) {
                        /**
                         * Se o status de salvamento estiver true, não possível
                         * ser feita a inserção.
                         */
                        salvoComSucesso = false;
                    } else {
                        /**
                         * Com o status de salvamento em false é possível 
                         * a sobreescrita da tupla, então são escritos os 
                         * atributos, Os ponteiros não são sobreescritos pois já 
                         * apontam para as tuplas corretas.
                         */
                        escreverAtributo(posicaoAtual, INDICE_NOME, u.getNome());
                        escreverAtributo(posicaoAtual, INDICE_COR, u.getCor());
                        escreverAtributo(posicaoAtual, INDICE_VALOR, u.getValor());
                        escreverAtributo(posicaoAtual, INDICE_QUANTIDADE, u.getQuantidade());
                        escreverAtributo(posicaoAtual, INDICE_STATUS_SALVO, true);
                    }
                    /**
                     * Como já achou o código e não existe dois iguais
                     * podemos parar de percorrer o arquivo.
                     */
                    break;
                } else if (u.getChassi() < codigoTupla) {
                    /**
                     * Se o código for menor, le se existe uma tupla à esquerda
                     * e salva como sendo a tupla atual.
                     */
                    posicaoAtual = (long) lerAtributo(posicaoAnterior, INDICE_TUPLA_ESQUERDA);
                    tuplaEsquerda = true;
                } else {
                    /**
                     * Se o código for maior, le se existe uma tupla à direita
                     * e salva como sendo a tupla atual.
                     */
                    posicaoAtual = (long) lerAtributo(posicaoAnterior, INDICE_TUPLA_DIREITA);
                    tuplaEsquerda = false;
                }

                if (posicaoAtual == -1 && tuplaEsquerda) {
                    /**
                     * Se a posição atual for inválida e tiver vindo
                     * de uma tentativa de leitura da tupla à esquerda,
                     * escreve o novo registro no final do arquivo e atualiza
                     * o ponteiro para a esquerda da tupla pai.
                     */
                    escreverRegistro(u, posicaoNovoRegistro);
                    escreverAtributo(posicaoAnterior, INDICE_TUPLA_ESQUERDA, posicaoNovoRegistro);
                    break;
                } else if (posicaoAtual == -1 && !tuplaEsquerda) {
                    /**
                     * Se a posição atual for inválida e tiver vindo
                     * de uma tentativa de leitura da tupla à direita,
                     * escreve o novo registro no final do arquivo e atualiza
                     * o ponteiro para a direita da tupla pai.
                     */
                    escreverRegistro(u, posicaoNovoRegistro);
                    escreverAtributo(posicaoAnterior, INDICE_TUPLA_DIREITA, posicaoNovoRegistro);
                    break;
                }

                posicaoAnterior = posicaoAtual;
            }

        }
        return salvoComSucesso;
    }

    /**
     * Pesquisa e se achar, retorna o usuário com o codigo especificado.
     * 
     * @param codigoUsuario Codigo de usuário que deve ser pesquisado.
     * 
     * @return Um Usuario se encontrar e null se não encontrar.
     */
    public Carro ler(long codigoUsuario) {
        Carro usuarioLido = null;
        
        /**
         * Chama uma outro para fazer a pesquisa pelo código.
         */
        long posicaoBusca = pesquisar(codigoUsuario);

        /**
         * Se for diferente de -1, ou seja, encontrou, 
         * le o usuario na posição e salva na variável.
         */
        if (posicaoBusca != -1) {
            usuarioLido = lerRegistro(posicaoBusca);
        }

        return usuarioLido;
    }

    /**
     * Pesquisa e se encontrar, exclui usuario pelo código.
     * 
     * @param codigoUsuario Codigo de usuário que deve ser excluido.
     * 
     * @return true se foi excluido e false caso contrário.
     */
    public boolean excluir(long codigoUsuario) {
        boolean excluido = false;

        /**
         * Chama método externo para a pesquisa.
         */
        long posicaoBusca = pesquisar(codigoUsuario);

        /**
         * Se encontrou, então o valor da posição é diferente de -1.
         */
        if (posicaoBusca != -1) {
            /**
             * Uma tupla só esta salva lógicamente se o seu 
             * status de salvamento for true, Caso contrário, false,
             * Assim para excluir uma tupla lógicamente basta alterar o 
             * atributo status de salvamento para false.
             */
            if( (boolean) lerAtributo(posicaoBusca, INDICE_STATUS_SALVO) ){
                escreverAtributo(posicaoBusca, INDICE_STATUS_SALVO, false);
                excluido = true;
            }
        }

        return excluido;
    }

    /**
     * Pesquisa pelo código e se encontrar, altera do usuário, 
     * todos os seus atributos.
     * 
     * @param u Usuário que deverá ter todos os seus atributos alterados.
     * 
     * @return True se a operação ocorreu e false senão ocoreu.
     */
    public boolean alterar(Carro u) {
        boolean alterado = false;

        /**
         * Chama método externo para a pesquisa do usuário.
         */
        long posicaoBusca = pesquisar(u.getChassi());

        /**
         * Se for diferente de -1 é por que encontrou.
         */
        if ( posicaoBusca != -1 ) {
            /**
            * Uma tupla só esta salva lógicamente se o seu 
            * status de salvamento for true, Caso contrário, false,
            * Assim para alterar uma tupla ela tem que ter esse atributo
            * com o valor true.
            */
            if ( (boolean) lerAtributo(posicaoBusca, INDICE_STATUS_SALVO) ) {
                escreverAtributo(posicaoBusca, INDICE_NOME, u.getNome());
                escreverAtributo(posicaoBusca, INDICE_COR, u.getCor());
                escreverAtributo(posicaoBusca, INDICE_VALOR, u.getValor());
                alterado = true;
            }
        }

        return alterado;
    }
    
    /**
     * Pesquisa pelo código e se encontrar, altera do usuário apenas atributos 
     * especificados.
     * 
     * @param u Usuário que deverá ter seus atributos alterados.
     * @param indicesAtributos Vetor com valores numéricos equivalentes aos 
     * indices dos atributos do usuário, ver constantes no inicio da classe.
     * 
     * @return true se a operação ocorreu e false senão ocorreu.
     */
    public boolean alterar(Carro u, int[] indicesAtributos) {
        boolean alterado = false;

        /**
         * Chama método externo para a pesquisa do usuário.
         */
        long posicaoBusca = pesquisar(u.getChassi());

        /**
         * Se for diferente de -1 é por que encontrou.
         */
        if ( posicaoBusca != -1 ) {
            /**
            * Uma tupla só esta salva lógicamente se o seu 
            * status de salvamento for true, Caso contrário, false,
            * Assim para alterar uma tupla ela tem que ter esse atributo
            * com o valor true.
            */
            if ( (boolean) lerAtributo(posicaoBusca, INDICE_STATUS_SALVO) ) {
                /**
                 * For each e Switch case para testar quais atributos 
                 * irão ser alterados.
                 */
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

    /**
     * Realiza pesquisa pelo código e retorna a posição no arquivo, 
     * ATENÇÂO: não checa se a tupla esta salva lógicamente, ou seja,
     * se o status de salvamento esta true.
     * 
     * @param codigoUsuario Código do usuário que deve ser encontrado.
     * 
     * @return A posição no arquivo se encontrar, caso contrário, -1.
     */
    private long pesquisar(long codigoUsuario) {
        /**
         * Se o arquivo estiver vazio nem precisa pesquisar, pois
         * o usuário não vai estar.
         */
        if (tamanhoArquivo() == 0) {
            return -1;
        }
        
        /**
         * Posição aonde devera estar o usuário pesquisado.
         */
        long posicao = -1;
        /**
         * Posição da tupla operada atualmente.
         */
        long posicaoAtual = 0;
        /**
         * Código da tupla operada atualmente.
         */
        long codigoTupla;

        /**
         * Vai repetir até a posição for diferente de -1.
         */
        while ( posicaoAtual != -1 ) {

            /**
             * Le o código do usuário na posição atual.
             */
            codigoTupla = (long) lerAtributo(posicaoAtual, INDICE_CHASSI);

            /**
             * Se o código na posição atual for igual ao código sendo
             * procura, então achou.
             */
            if (codigoTupla == codigoUsuario) {
                posicao = posicaoAtual;
                /**
                 * Como não pode existir repetições no código
                 * não precisa mais buscar.
                 */
                break;
            } else if (codigoUsuario < codigoTupla) {
                /**
                 * A posição atual vai ser a tupla à esquerda se o código que esta
                 * sendo procurado for menor que o código atual.
                 */
                posicaoAtual = (long) lerAtributo(posicaoAtual, INDICE_TUPLA_ESQUERDA);
            } else {
                /**
                 * A posição atual vai ser a tupla à direita se o código que esta
                 * sendo procurado for maior que o código atual.
                 */
                posicaoAtual = (long) lerAtributo(posicaoAtual, INDICE_TUPLA_DIREITA);
            }
        }

        return posicao;
    }

    /**
     * Pesquisa e retorna uma arrayList com todos os usuários que 
     * continverem o valor em um de seus atributos.
     * 
     * @param atributoUsuario Valor do atributo pelo deve ser feita a busca.
     * @param indiceAtributo Vetor com valores numéricos equivalentes aos 
     * indices dos atributos do usuário, ver constantes no inicio da classe
     * UsuarioDao.
     * 
     * @return ArrayList com todos os usuários encontrados, se não for 
     * encontrado nenhum, retorna arrayList vazio.
     */
    public ArrayList<Carro> pesquisarPorAtributos(Object atributoUsuario, int indiceAtributo) {
        /**
         * Se o arquivo estiver vazio nem precisa pesquisar, pois
         * o usuário não vai estar.
         */
        if (tamanhoArquivo() == 0) {
            return new ArrayList<>();
        }

        /**
         * Aonde vão ficar os usuário encontrados.
         */
        ArrayList<Carro> usuarios = new ArrayList<>();
        /**
         * Pilha utilizada para auxiliar a andar pela árvore, pois
         * precisamos guardar os pais das tuplas cada vez que descermos um
         * nível nela.
         */
        Stack<ItemArvore> pilha = new Stack();
        long aux;
        /**
         * Objeto auxiliar aonde podemos salvar, de cada tupla, o endereço, 
         * endereço da tupla à esquerda, endereço da tupla à direita 
         * e se foi visto já pelo algoritmo.
         */
        ItemArvore a;

        /**
         * Cria um ItemArvore da raiz.
         */
        a = new ItemArvore();
        a.setEndereco(0);
        a.setEsquerda((long) lerAtributo(0, INDICE_TUPLA_DIREITA));
        a.setDireita((long) lerAtributo(0, INDICE_TUPLA_ESQUERDA));
        a.setVisto(false);
        
        /**
         * Empilha a raiz.
         */
        pilha.push( a );
        
        /**
         * Repete enquanto a pilha não estiver vazia.
         */
        while (!pilha.isEmpty()) {
            
            /**
             * Se o elemento ainda não foi visitado e se o seu status de 
             * salvamento esta como verdadeiro, ou seja, se lógicamente ele
             * esta salvo checa se o atributo possível valor igual ao procurado.
             */
            if( !pilha.peek().isVisto() &&
                    (boolean) lerAtributo(pilha.peek().getEndereco(), INDICE_STATUS_SALVO) ){
                
                /**
                 * Switch case que verifica qual o atributo esta sendo procurado.
                 */
                switch(indiceAtributo){                        
                    case INDICE_NOME:
                        /**
                         * Se o algortimo estiver procurando pelo nome e o nome 
                         * da tupla atual for igual ao nome procurado, adiciona
                         * na lista de achados.
                         */
                        if(lerAtributo(pilha.peek().getEndereco(), INDICE_NOME).equals( atributoUsuario) ){
                            usuarios.add( lerRegistro( pilha.peek().getEndereco() ) );
                        }
                        break;
                    case INDICE_COR:
                        /**
                         * Se o algortimo estiver procurando pela qualificação
                         * e a qualificação da tupla atual for igual a 
                         * qualificação procurada, adiciona na lista de achados.
                         */
                        if(lerAtributo(pilha.peek().getEndereco(), INDICE_COR)
                                .equals( atributoUsuario)){
                            
                            usuarios.add( lerRegistro( pilha.peek().getEndereco() ) );
                        }
                        break;
                    case INDICE_VALOR:
                        /**
                         * Se o algortimo estiver procurando pelo salário
                         * e o salário da tupla atual for igual ao salário
                         * procurado, adiciona na lista de achados.
                         */
                        if( (double) lerAtributo(pilha.peek().getEndereco(), INDICE_VALOR)
                                == (double) atributoUsuario ){
                            usuarios.add( lerRegistro( pilha.peek().getEndereco() ) );
                        }
                        break;
                }
                
            }
            
            /**
             * O elemento no topo da pilha já foi visto.
             */
            pilha.peek().setVisto(true);
            
            /**
             * Se o topo da pilha tiver tupla à esquerda.
             */
            if(pilha.peek().getEsquerda() != -1){
                //Apenas salva em uma variável auxiliar.
                aux = pilha.peek().getEsquerda();
                
                /**
                 * Cria um novo elemento com a tupla à esquerda. 
                 */
                a = new ItemArvore();
                a.setEndereco(aux);
                a.setEsquerda((long) lerAtributo(aux, INDICE_TUPLA_DIREITA));
                a.setDireita((long) lerAtributo(aux, INDICE_TUPLA_ESQUERDA));
                a.setVisto(false);
                
                /**
                 * Marca que a tupla à esquerda da tupla no topo da pilha
                 * é nula, equivalente ao dizer que foi visitado anteriormente.
                 */
                pilha.peek().setEsquerda(-1);
                
                /**
                 * Empilha o novo elemento.
                 */
                pilha.push( a );
                
            }else if(pilha.peek().getDireita() != -1){
                /**
                * Se o topo da pilha tiver tupla à esquerda.
                */
                
                //Apenas salva em uma variável auxiliar.
                aux = pilha.peek().getDireita();
                
                /**
                 * Cria um novo elemento com a tupla à direita. 
                 */
                a = new ItemArvore();
                a.setEndereco(pilha.peek().getDireita());
                a.setEsquerda((long) lerAtributo(aux, INDICE_TUPLA_DIREITA));
                a.setDireita((long) lerAtributo(aux, INDICE_TUPLA_ESQUERDA));
                a.setVisto(false);
                
                /**
                 * Marca que a tupla à direita da tupla no topo da pilha
                 * é nula, equivalente ao dizer que foi visitado anteriormente.
                 */
                pilha.peek().setDireita(-1);
                
                /**
                 * Empilha o novo elemento.
                 */
                pilha.push( a );
                
            }else{
                /**
                 * Se para a esquerda e para a direita for nulo, 
                 * apenas desempilha.
                 */
                pilha.pop();
            }
            
        }

        return usuarios;
    }

    /**
     * Esvazia o arquivo aonde ficam as tuplas, ou seja, remove 
     * todas as tuplas no arquivo e deixa ele vazio.
     * 
     * @return true se a operação foi um sucesso.
     */
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

    /**
     * Retorna o tamanho do arquivo aonde fica salvos as tuplas.
     * 
     * @return Número que equivale ao numero de bytes do tamanho do arquivo.
     */
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

    /**
     * Escreve os atributos da tupla na posição especifica no parametro.
     * Escreve o código, nome, qualificação, salário, status de salvamento,
     * Ponteiro para a esquerda e o ponteiro para a diretia, nesta ordem.
     * 
     * @param u Usuário que deverá ter seus atributos escritos no arquivo.
     * @param posição posição no arquivo aonde deverão ser escritos os dados.
     * 
     * @exception IndiceInvalidoException, que lança um RunTimeException e 
     * parando o programa caso posicao % TAMANHO_TUPLA != 0, ou seja, caso 
     * a posição não seja um multiplo da tamanho da tupla.
     * 
     * @return true se a operação foi um sucesso, false se ocorreu um erro.
     */
    private boolean escreverRegistro(Carro u, long posição) {
        /**
         * Verifica se a posição é um múltiplo do tamanho da tupla para evitar
         * escrever de forma errada dados no arquivo.
         */
        if (posição % TAMANHO_TUPLA != 0) {
            throw new IndiceInvalidoException();
        }
        RandomAccessFile arq;
        try {
            arq = new RandomAccessFile(CAMINHO_ARQUIVO, "rw");
            //Coloca o cursos na posição indicada.
            arq.seek(posição);
            //Escreve o código do usuário.
            arq.writeLong(u.getChassi());
            //Escreve o nome, formatando para 35 caracteres.
            arq.writeChars(String.format("%1$" + TAMANHO_NOME + "s", u.getNome()));
            //Escreve o nome, formatando para 30 caracteres.
            arq.writeChars(String.format("%1$" + TAMANHO_COR + "s", u.getCor()));
            //Escreve o salário.
            arq.writeDouble(u.getValor());
            //escreve o status de salvamento, nesse o valor é true por
            //causa de ser uma nova inserção
            arq.writeBoolean(true);
            //Escreve o ponteiro para a esquerda, 
            //-1 por que não existe ainda um filho.
            arq.writeLong(-1);
            //Escreve o ponteiro para a direita, 
            //-1 por que não existe ainda um filho.
            arq.writeLong(-1);

            arq.close();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Arquivo não encontrado !!");
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao operar arquivo");
        }
        return true;
    }

    /**
     * Lê as informações da tupla gravadas no arquivo sem criar o objeto Usuário,
     * Le o código, nome, qualificação, salário, status de salvamento,
     * ponteiro para à esquerda e ponteiro à direita.
     * 
     * @param posicao Posição no arquivo aonde deve começar a leitura da tupla.
     * 
     * @exception IndiceInvalidoException, que lança um RunTimeException e 
     * parando o programa caso posicao % TAMANHO_TUPLA != 0, ou seja, caso 
     * a posição não seja um multiplo da tamanho da tupla.
     * 
     * @return Uma String com os atributos da tupla concatenados e
     * separados por vírgula.
     */
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
            
            //Coloca o cursor na posição passada com parametro.
            arq.seek(posicao);

            //Le o código, que é o primeiro atributo.
            s += arq.readLong() + ", ";

            //Esse for le 35 (que é o tamanho do nome) caracteres, que equivale
            //ao nome na tupla.
            aux  = "";
            for (int i = 0; i < TAMANHO_NOME; i++) {
                aux += arq.readChar();
            }
            aux = aux.trim();
            s += aux + ", ";

            aux = "";
            //Esse for le 30 (que é o tamanho da qualificação) caracteres, 
            //que equivale a qualificação na tupla.
            for (int i = 0; i < TAMANHO_COR; i++) {
                aux += arq.readChar();
            }
            aux = aux.trim();
            s += aux + ", ";

            //Le o salário na tupla.
            s += arq.readDouble() + ", ";

            //Le o status de salvamento
            s += arq.readBoolean() + ", ";

            //Le o ponteiro para o elemento à esquerda.
            s += arq.readLong() + ", ";

            //Le o ponteiro para o elemento à direito.
            s += arq.readLong() + ", ";

            arq.close();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Arquivo não encontrado !!");
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao operar arquivo");
        }

        return s;
    }

    /**
     * Le e cria um objeto Usuario com os valores 
     * lidos a partir da posição passada.
     * 
     * @param posicao Posição no arquivo aonde deve começar a leitura da tupla.
     * 
     * @exception IndiceInvalidoException, que lança um RunTimeException e 
     * parando o programa caso posicao % TAMANHO_TUPLA != 0, ou seja, caso 
     * a posição não seja um multiplo da tamanho da tupla.
     * 
     * @return Usuário com os atributos lidos.
     */
    private Carro lerRegistro(long posicao) {
        /**
         * Verifica se a posição é um múltiplo do tamanho da tupla para evitar
         * ler de forma errada dados no arquivo.
         */
        if (posicao % TAMANHO_TUPLA != 0) {
            throw new IndiceInvalidoException();
        }
        Carro u = new Carro();
        String aux;
        RandomAccessFile arq;
        try {
            arq = new RandomAccessFile(CAMINHO_ARQUIVO, "r");
            
            //Posiciona o cursos na posição passada como parametros.
            arq.seek(posicao);

            //Le o código e seta no objeto.
            u.setChassi(arq.readLong());

            //Laço que os caracteres que equivalem ao nome na tupla, 
            aux = "";
            for (int i = 0; i < TAMANHO_NOME; i++) {
                aux += arq.readChar();
            }
            aux = aux.trim();
            //Seta o nome no objeto
            u.setNome(aux);

            //Laço que le os caracteres que equivalem ao nome na tupla.
            aux = "";
            for (int i = 0; i < TAMANHO_COR; i++) {
                aux += arq.readChar();
            }
            aux = aux.trim();
            //Seta a qualificação no objeto.
            u.setCor(aux);

            //Le e seta o salário no objeto.
            u.setValor(arq.readDouble());

            arq.close();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Arquivo não encontrado !!");
        } catch (IOException ex) {
            throw new RuntimeException("Erro ao operar arquivo");
        }
        return u;
    }

    /**
     * Método genérico que escreve qualquer atributo do objeto e da tupla, 
     * ou seja, podem escrever o código, o nome, qualificação, salário,
     * status de salvamento, posição da tupla à esquerda e posição da tupla
     * à direita, na posição passada como parametro.
     * 
     * @param posicaoTupla Posição do inicio da tupla aonde deve 
     * ser escrito o atributo.
     * @param indiceAtributo Valor numérico que equivale ao indice do 
     * atributo que deve ser escrito, ver constantes no inicio da classe 
     * UsuarioDao.
     * @param dado O valor que deve ser escrito.
     * 
     * @throws  IndiceInvalidoException, que lança um RunTimeException e 
     * parando o programa caso posicao % TAMANHO_TUPLA != 0, ou seja, caso 
     * a posição não seja um multiplo da tamanho da tupla.
     * @throws  IndiceAtributoInvalido que lança um RunTimeException
     * parando o progrma caso o valor indiceAtributo não exista para o atributo.
     * 
     * @return true se a operação foi concluída com 
     * sucesso e false caso contrário.
     */
    private boolean escreverAtributo(long posicaoTupla, int indiceAtributo, Object dado) {
        /**
         * Verifica se a posição é um múltiplo do tamanho da tupla para evitar
         * ler de forma errada dados no arquivo.
         */
        if (posicaoTupla % TAMANHO_TUPLA != 0) {
            throw new IndiceInvalidoException();
        }
        /**
         * Verifica se o indice do atributo é válido, caso não seja
         * válido lança exceção.
         */
        if (indiceAtributo < 0 || indiceAtributo > NUMERO_ATRIBUTOS) {
            throw new IndiceAtributoInvalido();
        }
        RandomAccessFile arq;
        try {
            arq = new RandomAccessFile(CAMINHO_ARQUIVO, "rw");

            /**
             * Switch case que verifica qual atributo deve escrito.
             */
            switch (indiceAtributo) {
                
                case INDICE_CHASSI:
                    /**
                    * Para o caso da escrita do código, posiciona o cursor
                    * no inicio da tupla mais o número de bytes após o 
                    * inicio da tupla aonde fica o código.
                    */
                    arq.seek(posicaoTupla + POSICAO_CHASSI);
                    arq.writeLong((long) dado);
                    break;

                case INDICE_NOME:
                    /**
                    * Para o caso da escrita do nome, posiciona o cursor
                    * no inicio da tupla mais o número de bytes após o inicio
                    * da tupla aonde fica o nome, além de realizar a formatação 
                    * do nome para salvamento.
                    */
                    arq.seek(posicaoTupla + POSICAO_NOME);
                    String nome = (String) dado;
                    nome = String.format("%1$" + TAMANHO_NOME + "s", nome);
                    arq.writeChars(nome);
                    break;

                case INDICE_COR:
                    /**
                    * Para o caso da escrita da qualificação, posiciona o cursor
                    * no inicio da tupla mais o número de bytes após o
                    * inicio da tupla aonde fica a qualificação, além de realizar a formatação 
                    * do nome para salvamento.
                    */
                    arq.seek(posicaoTupla + POSICAO_COR);
                    String cor = (String) dado;
                    cor = String.format("%1$" + TAMANHO_COR + "s", cor);
                    arq.writeChars(cor);
                    break;

                case INDICE_VALOR:
                    /**
                    * Para o caso da escrita do salário, posiciona o cursor
                    * no inicio da tupla mais o número de bytes após o
                    * inicio da tupla aonde fica o salário.
                    */
                    arq.seek(posicaoTupla + POSICAO_VALOR);
                    arq.writeDouble((double) dado);
                    break;

                case INDICE_STATUS_SALVO:
                    /**
                    * Para o caso da escrita do status do salvamento,
                    * posiciona o cursor no inicio da tupla mais o 
                    * número de bytes após o inicio da tupla aonde fica
                    * o status de salvamento.
                    */
                    arq.seek(posicaoTupla + POSICAO_STATUS_SALVO);
                    arq.writeBoolean((boolean) dado);
                    break;

                case INDICE_TUPLA_ESQUERDA:
                    /**
                    * Para o caso da escrita do ponteiro da tupla à esquerda,
                    * posiciona o cursor no inicio da tupla mais o 
                    * número de bytes após o inicio da tupla aonde fica
                    * o ponteiro para a tupla à esquerda.
                    */
                    arq.seek(posicaoTupla + POSICAO_TUPLA_ESQUERDA);
                    arq.writeLong((long) dado);
                    break;

                case INDICE_TUPLA_DIREITA:
                    /**
                    * Para o caso da escrita do ponteiro da tupla à direita,
                    * posiciona o cursor no inicio da tupla mais o 
                    * número de bytes após o inicio da tupla aonde fica
                    * o ponteiro para a tupla à direita.
                    */
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

    /**
     * Le apenas um dos atributos da tupla na posição especificada.
     * 
     * @param posicaoTupla Posição da tupla aonde vai ser lido o atributo.
     * @param indiceAtributo Valor numérico que equivale ao indice do 
     * atributo que deve ser escrito, ver constantes no inicio da classe
     * UsuarioDao.
     * 
     * @throws  IndiceInvalidoException, que lança um RunTimeException e 
     * parando o programa caso posicao % TAMANHO_TUPLA != 0, ou seja, caso 
     * a posição não seja um multiplo da tamanho da tupla.
     * @throws  IndiceAtributoInvalido que lança um RunTimeException
     * parando o progrma caso o valor indiceAtributo não exista para o atributo.
     * 
     * @return 
     */
    private Object lerAtributo(long posicaoTupla, int indiceAtributo) {
        /**
         * Verifica se a posição é um múltiplo do tamanho da tupla para evitar
         * ler de forma errada dados no arquivo.
         */
        if (posicaoTupla % TAMANHO_TUPLA != 0) {
            throw new IndiceInvalidoException();
        }
        /**
         * Verifica se o indice do atributo é válido, caso não seja
         * válido lança exceção.
         */
        if (indiceAtributo < 1 || indiceAtributo > NUMERO_ATRIBUTOS) {
            throw new IndiceAtributoInvalido();
        }

        Object objetoLido = null;
        RandomAccessFile arq;
        try {
            arq = new RandomAccessFile(CAMINHO_ARQUIVO, "r");

            /**
             * Switch case para verificar qual atributo deve ser lido.
             */
            switch (indiceAtributo) {
                case INDICE_CHASSI:
                    /**
                    * Para o caso da leitura do código, posiciona o cursor
                    * no inicio da tupla mais o número de bytes após o 
                    * inicio da tupla aonde fica o código.
                    */
                    arq.seek(posicaoTupla + POSICAO_CHASSI);
                    objetoLido = arq.readLong();
                    break;

                case INDICE_NOME:
                    /**
                    * Para o caso da leitura do nome, posiciona o cursor
                    * no inicio da tupla mais o número de bytes após o 
                    * inicio da tupla aonde fica o nome, o laço le todos 
                    * os caracteres do nome.
                    */
                    arq.seek(posicaoTupla + POSICAO_NOME);
                    String nome = "";
                    for (int i = 0; i < TAMANHO_NOME; i++) {
                        nome += arq.readChar();
                    }
                    objetoLido = nome.trim();
                    break;

                case INDICE_COR:
                    /**
                    * Para o caso da leitura da qualificação, posiciona o cursor
                    * no inicio da tupla mais o número de bytes após o 
                    * inicio da tupla aonde fica o qualificação,
                    * o laço le todos os caracteres.
                    */
                    arq.seek(posicaoTupla + POSICAO_COR);
                    String qualificacao = "";
                    for (int i = 0; i < TAMANHO_COR; i++) {
                        qualificacao += arq.readChar();
                    }
                    objetoLido = qualificacao.trim();
                    break;

                case INDICE_VALOR:
                    /**
                    * Para o caso da leitura do salário, posiciona o cursor
                    * no inicio da tupla mais o número de bytes após o 
                    * inicio da tupla aonde fica o salário.
                    */
                    arq.seek(posicaoTupla + POSICAO_VALOR);
                    objetoLido = arq.readDouble();
                    break;

                case INDICE_STATUS_SALVO:
                    /**
                    * Para o caso da leitura do status de salvamento, 
                    * posiciona o cursor no inicio da tupla mais o 
                    * número de bytes após o inicio da tupla aonde 
                    * fica o status de salvamento.
                    */
                    arq.seek(posicaoTupla + POSICAO_STATUS_SALVO);
                    objetoLido = arq.readBoolean();
                    break;

                case INDICE_TUPLA_ESQUERDA:
                    /**
                    * Para o caso da leitura do ponteiro da tupla à esquerda,
                    * posiciona o cursor no inicio da tupla mais o 
                    * número de bytes após o inicio da tupla aonde 
                    * fica o ponteiro à esquerda.
                    */
                    arq.seek(posicaoTupla + POSICAO_TUPLA_ESQUERDA);
                    objetoLido = arq.readLong();
                    break;

                case INDICE_TUPLA_DIREITA:
                    /**
                    * Para o caso da leitura do ponteiro da tupla à direita,
                    * posiciona o cursor no inicio da tupla mais o 
                    * número de bytes após o inicio da tupla aonde 
                    * fica o ponteiro à direita.
                    */
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
