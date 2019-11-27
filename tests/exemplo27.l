/* testes erros id */

integer _1 = 0;
byte _2 = 0;
string nome;
const a = true;
boolean x = true;

main 
	x = not ((((((((((true)))))))))); /* Tem que funcionar */
	_1 = (1 + 1 + (1 + 1));
    nome = (('luana') + ('lucas'));
	nome = (('luana') == ('lucas'));
    _2 = (1 + 1 + (1 + 1));
    _2 = (1 + 1 + (1 + 2000));
    
    
     _1 = _1 * _2;
    x = false and true;
    x = true * false;
    
    
    /* nome = not (((((((('a')))))))); ERRO tipos incompativeis*/

   
    
end