/**
 * Token constante do tipo boolean - armazena o valor como Boolean.
 *
 * @author Alefe Lucas
 * @author Gabriella Mara
 * @author Ricardo Sena
 */
@SuppressWarnings("WeakerAccess")
public class TokenBoolean extends TokenConstant<Boolean> {

    /**
     * Inicializa o token constante como tipo boolean e obtem o valor a partir do lexema
     */
    public TokenBoolean(String lexeme) {
        super(lexeme, ConstantType.BOOLEAN);
        this.setConstant(Boolean.parseBoolean(lexeme));
    }

}