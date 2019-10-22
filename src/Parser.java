import java.util.Arrays;
import java.util.Comparator;

/**
 * Analisador sintatico - Implementa a gramatica da linguagem. Cada simbolo nao terminal
 * tem seu metodo e cada simbolo terminal eh casado com o matchToken.
 * <p>
 * S  =>  {D}main {C} end
 * D  =>  integer J|
 * boolean J|
 * string J|
 * byte J|
 * const id=[-]constant;
 * J  =>  id[=[-]constant]{,id[=[-]constant]};
 * C  =>  id=E;|
 * write K|
 * writeln K|
 * readln"("id")";|
 * while"("E")" L|
 * if"("E")" then L [else L]|
 * ;
 * K  =>  "("[E{,E}]")");
 * E  =>  F{(==|!=|<|>|<=|>=)F}
 * F  =>  [+|-]G{(+|-|or)G}
 * G  =>  H{(*|/|and)H}
 * H  =>  id|
 * constant|
 * "("E")"|
 * not H
 * L  =>  C|
 * begin {C} end
 *
 * @author Alefe Lucas
 * @author Gabriella Mara
 * @author Ricardo Sena
 */
@SuppressWarnings("WeakerAccess")
public class Parser {

    private Lexer lexer;
    private Token token;
    private Token matchedToken;
    private static final TokenType[] FIRST_D = {TokenType.BOOLEAN, TokenType.BYTE, TokenType.CONST, TokenType.INTEGER, TokenType.STRING};
    private static final TokenType[] FIRST_C = {TokenType.ID, TokenType.IF, TokenType.READLN, TokenType.SEMICOLON, TokenType.WHILE, TokenType.WRITE, TokenType.WRITELN};
    private static final TokenType[] FIRST_E = {TokenType.CONSTANT, TokenType.ID, TokenType.MINUS, TokenType.NOT, TokenType.OPEN_BRACE, TokenType.PLUS};
    private static final DataType[] INTEGER_CONSTANTS = {DataType.BYTE, DataType.INTEGER};

    /**
     * Construtor do analisador sintatico - cria e guarda uma instancia do analisador lexico;
     *
     * @param source string contendo o codigo fonte
     */
    public Parser(String source) {
        lexer = new Lexer(source);
    }

    /**
     * "Programa principal" do analisador sintatico - obtem o primeiro
     * token do analisador lexico e chama o simbolo inicial.
     */
    public void parse() {
        try {
            token = lexer.next();
            s();
        } catch (NullPointerException ex) {
            System.err.printf("%d:fim de arquivo nao esperado.\n", lexer.getLine());
            System.exit(1);
        }
    }

    /**
     * "Casa token" - verifica se o token atual eh o token esperado pela gramatica.
     *
     * @param expectedToken token esperado
     */
    private void matchToken(TokenType expectedToken) {
        if (expectedToken.equals(token.getValue())) {
            matchedToken = token;
            token = lexer.next();
        } else {
            System.err.printf("%d:token nao esperado [%s]\n", lexer.getLine(), token.getKey());
            System.exit(1);
        }
    }

    /**
     * Verifica se o dado tipo de token pertence a dada lista ordenada de tokens;
     * Usa pesquisa binaria.
     *
     * @param token         token chave de busca
     * @param orderedByName lista ordenada de token
     * @return true se presente, false caso contrario
     */
    private static boolean in(Enum token, Enum[] orderedByName) {
        return Arrays.binarySearch(orderedByName, token, Comparator.comparing(Enum::name)) >= 0;
    }

