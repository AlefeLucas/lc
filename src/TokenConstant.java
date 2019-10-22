/**
 * Subclasse de {@link Token} para constantes, contendo o valor da constante e o tipo de constante
 *
 * @author Alefe Lucas
 * @author Gabriella Mara
 * @author Ricardo Sena
 */
@SuppressWarnings("WeakerAccess")
public class TokenConstant extends Token {

    private Object constant;
    private DataType type;

    /**
     * Inicializa um token com o respectivo lexema e tipo de constante
     */
    public TokenConstant(String lexeme, DataType type) {
        super(lexeme, TokenType.CONSTANT);
        this.type = type;

        switch (type){
            case STRING:
                this.constant = getString(lexeme);
                break;
            case INTEGER:
                this.constant = getInteger(lexeme);
                break;
            case BYTE:
                this.constant = getByte(lexeme);
                break;
            case BOOLEAN:
                this.constant = getBoolean(lexeme);
                break;
            default:
                throw new NullPointerException("DataType should not be null");
        }
    }

    /**
     * Obtem o valor da constante
     *
     * @return valor da constante
     */
    public Object getConstant() {
        return constant;
    }

    /**
     * Converte para string para permitir visualizacao do conteudo do token
     */
    @Override
    public String toString() {
        switch (type){
            case STRING:
                return String.format("<\"%s\", %s, \"%s\", %s>", getKey(), getValue().name(), getConstant(), getType().name());
            default:
                return String.format("<\"%s\", %s, %s, %s>", getKey(), getValue().name(), getConstant(), getType().name());
        }
    }

    /**
     * Obtem o tipo de constante.
     *
     * @return tipo de constante
     */
    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    /**
     * Define o valor da constante
     *
     * @param constant valor da constante
     */
    protected void setConstant(Object constant) {
        this.constant = constant;
    }

    public static Boolean getBoolean(String lexeme) {
        return Boolean.parseBoolean(lexeme);
    }

    public static Short getInteger(String lexeme) {
        return Short.parseShort(lexeme);
    }

    public static Short getByte(String lexeme) {
        short s = Short.decode(lexeme.toLowerCase().replace('h', 'x'));
        if (s >= 0 && s <= 255) {
            return s;
        } else {
            IllegalStateException ex = new IllegalStateException("Erro");
            ex.printStackTrace();
            throw ex;
        }
    }



    public static String getString(String lexeme){
        return lexeme.substring(1, lexeme.length() - 1).replaceAll("''", "'");
    }

}
