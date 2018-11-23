package Exceptions;

/**
 * @author tiago
 */
public class IndiceAtributoInvalido extends RuntimeException{

    @Override
    public String getMessage() {
        return "Indice para buscar de atributo inválido !!";
    }
    
}
