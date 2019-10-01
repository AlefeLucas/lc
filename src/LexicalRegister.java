import java.util.HashMap;

@SuppressWarnings({"unused"})
public class LexicalRegister extends HashMap<String, TokenConstant> {

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(this.getClass().getCanonicalName() + "{\n");
        this.forEach((lexeme, token) -> s.append(token).append("\n"));
        s.append("}\n");
        return s.toString();
    }
}
