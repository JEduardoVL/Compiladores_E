import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Interprete {

    static boolean existenErrores = false;

    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println("Uso correcto: interprete [archivo.txt]");
            System.exit(64);
        } else if (args.length == 1) {
            ejecutarArchivo(args[0]);
        } else {
            ejecutarPrompt();
        }
    }

    private static void ejecutarArchivo(String path) {
        try {
            // Lee el contenido del archivo y ejecuta el programa
            String contenido = new String(Files.readAllBytes(Paths.get(path)), Charset.defaultCharset());
            ejecutar(contenido);

            // Se indica que existe un error si los hay
            if (existenErrores) System.exit(65);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            System.exit(66);
        }
    }

    private static void ejecutarPrompt() {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print(">>> ");
            try {
                String linea = reader.readLine();
                if (linea == null) break; // Presionar Ctrl + D
                ejecutar(linea);
                existenErrores = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void ejecutar(String source) {
        try {
            // Resto del código para ejecutar el programa
            Scanner scanner = new Scanner(source);
            List<Token> tokens = scanner.scan();

            for (Token token : tokens) {
                System.out.println(token);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /*
    El método error se puede usar desde las distintas clases
    para reportar los errores:
    Interprete.error(....);
     */
    static void error(int linea, String mensaje){
        reportar(linea, "", mensaje);
    }

    private static void reportar(int linea, String posicion, String mensaje){
        System.err.println(
                "[linea " + linea + "] Error " + posicion + ": " + mensaje
        );
        existenErrores = true;
    }

}
