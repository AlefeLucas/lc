# Linguagem L

## Definição da linguagem
A linguagem “L” é uma linguagem imperativa simplificada, com características do C e Pascal. A linguagem oferece tratamento para 4 tipos básicos: byte, integer, boolean e string. O tipo byte é um escalar que varia de 0 a 255, podendo ser escrito em formato decimal ou hexadecimal. Constantes em formato hexadecimal são da forma 0hDD, onde DD é um número hexadecimal. O tipo integer é um escalar que varia de –32768 a 32767, ocupando 2 bytes. O tipo string é um arranjo que pode conter até 255 caracteres úteis e quando armazenado em memória, é finalizado pelo caracter ‘$’. Variáveis do tipo string ocupam 256 bytes de memória. O tipo boolean pode ter os valores true e false, ocupando um byte de memória (0h para falso e FFh para verdadeiro). 
 
Os caracteres permitidos em um arquivo fonte são as letras, dígitos, espaço, sublinhado, ponto, vírgula, ponto-e-vírgula, e_comercial, dois-pontos, parênteses, colchetes, chaves, mais, menos, aspas, apóstrofo, barra, exclamação, interrogação, maior, menor e igual, além da quebra de linha (bytes 0Dh e 0Ah). Qualquer outro caractere é considerado inválido. 

Strings são delimitados, no programa-fonte, por apóstrofos e não podem conter quebra de linha. Para se representar um apóstrofo, deve-se utilizar dois em sequência. 
 
Os identificadores de constantes e variáveis são compostos de letras, dígitos e o sublinhado, não podem começar com dígitos, nem conter apenas sublinhados, e têm no máximo 255 caracteres. Maiúsculas e minúsculas são diferenciadas.  
 
As seguintes palavras são reservadas: 

|       |         |      |        |         |      |
|-------|---------|------|--------|---------|------|
| const | integer | byte | string | while   | if   |
| else  | and     | or   | not    | =       | ==   |
| (     | )       | <    | >      | !=      | >=   |
| <=    | ,       | +    | -      | *       | /    |
| ;     | begin   | end  | then   | readln  | main |
| write | writeln | true | false  | boolean |      |

Os comandos existentes em “L” permitem atribuição a variáveis através do operador =, entrada de valores pelo teclado e saída de valores para a tela, estruturas de repetição (enquanto),  estruturas de teste (se - então - senão), expressões aritméticas com inteiros e bytes, expressões lógicas e relacionais, além de atribuição, concatenação e comparação de igualdade entre strings. A ordem de precedência nas expressões é: a) parênteses; b) negação lógica (not); c) multiplicação aritmética (*), lógica (and) e divisão (/); d) subtração (-), adição aritmética (+), lógica ( or ) e concatenação de strings (+);  e) comparação aritmética (==,!=,<,>,<=,>=) e entre strings (==). 
 
Comentários são delimitados por /* */. A quebra de linha e o espaço podem ser usados livremente como delimitadores de lexemas.
A estrutura básica de um programa-fonte é da forma: 

<p align="center">
  Declarações   main Comandos end
</p>

### Alfabeto
### Gramática

## Compilador de L
1. Prática No.1 -  Implementação da Tabela de Símbolos e Analisador Léxico
1. Prática No.2 - Implementação do analisador sintático 

### Requerimentos
JDK 8

### Como usar
Para rodar o compilador, vá até o diretório raiz do projeto e digite o seguinte comando no terminal 
```
javac ./src/LC.java
```
E passe como parâmetros os arquivos de entrada e saída:
```
java LC <input> <output>
```
Como exemplo
```
java LC ./example/in.l  ./example/out.asm
```