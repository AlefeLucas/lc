import java.util.Arrays;
import java.util.Comparator;
@SuppressWarnings("WeakerAccess")
public class Parser {

    private Lexer lexer;
    private Token token;
    private static final TokenType[] FIRST_D = {TokenType.BOOLEAN, TokenType.BYTE, TokenType.CONST, TokenType.INTEGER, TokenType.STRING};
    private static final TokenType[] FIRST_C = {TokenType.ID, TokenType.IF, TokenType.READLN, TokenType.SEMICOLON, TokenType.WHILE, TokenType.WRITE, TokenType.WRITELN};
    private static final TokenType[] FIRST_E = {TokenType.ID, TokenType.MINUS, TokenType.NOT, TokenType.NUMERAL_CONSTANT, TokenType.OPEN_BRACE, TokenType.PLUS, TokenType.STRING_CONSTANT};


    public Parser(String source) {
        lexer = new Lexer(source);
    }

    public void parse() {
        try {
            token = lexer.next();
            s();
        }catch (NullPointerException ex){
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

    private void s() {
        while (Arrays.binarySearch(FIRST_D, token.getValue(), Comparator.comparing(Enum::name)) >= 0) {
            d();
        }
        matchToken(TokenType.MAIN);
        while (Arrays.binarySearch(FIRST_C, token.getValue(), Comparator.comparing(Enum::name)) >= 0) {
            c();
        }
        matchToken(TokenType.END);
    }

    private void d() {
        final TokenType[] OP1 = {TokenType.BOOLEAN, TokenType.BYTE, TokenType.INTEGER, TokenType.STRING};
        if (Arrays.binarySearch(OP1, token.getValue(), Comparator.comparing(Enum::name)) >= 0) {
            if (token.getValue() == TokenType.INTEGER) {
                matchToken(TokenType.INTEGER);
            } else if (token.getValue() == TokenType.STRING) {
                matchToken(TokenType.STRING);
            } else if (token.getValue() == TokenType.BOOLEAN) {
                matchToken(TokenType.BOOLEAN);
            } else {
                matchToken(TokenType.BYTE);
            }

            matchToken(TokenType.ID);
            matchToken(TokenType.SEMICOLON);
        } else {
            matchToken(TokenType.CONST);
            matchToken(TokenType.ID);
            matchToken(TokenType.ASSIGN);
            if (token.getValue() == TokenType.MINUS) {
                matchToken(TokenType.MINUS);
                matchToken(TokenType.NUMERAL_CONSTANT);
            } else if (token.getValue() == TokenType.NUMERAL_CONSTANT) {
                matchToken(TokenType.NUMERAL_CONSTANT);
            } else if (token.getValue() == TokenType.STRING_CONSTANT) {
                matchToken(TokenType.STRING_CONSTANT);
            } else {
                matchToken(TokenType.BOOL);
            }
            matchToken(TokenType.SEMICOLON);
        }
    }

    private void c() {
        final TokenType[] FUNCTIONS = {TokenType.READLN, TokenType.WRITE, TokenType.WRITELN};

        if (token.getValue() == TokenType.ID) {
            matchToken(TokenType.ID);
            matchToken(TokenType.ASSIGN);
            e();
            matchToken(TokenType.SEMICOLON);
        } else if (Arrays.binarySearch(FUNCTIONS, token.getValue(), Comparator.comparing(Enum::name)) >= 0) {
            if (token.getValue() == TokenType.WRITE) {
                matchToken(TokenType.WRITE);
            } else if (token.getValue() == TokenType.WRITELN) {
                matchToken(TokenType.WRITELN);
            } else {
                matchToken(TokenType.READLN);
            }
            matchToken(TokenType.OPEN_BRACE);
            if (Arrays.binarySearch(FIRST_E, token.getValue(), Comparator.comparing(Enum::name)) >= 0) {
                e();
                while (token.getValue() == TokenType.COMMA) {
                    matchToken(TokenType.COMMA);
                    e();
                }
            }
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


    private void e() {
        f();
        while (token.getValue() == TokenType.OR) {
            matchToken(TokenType.OR);
            f();
        }
    }

    private void f() {
        if (token.getValue() == TokenType.NOT) {
            matchToken(TokenType.NOT);
        }
        g();
        while (token.getValue() == TokenType.AND) {
            matchToken(TokenType.AND);
            if (token.getValue() == TokenType.NOT) {
                matchToken(TokenType.NOT);
            }
            g();
        }
    }

    private void g() {
        h();
        final TokenType[] LOGIC_OP = {TokenType.EQUAL, TokenType.GREATER, TokenType.GREATER_OR_EQUAL, TokenType.LESS, TokenType.LESS_OR_EQUAL, TokenType.NOT_EQUAL};
        while (Arrays.binarySearch(LOGIC_OP, token.getValue(), Comparator.comparing(Enum::name)) >= 0) {
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
            h();
        }
    }

    private void h() {
        if (token.getValue() == TokenType.PLUS) {
            matchToken(TokenType.PLUS);
        } else if (token.getValue() == TokenType.MINUS) {
            matchToken(TokenType.MINUS);
        }
        j();
        while (token.getValue() == TokenType.PLUS || token.getValue() == TokenType.MINUS) {
            if (token.getValue() == TokenType.PLUS) {
                matchToken(TokenType.PLUS);
            } else {
                matchToken(TokenType.MINUS);
            }
            j();
        }
    }

    private void j() {
        k();
        while (token.getValue() == TokenType.MULTIPLY || token.getValue() == TokenType.DIVIDE) {
            if (token.getValue() == TokenType.MULTIPLY) {
                matchToken(TokenType.MULTIPLY);
            } else {
                matchToken(TokenType.DIVIDE);
            }
            k();
        }
    }

    private void k() {
        if (token.getValue() == TokenType.ID) {
            matchToken(TokenType.ID);
        } else if (token.getValue() == TokenType.NUMERAL_CONSTANT) {
            matchToken(TokenType.NUMERAL_CONSTANT);
        } else if (token.getValue() == TokenType.STRING_CONSTANT) {
            matchToken(TokenType.STRING_CONSTANT);
        } else if (token.getValue() == TokenType.BOOL) {
            matchToken(TokenType.BOOL);
        } else {
            matchToken(TokenType.OPEN_BRACE);
            e();
            matchToken(TokenType.CLOSE_BRACE);
        }
    }

    private void l() {
        if (token.getValue() == TokenType.BEGIN) {
            matchToken(TokenType.BEGIN);
            while (Arrays.binarySearch(FIRST_C, token.getValue(), Comparator.comparing(Enum::name)) >= 0) {
                c();
            }
            matchToken(TokenType.END);
        } else {
            c();
        }
    }

}
