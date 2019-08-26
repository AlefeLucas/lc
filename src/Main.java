import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class Main {

    private static final String USAGE = "Usage: java lc <source file> <output assembly program>";

    /**
     * @param args argumentos da linha de comando do programa.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(USAGE);
        } else {
            String sourceFilePath = args[0];
            String outputFilePath = args[1];
            File sourceFile = getSourceFile(sourceFilePath);
            File outputFile = getOutputFile(outputFilePath);

            if (sourceFile != null && outputFile != null) {
                //TODO arquivos estao ok, prosseguir
                try {
                    String source = readFile(sourceFile, StandardCharsets.US_ASCII);
                    ArrayList<Token> tokens = Lexer.lex(source);
                    for (Token token : tokens) {
                        System.out.println(token);
                    }
                } catch (IOException e) {
                    System.out.println("Error: failed to read source file.\n" + e.getMessage());
                }

            } else {
                System.exit(1);
            }
        }
    }

    public static String readFile(File file, Charset charset) throws IOException {

        return new String(Files.readAllBytes(file.toPath()), charset);

    }


    /**
     * Obtem o arquivo de saida de um dado caminho.
     *
     * @param outputFilePath caminho para o arquivo de saida.
     * @return objeto {@link File} se a criacao do arquivo foi bem sucedida, null caso contrario.
     */
    private static File getOutputFile(String outputFilePath) {
        File output = null;

        if (validOutputName(outputFilePath)) {
            File file = new File((outputFilePath));
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
                output = file;
            } catch (IOException e) {
                System.out.println("lc: Could not create output file.");
            }
        } else {
            System.out.println("lc: Invalid output file format. Use assembly (.asm) extension.");
        }

        return output;
    }

    /**
     * Obtem um arquivo fonte de um dado caminho.
     *
     * @param sourceFilePath caminho do arquivo fonte.
     * @return objeto {@link File} se um arquivo valido e encontrado, null caso contrario.
     */
    private static File getSourceFile(String sourceFilePath) {
        File source = null;

        if (validSourceName(sourceFilePath)) {
            File file = new File(sourceFilePath);
            if (validateSourceFile(file)) {
                source = file;
            }
        } else {
            System.out.println("lc: Invalid source file format. Use .L extension.");
        }

        return source;
    }

    /**
     * Verifica se o arquivo fonte existe e eh legivel.
     */
    private static boolean validateSourceFile(File sourceFile) {
        boolean validSourceFile = false;
        if (sourceFile.exists()) {
            if (!sourceFile.canRead()) {
                System.out.println("lc: Cannot read from " + sourceFile.getPath());
            } else {
                validSourceFile = true;
            }
        } else {
            System.out.println("lc: file not found: " + sourceFile.getPath());
            System.out.println(USAGE);
        }

        return validSourceFile;
    }

    /**
     * Verifica se a string contem um nome de arquivo de saida valido (terminando em ".asm" case insensitive)
     */
    private static boolean validOutputName(String outputFileName) {
        return outputFileName.matches(".*[.][aA][sS][mM]$");
    }

    /**
     * Verifica se a string contem um nome de arquivo fonte valido (terminando em ".l" ou ".L")
     */
    private static boolean validSourceName(String sourceFilePath) {
        return sourceFilePath.matches(".*[.][lL]$");
    }
}
