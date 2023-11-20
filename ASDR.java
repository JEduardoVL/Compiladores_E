import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ASDR implements Program{

    private int i = 0;
    //private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;


    public ASDR(List<Token> tokens){
        this.tokens = tokens;
        preanalisis = this.tokens.get(i);
    }

    public boolean progra() {
        try {
            declaration();
            if (preanalisis.tipo == TipoToken.EOF) {
                System.out.println("ASDR correcto");
                return true;
            } else {
                System.out.println("Se encontraron errores ASDR");
            }
        } catch (ParserException e) {
            System.out.println("Se encontraron errores ASDR: " + e.getMessage());
            return false;
        }
        return false;
    }

    private Statement declaration() throws ParserException {
        Statement result = null;
        while (true) {
            if (preanalisis.getTipo() == TipoToken.FUN) {
                result = fun_decl();
            } else if (preanalisis.getTipo() == TipoToken.VAR) {
                result = var_decl();
            } else if (checkStatementStart(preanalisis.getTipo())) {
                result = statement();
            } else {
                break;
            }
        }
        return result;
    }
  
    private boolean checkStatementStart(TipoToken tipo) {
        // Retorna true si el tipo del token es el inicio de una sentencia
        return tipo == TipoToken.IF || tipo == TipoToken.FOR || tipo == TipoToken.PRINT
               || tipo == TipoToken.RETURN || tipo == TipoToken.WHILE || tipo == TipoToken.LEFT_BRACE;
    }

    private Statement fun_decl() throws ParserException {
    match(TipoToken.FUN);
    return function(); 
}

    private Statement var_decl() throws ParserException {
        match(TipoToken.VAR);
        match(TipoToken.IDENTIFIER);
        Token variableName = previous();
        Expression initializer = var_init(); 
        match(TipoToken.SEMICOLON);
        return new StmtVar(variableName, initializer); 
    }

    private Expression var_init() throws ParserException {
        if (preanalisis.getTipo() == TipoToken.EQUAL) {
            match(TipoToken.EQUAL);
            return expression();
        }
        return null; // E
    }

    //////////////////////////////////// SENTENCIAS


    //////////////////////////////Expresiones

    private Expression expression() throws ParserException{
         Expression expr = assignment();
        return expr;
    }

    private Expression assignment() throws ParserException{
        Expression expr = logic_or();
        expr = assignment_opc(expr);
        return expr;
    }


    private Expression assignment_opc(Expression expr) throws ParserException {
        if (preanalisis.getTipo() == TipoToken.EQUAL) {
            match(TipoToken.EQUAL);
            Expression value = assignment(); 
            expr = new ExprAssign(previous(), value); 
        }
        return expr;
    }

     private Expression logic_or() throws ParserException {
        Expression expr = logic_and();
        return logic_or_2(expr);
    }
    
    private Expression logic_or_2(Expression expr) throws ParserException {
        if (preanalisis.getTipo() == TipoToken.OR) {
            match(TipoToken.OR);
            Expression right = logic_and();
            expr = new ExprBinary(expr, new Token(TipoToken.OR, "or", null), right);
            return logic_or_2(expr);
        }
        return expr;
    }

    private Expression logic_and() throws ParserException {
        Expression expr = equality();
        return logic_and_2(expr);
    }
    
    private Expression logic_and_2(Expression expr) throws ParserException {
        if (preanalisis.getTipo() == TipoToken.AND) {
            match(TipoToken.AND);
            Expression right = equality();
            expr = new ExprBinary(expr, new Token(TipoToken.AND, "and", null), right);
            return logic_and_2(expr);
        }
        return expr;
    }

    private Expression equality() throws ParserException {
        Expression expr = comparison();
        return equality_2(expr);
    }

    private Expression equality_2(Expression expr) throws ParserException {
        while (preanalisis.getTipo() == TipoToken.BANG_EQUAL || preanalisis.getTipo() == TipoToken.EQUAL_EQUAL) {
            Token operator = preanalisis;
            match(preanalisis.getTipo());
            Expression right = comparison();
            expr = new ExprBinary(expr, operator, right);
        }
        return expr;
    }

    private Expression comparison() throws ParserException {
        Expression expr = term();
        return comparison_2(expr);
    }

    private Expression comparison_2(Expression expr) throws ParserException {
        while (preanalisis.getTipo() == TipoToken.GREATER || preanalisis.getTipo() == TipoToken.GREATER_EQUAL
               || preanalisis.getTipo() == TipoToken.LESS || preanalisis.getTipo() == TipoToken.LESS_EQUAL) {
            Token operator = preanalisis;
            match(preanalisis.getTipo());
            Expression right = term();
            expr = new ExprBinary(expr, operator, right);
        }
        return expr;
    }

    private Expression term() throws ParserException {
        Expression expr = factor();
        return term_2(expr);
    }

    private Expression term_2(Expression expr) throws ParserException {
        while (preanalisis.getTipo() == TipoToken.MINUS || preanalisis.getTipo() == TipoToken.PLUS) {
            Token operator = preanalisis;
            match(preanalisis.getTipo());
            Expression right = factor();
            expr = new ExprBinary(expr, operator, right);
        }
        return expr;
    }

    private Expression factor() throws ParserException{
        Expression expr = unary();
        expr = factor2(expr);
        return expr;
    }

    private Expression factor2(Expression expr) throws ParserException{
        switch (preanalisis.getTipo()){
            case SLASH:
                match(TipoToken.SLASH);
                Token operador = previous();
                Expression expr2 = unary();
                ExprBinary expb = new ExprBinary(expr, operador, expr2);
                return factor2(expb);
            case STAR:
                match(TipoToken.STAR);
                operador = previous();
                expr2 = unary();
                expb = new ExprBinary(expr, operador, expr2);
            default:
                
        }
        return expr;
    }

    private Expression unary() throws ParserException{
        switch (preanalisis.getTipo()){
            case BANG:
                match(TipoToken.BANG);
                Token operador = previous();
                Expression expr = unary();
                return new ExprUnary(operador, expr);
            case MINUS:
                match(TipoToken.MINUS);
                operador = previous();
                expr = unary();
                return new ExprUnary(operador, expr);
            default:
                return call();
        }
    }

    private Expression call() throws ParserException{
        Expression expr = primary();
        expr = call2(expr);
        return expr;
    }

    private Expression call2(Expression expr) throws ParserException {
        switch (preanalisis.getTipo()){
            case LEFT_PAREN:
                match(TipoToken.LEFT_PAREN);
                List<Expression> lstArguments = arguments_opc();
                match(TipoToken.RIGHT_PAREN);
                ExprCallFunction ecf = new ExprCallFunction(expr, lstArguments);
                return call2(ecf);
            default:
            // algo no esperado
        }
        return expr;
    }

    private Expression primary() throws ParserException{
        switch (preanalisis.getTipo()){
            case TRUE:
                match(TipoToken.TRUE);
                return new ExprLiteral(true);
            case FALSE:
                match(TipoToken.FALSE);
                return new ExprLiteral(false);
            case NULL:
                match(TipoToken.NULL);
                return new ExprLiteral(null);
            case NUMBER:
                match(TipoToken.NUMBER);
                Token numero = previous();
                return new ExprLiteral(numero.getLiteral());
            case STRING:
                match(TipoToken.STRING);
                Token cadena = previous();
                return new ExprLiteral(cadena.getLiteral());
            case IDENTIFIER:
                match(TipoToken.IDENTIFIER);
                Token id = previous();
                return new ExprVariable(id);
            case LEFT_PAREN:
                match(TipoToken.LEFT_PAREN);
                Expression expr = expression();
                match(TipoToken.RIGHT_PAREN);
                return new ExprGrouping(expr);
            default:
            //Algo diferente de lo anterior
        }
        return null;
    }

///////////////////////////Otras 



//////////////////////////////////////////////////   
    private void match(TipoToken tt) throws ParserException {
        if(preanalisis.getTipo() ==  tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            String message = "Error en la línea " +
                    preanalisis.getPosicion() +  // .getLine()
                    ". Se esperaba " + preanalisis.getTipo() +
                    " pero se encontró " + tt;
            throw new ParserException(message);
        }
    }

    private Token previous() {
        return this.tokens.get(i - 1);
    }
}

