@SuppressWarnings("WeakerAccess")
public class TokenInteger extends TokenConstant<Integer>{

    public TokenInteger(String lexeme) {
        super(lexeme, ConstantType.INTEGER);
        this.setConstant(Integer.parseInt(lexeme));
    }

}
