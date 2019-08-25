public class Temp {

    /**
     * Verifica a string contem um identificador valido segundo as regras da linguagem.
     * Ver identificadores.txt e automato_identificador.png
     */
    public static boolean validIdentifier(String id){
        return id.matches("(^[a-zA-Z][\\w]*$)|(^[_][_]*[a-zA-Z0-9][\\w]*$)") && id.length() <= 255;
    }
}
