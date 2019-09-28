import java.util.Iterator;

/**
 *
 */
@SuppressWarnings("WeakerAccess")
public class Lexer implements Iterator<Token> {
    private final char[] source;
    private static final String symbols = "!\"&'(*)+,-./:;<=>?[]_{} \n\r\t";
    private final Register symbolTable;
    private final Register lexical;
    private int line;
    private int index;

    public Lexer(String source) {
        this.source = source.stripTrailing().replace("\r\n", "\n").toCharArray();
        this.symbolTable = SymbolTableSingleton.getInstance();
        this.lexical = LexicalSingleton.getInstance();
        this.line = 1;
    }

    public int getLine() {
        return line;
    }

    @Override
    public boolean hasNext() {
        return index < source.length;
    }

    @Override
    public Token next() {
        int state = 0;
        StringBuilder lex = new StringBuilder();
        Token token = null;

        while (state != 3) {
            if (index <= source.length) {
                Character c = (index < source.length ? source[index] : '\0');

                if (!isFromAlphabet(c) && c != '\0') {
                    System.err.printf("%d:caractere invalido.\n", line);
                    System.exit(1);
                }

                switch (state) {
                    case 0:
                        if (c == '!') {
                            lex.append(c); //lex += c
                            index++;
                            state = 11;
                        } else if ("<>=".contains(c.toString())) {
                            lex.append(c);
                            index++;
                            state = 10;
                        } else if ("+-*,;()".contains(c.toString())) {
                            lex.append(c);
                            index++;
                            token = symbolTable.get(lex.toString());
                            state = 3;
                        } else if (c == '\'') {
                            lex.append(c);
                            index++;
                            state = 8;
                        } else if (c == '_') {
                            lex.append(c);
                            index++;
                            state = 1;
                        } else if (Character.isLetter(c)) {
                            lex.append(c);
                            index++;
                            state = 2;
                        } else if (c == '0') {
                            lex.append(c);
                            index++;
                            state = 5;
                        } else if ("123456789".contains(c.toString())) {
                            lex.append(c);
                            index++;
                            state = 4;
                        } else if ("\t\n\r\0".contains(c.toString()) || Character.isWhitespace(c)) {
                            index++;
                            if ("\n\r".contains(c.toString())) {
                                line++;
                            }
                        } else if (c == '/') {
                            lex.append(c);
                            index++;
                            state = 12;
                        } else {
                            System.err.printf("%d:lexema nao identificado [%s]\n", line, lex.toString());
                            System.exit(1);
                        }
                        break;
                    case 1:
                        if (c == '_') {
                            lex.append(c);
                            index++;
                        } else if (Character.isLetterOrDigit(c)) {
                            lex.append(c);
                            index++;
                            state = 2;
                        } else {
                            System.err.printf("%d:lexema nao identificado [%s]\n", line, lex.toString());
                            System.exit(1);
                        }
                        break;
                    case 2:
                        if (c == '_' || Character.isLetterOrDigit(c)) {
                            lex.append(c);
                            index++;
                        } else {
                            state = 3;
                            Token t = symbolTable.get(lex.toString());
                            if (t == null) {
                                symbolTable.put(lex.toString(), TokenType.ID);
                                token = new Token(lex.toString(), TokenType.ID);
                            } else {
                                token = t;
                            }
                            //devolve (não incrementar index)
                        }
                        break;
                    case 4:
                        if (Character.isDigit(c)) {
                            lex.append(c);
                            index++;
                        } else {
                            token = new TokenNumber(lex.toString(), TokenType.CONSTANT);
                            lexical.put(lex.toString(), token);
                            state = 3;
                            //devolve
                        }
                        break;
                    case 5:
                        if (Character.isDigit(c)) {
                            lex.append(c);
                            index++;
                            state = 4;
                        } else if ("Hh".contains(c.toString())) {
                            lex.append(c);
                            index++;
                            state = 6;
                        } else {
                            token = new TokenNumber(lex.toString(), TokenType.CONSTANT);
                            lexical.put(lex.toString(), token);
                            state = 3;
                            //devolve
                        }
                        break;
                    case 6:
                        if (isHexadecimal(c)) {
                            lex.append(c);
                            index++;
                            state = 7;
                        } else {
                            System.err.printf("%d:lexema nao identificado [%s]\n", line, lex.toString());
                            System.exit(1);
                        }
                        break;
                    case 7:
                        if (isHexadecimal(c)) {
                            lex.append(c);
                            token = new TokenNumber(lex.toString(), TokenType.CONSTANT);
                            index++;
                            state = 3;
                        } else {
                            token = new TokenNumber(lex.toString(), TokenType.CONSTANT);
                            lexical.put(lex.toString(), token);
                            state = 3;
                            //devolve
                        }
                        break;
                    case 8:
                        if (c == '\'') {
                            lex.append(c);
                            index++;
                            state = 9;
                        } else if (!"\r\n".contains(c.toString())) {
                            lex.append(c);
                            index++;
                        } else {
                            System.err.printf("%d:lexema nao identificado [%s]\n", line, lex.toString());
                            System.exit(1);
                        }
                        break;
                    case 9:
                        if (c == '\'') {
                            lex.append(c);
                            index++;
                            state = 8;
                        } else {
                            token = new TokenString(lex.toString(), TokenType.CONSTANT);
                            lexical.put(lex.toString(), token);
                            state = 3;
                            //devolve
                        }
                        break;
                    case 10:
                        /* TODO Cabe refatoracao? */
                        if (c == '=') {
                            lex.append(c);
                            index++;
                            token = symbolTable.get(lex.toString());
                            state = 3;
                        } else {
                            token = symbolTable.get(lex.toString());
                            state = 3;
                            //devolve
                        }
                        break;
                    case 11:
                        if (c == '=') {
                            lex.append(c);
                            index++;
                            token = symbolTable.get(lex.toString());
                            state = 3;
                        } else {
                            System.err.printf("%d:lexema nao identificado [%s]\n", line, lex.toString());
                            System.exit(1);
                        }
                        break;
                    case 12:
                        if (c == '*') {
                            lex.setLength(0); //descarta o lexem
                            index++;
                            state = 13;
                        } else {
                            token = symbolTable.get(lex.toString());
                            state = 3;
                            //devolve
                        }
                        break;
                    case 13:
                        if (c == '*') {
                            index++;
                            state = 14;
                        } else {
                            if ("\n\r".contains(c.toString())) {
                                line++;
                            }
                            index++;
                        }
                        break;
                    case 14:
                        if (c == '/') {
                            index++;
                            state = 0;
                        } else if (c == '*') {
                            index++;
                        } else {
                            if ("\n\r".contains(c.toString())) {
                                line++;
                            }
                            index++;
                            state = 13;
                        }
                        break;
                }
            } else {
                break;
            }
        }

        return token;
    }

    private boolean isHexadecimal(Character c) {
        return "abcdefABCDEF".contains(c.toString()) || Character.isDigit(c);
    }

    private boolean isFromAlphabet(char c) {
        return Character.isLetterOrDigit(c) || symbols.contains(c + "");
    }

    @Override
    public void remove() {

    }


}