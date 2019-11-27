/* testes erros id */

integer _1 = 0;
byte _2 = 2;
byte _3 = 255;
string nome;
const a = true;
boolean x = true;

main 
    
    x = true - false;
    x = true - nome;
    nome = 'a'- 'b';
    _1 = 1 - 3000;
    _2 = 1 - 1;/* ERRO tipos incompativeis*/
    _2 = 1 - 3000;/* ERRO tipos incompativeis*/
    nome = 'a' - 'b';/* ERRO tipos incompativeis*/
    nome = 'a' - true;/* ERRO tipos incompativeis*/
    x = true - 'a';/* ERRO tipos incompativeis*/
    _1 = _1 - _2;
    
    /* nome = not (((((((('a')))))))); ERRO tipos incompativeis*/

   
    
end