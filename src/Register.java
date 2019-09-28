import java.util.HashMap;

/**

 */
@SuppressWarnings({"WeakerAccess"})
public class Register extends HashMap<String, Token> {

    public Token put(String key, TokenType value) {
        return this.put(key, new Token(key, value));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(this.getClass().getCanonicalName() + "{\n");
        this.forEach((lexeme, token) -> s.append(token).append("\n"));
        s.append("}\n");
        return s.toString();
    }
}
