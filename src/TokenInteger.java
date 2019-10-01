@SuppressWarnings("WeakerAccess")
public class TokenInteger extends TokenConstant<Short>{

    public TokenInteger(String lexeme) {
        super(lexeme, ConstantType.INTEGER);
        this.setConstant(Short.parseShort(lexeme));
    }

}