    /**
     * S  =>  {D}main {C} end
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

    /**
     * D  =>    integer J|
     * boolean J|
     * string J|
     * byte J|
     * const id=[-]constant;
     */
    private void d() {
        if (token.getValue() == TokenType.INTEGER) {
            matchToken(TokenType.INTEGER);
            j(DataType.INTEGER);//{J.tipo := INTEGER}
        } else if (token.getValue() == TokenType.STRING) {
            matchToken(TokenType.STRING);
            j(DataType.STRING);//{J.tipo := STRING}
        } else if (token.getValue() == TokenType.BOOLEAN) {
            matchToken(TokenType.BOOLEAN);
            j(DataType.BOOLEAN);//{J.tipo := BOOLEAN}
        } else if (token.getValue() == TokenType.BYTE) {
            matchToken(TokenType.BYTE);
            j(DataType.BYTE);//{J.tipo := BYTE}
        } else {
            matchToken(TokenType.CONST);
            matchToken(TokenType.ID);

            /**
             * {
             *    if(id.classe == vazia)
             *       id.classe := classe-const;
             *    else ERRO;
             * }
             */
            TokenID id = (TokenID) matchedToken;
            if (id.getKlass() == null) {
                id.setKlass(IdClass.CONST);
            } else {
                System.err.printf("%d:identificador ja declarado [%s]\n", lexer.getLine(), matchedToken.getKey());
                System.exit(1);
            }

            matchToken(TokenType.ASSIGN);
            TokenConstant constant;
            if (token.getValue() == TokenType.CONSTANT) {
                matchToken(TokenType.CONSTANT);

                /**
                 * {
                 *    if(constant.tipo == INTEGER && constant.val >= 0 && constant.val <= 255) constant.tipo := BYTE;
                 * }
                 */
                constant = (TokenConstant) matchedToken;
                if (constant.getType() == DataType.INTEGER) {
                    short constantVal = (Short) constant.getConstant();
                    if (constantVal >= 0 && constantVal <= 255) {
                        constant.setType(DataType.BYTE);
                    }
                }

            } else {
                matchToken(TokenType.MINUS);
                matchToken(TokenType.CONSTANT);

                /**
                 * {
                 *    if(constant.tipo NOT IN(BYTE, INTEGER) ERRO;
                 *    else
                 *       constant.tipo := INTEGER;
                 * }
                 */
                constant = (TokenConstant) matchedToken;
                if (!in(constant.getType(), INTEGER_CONSTANTS)) {
                    System.err.printf("%d:classe de identificador incompatível [%s]\n", lexer.getLine(), matchedToken.getKey());
                    System.exit(1);
                } else {
                    constant.setType(DataType.INTEGER);
                }

            }

            //{id.tipo := constant.tipo}
            id.setType(constant.getType());

            matchToken(TokenType.SEMICOLON);
        }
    }

    /**
     * J  =>  id[=[-]constant]{,id[=[-]constant]};
     */
    private void j(DataType jType) {
        matchToken(TokenType.ID);

        /**
         * {
         *    id1.tipo := J.tipo;
         *    if(id1.classe == vazia)
         *        id1.classe := classe-var;
         *    else ERRO;
         * }
         */
        TokenID id1 = (TokenID) matchedToken;
        id1.setType(jType);
        if (id1.getKlass() == null) {
            id1.setKlass(IdClass.VAR);
        } else {
            System.err.printf("%d:identificador ja declarado [%s]\n", lexer.getLine(), matchedToken.getKey());
            System.exit(1);
        }

        if (token.getValue() == TokenType.ASSIGN) {
            matchToken(TokenType.ASSIGN);
            TokenConstant constant;
            if (token.getValue() == TokenType.CONSTANT) {
                matchToken(TokenType.CONSTANT);

                /**
                 * {
                 *    if(constant.tipo == INTEGER && constant.val >= 0 && constant.val <= 255) constant.tipo := BYTE;
                 * }
                 */
                constant = (TokenConstant) matchedToken;
                if (constant.getType() == DataType.INTEGER) {
                    short constantVal = (Short) constant.getConstant();
                    if (constantVal >= 0 && constantVal <= 255) {
                        constant.setType(DataType.BYTE);
                    }
                }
            } else {
                matchToken(TokenType.MINUS);
                matchToken(TokenType.CONSTANT);

                /**
                 * {
                 *    if(constant.tipo NOT IN(BYTE, INTEGER) ERRO;
                 *    else
                 *       constant.tipo := INTEGER;
                 * }
                 */
                constant = (TokenConstant) matchedToken;
                if (!in(constant.getType(), INTEGER_CONSTANTS)) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine(), matchedToken.getKey());
                    System.exit(1);
                } else {
                    constant.setType(DataType.INTEGER);
                }
            }

