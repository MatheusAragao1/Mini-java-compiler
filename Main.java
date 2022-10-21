import java.util.List;
public class Main 
{ 
  public static void main(String[] args) {
    try 
    {
      String fileName = "test.java";
      Scanner scan = new Scanner();
      List<Token> tokenList = scan.analize(fileName);
      for (int i = 0; i < tokenList.size(); i++)
      {
        System.out.println(tokenList.get(i));        
      }
    } 
    catch (Exception e)
    {
     System.out.println(e);
    }
  }
}