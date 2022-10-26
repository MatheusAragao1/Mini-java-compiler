import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

interface IScannerConfig
{
  public static List<String> IGNORE_LIST = Arrays.asList(" ", "\n", "\r", "\t", "$");
  public static List<String> ANOTHER_TOKENS = Arrays.asList(".", ":", ",", ";", "(", ")", "{", "}","<",">", "[", "]","+","-","*");
} 

public class Scanner implements IScannerConfig
{
  private int row = 1;
  private int column = 0;
  private List<Token> tokens;

  private Boolean isIgnorableCharacter(char character)
  {
    return IGNORE_LIST.contains(String.valueOf(character));
  }

  private Boolean isLineBreakAndHaveAnyToken(char character)
  {
    return character == '\n' && (this.tokens != null && this.tokens.size() > 0);
  }

  private Boolean canBeConcat(char character)
  {
    return Character.isLetter(character)  || "_".equals(String.valueOf(character))  || Character.isDigit(character);
  }

  private Boolean isAnotherToken(char character)
  {
    return ANOTHER_TOKENS.contains(String.valueOf(character));
  }

  private Boolean isBar(char character)
  {
    return "/".equals(String.valueOf(character));
  }

  private Boolean isNotEndOfSystemType(char character)
  {
    return character != ' ' && character != '(';
  }

  private Boolean isNumberContinuation(char character)
  {
    return Character.isDigit(character) || Type.PONTO.getId().equals(String.valueOf(character));
  }
  
  private Boolean isComposeChar(char character) 
  {
    String id = String.valueOf(character);
    return "&".equals(id) || "=".equals(id) || "!".equals(id);
  }

  private Boolean isComposeType(Type type) 
  {
    return Type.AND.equals(type) || Type.IGUAL.equals(type) || Type.DIFERENTE.equals(type);
  }

  private Boolean isNextLine(char character)
  {
    return "\n".equals(String.valueOf(character));
  }

  private Boolean hasNestedDots(String character, PushbackReader pushbackReader) throws IOException 
  {
    Boolean result = false;
    if (Type.PONTO.getId().equals(character)) 
    {
      char nextCharacter = getCharacter(pushbackReader);
      if (Type.PONTO.getId().equals(String.valueOf(nextCharacter))) {
        result = true;
      }
      else
      {
        previousCharacter(pushbackReader, nextCharacter);
      }
    }
    return result;
  }

  public List<Token> analize(String codeFileName) throws IOException 
  {
    PushbackReader pushbackReader = new PushbackReader(new BufferedReader(new InputStreamReader(new FileInputStream(codeFileName), "US-ASCII")));
    
    this.tokens = new ArrayList<>();

    char character = '.';
    while(character != '$')
    {
      character = getCharacter(pushbackReader);
      if (isIgnorableCharacter(character)) {
        continue;
      }
      Token token = getToken(character, pushbackReader);
      if (token != null) {
        this.tokens.add(token);

        if (Type.ERRO.equals(token.getType())) 
        {
          //Se achar erro para de ler os tokens
          break;
        }
      }        
    }
    return tokens;
  }

  private char getCharacter(PushbackReader pushbackReader) throws IOException {
    int data = pushbackReader.read();
    if (data != -1) {
      char character = (char) data;    
      if (isLineBreakAndHaveAnyToken(character)) {
        row++;
        column = 0;
      } else {
         column++;
      }
      return character;
    }
    return '$';
  }

  private void previousCharacter(PushbackReader pushbackReader, char character) throws IOException {
    pushbackReader.unread(character);
    if (character == '\n') {
      row--;
    } else {
      column--;
    }
  }

  private Token getToken(char character, PushbackReader pushbackReader) throws IOException {
    if (Character.isLetter(character)) 
    {
      return handleIdOrReservedWord(character, pushbackReader, this.column);
    }

    if (Character.isDigit(character)) 
    {
      return handleNumber(character, pushbackReader, this.column);
    }

    if (isComposeChar(character)) 
    {
      return handleComposeChars(character, pushbackReader, this.column);
    }

    if (isAnotherToken(character)) 
    {
      return handleAnotherChars(character, pushbackReader, this.column);
    }

    if (isBar(character)) 
    {
      return handleBar(character, pushbackReader, this.column);
    }

    return new Token(Type.ERRO, String.valueOf(character), this.row, this.column);
  }

