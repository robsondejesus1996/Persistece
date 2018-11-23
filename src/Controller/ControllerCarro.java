package Controller;

import Model.Carro;
import Persistencia.ManipularArquivos;
import Persistencia.CarroDao;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 * @author Robson de Jesus
 */
public class ControllerCarro {

    final String intrucoes = ("------------------------------------------------------------------------------\n"
                + "|                            Operações do Sistema                            |\n"
                + "| [1] - Adicionar registro                                                   |\n"
                + "ex:  criar -chassi: 01 -nome: Gol -cor: Preta -valor: 30000,00 -quantidade: 1|\n"
                + "|                                                                            |\n"
                + "| [2] - Remover registro                                                     |\n"
                + "ex: exluir 001                                                               |\n"
                + "|                                                                            |\n"
                + "| [3] - Alterar registro                                                     |\n"
                + "ex: alterar -chassi: 001 -n Nome -q Cor -s 7000,00 -quantidade: 2            |\n"
                + "|                                                                            |\n"
                + "| [4] - Consultar registro por chassi                                        |\n"
                + "ex:  pesquisar -chassi: 001                                                  |\n"
                + "|                                                                            |\n"
                + "| [5] - Consultar registro por nome                                          |\n"
                + "ex:  pesquisar -nome: Argo                                                   |\n"
                + "|                                                                            |\n"
                + "| [8] - Visualizar árvore                                                    |\n"
                + "ex:  Digite: m                                                               |\n"
                + "|                                                                            |\n"
                + "| [9] - Gerar Arquivo                                                        |\n"
                + "ex:  Digite g                                                                |\n"
                + "|                                                                            |\n"
                + "| [10] - Resetar                                                             |\n"
                + "ex:  digite r                                                                |\n"
                + "|                                                                            |\n"
                + "| [11] - Sair                                                                |\n"
                + "*----------------------------------------------------------------------------*");

    private final Scanner entrada = new Scanner(System.in);

    private final CarroDao uDao = new CarroDao();

    public void executar() {

        ManipularArquivos.gravar(ManipularArquivos.CAMINHO_ARQUIVO_COMANDOS,
                new Date() + "  Programa foi inicializado! ");

        System.out.print("Digite 'comecar' para ver todas as instruções do sistema.\n");
        String aux;
        String[] comandos;

        while (true) {
            System.out.println("==============================================================================");

            aux = entrada.nextLine();

            ManipularArquivos.gravar(ManipularArquivos.CAMINHO_ARQUIVO_COMANDOS,
                    new Date() + "  " + aux);

            comandos = aux.split("&&");

            for (String s : comandos) {
                menu(s.trim());
            }
        }

    }

    public void menu(String comando) {
        String[] argumentos = comando.split(" ");
        switch (argumentos[0].toLowerCase().trim()) {
            case "criar":
                cadastrar(argumentos);
                break;
            case "pesquisar":
                pesquisar(argumentos);
                break;
            case "excluir":
                excluir(argumentos);
                break;
            case "alterar":
                alterar(argumentos);
                break;
            case "m":
                System.out.println(uDao.imprimirArvore());
                break;
            case "g":
                uDao.escreverArvore();
                break;
            case "r":
                resetar();
                break;
            case "comecar":
                System.out.println(intrucoes);
                break;
            case "sair":
                ManipularArquivos.gravar(ManipularArquivos.CAMINHO_ARQUIVO_COMANDOS,
                        new Date() + "  Final da execução\n\n\n");
                System.exit(0);
                break;
            default:
                System.out.println("\nErro comando inválido, olhe nas instruções de operação!!");
        }
    }

