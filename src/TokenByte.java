@SuppressWarnings("WeakerAccess")
public class TokenByte extends TokenConstant<Short> {

    public TokenByte(String lexeme) {
        super(lexeme, ConstantType.BYTE);
        setConstant(Short.decode(lexeme.toLowerCase().replace('h', 'x')));
    }

}
