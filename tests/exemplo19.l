/* testes erros id */

integer _1 = 0;
byte _2 = 2;
byte _3 = 255;
string nome;
const a = true;
boolean x = true;

main 
    
    x = true or false;
    
    nome = 'a'or 'b';
    _1 = 1 or 3000;
    _2 = 1 or 1;/* ERRO tipos incompativeis*/
    _2 = 1 or 3000;/* ERRO tipos incompativeis*/
    nome = 'a' or 'b';/* ERRO tipos incompativeis*/
    nome = 'a' or true;/* ERRO tipos incompativeis*/
    x = true or 'a';/* ERRO tipos incompativeis*/
    _1 = _1 or _2;
    
    /* nome = not (((((((('a')))))))); ERRO tipos incompativeis*/

   
    
end