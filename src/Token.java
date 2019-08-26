
public class Token {
    private TokenType type;
    private String data;

    public Token(TokenType type, String data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("(%s %s)", type.getGroupName(), data);
    }
}
