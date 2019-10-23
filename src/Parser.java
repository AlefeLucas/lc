import java.util.Arrays;
import java.util.Comparator;

/**
 * Analisador sintatico - Implementa a gramatica da linguagem. Cada simbolo nao terminal
 * tem seu metodo e cada simbolo terminal eh casado com o matchToken.
 * <p>
 *     Analisador semântico - performa os esquemas de traducao enumerados e denotados por r#
 * <p>
 * S  =>  {D}main {C} end
 * D  =>  integer 1 J|
 * boolean 2 J {}|
 * string 3 J|
 * byte 4 J|
 * const id 5 P 8;
 * J  =>  9 M1{,10 M2};
 * M  =>  id 11 [P 12]
 * P  =>  =(constant 6| - constant 7)
 * C  =>  id 13 =E 14; |
 * write K|
 * writeln K|
 * readln”(“id 13”)”;|
 * while N L|
 * if N then L [else L]|
 * ;
 * N  =>  ”(“E 15”)”
 * K  =>  ”(“[E1{,E2}]”)”);
 * E  =>  F1 16 {(== F2 17|!= F2 18|< F2 19|> F2 19 |<= F2 19|>= F2 19) 20}
 * F  =>  (+ G1 21 |- G1 22| G1)23 {(+ 24 G2 25|- 26 G2 27|or 28 G2 29)}
 * G  =>  H1 30 {(* 31 H2 32|/ 33 H2 34|and 35 H2 36)}
 * H  =>  id 37|
 * constant 6 38|
 * “(“E”)” 39|
 * not H1 40
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
     * D  =>  integer J|
     * boolean J|
     * string J|
     * byte J|
     * const id P;
     */
    private void d() {
        if (token.getValue() == TokenType.INTEGER) {
            matchToken(TokenType.INTEGER);
            j(DataType.INTEGER);//1
        } else if (token.getValue() == TokenType.STRING) {
            matchToken(TokenType.STRING);
            j(DataType.STRING);//2
        } else if (token.getValue() == TokenType.BOOLEAN) {
            matchToken(TokenType.BOOLEAN);
            j(DataType.BOOLEAN);//3
        } else if (token.getValue() == TokenType.BYTE) {
            matchToken(TokenType.BYTE);
            j(DataType.BYTE);//4
        } else {
            matchToken(TokenType.CONST);
            matchToken(TokenType.ID);

            //5
            TokenID id = (TokenID) matchedToken;
            r5(id);

            p();

            //8
            TokenConstant constant = (TokenConstant) matchedToken;
            r8(id, constant);

            matchToken(TokenType.SEMICOLON);
        }
    }

    /**
     * P  =>  =(constant | - constant)
     */
    private void p() {
        matchToken(TokenType.ASSIGN);
        TokenConstant constant;
        if (token.getValue() == TokenType.CONSTANT) {
            matchToken(TokenType.CONSTANT);

            //6
            constant = (TokenConstant) matchedToken;
            r6(constant);

        } else {
            matchToken(TokenType.MINUS);
            matchToken(TokenType.CONSTANT);

            //7
            constant = (TokenConstant) matchedToken;
            r7(constant);
        }
    }

    /**
     * J  =>  M{,M};
     */
    private void j(DataType jType) {
        m(jType); //9
        while (token.getValue() == TokenType.COMMA) {
            matchToken(TokenType.COMMA);
            m(jType); //10
        }
        matchToken(TokenType.SEMICOLON);
    }

    /**
     * M  =>  id[P]
     */
    private void m(DataType mType) {
        matchToken(TokenType.ID);

        TokenID id = (TokenID) matchedToken;
        //11
        r11(mType, id);

        if (token.getValue() == TokenType.ASSIGN) {
            p();

            //12
            TokenConstant constant = (TokenConstant) matchedToken;
            r12(id, constant);
        }
    }

    /**
     * C  =>  id=E;|
     * write K|
     * writeln K|
     * readln"("id")";|
     * while N L|
     * if N then L [else L]|
     * ;
     */
    private void c() {
        if (token.getValue() == TokenType.ID) {
            matchToken(TokenType.ID);

            //13
            TokenID id = (TokenID) matchedToken;
            r13(id);

            matchToken(TokenType.ASSIGN);
            Wrapper<DataType> eType = new Wrapper<>();
            e(eType);

            //14
            r14(id, eType);

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

            //13
            TokenID id = (TokenID) matchedToken;
            r13(id);

            matchToken(TokenType.CLOSE_BRACE);
            matchToken(TokenType.SEMICOLON);
        } else if (token.getValue() == TokenType.WHILE) {
            matchToken(TokenType.WHILE);
            n();
            l();
        } else if (token.getValue() == TokenType.IF) {
            matchToken(TokenType.IF);
            n();
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
     * N  =>  ”(“E”)”
     */
    private void n() {
        matchToken(TokenType.OPEN_BRACE);
        Wrapper<DataType> eType = new Wrapper<>();
        e(eType);

        //15
        r15(eType);

        matchToken(TokenType.CLOSE_BRACE);
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
     * E  =>  F{(== F|!= F|< F|> F|<= F|>= F)}
     */
    private void e(Wrapper<DataType> eType) {
        Wrapper<DataType> f1Type = new Wrapper<>();
        f(f1Type);

        //16
        r16(eType, f1Type);

        final TokenType[] LOGIC_OP = {TokenType.EQUAL, TokenType.GREATER, TokenType.GREATER_OR_EQUAL, TokenType.LESS, TokenType.LESS_OR_EQUAL, TokenType.NOT_EQUAL};
        while (in(token.getValue(), LOGIC_OP)) {
            if (token.getValue() == TokenType.EQUAL) {
                matchToken(TokenType.EQUAL);
                Wrapper<DataType> f2Type = new Wrapper<>();
                f(f2Type);

                //17
                r17(eType, f2Type);
            } else if (token.getValue() == TokenType.NOT_EQUAL) {
                matchToken(TokenType.NOT_EQUAL);
                Wrapper<DataType> f2Type = new Wrapper<>();
                f(f2Type);

                //18
                r18(eType, f2Type);
            } else if (token.getValue() == TokenType.LESS) {
                matchToken(TokenType.LESS);

                Wrapper<DataType> f2Type = new Wrapper<>();
                f(f2Type);
                r19(eType, f2Type);
            } else if (token.getValue() == TokenType.LESS_OR_EQUAL) {
                matchToken(TokenType.LESS_OR_EQUAL);

                Wrapper<DataType> f2Type = new Wrapper<>();
                f(f2Type);
                r19(eType, f2Type);
            } else if (token.getValue() == TokenType.GREATER) {
                matchToken(TokenType.GREATER);

                Wrapper<DataType> f2Type = new Wrapper<>();
                f(f2Type);
                r19(eType, f2Type);
            } else {
                matchToken(TokenType.GREATER_OR_EQUAL);

                Wrapper<DataType> f2Type = new Wrapper<>();
                f(f2Type);
                r19(eType, f2Type);
            }

            //{E.tipo := BOOLEAN}
            r20(eType);
        }
    }

    /**
     * F  =>  (+ G|- G| G){(+ G|- G|or G)}
     */
    private void f(Wrapper<DataType> fType) {
        Wrapper<DataType> g1Type;

        if (token.getValue() == TokenType.PLUS) {
            matchToken(TokenType.PLUS);
            g1Type = new Wrapper<>();
            g(g1Type);

            //21
            r21(g1Type);
        } else if (token.getValue() == TokenType.MINUS) {
            matchToken(TokenType.MINUS);
            g1Type = new Wrapper<>();
            g(g1Type);

            //22
            r22(g1Type);
        } else {
            g1Type = new Wrapper<>();
            g(g1Type);
        }
        //23
        r23(fType, g1Type);

        final TokenType[] OP = {TokenType.MINUS, TokenType.OR, TokenType.PLUS};
        while (in(token.getValue(), OP)) {
            Wrapper<DataType> g2Type;
            if (token.getValue() == TokenType.PLUS) {
                matchToken(TokenType.PLUS);
                //24
                r24(fType);

                g2Type = new Wrapper<>();
                g(g2Type);

                //25
                r25(fType, g2Type);
            } else if (token.getValue() == TokenType.MINUS) {
                matchToken(TokenType.MINUS);

                //26
                r26(fType);

                g2Type = new Wrapper<>();
                g(g2Type);

                //27
                r27(fType, g2Type);
            } else {
                matchToken(TokenType.OR);

                //28
                r28(fType);

                g2Type = new Wrapper<>();
                g(g2Type);

                //29
                r29(g2Type);
            }
        }
    }

    /**
     * G  =>  H{(* H|/ H|and H)}
     */
    private void g(Wrapper<DataType> gType) {
        Wrapper<DataType> h1Type = new Wrapper<>();
        h(h1Type);

        //30
        r30(gType, h1Type);

        final TokenType[] OP = {TokenType.AND, TokenType.DIVIDE, TokenType.MULTIPLY};
        while (in(token.getValue(), OP)) {
            if (token.getValue() == TokenType.MULTIPLY) {
                matchToken(TokenType.MULTIPLY);

                //31
                r31(gType);
                Wrapper<DataType> h2Type = new Wrapper<>();
                h(h2Type);

                //32
                r32(gType, h2Type);
            } else if (token.getValue() == TokenType.DIVIDE) {
                matchToken(TokenType.DIVIDE);

                //33
                r33(gType);

                Wrapper<DataType> h2Type = new Wrapper<>();
                h(h2Type);

                //34
                r34(h2Type);
            } else {
                matchToken(TokenType.AND);

                //35
                r35(gType);

                Wrapper<DataType> h2Type = new Wrapper<>();
                h(h2Type);

                //36
                r36(h2Type);
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

            //37
            TokenID id = (TokenID) matchedToken;
            r37(hType, id);
        } else if (token.getValue() == TokenType.CONSTANT) {
            matchToken(TokenType.CONSTANT);

            //6
            TokenConstant constant = (TokenConstant) matchedToken;
            r6(constant);

            //38
            r38(hType, constant);
        } else if (token.getValue() == TokenType.OPEN_BRACE) {
            matchToken(TokenType.OPEN_BRACE);
            Wrapper<DataType> eType = new Wrapper<>();
            e(eType);

            //39
            r39(hType, eType);

            matchToken(TokenType.CLOSE_BRACE);
        } else {
            matchToken(TokenType.NOT);
            Wrapper<DataType> h1Type = new Wrapper<>();
            h(h1Type);

            //40
            r40(hType, h1Type);
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

    private void r5(TokenID id) {
        if (id.getKlass() == null) {
            id.setKlass(IdClass.CONST);
        } else {
            errorIdDuplicated();
        }
    }

    private void r6(TokenConstant constant) {
        if (constant.getType() == DataType.INTEGER) {
            short constantVal = (Short) constant.getConstant();
            if (constantVal >= 0 && constantVal <= 255) {
                constant.setType(DataType.BYTE);
            }
        }
    }

    private void r7(TokenConstant constant) {
        if (!in(constant.getType(), INTEGER_CONSTANTS)) {
            errorIncompatibleType();
        } else {
            constant.setType(DataType.INTEGER);
        }
    }


    private void r8(TokenID id, TokenConstant constant) {
        id.setType(constant.getType());
    }

    private void r11(DataType mType, TokenID id) {
        id.setType(mType);
        if (id.getKlass() == null) {
            id.setKlass(IdClass.VAR);
        } else {
            errorIdDuplicated();
        }
    }

    private void r12(TokenID id, TokenConstant constant) {
        if (id.getType() != constant.getType()) {
            errorIncompatibleId();
        }
    }

    private void r13(TokenID id) {
        if (id.getKlass() == null) {
            errorIdNotDeclared();
        } else if (id.getKlass() == IdClass.CONST) {
            errorIncompatibleId();
        }
    }

    private void r14(TokenID id, Wrapper<DataType> eType) {
        if (id.getType() != eType.getValue() && (id.getType() != DataType.INTEGER || eType.getValue() != DataType.BYTE)) {
            errorIncompatibleId();
        }
    }

    private void r15(Wrapper<DataType> eType) {
        if (eType.getValue() != DataType.BOOLEAN) {
            errorIncompatibleId();
        }
    }

    private void r16(Wrapper<DataType> eType, Wrapper<DataType> f1Type) {
        eType.setValue(f1Type.getValue());
    }

    private void r17(Wrapper<DataType> eType, Wrapper<DataType> f2Type) {
        switch (eType.getValue()) {
            case INTEGER:
            case BYTE:
                if (f2Type.getValue() != DataType.INTEGER && f2Type.getValue() != DataType.BYTE) {
                    errorIncompatibleType();
                }
                break;
            case STRING:
                if (f2Type.getValue() != DataType.STRING) {
                    errorIncompatibleType();
                }
                break;
            case BOOLEAN:
                if (f2Type.getValue() != DataType.BOOLEAN) {
                    errorIncompatibleType();
                }
        }
    }

    private void r18(Wrapper<DataType> eType, Wrapper<DataType> f2Type) {
        switch (eType.getValue()) {
            case INTEGER:
            case BYTE:
                if (f2Type.getValue() != DataType.INTEGER && f2Type.getValue() != DataType.BYTE) {
                    errorIncompatibleType();
                }
                break;
            case STRING:
                errorIncompatibleType();
                break;
            case BOOLEAN:
                if (f2Type.getValue() != DataType.BOOLEAN) {
                    errorIncompatibleType();
                }
        }
    }

    private void r19(Wrapper<DataType> eType, Wrapper<DataType> f2Type) {
        switch (eType.getValue()) {
            case INTEGER:
            case BYTE:
                if (f2Type.getValue() != DataType.INTEGER && f2Type.getValue() != DataType.BYTE) {
                    errorIncompatibleType();
                }
                break;
            case STRING:
            case BOOLEAN:
                errorIncompatibleType();
                break;
        }
    }

    private void r20(Wrapper<DataType> eType) {
        eType.setValue(DataType.BOOLEAN);
    }

    private void r21(Wrapper<DataType> g1Type) {
        if (!in(g1Type.getValue(), INTEGER_CONSTANTS)) {
            errorIncompatibleType();
        }
    }

    private void r22(Wrapper<DataType> g1Type) {
        if (g1Type.getValue() == DataType.BYTE) {
            g1Type.setValue(DataType.INTEGER);
        } else if (g1Type.getValue() != DataType.INTEGER) {
            errorIncompatibleType();
        }
    }

    private void r23(Wrapper<DataType> fType, Wrapper<DataType> g1Type) {
        fType.setValue(g1Type.getValue());
    }

    private void r24(Wrapper<DataType> fType) {
        if (!in(fType.getValue(), INTEGER_CONSTANTS) && fType.getValue() != DataType.STRING) {
            errorIncompatibleType();
        }
    }

    private void r25(Wrapper<DataType> fType, Wrapper<DataType> g2Type) {
        if (in(fType.getValue(), INTEGER_CONSTANTS)) {
            if (!in(g2Type.getValue(), INTEGER_CONSTANTS)) {
                errorIncompatibleType();
            } else if (g2Type.getValue() == DataType.INTEGER) {
                fType.setValue(DataType.INTEGER);
            }
        } else if (g2Type.getValue() != DataType.STRING) {
            errorIncompatibleType();
        }
    }

    private void r26(Wrapper<DataType> fType) {
        if (!in(fType.getValue(), INTEGER_CONSTANTS)) {
            errorIncompatibleType();
        }
    }

    private void r27(Wrapper<DataType> fType, Wrapper<DataType> g2Type) {
        if (!in(g2Type.getValue(), INTEGER_CONSTANTS)) {
            errorIncompatibleType();
        } else if (g2Type.getValue() == DataType.INTEGER) {
            fType.setValue(DataType.INTEGER);
        }
    }

    private void r28(Wrapper<DataType> fType) {
        if (fType.getValue() != DataType.BOOLEAN) {
            errorIncompatibleType();
        }
    }

    private void r29(Wrapper<DataType> g2Type) {
        if (g2Type.getValue() != DataType.BOOLEAN) {
            errorIncompatibleType();
        }
    }

    private void r30(Wrapper<DataType> gType, Wrapper<DataType> h1Type) {
        gType.setValue(h1Type.getValue());
    }

    private void r31(Wrapper<DataType> gType) {
        if (!in(gType.getValue(), INTEGER_CONSTANTS)) {
            errorIncompatibleType();
        }
    }

    private void r32(Wrapper<DataType> gType, Wrapper<DataType> h2Type) {
        if (!in(h2Type.getValue(), INTEGER_CONSTANTS)) {
            errorIncompatibleType();
        } else if (h2Type.getValue() == DataType.INTEGER) {
            gType.setValue(DataType.INTEGER);
        }
    }

    private void r33(Wrapper<DataType> gType) {
        if (gType.getValue() == DataType.BYTE) {
            gType.setValue(DataType.INTEGER);
        } else if (gType.getValue() != DataType.INTEGER) {
            errorIncompatibleType();
        }
    }

    private void r34(Wrapper<DataType> h2Type) {
        if (h2Type.getValue() == DataType.BYTE) {
            h2Type.setValue(DataType.INTEGER);
        } else if (h2Type.getValue() != DataType.INTEGER) {
            errorIncompatibleType();
        }
    }

    private void r35(Wrapper<DataType> gType) {
        if (gType.getValue() != DataType.BOOLEAN) {
            errorIncompatibleType();
        }
    }

    private void r36(Wrapper<DataType> h2Type) {
        if (h2Type.getValue() != DataType.BOOLEAN) {
            errorIncompatibleType();
        }
    }

    private void r37(Wrapper<DataType> hType, TokenID id) {
        if (id.getKlass() == null) {
            errorIdNotDeclared();
        } else {
            hType.setValue(id.getType());
        }
    }

    private void r38(Wrapper<DataType> hType, TokenConstant constant) {
        hType.setValue(constant.getType());
    }

    private void r39(Wrapper<DataType> hType, Wrapper<DataType> eType) {
        hType.setValue(eType.getValue());
    }

    private void r40(Wrapper<DataType> hType, Wrapper<DataType> h1Type) {
        if (h1Type.getValue() != DataType.BOOLEAN) {
            errorIncompatibleType();
        } else {
            hType.setValue(DataType.BOOLEAN);
        }
    }

    private void errorIncompatibleId() {
        System.err.printf("%d:classe de identificador incompatível [%s]\n", lexer.getLine(), matchedToken.getKey());
        System.exit(1);
    }


    private void errorIdDuplicated() {
        System.err.printf("%d:identificador ja declarado [%s]\n", lexer.getLine(), matchedToken.getKey());
        System.exit(1);
    }

    private void errorIncompatibleType() {
        System.err.printf("%d:tipos incompatíveis \n", lexer.getLine());
        System.exit(1);
    }

    private void errorIdNotDeclared() {
        System.err.printf("%d:identificador nao declarado [%s]\n", lexer.getLine(), matchedToken.getKey());
        System.exit(1);
    }

}
