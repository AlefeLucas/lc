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
        StringBuilder tok = new StringBuilder();
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
                            tok.append(c);
                            index++;
                            state = 11;
                        } else if ("<>=".contains(c.toString())) {
                            tok.append(c);
                            index++;
                            state = 10;
                        } else if ("+-*,;()".contains(c.toString())) {
                            tok.append(c);
                            index++;
                            token = symbolTable.get(tok.toString());
                            state = 3;
                        } else if (c == '\'') {
                            tok.append(c);
                            index++;
                            state = 8;
                        } else if (c == '_') {
                            tok.append(c);
                            index++;
                            state = 1;
                        } else if (Character.isLetter(c)) {
                            tok.append(c);
                            index++;
                            state = 2;
                        } else if (c == '0') {
                            tok.append(c);
                            index++;
                            state = 5;
                        } else if ("123456789".contains(c.toString())) {
                            tok.append(c);
                            index++;
                            state = 4;
                        } else if ("\t\n\r\0".contains(c.toString() ) || Character.isWhitespace(c)) {
                            index++;
                            if ("\n\r".contains(c.toString())) {
                                line++;
                            }
                        } else if (c == '/') {
                            tok.append(c);
                            index++;
                            state = 12;
                        } else {
                            System.err.printf("%d:lexema nao identificado [%s]\n", line, tok.toString());
                            System.exit(1);
                        }
                        break;
                    case 1:
                        if (c == '_') {
                            tok.append(c);
                            index++;
                        } else if (Character.isLetterOrDigit(c)) {
                            tok.append(c);
                            index++;
                            state = 2;
                        } else {
                            System.err.printf("%d:lexema nao identificado [%s]\n", line, tok.toString());
                            System.exit(1);
                        }
                        break;
                    case 2:
                        if (c == '_' || Character.isLetterOrDigit(c)) {
                            tok.append(c);
                            index++;
                        } else {
                            state = 3;
                            Token t = symbolTable.get(tok.toString());
                            if (t == null) {
                                symbolTable.put(tok.toString(), TokenType.ID);
                                token = new Token(tok.toString(), TokenType.ID);
                            } else {
                                token = t;
                            }
                            //devolve (não incrementar index)
                        }
                        break;
                    case 4:
                        if (Character.isDigit(c)) {
                            tok.append(c);
                            index++;
                        } else {
                            token = new TokenNumber(tok.toString(), TokenType.NUMERAL_CONSTANT);
                            lexical.put(tok.toString(), token);
                            state = 3;
                            //devolve
                        }
                        break;
                    case 5:
                        if (Character.isDigit(c)) {
                            tok.append(c);
                            index++;
                            state = 4;
                        } else if ("Hh".contains(c.toString())) {
                            tok.append(c);
                            index++;
                            state = 6;
                        } else {
                            token = new TokenNumber(tok.toString(), TokenType.NUMERAL_CONSTANT);
                            lexical.put(tok.toString(), token);
                            state = 3;
                            //devolve
                        }
                        break;
                    case 6:
                        if (isHexadecimal(c)) {
                            tok.append(c);
                            index++;
                            state = 7;
                        } else {
                            System.err.printf("%d:lexema nao identificado [%s]\n", line, tok.toString());
                            System.exit(1);
                        }
                        break;
                    case 7:
                        if (isHexadecimal(c)) {
                            tok.append(c);
                            index++;
                        } else {
                            token = new TokenNumber(tok.toString(), TokenType.NUMERAL_CONSTANT);
                            lexical.put(tok.toString(), token);
                            state = 3;
                            //devolve
                        }
                        break;
                    case 8:
                        if (c == '\'') {
                            tok.append(c);
                            index++;
                            state = 9;
                        } else if (!"\r\n".contains(c.toString())) {
                            tok.append(c);
                            index++;
                        } else {
                            System.err.printf("%d:lexema nao identificado [%s]\n", line, tok.toString());
                            System.exit(1);
                        }
                        break;
                    case 9:
                        if (c == '\'') {
                            tok.append(c);
                            index++;
                            state = 8;
                        } else {
                            token = new TokenString(tok.toString(), TokenType.STRING_CONSTANT);
                            lexical.put(tok.toString(), token);
                            state = 3;
                            //devolve
                        }
                        break;
                    case 10:
                        /* TODO Cabe refatoracao? */
                        if (c == '=') {
                            tok.append(c);
                            index++;
                            token = symbolTable.get(tok.toString());
                            state = 3;
                        } else {
                            token = symbolTable.get(tok.toString());
                            state = 3;
                            //devolve
                        }
                        break;
                    case 11:
                        if (c == '=') {
                            tok.append(c);
                            index++;
                            token = symbolTable.get(tok.toString());
                            state = 3;
                        } else {
                            System.err.printf("%d:lexema nao identificado [%s]\n", line, tok.toString());
                            System.exit(1);
                        }
                        break;
                    case 12:
                        if (c == '*') {
                            tok.setLength(tok.length() - 1);
                            index++;
                            state = 13;
                        } else {
                            token = symbolTable.get(tok.toString());
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
                        } else {
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