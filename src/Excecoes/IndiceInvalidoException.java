package Excecoes;

/**
 * @author Robson de Jesus
 */
public class IndiceInvalidoException extends RuntimeException{

    @Override
    public String getMessage() {
        return "gravação/leitura inválido !!";
    }
    
}
