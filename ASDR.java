import java.util.List;

public class ASDR implements Program{

    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;


    public ASDR(List<Token> tokens){
        this.tokens = tokens;
        preanalisis = this.tokens.get(i);
    }

    @Override
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
        if(preanalisis.tipo == TipoToken.VAR && preanalisis.tipo== TipoToken.IDENTIFIER){
            match(TipoToken.VAR);
            match(TipoToken.IDENTIFIER);
            Var_Init();
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
        }
    }
// Sentencias

    private void Statement(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.FOR){
           // match(TipoToken.FOR);
            For_Stmt();
        }else if(preanalisis.tipo == TipoToken.IF){
         //   match(TipoToken.IF);
            If_Stmt();
        }else if(preanalisis.tipo == TipoToken.PRINT){
          //  match(TipoToken.PRINT);
            Print_Stmt();
        }else if(preanalisis.tipo == TipoToken.RETURN){
          //  match(TipoToken.RETURN);
            Return_Stmt();
        }else if(preanalisis.tipo == TipoToken.WHILE){
         //   match(TipoToken.WHILE);
            While_Stmt();
        }else if(preanalisis.tipo == TipoToken.LEFT_BRACE){
           // match(TipoToken.LEFT_BRACE);
            Block();
        }else if(preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER){
            Expr_Stmt();
        }else{
            hayErrores = true;
            System.out.println("Error de Análisis");
        }
    }

    private void Expr_Stmt(){
        if(hayErrores)
        return;
        if(preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER){
            Expr_Stmt();
            if(preanalisis.tipo ==TipoToken.SEMICOLON){
                match(TipoToken.SEMICOLON);
            }
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
        if(preanalisis.tipo == TipoToken.SEMICOLON){
            match(TipoToken.SEMICOLON);
        }else if(preanalisis.tipo == TipoToken.VAR){
            Var_Decl();
        }else if(preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER){
            Expr_Stmt();
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
        }
        else{
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
        
    }

    private void Expression(){

    }

    

    

    

    

    private void Block(){

    }
   
    private void Function(){

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

 

}
