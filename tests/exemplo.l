/* Exemplo de Programa na linguagem L */

integer n;
string nome = 'A';
byte b, c, d=255;
boolean naoTerminou = false;
const MAX=45;

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


