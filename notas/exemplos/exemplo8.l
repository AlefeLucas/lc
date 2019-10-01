/* Erro token nao esperado */

integer n=7+8;
string nome;
boolean naoTerminou;
const MAXITER=0hff;

/* Programa Principal */
main
	write('Digite seu nome: ');
	readln(nome);
	naoTerminou=true;
	n=0;
	while (naoTerminou) begin
		writeln('Ola'' ',nome);
		n=n+1;
		naoTerminou=n<MAXITER;
	end
end