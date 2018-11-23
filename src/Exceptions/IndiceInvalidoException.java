package Exceptions;

/**
 * @author Robson de Jesus
 */
public class IndiceInvalidoException extends RuntimeException{

    @Override
    public String getMessage() {
        return "Indice para gravação/leitura inválido !!";
    }
    
}
