import java.util.Arrays;
import java.util.Comparator;

@SuppressWarnings("WeakerAccess")
public class Parser {

    private Lexer lexer;
    private Token token;
    private static final TokenType[] FIRST_D = {TokenType.BOOLEAN, TokenType.BYTE, TokenType.CONST, TokenType.INTEGER, TokenType.STRING};
    private static final TokenType[] FIRST_C = {TokenType.ID, TokenType.IF, TokenType.READLN, TokenType.SEMICOLON, TokenType.WHILE, TokenType.WRITE, TokenType.WRITELN};
    private static final TokenType[] FIRST_E = {TokenType.CONSTANT, TokenType.ID, TokenType.MINUS, TokenType.NOT, TokenType.OPEN_BRACE, TokenType.PLUS};


    public Parser(String source) {
        lexer = new Lexer(source);
    }

    public void parse() {
        try {
            token = lexer.next();
            s();
        } catch (NullPointerException ex) {
            System.err.printf("%d:fim de arquivo nao esperado.\n", lexer.getLine());
        }
    }

    private void matchToken(TokenType expectedToken) {
        if (expectedToken.equals(token.getValue())) {
            token = lexer.next();
        } else {
            String error = String.format("%d:token nao esperado [%s]\n", lexer.getLine(), token.getKey());
            System.err.print(error);
            System.exit(1);
        }
    }

    /**
     *  S  =>  {D}main {C} end
     */
    private void s() {
        while (in(token.getValue(), FIRST_D)) {
            d();
        }
        matchToken(TokenType.MAIN);
        while (in(token.getValue(), FIRST_C)) {
            c();
        }
        matchToken(TokenType.END);
    }

    private void d() {
        if (token.getValue() == TokenType.INTEGER) {
            matchToken(TokenType.INTEGER);
            j();
        } else if (token.getValue() == TokenType.STRING) {
            matchToken(TokenType.STRING);
            j();
        } else if (token.getValue() == TokenType.BOOLEAN) {
            matchToken(TokenType.BOOLEAN);
            j();
        } else if (token.getValue() == TokenType.BYTE) {
            matchToken(TokenType.BYTE);
            j();
        } else {
            matchToken(TokenType.CONST);
            matchToken(TokenType.ID);
            matchToken(TokenType.ASSIGN);
            if (token.getValue() == TokenType.MINUS) {
                matchToken(TokenType.MINUS);
            }
            matchToken(TokenType.CONSTANT);
            matchToken(TokenType.SEMICOLON);
        }
    }

    private void j() {
        matchToken(TokenType.ID);
        if (token.getValue() == TokenType.ASSIGN) {
            matchToken(TokenType.ASSIGN);
            e();
        }
        while (token.getValue() == TokenType.COMMA) {
            matchToken(TokenType.COMMA);
            matchToken(TokenType.ID);
            if (token.getValue() == TokenType.ASSIGN) {
                matchToken(TokenType.ASSIGN);
                e();
            }
        }
        matchToken(TokenType.SEMICOLON);
    }

    private void c() {

        if (token.getValue() == TokenType.ID) {
            matchToken(TokenType.ID);
            matchToken(TokenType.ASSIGN);
            e();
            matchToken(TokenType.SEMICOLON);
        } else if (token.getValue() == TokenType.WRITE) {
            matchToken(TokenType.WRITE);
            k();
        } else if (token.getValue() == TokenType.WRITELN) {
            matchToken(TokenType.WRITELN);
            k();
        } else if (token.getValue() == TokenType.READLN) {
            matchToken(TokenType.READLN);
            matchToken(TokenType.OPEN_BRACE);
            matchToken(TokenType.ID);
            matchToken(TokenType.CLOSE_BRACE);
            matchToken(TokenType.SEMICOLON);
        } else if (token.getValue() == TokenType.WHILE) {
            matchToken(TokenType.WHILE);
            matchToken(TokenType.OPEN_BRACE);
            e();
            matchToken(TokenType.CLOSE_BRACE);
            l();
        } else if (token.getValue() == TokenType.IF) {
            matchToken(TokenType.IF);
            matchToken(TokenType.OPEN_BRACE);
            e();
            matchToken(TokenType.CLOSE_BRACE);
            matchToken(TokenType.THEN);
            l();
            if (token.getValue() == TokenType.ELSE) {
                matchToken(TokenType.ELSE);
                l();
            }
        } else {
            matchToken(TokenType.SEMICOLON);
        }
    }

