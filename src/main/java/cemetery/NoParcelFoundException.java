package cemetery;

public class NoParcelFoundException extends IllegalArgumentException{

    public NoParcelFoundException(String s) {
        super(s);
    }
}
