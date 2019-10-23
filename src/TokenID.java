/**
 * Subclasse de {@link Token} para identificadores, contendo o tipo e a classe do ID
 *
 * @author Alefe Lucas
 * @author Gabriella Mara
 * @author Ricardo Sena
 */
@SuppressWarnings("WeakerAccess")
public class TokenID extends Token {

    private DataType type;
    private IdClass klass;

    public TokenID(String lexeme) {
        super(lexeme, TokenType.ID);
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public IdClass getKlass() {
        return klass;
    }

    public void setKlass(IdClass klass) {
        this.klass = klass;
    }

    @Override
    public String toString() {
        return String.format("<\"%s\", %s, %s, %s>", getKey(), getValue().name(), getKlass().name(), getType().name());    }
}
