primer avance Proyecto Final:

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
       }else if(preanalisis.tipo == TipoToken.VAR && preanalisis.tipo== TipoToken.IDENTIFIER){
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
            match(TipoToken.LEFT_BRACE);
            Block();
        }


    }

    private void Expression(){

    }

    private void For_Stmt(){

    }

    private void If_Stmt(){

    }

    private void Print_Stmt(){

    }

    private void Return_Stmt(){

    }

    private void While_Stmt(){

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