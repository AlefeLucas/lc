/* testes erros id */

integer _1 = 0;
string nome;
const a = true;
boolean x = true;

main 
	
_1 = not true; /* ERRO tipos incompativeis*/
a = not false; /* ERRO const nao recebe valor*/
x = not true; /* Tem que funcionar */
x = true; /* Tem que funcionar */
nome = not 'a';/ * ERRO tipos incompativeis*/

end