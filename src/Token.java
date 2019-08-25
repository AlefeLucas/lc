@SuppressWarnings("unused")
public enum Token {
    IF, //if
    ELSE, //else
    THEN, //then
    BEGIN, //begin
    END, //end
    WHILE, //while
    CONST, //const
    TYPE, //string, integer, boolean, byte
    ID, //ex: x, a1, b3_, cont
    NUMBER, //ex: 123, -123, 0hFF
    LITERAL, //ex: 'uma string'
    SEMICOLON, //;
    COMMA, //,
    ARITHMETIC_OPERATOR, //+, -, *, /
    LOGIC_OPERATOR, //and, or, not
    ARITHMETIC_COMPARISON, //==, <=, >=, !=, >, <
    STRING_OPERATOR, //+
    STRING_COMPARISON, //==
    READLN, //readln()
    WRITE, //write()
    WRITELN, //writeln()
    PARENTHESIS, //( )
}
