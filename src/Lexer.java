import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analisador Lexico
 */
public class Lexer {

    public static ArrayList<Token> lex(String input) {
        ArrayList<Token> tokens = new ArrayList<>();

        StringBuilder tokenPatternsBuffer = new StringBuilder();
        for (TokenType tokenType : TokenType.values())
            tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType.getGroupName(), tokenType.getRegex()));
        Pattern tokenPatterns = Pattern.compile(tokenPatternsBuffer.substring(1));

        Matcher matcher = tokenPatterns.matcher(input);
        while (matcher.find()) {
            for (TokenType type : TokenType.values()) {
                if (matcher.group(type.getGroupName()) != null) {
                    tokens.add(new Token(type, matcher.group(type.getGroupName())));
                }
            }

        }

        return tokens;
    }
}
