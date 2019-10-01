@SuppressWarnings("WeakerAccess")
public abstract class TokenConstant<T> extends Token{

    private T constant;
    private ConstantType type;

    public TokenConstant(String lexeme, ConstantType type) {
        super(lexeme, TokenType.CONSTANT);
        this.type = type;
    }

    public T getConstant() {
        return constant;
    }

    @Override
    public String toString() {
        return String.format("<\"%s\", %s, %s>", getKey(), getValue().name(), getConstant());
    }

    public ConstantType getType() {
        return type;
    }

    protected void setConstant(T constant) {
        this.constant = constant;
    }

}
