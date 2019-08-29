import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analisador Lexico, nao segue o modelo do prof, fazer outro baseado no automato
 */
public class LexerOld {

    public static  ArrayList<Token> lex(String input) {
        input = removingNewLineAndComments(input);

        ArrayList<Token> tokens = new ArrayList<>();
        SymbolTable symbolTable = SymbolTableSingleton.getInstance();

        StringBuilder tokenPatternsBuffer = new StringBuilder();
        for (TokenType tokenType : TokenType.values())
            tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType.getGroupName(), tokenType.getRegex()));
        Pattern tokenPatterns = Pattern.compile(tokenPatternsBuffer.substring(1));

        Matcher matcher = tokenPatterns.matcher(input);
        while (matcher.find()) {
            for (TokenType type : TokenType.values()) {
                if (matcher.group(type.getGroupName()) != null) {
                    Token token = new Token(matcher.group(type.getGroupName()), type);
                    if(token.getValue() == TokenType.ID && symbolTable.get(token.getKey()) == null){
                        symbolTable.put(token.getKey(), TokenType.ID);
                    }
                    tokens.add(token);
                }
            }
        }
        return tokens;
    }

    private static String removingNewLineAndComments(String input) {
        input = input
                .replaceAll("\r", " ")
                .replaceAll("\n", " ")
                .replaceAll("\\/\\*[\\w!\"&(*)+,\\-.\\/:;<=>?\\[\\]{} ]*?\\*\\/", " ");
        return input;
    }
}
