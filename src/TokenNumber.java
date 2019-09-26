public class TokenNumber extends Token implements TokenConstant<Integer>{

    private int constant;

    public TokenNumber(String lexeme, TokenType token) {
        super(lexeme, token);
        this.constant = parseNumeralConstant(lexeme);
    }

    /**
     * Obtem um int a partir de uma constante numerica em String nos formatos que a linguagem aceita,
     * incluindo o hexadecimal 0hfff.
     */
    public static int parseNumeralConstant(String numeralConstant){
        if(numeralConstant.matches("[0][hH][0-9a-fA-F]+")){
            numeralConstant = numeralConstant.toLowerCase().replace('h', 'x');
            return Integer.decode(numeralConstant);
        }
        return Integer.parseInt(numeralConstant, 10);
    }

    public Integer getConstant() {
        return constant;
    }

    @Override
    public String toString() {
        return String.format("<\"%s\", %s, %s>", getKey(), getValue().name(), getConstant());
    }
}
