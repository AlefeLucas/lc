/* testes erros id */

integer _1 = 0;
byte _2 = 2;
byte _3 = 255;
string nome;
const a = true;
boolean x = true;

main 
    
    x = true and false;
    x = a and true;
    nome = 'a'and 'b';
    _1 = 1 and 3000;
    _2 = 1 and 1;/* ERRO tipos incompativeis*/
    _2 = 1 and 3000;/* ERRO tipos incompativeis*/
    nome = 'a' and 'b';/* ERRO tipos incompativeis*/
    nome = 'a' and true;/* ERRO tipos incompativeis*/
    x = true and 'a';/* ERRO tipos incompativeis*/
    _1 = _1 and _2;

    /* nome = not (((((((('a')))))))); ERRO tipos incompativeis*/

   
    
end