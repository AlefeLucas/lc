/* testes erros id */

integer _1 = 0;
byte _2 = 2;
byte _3 = 255;
string nome;
const a = true;
boolean x = true;

main 
	
    a = 'a'* 'x';/* ERRO tipos incompativeis*/
    _1 = _1 * _2;
    x = true * false;/* ERRO tipos incompativeis*/
    _1 = 1 * 3000;
    _2 = 1 * 1;
    _2 = _2 * _3;
    nome = 'a'* 'b';/* ERRO tipos incompativeis*/
    nome = 'a'* true;/* ERRO tipos incompativeis*/
    x = true * 'a';/* ERRO tipos incompativeis*/
    
    /* nome = not (((((((('a')))))))); ERRO tipos incompativeis*/

   
    
end





