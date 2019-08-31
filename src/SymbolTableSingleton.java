@SuppressWarnings("unused")
public abstract class SymbolTableSingleton {

    private static SymbolTable symbolTable;

    public static SymbolTable getInstance() {
        if (symbolTable == null) {
            symbolTable = new SymbolTable();
        }

        return symbolTable;
    }
}
