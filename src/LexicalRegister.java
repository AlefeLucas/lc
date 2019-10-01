import java.util.HashMap;

/**
 * Registro lexico. Implementado como uma hash que mapeia o lexema no token de constante.
 */
@SuppressWarnings({"unused"})
public class LexicalRegister extends HashMap<String, TokenConstant> {

    /**
     * Converte o registro lexico para string - para permitir a visualizacao dos conteudos do mesmo.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(this.getClass().getCanonicalName() + "{\n");
        this.forEach((lexeme, token) -> s.append(token).append("\n"));
        s.append("}\n");
        return s.toString();
    }
}
