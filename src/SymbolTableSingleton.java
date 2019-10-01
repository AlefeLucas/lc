
@SuppressWarnings({"unused","WeakerAccess"})
public abstract class SymbolTableSingleton {

    private static SymbolTable symbolTable;

    public static SymbolTable getInstance() {
        if (symbolTable == null) {
            symbolTable = new SymbolTable();
            symbolTable.put("main", TokenType.MAIN);
            symbolTable.put("const", TokenType.CONST);
            symbolTable.put("integer", TokenType.INTEGER);
            symbolTable.put("byte", TokenType.BYTE);
            symbolTable.put("string", TokenType.STRING);
            symbolTable.put("boolean", TokenType.BOOLEAN);
            symbolTable.put("while", TokenType.WHILE);
            symbolTable.put("if", TokenType.IF);
            symbolTable.put("else", TokenType.ELSE);
            symbolTable.put("begin", TokenType.BEGIN);
            symbolTable.put("end", TokenType.END);
            symbolTable.put("then", TokenType.THEN);
            symbolTable.put("readln", TokenType.READLN);
            symbolTable.put("write", TokenType.WRITE);
            symbolTable.put("writeln", TokenType.WRITELN);
            symbolTable.put("true", new TokenBoolean("true"));
            symbolTable.put("false", new TokenBoolean("false"));
            symbolTable.put("and", TokenType.AND);
            symbolTable.put("or", TokenType.OR);
            symbolTable.put("not", TokenType.NOT);
            symbolTable.put("<=", TokenType.LESS_OR_EQUAL);
            symbolTable.put(">=", TokenType.GREATER_OR_EQUAL);
            symbolTable.put("!=", TokenType.NOT_EQUAL);
            symbolTable.put("==", TokenType.EQUAL);
            symbolTable.put("<", TokenType.LESS);
            symbolTable.put(">", TokenType.GREATER);
            symbolTable.put("=", TokenType.ASSIGN);
            symbolTable.put("+", TokenType.PLUS);
            symbolTable.put("-", TokenType.MINUS);
            symbolTable.put("*", TokenType.MULTIPLY);
            symbolTable.put("/", TokenType.DIVIDE);
            symbolTable.put(",", TokenType.COMMA);
            symbolTable.put(";", TokenType.SEMICOLON);
            symbolTable.put("(", TokenType.OPEN_BRACE);
            symbolTable.put(")", TokenType.CLOSE_BRACE);
        }

        return symbolTable;
    }
}
