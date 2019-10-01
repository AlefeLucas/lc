/**
 * Token constante do tipo string - armazena o valor como String
 */
@SuppressWarnings("WeakerAccess")
public class TokenString extends TokenConstant<String> {

    /**
     * Inicializa o token constante como tipo string e obtem o valor a partir do lexema
     */
    public TokenString(String lexeme) {
        super(lexeme, ConstantType.STRING);
        this.setConstant(lexeme.substring(1, lexeme.length() - 1).replaceAll("''", "'"));
    }

    /**
     * Converte para string para permitir visualizacao do conteudo do token
     */
    @Override
    public String toString() {
        return String.format("<\"%s\", %s, \"%s\", %s>", getKey(), getValue().name(), getConstant(), getType().name());
    }


}