    private void k() {
        matchToken(TokenType.OPEN_BRACE);
        if (in(token.getValue(), FIRST_E)) {
            e();
            while (token.getValue() == TokenType.COMMA) {
                matchToken(TokenType.COMMA);
                e();
            }
        }
        matchToken(TokenType.CLOSE_BRACE);

        matchToken(TokenType.SEMICOLON);
    }

    private static boolean in(TokenType token, TokenType[] orderedByName) {
        return Arrays.binarySearch(orderedByName, token, Comparator.comparing(Enum::name)) >= 0;
    }

    private void e() {
        f();
        final TokenType[] LOGIC_OP = {TokenType.EQUAL, TokenType.GREATER, TokenType.GREATER_OR_EQUAL, TokenType.LESS, TokenType.LESS_OR_EQUAL, TokenType.NOT_EQUAL};
        while (in(token.getValue(), LOGIC_OP)) {
            if (token.getValue() == TokenType.EQUAL) {
                matchToken(TokenType.EQUAL);
            } else if (token.getValue() == TokenType.NOT_EQUAL) {
                matchToken(TokenType.NOT_EQUAL);
            } else if (token.getValue() == TokenType.LESS) {
                matchToken(TokenType.LESS);
            } else if (token.getValue() == TokenType.LESS_OR_EQUAL) {
                matchToken(TokenType.LESS_OR_EQUAL);
            } else if (token.getValue() == TokenType.GREATER) {
                matchToken(TokenType.GREATER);
            } else {
                matchToken(TokenType.GREATER_OR_EQUAL);
            }
            f();
        }
    }

    private void f() {
        if (token.getValue() == TokenType.PLUS) {
            matchToken(TokenType.PLUS);
        } else if (token.getValue() == TokenType.MINUS) {
            matchToken(TokenType.MINUS);
        }
        g();
        final TokenType[] OP = {TokenType.MINUS, TokenType.OR, TokenType.PLUS};
        while (in(token.getValue(), OP)) {
            if (token.getValue() == TokenType.PLUS) {
                matchToken(TokenType.PLUS);
            } else if (token.getValue() == TokenType.MINUS) {
                matchToken(TokenType.MINUS);
            } else {
                matchToken(TokenType.OR);
            }
            g();
        }
    }

    private void g() {
        h();
        final TokenType[] OP = {TokenType.AND, TokenType.DIVIDE, TokenType.MULTIPLY};
        while (in(token.getValue(), OP)) {
            if (token.getValue() == TokenType.MULTIPLY) {
                matchToken(TokenType.MULTIPLY);
            } else if (token.getValue() == TokenType.DIVIDE) {
                matchToken(TokenType.DIVIDE);
            } else {
                matchToken(TokenType.AND);
            }
            h();
        }
    }

    private void h() {
        if (token.getValue() == TokenType.NOT) {
            matchToken(TokenType.NOT);
        }
        i();
    }

    private void i() {
        if (token.getValue() == TokenType.ID) {
            matchToken(TokenType.ID);
        } else if (token.getValue() == TokenType.CONSTANT) {
            matchToken(TokenType.CONSTANT);
        } else {
            matchToken(TokenType.OPEN_BRACE);
            e();
            matchToken(TokenType.CLOSE_BRACE);
        }
    }

    private void l() {
        if (token.getValue() == TokenType.BEGIN) {
            matchToken(TokenType.BEGIN);
            while (in(token.getValue(), FIRST_C)) {
                c();
            }
            matchToken(TokenType.END);
        } else {
            c();
        }
    }

}
