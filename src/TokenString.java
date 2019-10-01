/**
 * Teste
 */
@SuppressWarnings("WeakerAccess")
public class TokenString extends TokenConstant<String> {

    public TokenString(String lexeme) {
        super(lexeme, ConstantType.STRING);
        this.setConstant(lexeme.substring(1, lexeme.length() - 1).replaceAll("''", "'"));
    }

}
