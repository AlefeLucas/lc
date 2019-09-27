@SuppressWarnings("unused")
public abstract class LexicalSingleton {

    private static Register lexical;

    public static Register getInstance() {
        if (lexical == null) {
            lexical = new Register();
        }

        return lexical;
    }
}
