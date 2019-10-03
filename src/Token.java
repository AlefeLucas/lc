import java.util.Map;

/**
 * Token - associacao entre o lexema e o tipo de token correspondente
 *
 * @author Alefe Lucas
 * @author Gabriella Mara
 * @author Ricardo Sena
 */
@SuppressWarnings("WeakerAccess")
public class Token implements Map.Entry<String, TokenType> {
    private String lexeme;
    private TokenType token;

    /**
     * Inicializa um token com o respectivo lexema e tipo de token
     */
    public Token(String lexeme, TokenType token) {
        this.token = token;
        this.lexeme = lexeme;
    }

    /**
     * Converte para string para permitir visualizacao do conteudo do token
     */
    @Override
    public String toString() {
        return String.format("<\"%s\", %s>", lexeme, token.name());
    }

    /**
     * Obtem o lexema
     *
     * @return lexema
     */
    @Override
    public String getKey() {
        return lexeme;
    }

    /**
     * Obtem o tipo de token
     *
     * @return tipo de token
     */
    @Override
    public TokenType getValue() {
        return token;
    }

    /**
     * Define o tipo de token, retorna o tipo previamente associado
     *
     * @param token tipo de token
     * @return tipo de token anterior
     */
    @Override
    public TokenType setValue(TokenType token) {
        TokenType old = this.token;
        this.token = token;
        return old;
    }
}
