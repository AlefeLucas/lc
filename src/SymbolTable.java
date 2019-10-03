import java.util.HashMap;

/**
 * Tabela de simbolos - implementada como um hash que mapeia o lexema no token.
 *
 * @author Alefe Lucas
 * @author Gabriella Mara
 * @author Ricardo Sena
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class SymbolTable extends HashMap<String, Token> {

    /**
     * Construtor padrao - inicializa a tabela de simbolos com
     * todas as palavras reservadas e simbolos ja inseridos;
     */
    public SymbolTable() {
        super(TokenType.values().length - 2);
        this.put("main", TokenType.MAIN);
        this.put("const", TokenType.CONST);
        this.put("integer", TokenType.INTEGER);
        this.put("byte", TokenType.BYTE);
        this.put("string", TokenType.STRING);
        this.put("boolean", TokenType.BOOLEAN);
        this.put("while", TokenType.WHILE);
        this.put("if", TokenType.IF);
        this.put("else", TokenType.ELSE);
        this.put("begin", TokenType.BEGIN);
        this.put("end", TokenType.END);
        this.put("then", TokenType.THEN);
        this.put("readln", TokenType.READLN);
        this.put("write", TokenType.WRITE);
        this.put("writeln", TokenType.WRITELN);
        this.put("true", new TokenBoolean("true"));
        this.put("false", new TokenBoolean("false"));
        this.put("and", TokenType.AND);
        this.put("or", TokenType.OR);
        this.put("not", TokenType.NOT);
        this.put("<=", TokenType.LESS_OR_EQUAL);
        this.put(">=", TokenType.GREATER_OR_EQUAL);
        this.put("!=", TokenType.NOT_EQUAL);
        this.put("==", TokenType.EQUAL);
        this.put("<", TokenType.LESS);
        this.put(">", TokenType.GREATER);
        this.put("=", TokenType.ASSIGN);
        this.put("+", TokenType.PLUS);
        this.put("-", TokenType.MINUS);
        this.put("*", TokenType.MULTIPLY);
        this.put("/", TokenType.DIVIDE);
        this.put(",", TokenType.COMMA);
        this.put(";", TokenType.SEMICOLON);
        this.put("(", TokenType.OPEN_BRACE);
        this.put(")", TokenType.CLOSE_BRACE);
    }

    /**
     * Adiciona um item a tabela de simbolos pelo lexema e tipo de token
     *
     * @return o token previamente associado com aquele lexema, se houver
     */
    public Token put(String lexeme, TokenType tokenType) {
        return this.put(lexeme, new Token(lexeme, tokenType));
    }

    /**
     * Converte a tabela de simbolos para string - para permitir a visualizacao dos conteudos do mesmo.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(this.getClass().getCanonicalName() + "{\n");
        this.forEach((lexeme, token) -> s.append(token).append("\n"));
        s.append("}\n");
        return s.toString();
    }
}
