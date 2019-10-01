import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class Token implements Map.Entry<String, TokenType>{
    private String lexeme;
    private TokenType token;

    public Token(String lexeme, TokenType token) {
        this.token = token;
        this.lexeme = lexeme;
    }

    @Override
    public String toString() {
        return String.format("<\"%s\", %s>", lexeme, token.name());
    }

    @Override
    public String getKey() {
        return lexeme;
    }

    @Override
    public TokenType getValue() {
        return token;
    }

    @Override
    public TokenType setValue(TokenType value) {
        TokenType old = this.token;
        this.token = value;
        return old;
    }
}
