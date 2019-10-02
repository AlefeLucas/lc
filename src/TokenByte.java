/**
 * Token constante do tipo byte - armazena o valor como Short pois eh o menor inteiro que comporta os valores 0 a 255.
 */
@SuppressWarnings("WeakerAccess")
public class TokenByte extends TokenConstant<Short> {

    /**
     * Inicializa o token constante como tipo byte e obtem o valor a partir do lexema
     */
    public TokenByte(String lexeme) {
        super(lexeme, ConstantType.BYTE);
        setConstant(Short.decode(lexeme.toLowerCase().replace('h', 'x')));
    }

}
