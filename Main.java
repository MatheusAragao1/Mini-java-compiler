import java.util.List;

public class Main 
{  
  public static void main(String[] args) {
    try 
    {
      String fileName = "test.java";
      Scanner analisadorLexico = new Scanner();
      List<Token> analiseMiniJava = analisadorLexico.analise(fileName);
      for (int i = 0; i < analiseMiniJava.size(); i++)
      {
        System.out.println(analiseMiniJava.get(i));        
      }
    } 
    catch (Exception e)
    {
     System.out.println(e);
    }
  }
}