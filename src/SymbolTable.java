import java.util.HashMap;

/**
 * TP1:
 * 2- Implemente o tipo abstrato de dados “tabela de símbolos”, onde serão armazenados
 * apenas as palavras reservadas e os identificadores. A tabela deverá armazenar
 * registros que conterão, a princípio, campos para o número do token (byte) e o
 * lexema (arranjo de caracteres) da palavra ou identificador. Devem ser
 * implementadas duas funções:
 * <p>
 * • Uma função que pesquisa a tabela em busca de um lexema e retorna o endereço do
 * registro correspondente, caso exista, ou NULL se este não estiver na tabela.
 * • Uma função que insere dinamicamente um registro na tabela, com o token e seu
 * lexema, retornando o endereço de inserção.
 *
 * @see <link>https://stackoverflow.com/questions/14954721/what-is-the-difference-between-a-token-and-a-lexeme</link>
 */
@SuppressWarnings({"WeakerAccess"})
public class SymbolTable extends HashMap<String, Token> {


    public SymbolTable() {
        super();
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
        this.put("true", TokenType.BOOL);
        this.put("false", TokenType.BOOL);
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

    public Token put(String key, TokenType value) {
        return this.put(key, new Token(key, value));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("SymbolTable{\n");
        this.forEach((lexeme, token) -> s.append(token).append("\n"));
        s.append("}\n");
        return s.toString();
    }
}
