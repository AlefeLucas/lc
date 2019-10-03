/**
 * Subclasse de {@link Token} para constantes, contendo o valor da constante e o tipo de constante
 *
 * @param <T> tipo usado para armazenar o valor da constante
 * @author Alefe Lucas
 * @author Gabriella Mara
 * @author Ricardo Sena
 */
@SuppressWarnings("WeakerAccess")
public abstract class TokenConstant<T> extends Token {

    private T constant;
    private ConstantType type;

    /**
     * Inicializa um token com o respectivo lexema e tipo de constante
     */
    public TokenConstant(String lexeme, ConstantType type) {
        super(lexeme, TokenType.CONSTANT);
        this.type = type;
    }

    /**
     * Obtem o valor da constante
     *
     * @return valor da constante
     */
    public T getConstant() {
        return constant;
    }

    /**
     * Converte para string para permitir visualizacao do conteudo do token
     */
    @Override
    public String toString() {
        return String.format("<\"%s\", %s, %s, %s>", getKey(), getValue().name(), getConstant(), getType().name());
    }

    /**
     * Obtem o tipo de constante.
     *
     * @return tipo de constante
     */
    public ConstantType getType() {
        return type;
    }

    /**
     * Define o valor da constante
     *
     * @param constant valor da constante
     */
    protected void setConstant(T constant) {
        this.constant = constant;
    }

}
