/**
 * Uma vez que somente uma instancia do registro lexico eh usada durante o
 * processo de compilacao, eh implementado um singleton para o registro lexico.
 */
@SuppressWarnings({"unused","WeakerAccess"})
public abstract class LexicalRegisterSingleton {

    private static LexicalRegister lexicalRegister;

    /**
     * Se nao houver uma instancia, esta eh criada e retornada. Caso contrario, a instancia ja criada eh retornada.
     */
    public static LexicalRegister getInstance() {
        if (lexicalRegister == null) {
            lexicalRegister = new LexicalRegister();
        }
        return lexicalRegister;
    }
}