    public boolean cadastrar(String[] argumentos) {

        if (argumentos.length < 10) {
            System.out.println("Erro faltando comandos!!");
            return false;
        }

        Carro u = new Carro();
        long chassi;
        String nome = "", cor = "";
        double valor;
        double quantidade = 0;
        int posicao;

        if (argumentos[1].equals("-chassi:")) {
            try {
                chassi = Long.parseLong(argumentos[2]);
            } catch (NumberFormatException ex) {
                System.out.println("Digite o chassi, valor númerico !!");
                return false;
            }
        } else {
            System.out.println("Comando inválido Erro!!!");
            return false;
        }

        if (argumentos[3].equals("-nome:")) {
            posicao = 4;

            do {
                nome += " " + argumentos[posicao];
                posicao ++;

                if (posicao >= argumentos.length) {
                    System.out.println("Comando inválido Erro!!!");
                    return false;
                }
            } while (!argumentos[posicao].equals("-cor:"));
            nome = nome.trim();
        } else {
            System.out.println("Sintaxe inválida !!");
            return false;
        }

        if (argumentos[posicao].equals("-cor:")) {
            posicao ++;

            do {
                cor += " " + argumentos[posicao];
                posicao ++;

                if (posicao >= argumentos.length) {
                    System.out.println("Comando inválido Erro!!!");
                    return false;
                }
            } while (!argumentos[posicao].equals("-valor:"));
            cor = cor.trim();
        } else {
            System.out.println("Comando inválido Erro!!!");
            return false;
        }

        if (argumentos[posicao].equals("-valor:")) {
            posicao ++;
            try {
                valor = Double.parseDouble(argumentos[posicao].replaceAll(",", "."));
            } catch (NumberFormatException ex) {
                System.out.println("Comando inválido Erro!!!");
                return false;
            }
        } else {
            System.out.println("Comando inválido Erro!!!");
            return false;
        }
        
        //teste argumento de quantidade novo atributo
        if(argumentos[posicao].equals("-quantidade:")){
            posicao ++;
            try {
                quantidade = Double.parseDouble(argumentos[posicao].replaceAll(",", "."));
            } catch (NumberFormatException ex) {
                System.out.println("Comando inválido Erro!!!");
                return false;
            }
        }

        u.setChassi(chassi);
        u.setNome(nome);
        u.setCor(cor);
        u.setValor(valor);
        u.setQuantidade(quantidade);

        if (uDao.inserir(u)) {
            System.out.println("\nRegistro inserido com sucesso!!!\n");
        } else {
            System.out.println("\nAtualmente existe um registro com esse codigo de chassi, "
                    + "informe outro chassi!!\n");
        }
        return true;
    }

    public boolean pesquisar(String[] argumentos) {
        if (argumentos.length < 3) {
            System.out.println("Estão faltando argumentos no comando.");
            return false;
        }
        ArrayList<Carro> lista;
        int posicao;

        switch (argumentos[1]) {
            case "-chassi:":
                long chassi;
                Carro u;
                try {
                    chassi = Long.parseLong(argumentos[2]);
                } catch (NumberFormatException ex) {
                    System.out.println("Informe o chassi que deseja pesquisar "
                            + "por código.");
                    return false;
                }
                u = uDao.ler(chassi);
                if (u != null) {
                    System.out.println("\nPesquisa realizada com sucesso\n" + u.toString() + "\n");
                } else {
                    System.out.println("\nAtualmente nenhum registro foi achado com esse chassi!!\n");
                }
                break;

            case "-nome:":
                String nome = "";
                posicao = 2;

                do {
                    nome += " " + argumentos[posicao];
                    posicao++;
                } while (posicao < argumentos.length);
                nome = nome.trim();

                lista = uDao.pesquisarPorAtributos(nome, uDao.INDICE_NOME);

                if (!lista.isEmpty()) {
                    System.out.println("\nPesquisa realizada com sucesso\n");
                    for (Carro usu : lista) {
                        System.out.println("   " + usu.toString() + "\n");
                    }
                } else {
                    System.out.println("\nAtualmente nenhum registro foi achado com esse nome!!!!\n");
                }
                break;
            default:
                System.out.println("Erro no comando, olhe nas intruções!!");
                return false;
        }

        return true;
    }

