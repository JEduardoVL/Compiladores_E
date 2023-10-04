import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.HeaderTokenizer.Token;

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
        int estado = 0;
        String lexema = "";
        char c;

        for(int i=0; i<source.length(); i++){
            c = source.charAt(i);

            switch (estado){
                case 0:
                    if(Character.isLetter(c)){
                        estado = 13;
                        lexema += c;
                    }
                    else if(Character.isDigit(c)){
                        estado = 15;
                        lexema += c;

                        /*while(Character.isDigit(c)){
                            lexema += c;
                            i++;
                            c = source.charAt(i);
                        }
                        Token t = new Token(TipoToken.NUMBER, lexema, Integer.valueOf(lexema));
                        lexema = "";
                        estado = 0;
                        tokens.add(t);
                        */

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
                    }
                    break;
               /*  case 1:
                    lexema +=c;
                    if(c != '>' && c =='='){
                        Token t = new Token(TipoToken.GREATER_EQUAL, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema ="";
                    }
                    else {
                        Token t = new Token(TipoToken.GREATER, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }
                break;
                case 2:
                    lexema += c;
                    if( c != '='){
                        Token t = new Token(TipoToken.GREATER_EQUAL, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema ="";
                    }
                break;
                case 4:
                    lexema +=c;
                    if(c != '<' && c == '='){
                        Token t = new Token(TipoToken.LESS_EQUAL, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema ="";
                    }else {
                        Token t = new Token(TipoToken.LESS, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }
                break;*/
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
                }
                break;            
                /*case 7:
                    lexema +=c;
                    if(c == '='){
                        Token t = new Token(TipoToken.EQUAL_EQUAL, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema ="";
                    }else{
                        Token t = new Token(TipoToken.EQUAL, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }
                break;*/
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
                /*case 10:
                    lexema +=c ;
                    if(c != '!' && c == '='){
                        Token t = new Token(TipoToken.BANG_EQUAL, lexema);
                        tokens.add(t);
                    }else {
                        Token t = new Token(TipoToken.BANG, lexema);
                        tokens.add(t);
                    }
                    estado = 0;
                    lexema = ""; 
                break;*/
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
                        Token t = new Token(TipoToken.STRING, lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }else if (c == '\n') {
                        throw new Exception("Error en el análisis léxico: Salto de línea no permitido dentro de una cadena.");
                    }
                    break;
                case 26:
                    lexema += c;
                    if(c=='*'){
                        estado = 27;
                        lexema += c;
                    }
                    else if(c == '/'){
                        estado = 30;
                        lexema += c;
                    }else{
                        Token t = new Token(TipoToken.SLASH,lexema);
                        tokens.add(t);
                        estado = 0;
                        lexema = ""; 
                    }
                break;
                case 27:
                    lexema += c;
                    if(c=='*'){
                        estado = 28;
                        lexema += c;
                    }else {
                        estado = 27;
                        lexema += c;
                    }
                break;
                case 28:
                    lexema +=c;
                    if(c=='*'){
                        estado =28;
                        lexema += c;
                    }
                    else if(c == '/'){
                        estado = 0;
                        lexema = "";
                    }else{
                        estado = 27;
                        lexema += c;
                    }

                break;
                case 30:
                    if(c == '\n'){
                        estado= 0;
                        lexema += c;
                    }else{
                        estado = 30;
                        lexema += c;
                    }
                break;
            }
        }

    
    // Verificar si el comentario se dejó abierto al finalizar el análisis, no si es necesario
    if (estado == 27) {
        throw new Exception("Error en el análisis léxico: El comentario no se cerró adecuadamente.");
    }

        return tokens;
    }
    private void addToken(TipoToken tipo, String lexema) {
        Token t = new Token(tipo, lexema);
        tokens.add(t);
        lexema = "";
    }
}
