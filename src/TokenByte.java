@SuppressWarnings("WeakerAccess")
public class TokenByte extends TokenConstant<Byte> {

    public TokenByte(String lexeme) {
        super(lexeme, ConstantType.BYTE);
        setConstant(Byte.decode(lexeme.toLowerCase().replace('h', 'x')));
    }

}
