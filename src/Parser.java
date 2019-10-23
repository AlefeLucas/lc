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

            /*
              {
                 if(id.classe == vazia)
                    id.classe := classe-const;
                 else ERRO;
              }
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
                constant = verifyByteInterval();

            } else {
                matchToken(TokenType.MINUS);
                matchToken(TokenType.CONSTANT);

                /*
                  {
                     if(constant.tipo NOT IN(BYTE, INTEGER) ERRO;
                     else
                        constant.tipo := INTEGER;
                  }
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

    private TokenConstant verifyByteInterval() {
        TokenConstant constant;
        matchToken(TokenType.CONSTANT);

                /*
                  {
                     if(constant.tipo == INTEGER && constant.val >= 0 && constant.val <= 255) constant.tipo := BYTE;
                  }
                 */
        constant = (TokenConstant) matchedToken;
        if (constant.getType() == DataType.INTEGER) {
            short constantVal = (Short) constant.getConstant();
            if (constantVal >= 0 && constantVal <= 255) {
                constant.setType(DataType.BYTE);
            }
        }
        return constant;
    }

    /**
     * J  =>  id[=[-]constant]{,id[=[-]constant]};
     */
    private void j(DataType jType) {
        getDeclaration(jType);
        while (token.getValue() == TokenType.COMMA) {
            matchToken(TokenType.COMMA);
            getDeclaration(jType);
        }
        matchToken(TokenType.SEMICOLON);
    }

    private void getDeclaration(DataType jType) {
        matchToken(TokenType.ID);

        /*
          {
             id1.tipo := J.tipo;
             if(id1.classe == vazia)
                 id1.classe := classe-var;
             else ERRO;
          }
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
                constant = verifyByteInterval();
            } else {
                matchToken(TokenType.MINUS);
                matchToken(TokenType.CONSTANT);

                /*
                  {
                     if(constant.tipo NOT IN(BYTE, INTEGER) ERRO;
                     else
                        constant.tipo := INTEGER;
                  }
                 */
                constant = (TokenConstant) matchedToken;
                if (!in(constant.getType(), INTEGER_CONSTANTS)) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                    System.exit(1);
                } else {
                    constant.setType(DataType.INTEGER);
                }
            }

            //{if(id1.tipo != constant.tipo) ERRO;}
            if (id1.getType() != constant.getType()) {
                System.err.printf("%d:classe de identificador incompatível [%s]\n", lexer.getLine(), matchedToken.getKey());
                System.exit(1);
            }

        }
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
            TokenID id = checkVar();

            matchToken(TokenType.ASSIGN);
            Wrapper<DataType> eType = new Wrapper<>();
            e(eType);

            //{if(id.tipo != E.tipo && (id.tipo != INTEGER || E.tipo != BYTE)) ERRO}
            if (id.getType() != eType.getValue() && (id.getType() != DataType.INTEGER || eType.getValue() != DataType.BYTE)) {
                System.err.printf("%d:classe de identificador incompatível [%s]\n", lexer.getLine(), matchedToken.getKey());
                System.exit(1);
            }
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
            TokenID id = checkVar();

            matchToken(TokenType.CLOSE_BRACE);
            matchToken(TokenType.SEMICOLON);
        } else if (token.getValue() == TokenType.WHILE) {
            matchToken(TokenType.WHILE);
            logicalBraces();
            l();
        } else if (token.getValue() == TokenType.IF) {
            matchToken(TokenType.IF);
            logicalBraces();
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

    private void logicalBraces() {
        matchToken(TokenType.OPEN_BRACE);
        Wrapper<DataType> eType = new Wrapper<>();
        e(eType);

        //{if(E.tipo != BOOLEAN) ERRO}
        if (eType.getValue() != DataType.BOOLEAN) {
            System.err.printf("%d:classe de identificador incompatível [%s]\n", lexer.getLine(), matchedToken.getKey());
            System.exit(1);
        }

        matchToken(TokenType.CLOSE_BRACE);
    }

    private TokenID checkVar() {
        matchToken(TokenType.ID);

            /*
              {
                 if(id.classe == vazia)
                    ERRO;
                 else if(id.classe == classe-const)
                    ERRO;
              }
             */
        TokenID id = (TokenID) matchedToken;
        if (id.getKlass() == null) {
            System.err.printf("%d:identificador nao declarado [%s]\n", lexer.getLine(), matchedToken.getKey());
            System.exit(1);
        } else if (id.getKlass() == IdClass.CONST) {
            System.err.printf("%d:classe de identificador incompatível [%s]\n", lexer.getLine(), matchedToken.getKey());
            System.exit(1);
        }
        return id;
    }

    /**
     * K  =>  "("[E{,E}]")");
     */
    private void k() {
        matchToken(TokenType.OPEN_BRACE);
        if (in(token.getValue(), FIRST_E)) {
            Wrapper<DataType> e1Type = new Wrapper<>();
            e(e1Type);
            while (token.getValue() == TokenType.COMMA) {
                matchToken(TokenType.COMMA);
                Wrapper<DataType> e2Type = new Wrapper<>();
                e(e2Type);
            }
        }
        matchToken(TokenType.CLOSE_BRACE);

        matchToken(TokenType.SEMICOLON);
    }

    /**
     * E  =>  F{(==|!=|<|>|<=|>=)F}
     */
    private void e(Wrapper<DataType> eType) {
        Wrapper<DataType> f1Type = new Wrapper<>();
        f(f1Type);

        //{E.tipo := F1.tipo}
        eType.setValue(f1Type.getValue());

        final TokenType[] LOGIC_OP = {TokenType.EQUAL, TokenType.GREATER, TokenType.GREATER_OR_EQUAL, TokenType.LESS, TokenType.LESS_OR_EQUAL, TokenType.NOT_EQUAL};
        while (in(token.getValue(), LOGIC_OP)) {
            if (token.getValue() == TokenType.EQUAL) {
                matchToken(TokenType.EQUAL);
                Wrapper<DataType> f2Type = new Wrapper<>();
                f(f2Type);

                /*
                  {
                      switch(E.tipo)
                         case INTEGER:
                         case BYTE:
                            if(F2.tipo != INTEGER && F2.tipo != BYTE) ERRO;
                            break;
                         case STRING:
                            if(F2.tipo != STRING) ERRO;
                         case BOOLEAN:
                            if(F2.tipo != BOOLEAN) ERRO;
                  }
                 */
                switch (eType.getValue()) {
                    case INTEGER:
                    case BYTE:
                        if (f2Type.getValue() != DataType.INTEGER && f2Type.getValue() != DataType.BYTE) {
                            System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                            System.exit(1);
                        }
                        break;
                    case STRING:
                        if (f2Type.getValue() != DataType.STRING) {
                            System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                            System.exit(1);
                        }
                        break;
                    case BOOLEAN:
                        if (f2Type.getValue() != DataType.BOOLEAN) {
                            System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                            System.exit(1);
                        }
                }
            } else if (token.getValue() == TokenType.NOT_EQUAL) {
                matchToken(TokenType.NOT_EQUAL);
                Wrapper<DataType> f2Type = new Wrapper<>();
                f(f2Type);

                /*
                  {
                      switch(E.tipo)
                         case INTEGER:
                         case BYTE:
                            if(F2.tipo != INTEGER && F2.tipo != BYTE) ERRO;
                            break;
                         case STRING:
                            ERRO;
                         case BOOLEAN:
                            if(F2.tipo != BOOLEAN) ERRO;
                  }
                 */
                switch (eType.getValue()) {
                    case INTEGER:
                    case BYTE:
                        if (f2Type.getValue() != DataType.INTEGER && f2Type.getValue() != DataType.BYTE) {
                            System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                            System.exit(1);
                        }
                        break;
                    case STRING:
                        System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                        System.exit(1);
                        break;
                    case BOOLEAN:
                        if (f2Type.getValue() != DataType.BOOLEAN) {
                            System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                            System.exit(1);
                        }
                }
            } else if (token.getValue() == TokenType.LESS) {
                matchToken(TokenType.LESS);
                checkOnlyNumber(eType);
            } else if (token.getValue() == TokenType.LESS_OR_EQUAL) {
                matchToken(TokenType.LESS_OR_EQUAL);
                checkOnlyNumber(eType);
            } else if (token.getValue() == TokenType.GREATER) {
                matchToken(TokenType.GREATER);
                checkOnlyNumber(eType);
            } else {
                matchToken(TokenType.GREATER_OR_EQUAL);
                checkOnlyNumber(eType);
            }

            //{E.tipo := BOOLEAN}
            eType.setValue(DataType.BOOLEAN);
        }
    }

    private void checkOnlyNumber(Wrapper<DataType> eType) {
        Wrapper<DataType> f2Type = new Wrapper<>();
        f(f2Type);

                /*
                  {
                      switch(E.tipo)
                         case INTEGER:
                         case BYTE:
                            if(F2.tipo != INTEGER && F2.tipo != BYTE) ERRO;
                            break;
                         case STRING:
                         case BOOLEAN:
                            ERRO;
                  }
                 */
        switch (eType.getValue()) {
            case INTEGER:
            case BYTE:
                if (f2Type.getValue() != DataType.INTEGER && f2Type.getValue() != DataType.BYTE) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                    System.exit(1);
                }
                break;
            case STRING:
            case BOOLEAN:
                System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                System.exit(1);
                break;
        }
    }

    /**
     * F  =>  [+|-]G{(+|-|or)G}
     */
    private void f(Wrapper<DataType> fType) {
        Wrapper<DataType> g1Type;

        if (token.getValue() == TokenType.PLUS) {
            matchToken(TokenType.PLUS);
            g1Type = new Wrapper<>();
            g(g1Type);

            //{if(G1.tipo NOT IN(BYTE, INTEGER)) ERRO;}
            if (!in(g1Type.getValue(), INTEGER_CONSTANTS)) {
                System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                System.exit(1);
            }
        } else if (token.getValue() == TokenType.MINUS) {
            matchToken(TokenType.MINUS);
            g1Type = new Wrapper<>();
            g(g1Type);

            /*
              {
                 if(G1.tipo == BYTE)
                    G1.tipo := INTEGER;
                 else if(G1.tipo != INTEGER)
                    ERRO;
              }
             */
            if (g1Type.getValue() == DataType.BYTE) {
                g1Type.setValue(DataType.INTEGER);
            } else if (g1Type.getValue() != DataType.INTEGER) {
                System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                System.exit(1);
            }
        } else {
            g1Type = new Wrapper<>();
            g(g1Type);
        }
        //{F.tipo := G1.tipo}
        fType.setValue(g1Type.getValue());

        final TokenType[] OP = {TokenType.MINUS, TokenType.OR, TokenType.PLUS};
        while (in(token.getValue(), OP)) {
            Wrapper<DataType> g2Type;
            if (token.getValue() == TokenType.PLUS) {
                matchToken(TokenType.PLUS);
                //{if(F.tipo NOT IN(BYTE, INTEGER, STRING)) ERRO}
                if (!in(fType.getValue(), INTEGER_CONSTANTS) && fType.getValue() != DataType.STRING) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                    System.exit(1);
                }

                g2Type = new Wrapper<>();
                g(g2Type);

                /*
                  {
                      if(F.tipo IN(BYTE, INTEGER)
                         if(G2.tipo NOT IN(BYTE, INTEGER)) ERRO;
                         else if(G2.tipo == INTEGER) F.tipo := INTEGER;
                      else if(G2.tipo != STRING) ERRO;
                  }
                 */
                if (in(fType.getValue(), INTEGER_CONSTANTS)) {
                    if (!in(g2Type.getValue(), INTEGER_CONSTANTS)) {
                        System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                        System.exit(1);
                    } else if (g2Type.getValue() == DataType.INTEGER) {
                        fType.setValue(DataType.INTEGER);
                    }
                } else if (g2Type.getValue() != DataType.STRING) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                    System.exit(1);
                }

            } else if (token.getValue() == TokenType.MINUS) {
                matchToken(TokenType.MINUS);

                //{if(F.tipo NOT IN(BYTE, INTEGER)) ERRO}
                if (!in(fType.getValue(), INTEGER_CONSTANTS)) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                    System.exit(1);
                }

                g2Type = new Wrapper<>();
                g(g2Type);

                /*
                  {
                     if(G2.tipo NOT IN(BYTE, INTEGER)) ERRO;
                     else if(G2.tipo == INTEGER) F.tipo := INTEGER;
                  }
                 */
                if (!in(g2Type.getValue(), INTEGER_CONSTANTS)) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                    System.exit(1);
                } else if (g2Type.getValue() == DataType.INTEGER) fType.setValue(DataType.INTEGER);
            } else {
                matchToken(TokenType.OR);

                //{if(F.tipo != BOOLEAN) ERRO}
                if (fType.getValue() != DataType.BOOLEAN) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                    System.exit(1);
                }

                g2Type = new Wrapper<>();
                g(g2Type);

                //{if(G2.tipo != BOOLEAN) ERRO}
                if (g2Type.getValue() != DataType.BOOLEAN) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                    System.exit(1);
                }

            }
        }
    }

    /**
     * G  =>  H{(*|/|and)H}
     */
    private void g(Wrapper<DataType> gType) {
        Wrapper<DataType> h1Type = new Wrapper<>();
        h(h1Type);

        //{G.tipo := H1.tipo}
        gType.setValue(h1Type.getValue());

        final TokenType[] OP = {TokenType.AND, TokenType.DIVIDE, TokenType.MULTIPLY};
        while (in(token.getValue(), OP)) {
            if (token.getValue() == TokenType.MULTIPLY) {
                matchToken(TokenType.MULTIPLY);

                //{if(G.tipo NOT IN(BYTE, INTEGER)) ERRO}
                if (!in(gType.getValue(), INTEGER_CONSTANTS)) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                    System.exit(1);
                }
                Wrapper<DataType> h2Type = new Wrapper<>();
                h(h2Type);

                /*
                  {
                     if(H2.tipo NOT IN(BYTE, INTEGER)) ERRO;
                     else if(H2.tipo == INTEGER) G.tipo := INTEGER;
                  }
                 */
                if (!in(h2Type.getValue(), INTEGER_CONSTANTS)) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                    System.exit(1);
                } else if (h2Type.getValue() == DataType.INTEGER) gType.setValue(DataType.INTEGER);
            } else if (token.getValue() == TokenType.DIVIDE) {
                matchToken(TokenType.DIVIDE);

                /*
                   {
                      if(G.tipo == BYTE)
                         G.tipo := INTEGER;
                      else if(G.tipo != INTEGER)
                         ERRO;
                   }
                 */
                if (gType.getValue() == DataType.BYTE) {
                    gType.setValue(DataType.INTEGER);
                } else if (gType.getValue() != DataType.INTEGER) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                    System.exit(1);
                }

                Wrapper<DataType> h2Type = new Wrapper<>();
                h(h2Type);

                /*
                  {
                     if(H2.tipo == BYTE)
                        H2.tipo := INTEGER;
                     else if(H2.tipo != INTEGER)
                        ERRO;
                  }
                 */
                if (h2Type.getValue() == DataType.BYTE) {
                    h2Type.setValue(DataType.INTEGER);
                } else if (h2Type.getValue() != DataType.INTEGER) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                    System.exit(1);
                }
            } else {
                matchToken(TokenType.AND);

                //{if(G.tipo != BOOLEAN) ERRO}
                if (gType.getValue() != DataType.BOOLEAN) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                    System.exit(1);
                }

                Wrapper<DataType> h2Type = new Wrapper<>();
                h(h2Type);

                //{if(H2.tipo != BOOLEAN) ERRO}
                if (h2Type.getValue() != DataType.BOOLEAN) {
                    System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                    System.exit(1);
                }
            }

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

            /*
              {
                 if(id.classe == vazia)
                    ERRO;
                 else
                    H.tipo := id.tipo;
              }
             */
            TokenID id = (TokenID) matchedToken;
            if (id.getKlass() == null) {
                System.err.printf("%d:identificador nao declarado [%s]\n", lexer.getLine(), matchedToken.getKey());
                System.exit(1);
            } else {
                hType.setValue(id.getType());
            }
        } else if (token.getValue() == TokenType.CONSTANT) {
            matchToken(TokenType.CONSTANT);

            /*
               {
                  if(constant.tipo == INTEGER && constant.val >= 0 && constant.val <= 255)
                     constant.tipo := BYTE;
                  H.tipo := constant.tipo;
               }
             */
            TokenConstant constant = (TokenConstant) matchedToken;
            if (constant.getType() == DataType.INTEGER) {
                short constantVal = (Short) constant.getConstant();
                if (constantVal >= 0 && constantVal <= 255) {
                    constant.setType(DataType.BYTE);
                }
            }
            hType.setValue(constant.getType());
        } else if (token.getValue() == TokenType.NOT) {
            matchToken(TokenType.NOT);
            Wrapper<DataType> h1Type = new Wrapper<>();
            h(h1Type);

            /*
              {
                 if(H1.tipo != BOOLEAN)
                    ERRO;
                  else
                    H.tipo := BOOLEAN;
              }
             */
            if (h1Type.getValue() != DataType.BOOLEAN) {
                System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
                System.exit(1);
            } else {
                hType.setValue(DataType.BOOLEAN);
            }
        } else {
            matchToken(TokenType.OPEN_BRACE);
            Wrapper<DataType> eType = new Wrapper<>();
            e(eType);

            //{H.tipo := E.tipo}
            hType.setValue(eType.getValue());

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
