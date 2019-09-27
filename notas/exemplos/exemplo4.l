/**
 * Teste que da certo
 */
 integer n;

 /******************************/
 main
    writeln('Digite um numero maior que zero: ');
    readln(n);
    while(n > 0) begin
        if(n/2 == (n-1)/2) then writeln('Numero impar: ', n);
        else writeln('Numero par: ', n);
        n=n-1;
    end
 end
