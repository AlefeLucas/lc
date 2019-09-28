/**
 * Teste
 */
public class TokenString extends Token implements TokenConstant<String> {

    private String constant;

    public TokenString(String lexeme, TokenType token) {
        super(lexeme, token);

        this.constant = lexeme.substring(1, lexeme.length() -1).replaceAll("''", "'");
    }

    public String getConstant() {
        return constant;
    }

    @Override
    public String toString() {
        return String.format("<\"%s\", %s, \"%s\">", getKey(), getValue().name(), getConstant());
    }
}
