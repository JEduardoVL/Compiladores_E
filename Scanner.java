import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    private static final Map<String, TipoToken> palabrasReservadas;

    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and",    TipoToken.AND);
        palabrasReservadas.put("else",   TipoToken.ELSE);
        palabrasReservadas.put("false",  TipoToken.FALSE);
        palabrasReservadas.put("for",    TipoToken.FOR);
        palabrasReservadas.put("fun",    TipoToken.FUN);
        palabrasReservadas.put("if",     TipoToken.IF);
        palabrasReservadas.put("null",   TipoToken.NULL);
        palabrasReservadas.put("or",     TipoToken.OR);
        palabrasReservadas.put("print",  TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("true",   TipoToken.TRUE);
        palabrasReservadas.put("var",    TipoToken.VAR);
        palabrasReservadas.put("while",  TipoToken.WHILE);
    }

    private final String source;

    private final List<Token> tokens = new ArrayList<>();
    
    public Scanner(String source){
        this.source = source + " ";
    }

    public List<Token> scan() throws Exception {
        String lexema = "";
        int estado = 0;
        char c;

        for(int i=0; i<source.length(); i++){
            c = source.charAt(i);

            switch (estado){
                case 0:
                    if(Character.isLetter(c)){
                        estado = 9;
                        lexema += c;
                    }
                    else if(Character.isDigit(c)){
                        estado = 15;
                        lexema += c;
                    }
                    else if(c == '"'){ 
                        estado =24;
                        lexema += c;
                    }
                    else if(c== '>'){
                        estado =1;
                        lexema += c;
                    }
                    else if(c=='<'){
                        estado =4;
                        lexema +=c;
                    }
                    else if(c == '='){
                        estado = 7;
                        lexema += c;
                    }
                    else if(c == '!'){
                        estado = 10;
                        lexema += c;
                    }
                    else if(c == '/'){
                        estado = 26;
                        lexema += c;  
                    }else if(c == '('){     
                        Token t = new Token(TipoToken.LEFT_PAREN, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }else if(c == ')'){    
                        Token t = new Token(TipoToken.RIGHT_PAREN, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }else if(c == '{'){  
                        Token t = new Token(TipoToken.LEFT_BRACE, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }else if(c == '}'){    
                        Token t = new Token(TipoToken.RIGHT_BRACE, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }else if(c == ','){  
                        Token t = new Token(TipoToken.COMMA, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }
                    else if(c == '.'){  
                        Token t = new Token(TipoToken.DOT, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }else if(c == '-'){       
                        Token t = new Token(TipoToken.MINUS, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }else if(c == '+'){       
                        Token t = new Token(TipoToken.PLUS, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }else if(c == ';'){  
                        Token t = new Token(TipoToken.SEMICOLON, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }else if(c == '*'){       
                        Token t = new Token(TipoToken.STAR, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }
                break;
                case 1:
                    lexema += c;
                    if (c == '=') {
                        tokens.add(new Token(TipoToken.GREATER_EQUAL, lexema));
                        estado = 0;
                        lexema = "";
                    } else {
                        Token t = new Token(TipoToken.GREATER, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                        i --;  
                    }
                    break;
                case 4:
                lexema += c;
                if (c == '=') {
                    tokens.add(new Token(TipoToken.LESS_EQUAL, lexema));
                    estado = 0;
                    lexema = "";
                } else {
                    Token t = new Token(TipoToken.LESS, lexema);
                    tokens.add(t);
                    estado = 0;
                    lexema = ""; 
                    i--;  
                }
                break;            
                case 7:
                    lexema += c;
                    if (c == '=') {
                        tokens.add(new Token(TipoToken.EQUAL_EQUAL, lexema));
                        estado = 0;
                        lexema = "";
                    } else {
                        Token t = new Token(TipoToken.EQUAL, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                        i --;
                    }
                    break;
                case 9:
                    if(Character.isLetter(c) || Character.isDigit(c)){
                        estado = 9;
                        lexema += c;
                    }
                    else{
                        // Vamos a crear el Token de identificador o palabra reservada
                        TipoToken tt = palabrasReservadas.get(lexema);

                        if(tt == null){
                            Token t = new Token(TipoToken.IDENTIFIER, lexema);
                            tokens.add(t);
                        }
                        else{
                            Token t = new Token(tt, lexema);
                            tokens.add(t);
                        }

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 10:
                    lexema += c;
                    if (c == '=') {
                        tokens.add(new Token(TipoToken.BANG_EQUAL, lexema));
                        estado = 0;
                        lexema = "";
                    } else {
                        Token t = new Token(TipoToken.BANG, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                        i--; 
                    }
                    break;
                case 15:
                    if (Character.isDigit(c)) {
                        estado = 15;
                        lexema += c;
                    } else if (c == '.') {
                        estado = 16;
                        lexema += c;
                    } else if (c == 'E') {
                        estado = 18;
                        lexema += c;
                    } else {
                        Token t = new Token(TipoToken.NUMBER, lexema,Integer.valueOf(lexema));  //Double.valueOf(lexema)
                        tokens.add(t);
                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 16:
                    if (Character.isDigit(c)) {
                        estado = 17;
                        lexema += c;
                    } else {
                        throw new Exception("Error en el análisis léxico: Se esperaba un dígito después del punto.");
                    }
                    break;
                case 17:
                    if (Character.isDigit(c)) {
                        estado = 17;
                        lexema += c;
                    } else if (c == 'E') {
                        estado = 18;
                        lexema += c;
                    } else {
                        Token t = new Token(TipoToken.NUMBER, lexema, Double.valueOf(lexema));
                        tokens.add(t);
                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 18:
                    if (Character.isDigit(c)) {
                        estado = 20;
                        lexema += c;
                    } else if (c == '+' || c == '-') {
                        estado = 19;
                        lexema += c;
                    } else {
                        throw new Exception("Error en el análisis léxico: Se esperaba un dígito o '+'/'-' después de 'E'.");
                    }
                    break;
                case 19:
                    if (Character.isDigit(c)) {
                        estado = 20;
                        lexema += c;
                    } else {
                        throw new Exception("Error en el análisis léxico: Se esperaba un dígito después de '+'/'-'.");
                    }
                    break;
                case 20:
                    if (Character.isDigit(c)) {
                        estado = 20;
                        lexema += c;
                    } else {
                        Token t = new Token(TipoToken.NUMBER, lexema, Double.valueOf(lexema));
                        tokens.add(t);
                        estado = 0;
                        lexema = "";
                        i--; // Retrocede un paso para analizar el siguiente caracter
                    }
                    break;
                case 24:
                    lexema += c;
                    if (c == '"') {
                        // Extraer la cadena sin las comillas
                        String cadenaSinComillas = lexema.substring(1, lexema.length() - 1);
                        // Crear un nuevo token con la cadena como literal
                        tokens.add(new Token(TipoToken.STRING, lexema, cadenaSinComillas));
                        estado = 0;
                        lexema = "";
                    } else if (c == '\n') {
                        throw new Exception("Error en el análisis léxico: Salto de línea no permitido dentro de una cadena.");
                    }
                    break;
                case 26:
                    lexema += c;
                    if (c == '*') {
                        estado = 27;
                    } else if (c == '/') {
                        estado = 30;
                    } else {
                        Token t = new Token(TipoToken.SLASH, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = "";
                        i--; // Retrocede un paso para analizar el siguiente caracter
                    }
                    break;
                case 27:
                    lexema += c;
                    if (c == '*') {
                        estado = 28;
                    } else {
                        // Permanece en el estado 27 mientras no encuentres '*'
                    }
                    break;
                case 28:
                    lexema += c;
                    if (c == '*') {
                        // Permanece en el estado 28 mientras encuentres '*'
                    } else if (c == '/') {
                        estado = 0;
                        lexema = "";
                    } else {
                        estado = 27;
                    }
                    break;
                case 30:
                    if (c == '\n') {
                        estado = 0;
                        lexema = "";
                    } else {
                        // Permanece en el estado 30 mientras no encuentres '\n'
                    }
                    break;
            }
        }

    
    // Verificar si el comentario se dejó abierto al finalizar el análisis
    if (estado == 27) {
        throw new Exception("Error en el análisis léxico: El comentario no se cerró adecuadamente.");
    }
        return tokens;
    }
}
