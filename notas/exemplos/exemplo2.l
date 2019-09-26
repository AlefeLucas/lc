integer n;
string nome;
boolean naoTerminou;
const MAXITER=0ha;

main
  write('Digite seu nome: ');
  readln(nome);
  naoTerminou=true;
  n=0hFF;
  while (naoTerminou) begin
    writeln('Ola'' ',nome);
    n=n+1;
    naoTerminou=n<MAXITER;
  end
end