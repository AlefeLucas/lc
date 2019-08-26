@SuppressWarnings("unused")
public enum TokenType {
    /*
    KEYWORD("\\b((const)|(integer)|(byte)|(boolean)|(string)|(while)|(if)|(else)|(begin)" +
            "|(end)|(then)|(readln)|(main)|(write)|(writeln))\\b"),
    NUMERAL("\\b((\\d+)|([-]\\d+)|([0][hH][0-9a-fA-F]+))\\b"),
    STRING("[']([\\w!\"&(*)+,\\-.\\/:;<=>?\\[\\]{} ]|(''))*[']"),
    OPERATOR("([<>+\\-*\\/=]|(!=)|(==)|(>=)|(<=)|(and)|(or)|(not))"),
    SPECIAL("([(),;])"),
    BOOLEAN("\\b(true)|(false)\\b"),
    ID("\\b([a-zA-Z][\\w]*)|([_][_]*[a-zA-Z0-9][\\w]*)\\b");
    */

    //Alternativa
    CONST("\\b(const)\\b"),
    INTEGER("\\b(integer)\\b"),
    BYTE("\\b(byte)\\b"),
    STRING("\\b(string)\\b"),
    BOOLEAN("\\b(boolean)\\b"),
    WHILE("\\b(while)\\b"),
    IF("\\b(if)\\b"),
    ELSE("\\b(else)\\b"),
    BEGIN("\\b(begin)\\b"),
    END("\\b(end)\\b"),
    THEN("\\b(then)\\b"),
    READLN("\\b(readln)\\b"),
    WRITE("\\b(write)\\b"),
    WRITELN("\\b(writeln)\\b"),
    TRUE("\\b(true)\\b"),
    FALSE("\\b(false)\\b"),
    AND("(and)"),
    OR("(or)"),
    NOT("(not)"),
    LESS_OR_EQUAL("(<=)"),
    GREATER_OR_EQUAL("(>=)"),
    NOT_EQUAL("(!=)"),
    EQUAL("(==)"),
    LESS("<"),
    GREATER(">"),
    ASSIGN("="),
    PLUS("\\+"),
    MINUS("\\-"),
    MULTIPLY("\\*"),
    DIVIDE("\\/"),
    COMMA(","),
    SEMICOLON(";"),
    OPEN_BRACE("\\("),
    CLOSE_BRACE("\\)"),
    NUMERAL_CONSTANT("\\b((\\d+)|([-]\\d+)|([0][hH][0-9a-fA-F]+))\\b"),
    STRING_CONSTANT("[']([\\w!\"&(*)+,\\-.\\/:;<=>?\\[\\]{} ]|(''))*[']"),
    ID("\\b([a-zA-Z][\\w]*)|([_][_]*[a-zA-Z0-9][\\w]*)\\b");




    private final String regex;

    TokenType(String regex){
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }

    public String getGroupName(){
        return StringUtils.toCamelCase(this.name());
    }


}
