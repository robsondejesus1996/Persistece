package Exceptions;

/**
 * @author Robson de Jesus
 */
public class IndiceAtributoInvalido extends RuntimeException{

    @Override
    public String getMessage() {
        return "Indice para buscar de atributo inválido !!";
    }
    
}
