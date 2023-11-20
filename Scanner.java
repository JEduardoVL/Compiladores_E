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
        int inicioLexema = 0;
        char c;

        for (int i = 0; i < source.length(); i++) {
            c = source.charAt(i);

            switch (estado) {
                case 0:
                    inicioLexema = i;
                    if (Character.isLetter(c)) {
                        estado = 9;
                        lexema += c;
                       // inicioLexema =i;
                    } else if (Character.isDigit(c)) {
                        estado = 15;
                        lexema += c;
                        inicioLexema =i;
                    } else if (c == '"') {
                        estado = 24;
                        lexema += c;
                        inicioLexema = i;
                    } else if (c == '>') {
                        if (i + 1 < source.length() && source.charAt(i + 1) == '=') {
                            tokens.add(new Token(TipoToken.GREATER_EQUAL, ">=", i + 1));
                            i++;
                        } else {
                            tokens.add(new Token(TipoToken.GREATER, ">", i + 1));
                        }
                    } else if (c == '<') {
                        if (i + 1 < source.length() && source.charAt(i + 1) == '=') {
                            tokens.add(new Token(TipoToken.LESS_EQUAL, "<=", i + 1));
                            i++;
                        } else {
                            tokens.add(new Token(TipoToken.LESS, "<", i + 1));
                        }
                    } else if (c == '=') {
                        if (i + 1 < source.length() && source.charAt(i + 1) == '=') {
                            tokens.add(new Token(TipoToken.EQUAL_EQUAL, "==", i + 1));
                            i++;
                        } else {
                            tokens.add(new Token(TipoToken.EQUAL, "=", i + 1));
                        }
                    } else if (c == '!') {
                        if (i + 1 < source.length() && source.charAt(i + 1) == '=') {
                            tokens.add(new Token(TipoToken.BANG_EQUAL, "!=", i + 1));
                            i++;
                        } else {
                            tokens.add(new Token(TipoToken.BANG, "!", i + 1));
                        }
                    } else if (c == '/') {
                        if (i + 1 < source.length() && source.charAt(i + 1) == '/') {
                            estado = 30;
                            i++;
                        } else if (i + 1 < source.length() && source.charAt(i + 1) == '*') {
                            estado = 27;
                            i++;
                        } else {
                            tokens.add(new Token(TipoToken.SLASH, "/", i + 1));
                        }
                    }else if(c == '('){    
                        tokens.add(new Token(TipoToken.LEFT_PAREN,"(",i+1));
                    }else if(c == ')'){   
                        tokens.add(new Token(TipoToken.RIGHT_PAREN,")",i+1)); 
                    }else if(c == '{'){  
                        tokens.add(new Token(TipoToken.LEFT_BRACE,"{",i+1));
                    }else if(c == '}'){  
                        tokens.add(new Token(TipoToken.RIGHT_BRACE,"}",i+1));
                    }else if(c == ','){  
                        tokens.add(new Token(TipoToken.COMMA,",",i+1));
                    }else if(c == '.'){  
                        tokens.add(new Token(TipoToken.DOT,".",i+1));
                    }else if(c == '-'){    
                        tokens.add(new Token(TipoToken.MINUS,"-",i+1));
                    }else if(c == '+'){ 
                        tokens.add(new Token(TipoToken.PLUS,"+",i+1));
                    }else if(c == ';'){ 
                        tokens.add(new Token(TipoToken.SEMICOLON,";",i+1));
                    }else if(c == '*'){   
                        tokens.add(new Token(TipoToken.STAR,"*",i+1));
                    }
                    break; 
                case 9:
                    if(Character.isLetter(c) || Character.isDigit(c)){
                       // estado = 9;
                        lexema += c;
                    }
                    else{
                        // Vamos a crear el Token de identificador o palabra reservada
                        TipoToken tt = palabrasReservadas.get(lexema);

                        if(tt == null){
                            tokens.add(new Token(TipoToken.IDENTIFIER, lexema, inicioLexema +1));
                        }
                        else{
                            tokens.add(new Token(tt, lexema, inicioLexema + 1));
                        }

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
                        tokens.add(new Token(TipoToken.NUMBER, lexema, inicioLexema +1));  //Double.valueOf(lexema)
                        estado = 0;
                        lexema = "";
                        inicioLexema=0;
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
                        tokens.add(new Token(TipoToken.NUMBER, lexema, inicioLexema +1));
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
                        tokens.add(new Token(TipoToken.NUMBER, lexema, inicioLexema +1));
                        estado = 0;
                        lexema = "";
                        i--; // Retrocede un paso para analizar el siguiente caracter
                    }
                    break;
                    case 24:
                    if (c != '"') {
                        lexema += c;
                    } else {
                        lexema += c;
                        tokens.add(new Token(TipoToken.STRING, lexema, inicioLexema + 1));
                        estado = 0;
                        lexema = "";
                        inicioLexema = 0;
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
                    if (c != '\n') {
                        lexema += c;
                    } else {
                        estado=0;
                        lexema="";
                        inicioLexema=0;
                    }
                    break;
            }
        }

    
    // Verificar si el comentario se dejó abierto al finalizar el análisis
    if (estado == 27) {
        throw new Exception("Error en el análisis léxico: El comentario no se cerró adecuadamente.");
    }
    
    tokens.add(new Token(TipoToken.EOF, "",  source.length())); //Fin del archivo
    return tokens; 
    }


}
