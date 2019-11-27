/* testes erros id */

boolean b;

integer _1 = 0;
string nome;
const y = 'a';

main 
	
	writeln(nome,true);
	writeln(true);
	if(true)then;/* Tem que funcionar */
	while(true)while(false);/* Tem que funcionar */
	while(not true)while(false);/* Tem que funcionar */
	if('luana' =='lucas')then nome = 'a';/* Tem que funcionar*/
	while(true) nome = 'a';/* ERRO tipos incompativeis*/
	while(true) nome = 'a';/* ERRO tipos incompativeis*/
	while(nome);/* ERRO tipos incompativeis*/

	readln(nome);

end