            //{if(id1.tipo != constant.tipo) ERRO;}
            if(id1.getType() != constant.getType()){
                System.err.printf("%d:classe de identificador incompatível [%s]\n", lexer.getLine(), matchedToken.getKey());
                System.exit(1);
            }

        }
        while (token.getValue() == TokenType.COMMA) {
            matchToken(TokenType.COMMA);
            matchToken(TokenType.ID);

            /**
             *  {
             *    id2.tipo := J.tipo;
             *    if(id2.classe == vazia)
             *      id2.classe := classe-var;
             *    else ERRO;
             * }
             */
            TokenID id2 = (TokenID) matchedToken;
            id2.setType(jType);
            if(id2.getKlass() == null){
                id2.setKlass(IdClass.VAR);
            } else {
                System.err.printf("%d:identificador ja declarado [%s]\n", lexer.getLine(), matchedToken.getKey());
                System.exit(1);
            }

            if (token.getValue() == TokenType.ASSIGN) {
                matchToken(TokenType.ASSIGN);

                TokenConstant constant;
                if (token.getValue() == TokenType.CONSTANT) {
                    matchToken(TokenType.CONSTANT);

                    /**
                     * {
                     *    if(constant.tipo == INTEGER && constant.val >= 0 && constant.val <= 255) constant.tipo := BYTE;
                     * }
                     */
                    constant = (TokenConstant) matchedToken;
                    if (constant.getType() == DataType.INTEGER) {
                        short constantVal = (Short) constant.getConstant();
                        if (constantVal >= 0 && constantVal <= 255) {
                            constant.setType(DataType.BYTE);
                        }
                    }
                } else {
                    matchToken(TokenType.MINUS);
                    matchToken(TokenType.CONSTANT);

                    /**
                     * {
                     *    if(constant.tipo NOT IN(BYTE, INTEGER) ERRO;
                     *    else
                     *       constant.tipo := INTEGER;
                     * }
                     */
                    constant = (TokenConstant) matchedToken;
                    if (!in(constant.getType(), INTEGER_CONSTANTS)) {
                        System.err.printf("%d:tipos incompatíveis \n", lexer.getLine(), matchedToken.getKey());
                        System.exit(1);
                    } else {
                        constant.setType(DataType.INTEGER);
                    }
                }
                //{if(id2.tipo != constant.tipo) ERRO;}
                if(id2.getType() != constant.getType()){
                    System.err.printf("%d:classe de identificador incompatível [%s]\n", lexer.getLine(), matchedToken.getKey());
                    System.exit(1);
                }
            }
        }
        matchToken(TokenType.SEMICOLON);
    }

    /**
     * C  =>  id=E;|
     * write K|
     * writeln K|
     * readln"("id")";|
     * while"("E")" L|
     * if"("E")" then L [else L]|
     * ;
     */
    private void c() {
        if (token.getValue() == TokenType.ID) {
            matchToken(TokenType.ID);

            /**
             * {
             *    if(id.classe == vazia)
             *       ERRO;
             *    else if(id.classe == classe-const)
             *       ERRO;
             * }
             */
            TokenID id = (TokenID) matchedToken;
            if(id.getKlass() == null){
                System.err.printf("%d:identificador nao declarado [%s]\n", lexer.getLine(), matchedToken.getKey());
                System.exit(1);
            } else if(id.getKlass() == IdClass.CONST){
                System.err.printf("%d:classe de identificador incompatível [%s]\n", lexer.getLine(), matchedToken.getKey());
                System.exit(1);
            }

            matchToken(TokenType.ASSIGN);
            Wrapper<DataType> eType = new Wrapper<>();
            e(eType);
            matchToken(TokenType.SEMICOLON);

            //{if(id.tipo != E.tipo) ERRO;}

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

    /**
     * K  =>  "("[E{,E}]")");
     */
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

    /**
     * E  =>  F{(==|!=|<|>|<=|>=)F}
     * @param eType
     */
    private void e(Wrapper<DataType> eType) {
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

    /**
     * F  =>  [+|-]G{(+|-|or)G}
     */
    private void f(Wrapper<DataType> fType) {
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

    /**
     * G  =>  H{(*|/|and)H}
     */
    private void g(Wrapper<DataType> gType) {
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

    /**
     * H  =>  id|
     * constant|
     * "("E")"|
     * not H
     */
    private void h(Wrapper<DataType> hType) {
        if (token.getValue() == TokenType.ID) {
            matchToken(TokenType.ID);
        } else if (token.getValue() == TokenType.CONSTANT) {
            matchToken(TokenType.CONSTANT);
        } else if (token.getValue() == TokenType.NOT) {
            matchToken(TokenType.NOT);
            matchToken(TokenType.ID);
        } else {
            matchToken(TokenType.OPEN_BRACE);
            e();
            matchToken(TokenType.CLOSE_BRACE);
        }
    }

    /**
     * L  =>  C|
     * begin {C} end
     */
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
