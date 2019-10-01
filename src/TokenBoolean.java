@SuppressWarnings("WeakerAccess")
public class TokenBoolean extends TokenConstant<Boolean> {

    public TokenBoolean(String lexeme) {
        super(lexeme, ConstantType.BOOLEAN);
        this.setConstant(Boolean.parseBoolean(lexeme));
    }

}