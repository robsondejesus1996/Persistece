package Excecoes;

/**
 * @author Robson de Jesus
 */
public class IndiceAtributoInvalido extends RuntimeException{

    @Override
    public String getMessage() {
        return "buscar de atributo inválido !!";
    }
    
}
