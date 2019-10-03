/**
 * Uma vez que somente uma instancia da tabela de simbolos eh usada durante o
 * processo de compilacao, eh implementado um singleton para a tabela de simbolos.
 *
 * @author Alefe Lucas
 * @author Gabriella Mara
 * @author Ricardo Sena
 */
@SuppressWarnings({"unused","WeakerAccess"})
public abstract class SymbolTableSingleton {

    private static SymbolTable symbolTable;

    /**
     * Se nao houver uma instancia, esta eh criada e retornada. Caso contrario, a instancia ja criada eh retornada.
     */
    public static SymbolTable getInstance() {
        if (symbolTable == null) {
            symbolTable = new SymbolTable();
        }

        return symbolTable;
    }
}
