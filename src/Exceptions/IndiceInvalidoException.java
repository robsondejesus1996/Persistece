package Exceptions;

/**
 * @author tiago
 */
public class IndiceInvalidoException extends RuntimeException{

    @Override
    public String getMessage() {
        return "Indice para gravação/leitura inválido !!";
    }
    
}
