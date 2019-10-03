/**
 * Token constante do tipo integer - armazena o valor como Short pois a faixa de valores eh equivalente.
 *
 * @author Alefe Lucas
 * @author Gabriella Mara
 * @author Ricardo Sena
 */
@SuppressWarnings("WeakerAccess")
public class TokenInteger extends TokenConstant<Short>{

    /**
     * Inicializa o token constante como tipo integer e obtem o valor a partir do lexema
     */
    public TokenInteger(String lexeme) {
        super(lexeme, ConstantType.INTEGER);
        this.setConstant(Short.parseShort(lexeme));
    }

}
