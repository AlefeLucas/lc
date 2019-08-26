public class Temp {

    /**
     * Verifica a string contem um identificador valido segundo as regras da linguagem.
     * Ver identificadores.txt e automato_identificador.png
     */
    public static boolean validIdentifier(String id){
        return id.matches("(^[a-zA-Z][\\w]*$)|(^[_][_]*[a-zA-Z0-9][\\w]*$)") && id.length() <= 255;
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
}