  private Token handleIdOrReservedWord(char character, PushbackReader pushbackReader, int coluna) throws IOException 
  {
    String id = String.valueOf(character);
    char nextCharacter = getCharacter(pushbackReader);

    while (canBeConcat(nextCharacter)) 
    {
      id = id.concat(String.valueOf(nextCharacter));
      nextCharacter = getCharacter(pushbackReader);
    }

    previousCharacter(pushbackReader, nextCharacter);

    Type type = Type.getTypeById(id);
    if (Type.SYSTEM.equals(type))
    {
      nextCharacter = getCharacter(pushbackReader);
      while (isNotEndOfSystemType(nextCharacter)) 
      {
        id = id.concat(String.valueOf(nextCharacter));
        nextCharacter = getCharacter(pushbackReader);
      }
      previousCharacter(pushbackReader, nextCharacter);
      Type printStatement = Type.getTypeById(id);
      return new Token(printStatement, id, this.row, coluna);
    }

    return new Token(type, id, this.row, coluna);
  }

  private Token handleNumber(char character, PushbackReader pushbackReader, int coluna) throws IOException 
  {
    String number = String.valueOf(character);
    char nextCharacter = getCharacter(pushbackReader);

    while (isNumberContinuation(nextCharacter)) 
    {
      if (hasNestedDots(String.valueOf(nextCharacter), pushbackReader)) 
      {
        return new Token(Type.ERRO, String.valueOf(nextCharacter), this.row, coluna);
      }
      number = number.concat(String.valueOf(nextCharacter));
      nextCharacter = getCharacter(pushbackReader);
    }
    previousCharacter(pushbackReader, nextCharacter);
    return new Token(Type.NUMERO, number, this.row, coluna);
  }

  private Token handleComposeChars(char character, PushbackReader pushbackReader, int coluna) throws IOException 
  {
    String compCharacter = String.valueOf(character);
    char nextCharacter = getCharacter(pushbackReader);
    String concat = compCharacter.concat(String.valueOf(nextCharacter));
    Type typeById = Type.getTypeById(concat);

    if (isComposeType(typeById)) 
    {
      return new Token(typeById, concat, this.row, coluna);
    }
    previousCharacter(pushbackReader, nextCharacter);

    Type type = Type.getTypeById(compCharacter);
    return new Token(type, compCharacter, this.row, coluna);
  }

  private Token handleAnotherChars(char character, PushbackReader pushbackReader, int coluna) throws IOException 
  {
    String anotherCharacter = String.valueOf(character);
    Type type = Type.getTypeById(anotherCharacter);
    return new Token(type, anotherCharacter, this.row, coluna);
  }

  private void goUntilFinishComment(char nextCharacter, PushbackReader pushbackReader) throws IOException
  {
    while (true) 
    {
       nextCharacter = getCharacter(pushbackReader);
       if ("*".equals(String.valueOf(nextCharacter))) 
       {
         nextCharacter = getCharacter(pushbackReader);
         if ("/".equals(String.valueOf(nextCharacter))) 
         {
           break;
         }
       }
    }
  }

  private Token handleBar(char character, PushbackReader pushbackReader, int coluna) throws IOException 
  {
    char nextCharacter = getCharacter(pushbackReader);

    if (isBar(nextCharacter)) 
    {
      nextCharacter = getCharacter(pushbackReader);
      while (!isNextLine(nextCharacter)) 
      {
        nextCharacter = getCharacter(pushbackReader);
      }
      return null;

    } 
    else if ("*".equals(String.valueOf(nextCharacter))) 
    {
      goUntilFinishComment(nextCharacter, pushbackReader);
      return null;
    } 
    else 
    {
      previousCharacter(pushbackReader, nextCharacter);
      String id = String.valueOf(character);
      Type type = Type.getTypeById(id);
      return new Token(type, id, this.row, coluna);
    }
  }
}