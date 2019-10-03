import java.util.Iterator;

/**
 * Analisador Lexico - implementacao do automato;
 * 15 estados;
 * Dado o comportamento iterativo do analisador lexico, eh implementado como um Iterator de Token;
 *
 * @author Alefe Lucas
 * @author Gabriella Mara
 * @author Ricardo Sena
 */
@SuppressWarnings("WeakerAccess")
public class Lexer implements Iterator<Token> {
    private final char[] source;
    private static final String symbols = "!\"&'(*)+,-./:;<=>?[]_{} \n\r\t";
    private final SymbolTable symbolTable;
    private final LexicalRegister lexicalRegister;
    private int line;
    private int index;
    private static final int INITIAL = 0;
    private static final int FINAL = 3;

    /**
     * Construtor do analisador lexico - pre-processa o codigo fonte, inicializa a tabela de simbolos e registro lexico.
     *
     * @param source string contendo o codigo fonte
     */
    public Lexer(String source) {
        // remove espacos e quebras de linha no final do codigo; substitui \r\n por \n, ja que o windows codifica cada quebra de linha como \r\n;
        this.source = source.stripTrailing().replace("\r\n", "\n").toCharArray();
        this.symbolTable = SymbolTableSingleton.getInstance();
        this.lexicalRegister = LexicalRegisterSingleton.getInstance();
        this.line = 1;
    }

    /**
     * Obtem o numero da linha atual em que se encontra o analisador lexico.
     *
     * @return linha atual
     */
    public int getLine() {
        return line;
    }

    /**
     * Diz se existe um proximo token
     *
     * @return true se houve um proximo token, false caso contrario
     */
    @Override
    public boolean hasNext() {
        return index < source.length;
    }

    /**
     * Realiza os passos do automato, do estado inicial ate o final, para retornar o proximo token.
     *
     * @return o proximo token
     */
    @Override
    public Token next() {
        int state = INITIAL;
        StringBuilder lex = new StringBuilder(); //usando string builder por razoes performaticas
        Token token = null;

        while (state != FINAL) {
            if (index <= source.length) {
                Character c = getCurrentChar();
                assertValidChar(c);

                switch (state) {
                    case INITIAL:
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
                            state = FINAL;
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
                        } else if ("\t\n\r".contains(c.toString()) || Character.isWhitespace(c)) {
                            index++;
                            if ("\n\r".contains(c.toString())) {
                                line++;
                            }
                        } else if (c == '/') {
                            lex.append(c);
                            index++;
                            state = 12;
                        } else {
                            lex.append(c);
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
                            state = FINAL;
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
                            token = new TokenInteger(lex.toString());
                            lexicalRegister.put(lex.toString(), (TokenConstant) token);
                            state = FINAL;
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
                            token = new TokenInteger(lex.toString());
                            lexicalRegister.put(lex.toString(), (TokenConstant) token);
                            state = FINAL;
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
                            token = new TokenByte(lex.toString());
                            lexicalRegister.put(lex.toString(), (TokenConstant) token);
                            index++;
                            state = FINAL;
                        } else {
                            token = new TokenByte(lex.toString());
                            lexicalRegister.put(lex.toString(), (TokenConstant) token);
                            state = FINAL;
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
                            token = new TokenString(lex.toString());
                            lexicalRegister.put(lex.toString(), (TokenConstant) token);
                            state = FINAL;
                            //devolve
                        }
                        break;
                    case 10:
                        if (c == '=') {
                            lex.append(c);
                            index++;
                        }
                        token = symbolTable.get(lex.toString());
                        state = FINAL;
                        break;
                    case 11:
                        if (c == '=') {
                            lex.append(c);
                            index++;
                            token = symbolTable.get(lex.toString());
                            state = FINAL;
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
                            state = FINAL;
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
                            state = INITIAL;
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

    /**
     * Obtem o caractere atual; E necessario avancar um caractere alem do ultimo do
     * arquivo para identificar o ultimo lexema, portanto um espaco e adicionado.
     *
     * @return caractere atual
     */
    private char getCurrentChar() {
        return index < source.length ? source[index] : ' ';
    }

    /**
     * Se o caractere nao for permitido em um arquivo fonte, reporta erro e finaliza o programa
     *
     * @param c caractere
     */
    private void assertValidChar(Character c) {
        if (!isValidChar(c)) {
            System.err.printf("%d:caractere invalido.\n", line);
            System.exit(1);
        }
    }

    private boolean isHexadecimal(Character c) {
        return "abcdefABCDEF".contains(c.toString()) || Character.isDigit(c);
    }

    private boolean isValidChar(char c) {
        return Character.isLetterOrDigit(c) || symbols.contains(c + "");
    }

    /**
     * Dummy - exigido pelo Iterator mas sem uso pratico nessa aplicacao
     */
    @Override
    public void remove() {

    }


}