// Anterior codigo que solo corresponde a la gramtica. 
    /*@Override
    public boolean progra() {
        Declaration();
        if(preanalisis.tipo == TipoToken.EOF && !hayErrores){
            System.out.println("ASDR correcto");
            return  true;
        }else {
            System.out.println("Se encontraron errores ASDR");
        }
        return false;
    }

    // Declaraciones
    private void Declaration(){
       if(hayErrores) return;
       if(preanalisis.tipo == TipoToken.FUN){
            Fun_Decl();
            Declaration();
       }else if(preanalisis.tipo == TipoToken.VAR){
            Var_Decl();
            Declaration();
       }else if(preanalisis.tipo == TipoToken.FOR || preanalisis.tipo == TipoToken.IF || preanalisis.tipo == TipoToken.PRINT || preanalisis.tipo == TipoToken.RETURN || preanalisis.tipo == TipoToken.WHILE || preanalisis.tipo == TipoToken.LEFT_BRACE){
            Statement();
            Declaration();
       }else{
        // Vacio 
       }

    }

    private void Fun_Decl(){
        if(hayErrores)
        return;
        if(preanalisis.tipo==TipoToken.FUN){
            match(TipoToken.FUN);
            Function();
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }

    }

    private void Var_Decl(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.VAR ){
            match(TipoToken.VAR);
            if(preanalisis.tipo == TipoToken.IDENTIFIER){
                match(TipoToken.IDENTIFIER);
                Var_Init();
                if(preanalisis.tipo == TipoToken.SEMICOLON){
                    match(TipoToken.SEMICOLON);
                }
            }
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }
    }

    private void Var_Init(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            Expression();
        }else{
            // vacio
        }
    }
// Sentencias

    private void Statement(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.FOR){
            For_Stmt();
        }else if(preanalisis.tipo == TipoToken.IF){
            If_Stmt();
        }else if(preanalisis.tipo == TipoToken.PRINT){
            Print_Stmt();
        }else if(preanalisis.tipo == TipoToken.RETURN){
            Return_Stmt();
        }else if(preanalisis.tipo == TipoToken.WHILE){
            While_Stmt();
        }else if(preanalisis.tipo == TipoToken.LEFT_BRACE){
            Block();
        }else if(preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER){
            Expr_Stmt();
        }
    }

    private void Expr_Stmt(){
        if(hayErrores)
        return;
        Expression();
        if(preanalisis.tipo == TipoToken.SEMICOLON){
            match(TipoToken.SEMICOLON);
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }
    }

    private void For_Stmt(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.FOR){
            match(TipoToken.FOR);
            if(preanalisis.tipo ==TipoToken.LEFT_PAREN){
                match(TipoToken.LEFT_PAREN);
                For_Stmt_1();
                For_Stmt_2();
                For_Stmt_3();
                if(preanalisis.tipo == TipoToken.RIGHT_PAREN){
                    match(TipoToken.RIGHT_PAREN);
                    Statement();
                }
            }
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }
        
    }

    private void For_Stmt_1(){
        if(hayErrores)
        return;
       if(preanalisis.tipo == TipoToken.VAR){
        Var_Decl();
       }else if(preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER){
            Expr_Stmt();
        }else if(preanalisis.tipo == TipoToken.SEMICOLON){
            match(TipoToken.SEMICOLON);
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }
}

    private void For_Stmt_2(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER){
            Expr_Stmt();
            if(preanalisis.tipo == TipoToken.SEMICOLON){
                match(TipoToken.SEMICOLON);
            }
        }else if(preanalisis.tipo == TipoToken.SEMICOLON){
            match(TipoToken.SEMICOLON);
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }
    }

    private void For_Stmt_3(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER){
            Expr_Stmt();
        }else{
           // vacio
        }
    }

    private void If_Stmt(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.IF){
            match(TipoToken.IF);
            if(preanalisis.tipo == TipoToken.LEFT_PAREN){
                match(TipoToken.LEFT_PAREN);
                Expression();
                if(preanalisis.tipo == TipoToken.RIGHT_PAREN){
                    match(TipoToken.RIGHT_PAREN);
                    Statement();
                    Else_Statement();
                }
            }
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }
    }

    private void Else_Statement(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.ELSE){
            match(TipoToken.ELSE);
            Statement();
        }else{
            // vacio
        }
    }

    private void Print_Stmt(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.PRINT){
            match(TipoToken.PRINT);
            Expression();
            if(preanalisis.tipo == TipoToken.SEMICOLON){
                match(TipoToken.SEMICOLON);
            }
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }
    }

    private void Return_Stmt(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.RETURN){
            match(TipoToken.RETURN);
            Return_Exp_Opc();
            if(preanalisis.tipo == TipoToken.SEMICOLON){
                match(TipoToken.SEMICOLON);
            }
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }
    }

    private void Return_Exp_Opc(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER){
            Expression();
        }
        else{
           // vacio
        }
    }

    private void While_Stmt(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.WHILE){
            match(TipoToken.WHILE);
            if(preanalisis.tipo == TipoToken.LEFT_PAREN){
                match(TipoToken.LEFT_PAREN);
                Expression();
                if(preanalisis.tipo == TipoToken.RIGHT_PAREN){
                    match(TipoToken.RIGHT_PAREN);
                    Statement();
                }
            }
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }
    }

    private void Block(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.LEFT_BRACE){
            match(TipoToken.LEFT_BRACE);
            Declaration();
            if(preanalisis.tipo == TipoToken.RIGHT_BRACE){
                match(TipoToken.RIGHT_BRACE);
            }
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }
    }

    private void Expression(){
        if(hayErrores) 
        return;
        Assignment();
    }

    private void Assignment(){
        if(hayErrores) 
        return;
        Logic_Or();
        Assignment_Opc();
    }

    private void Logic_Or(){
        if(hayErrores) 
        return;
        Logic_And();
        Logic_Or_2();
    }

    private void Logic_And(){
        if(hayErrores) 
        return;
        Equality();
        Logic_And_2();
    }

    private void Logic_And_2(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.AND){
            match(TipoToken.AND);
            Equality();
            Logic_And_2();
        }else{
            // vacio
        }
    }

    private void Equality(){
        if(hayErrores)
        return;
        Comparison();
        Equality_2();
    }

    private void Equality_2(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.BANG_EQUAL){
            match(TipoToken.BANG_EQUAL);
            Comparison();
            Equality_2();
        }else if(preanalisis.tipo == TipoToken.EQUAL_EQUAL){
            match(TipoToken.EQUAL_EQUAL);
            Comparison();
            Equality_2();
        }else{
            // vacio
        }
    }
    
    private void Comparison(){
        if(hayErrores)
        return;
        Term();
        Comparison_2();
    }

    private void Comparison_2(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.GREATER){
            match(TipoToken.GREATER);
            Term();
            Comparison_2();
        }else if(preanalisis.tipo == TipoToken.GREATER_EQUAL){
            match(TipoToken.GREATER_EQUAL);
            Term();
            Comparison_2();
        }else if(preanalisis.tipo == TipoToken.LESS){
            match(TipoToken.LESS);
            Term();
            Comparison_2();
        }else if(preanalisis.tipo == TipoToken.LESS_EQUAL){
            match(TipoToken.LESS_EQUAL);
            Term();
            Comparison_2();
        }else{
            //vacio
        }
    }

    private void Term(){
        if(hayErrores)
        return;
        Factor();
        Term_2();
    }

    private void Term_2(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.MINUS){
            match(TipoToken.MINUS);
            Factor();
            Term_2();
        }else if(preanalisis.tipo == TipoToken.PLUS){
            match(TipoToken.PLUS);
            Factor();
            Term_2();
        }else{
            // vacio
        }
    }

    private void Factor(){
        if(hayErrores)
        return;
        Unary();
        Factor_2();
    }

    private void Factor_2(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.SLASH){
            match(TipoToken.SLASH);
            Unary();
            Factor_2();
        }else if(preanalisis.tipo == TipoToken.STAR){
            match(TipoToken.STAR);
            Unary();
            Factor_2();
        }else{
            // vacio
        }
    }

    private void Unary(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.BANG){
            match(TipoToken.BANG);
            Unary();
        }else  if(preanalisis.tipo == TipoToken.MINUS){
            match(TipoToken.MINUS);
            Unary();
        }else{
            Call();
        }
    }

    private void Call(){
        if(hayErrores)
        return;
        Primary();
        Call_2();
    }

    private void Call_2(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.LEFT_PAREN){
            match(TipoToken.LEFT_PAREN);
            Arguments_Opc();
            if(preanalisis.tipo == TipoToken.RIGHT_PAREN){
                match(TipoToken.RIGHT_PAREN);
                Call_2();
            }
        }
        // duda
        else if(preanalisis.tipo == TipoToken.DOT){
            match(TipoToken.DOT);
            if(preanalisis.tipo == TipoToken.IDENTIFIER){
                match(TipoToken.IDENTIFIER);
                Call_2();
            }
        }
        else{
            // vacio
        }
    }

    private void Primary(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.TRUE){
            match(TipoToken.TRUE);
        }else if(preanalisis.tipo == TipoToken.FALSE){
            match(TipoToken.FALSE);
        }else if(preanalisis.tipo == TipoToken.NULL){
            match(TipoToken.NULL);
        }else if(preanalisis.tipo == TipoToken.NUMBER){
            match(TipoToken.NUMBER);
        }else if(preanalisis.tipo == TipoToken.STRING){
            match(TipoToken.STRING);
        }else if(preanalisis.tipo == TipoToken.IDENTIFIER){
            match(TipoToken.IDENTIFIER);
        }else if(preanalisis.tipo == TipoToken.LEFT_PAREN){
            match(TipoToken.LEFT_PAREN);
            Expression();
            if(preanalisis.tipo == TipoToken.RIGHT_PAREN){
                match(TipoToken.RIGHT_PAREN);
            }
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }
    }
    
    private void Logic_Or_2(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.OR){
            match(TipoToken.OR);
            Logic_And();
            Logic_Or_2();
        }else{
            // vacio
        }
    }

    private void Function(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            match(TipoToken.IDENTIFIER);
            if(preanalisis.tipo == TipoToken.LEFT_PAREN){
                match(TipoToken.LEFT_PAREN);
                Parameters_Opc();
                if(preanalisis.tipo == TipoToken.RIGHT_PAREN){
                    match(TipoToken.RIGHT_PAREN);
                    Block();
                }
            }
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }
    }

    private void Parameters_Opc(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            match(TipoToken.IDENTIFIER);
            Parameters();
        }else{
            // vacio
        }
    }

    private void Parameters(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            match(TipoToken.IDENTIFIER);
            Parameters_2();
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }
    }
    
    private void Parameters_2(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
            if(preanalisis.tipo == TipoToken.IDENTIFIER){
                match(TipoToken.IDENTIFIER);
                Parameters_2();
            }
        }else{
            // vacio
        }
    }
    
    private void Assignment_Opc(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            Expression();
        }else{
            // vacio
        }
    }

    private void Arguments_Opc(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER){
            Expression(); // duda
            Arguments();
        }else{
            // vacio
        }
    }

    private void Arguments(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
            Expression();
            Arguments();
        }else{
            // vacio
        }
    }

    private void match(TipoToken tt){
        if(preanalisis.tipo == tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            hayErrores = true;
            System.out.println("Error encontrado");
        }

    }
} */
