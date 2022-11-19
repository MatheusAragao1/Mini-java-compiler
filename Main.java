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
        Token token = tokenList.get(i);
        System.out.println(String.format("linha: %s, Tipo: %s, Palavra: %s", token.linha, token.type, token.palavra));        
      }
      Parser parse = new Parser();
      Boolean isOk = parse.analize(tokenList);
      System.out.println("Analise Sintatica: " + isOk);
    } 
    catch (Exception e)
    {
     System.out.println(e);
    }
  }
}