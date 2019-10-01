@SuppressWarnings({"unused","WeakerAccess"})
public abstract class LexicalSingleton {

    private static LexicalRegister lexicalRegister;

    public static LexicalRegister getInstance() {
        if (lexicalRegister == null) {
            lexicalRegister = new LexicalRegister();
        }

        return lexicalRegister;
    }
}