    public boolean excluir(String[] argumentos) {
        if (argumentos.length < 2) {
            System.out.println("Erro no comando olhe nas instruções");
            return false;
        }
        long codigo;

        try {
            codigo = Long.parseLong(argumentos[1]);
        } catch (NumberFormatException ex) {
            System.out.println("Informe o valor do chassi para fazer a exclusão!!");
            return false;
        }

        if (uDao.excluir(codigo)) {
            System.out.println("\nRegistro excluido com sucesso!!\n");
        } else {
            System.out.println("Nenhum usuário com esse código foi achado, "
                    + "logo não houve exclusão. !!\n");
        }

        return true;
    }

    public void resetar() {
        char opcao;
        while (true) {
            System.out.print("Para excluir todos os registro no arquivo digite:(s/n): ");
            opcao = entrada.next().toLowerCase().trim().charAt(0);
            entrada.nextLine();

            if (opcao == 's') {
                uDao.limparArquivo();
                System.out.println("Arquivo resetado!\n");
                break;
            } else if (opcao == 'n') {
                System.out.println("Usuário calcelou operação!\n");
                break;
            }
        }
    }

    public boolean alterar(String[] argumentos) {
        if (argumentos.length < 3) {
            System.out.println("Estão faltando argumentos no comando.");
            return false;
        }
        Carro u = new Carro();
        long chassi;
        String nome = "", cor = "";
        double valor;
        int posicao;
        int posicaoN = -1, posicaoQ = -1, posicaoS = -1;
        int[] arg = new int[3];

        for (int i = 0; i < argumentos.length; i++) {
            switch (argumentos[i]) {
                case "-nome:":
                    posicaoN = i;
                    arg[0] = uDao.INDICE_NOME;
                    break;
                case "-cor:":
                    posicaoQ = i;
                    arg[1] = uDao.INDICE_COR;
                    break;
                case "-valor:":
                    posicaoS = i;
                    arg[2] = uDao.INDICE_VALOR;
                    break;
                default:
                    break;
            }
        }

        if (posicaoN == -1 && posicaoQ == -1 && posicaoS == -1) {
            System.out.println("Foi passado apenas o parametro -c para "
                    + "atualização de usuário");
            return false;
        }

        if (argumentos[1].equals("-chassi:")) {
            try {
                chassi = Long.parseLong(argumentos[2]);
            } catch (NumberFormatException ex) {
                System.out.println("Digite um valor número para o chassi.");
                return false;
            }
        } else {
            System.out.println("Erro no comando!!");
            return false;
        }
        u.setChassi(chassi);

        if (posicaoN != -1) {
            posicao = posicaoN + 1;
            do {
                nome += " " + argumentos[posicao];
                posicao++;

            } while (!(posicao == posicaoQ
                    || posicao == posicaoS
                    || posicao == argumentos.length));
            u.setNome(nome.trim());
        }

        if (posicaoQ != -1) {
            posicao = posicaoQ + 1;
            do {
                cor += " " + argumentos[posicao];
                posicao++;

            } while (!(posicao == posicaoN
                    || posicao == posicaoS
                    || posicao == argumentos.length));
            u.setCor(cor.trim());
        }

        if (posicaoQ != -1) {
            posicao = posicaoS + 1;
            try {
                valor = Double.parseDouble(argumentos[posicao].replaceAll(",", "."));
            } catch (NumberFormatException ex) {
                System.out.println("Erro no comando!!");
                return false;
            }
            u.setValor(valor);
        }

        if (uDao.alterar(u, arg)) {
            System.out.println("\nRegistro alterado com sucesso\n");
        } else {
            System.out.println("\natualmente não existe um chassi com esse código, "
                    + "alteração não foi realizada.\n");
        }
        return true;
    }
}
