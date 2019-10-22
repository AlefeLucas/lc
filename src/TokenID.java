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
}
