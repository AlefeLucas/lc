public class TokenBoolean extends Token implements TokenConstant<Boolean> {

    private Boolean constant;

    public TokenBoolean(String lexeme, TokenType token) {
        super(lexeme, token);
        this.constant = Boolean.parseBoolean(lexeme);
    }

    public Boolean getConstant() {
        return constant;
    }

    @Override
    public String toString() {
        return String.format("<\"%s\", %s, \"%s\">", getKey(), getValue().name(), getConstant());
    }
}