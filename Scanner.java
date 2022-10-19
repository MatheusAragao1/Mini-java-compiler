import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scanner {
  private int linha = 1;
  private int coluna = 0;
  private List<Token> tokens;

  private static final List<String> IGNORE_LIST = Arrays.asList(" ", "\n", "\r", "\t", "@");
  private static final List<String> SPECIAL_TOKENS = Arrays.asList(".", ":", ",", ";", "(", ")", "=", "!", "{", "}",
      "<", "&", "[", "]", "*");
  private static final String AND_SYMBOL = "&";
  private static final String UNDER_LINE_SYMBOL = "_";
  private static final String BAR_SYMBOL = "/";
  private static final String EQUALS_SYMBOL = "=";
  private static final String NEGATE_SYMBOL = "!";
  private static final String ASTERISK_SYMBOL = "*";
  private static final String NEW_LINE = "\n";

  public Boolean isIgnorableCharacter(char character)
  {
    return IGNORE_LIST.contains(String.valueOf(character));
  }

  public List<Token>analise(String codeFileName) throws IOException {
    PushbackReader pushbackReader = new PushbackReader(new BufferedReader(new InputStreamReader(new FileInputStream(codeFileName), "US-ASCII")));
    this.tokens = new ArrayList<>();

    char character = '.';
    while(character != '@')
    {
      character = readCharacter(pushbackReader);
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

  private char readCharacter(PushbackReader pushbackReader) throws IOException {
    int data = pushbackReader.read();
    if (data != -1) {
      char character = (char) data;    
      if (character == '\n' && (tokens != null && tokens.size() > 0)) {
        linha++;
        coluna = 0;
      } else {
         coluna++;
      }
      return character;
    } else {
      return '@';
    }
  }

  private void unreadChar(PushbackReader pushbackReader, char character) throws IOException {
    pushbackReader.unread(character);
    if (character == '\n') {
      linha--;
    } else {
      coluna--;
    }
  }

  private Token getToken(char character, PushbackReader pushbackReader) throws IOException {
    if (Character.isLetter(character)) {
      int col = coluna;
      return handleIdentifierAndReservedWord(character, pushbackReader, col);
    }

    if (Type.MAIS.getId().equals(String.valueOf(character))
        || Type.MENOS.getId().equals(String.valueOf(character))) {
      int col = coluna;
      return handlePlusAndMinusOperation(character, pushbackReader, col);
    }

    if (Character.isDigit(character)) {
      int col = coluna;
      return handleDigit(character, pushbackReader, col);
    }

    if (SPECIAL_TOKENS.contains(String.valueOf(character))) {
      int col = coluna;
      return handleSpecialChars(character, pushbackReader, col);
    }

    if (BAR_SYMBOL.equals(String.valueOf(character))) {
      int col = coluna;
      return handleBar(character, pushbackReader, col);
    }

    return new Token(Type.ERRO, String.valueOf(character), linha, coluna);
  }

  private Boolean canBeConcat(char character)
  {
    return Character.isLetter(character)
        || UNDER_LINE_SYMBOL.equals(String.valueOf(character))
        || Character.isDigit(character);
  }

  private Token handleIdentifierAndReservedWord(char character, PushbackReader pushbackReader, int coluna)
      throws IOException {
    String id = String.valueOf(character);
    char nextCharacter = readCharacter(pushbackReader);

    while (canBeConcat(nextCharacter)) {
      id = id.concat(String.valueOf(nextCharacter));
      nextCharacter = readCharacter(pushbackReader);
    }
    unreadChar(pushbackReader, nextCharacter);
    Type type = Type.getTypeById(id);

    if (Type.SYSTEM.equals(type)) {
      nextCharacter = readCharacter(pushbackReader);
      while (nextCharacter != ' ' && nextCharacter != '(') {
        id = id.concat(String.valueOf(nextCharacter));
        nextCharacter = readCharacter(pushbackReader);
      }
      unreadChar(pushbackReader, nextCharacter);
      Type type2 = Type.getTypeById(id);
      return new Token(type2, id, linha, coluna);
    }

    return new Token(type, id, linha, coluna);
  }

  private Token handlePlusAndMinusOperation(char character, PushbackReader pushbackReader, int coluna)
      throws IOException {
    String operator = String.valueOf(character);
    char nextCharacter = readCharacter(pushbackReader);

    if (Character.isSpaceChar(nextCharacter)) {
      Type type = Type.getTypeById(operator);
      return new Token(type, operator, linha, coluna);
    }

    Token token = handleDigit(nextCharacter, pushbackReader, coluna);
    token.setPalavra(operator.concat(token.getPalavra()));
    return token;

  }

  private Token handleDigit(char character, PushbackReader pushbackReader, int coluna) throws IOException {
    String num = String.valueOf(character);
    char nextCharacter = readCharacter(pushbackReader);
    while (Character.isDigit(nextCharacter) || Type.PONTO.getId().equals(String.valueOf(nextCharacter))) {
      if (isInvalidFloatNumber(String.valueOf(nextCharacter), pushbackReader)) {
        return new Token(Type.ERRO, String.valueOf(nextCharacter), linha, coluna);
      }
      num = num.concat(String.valueOf(nextCharacter));
      nextCharacter = readCharacter(pushbackReader);
    }
    unreadChar(pushbackReader, nextCharacter);
    return new Token(Type.NUMERO, num, linha, coluna);
  }

  private Token handleSpecialChars(char character, PushbackReader pushbackReader, int coluna) throws IOException {
    String specialCharacter = String.valueOf(character);

    if (isComposeChar(specialCharacter)) {
      char nextCharacter = readCharacter(pushbackReader);
      String ch = specialCharacter.concat(String.valueOf(nextCharacter));
      Type typeById = Type.getTypeById(ch);

      if (isComposeType(typeById)) {
        return new Token(typeById, ch, linha, coluna);
      }
      unreadChar(pushbackReader, nextCharacter);
    }

    Type type = Type.getTypeById(specialCharacter);
    return new Token(type, specialCharacter, linha, coluna);
  }

  private Boolean isComposeChar(String character) {
    return AND_SYMBOL.equals(character) || EQUALS_SYMBOL.equals(character) || NEGATE_SYMBOL.equals(character);
  }

  private Boolean isComposeType(Type type) {
    return Type.AND.equals(type) || Type.IGUALDADE.equals(type) || Type.DIFERENCA.equals(type);
  }

  private Token handleBar(char character, PushbackReader pushbackReader, int coluna) throws IOException {
    char nextCharacter = readCharacter(pushbackReader);

    if (BAR_SYMBOL.equals(String.valueOf(nextCharacter))) {
      nextCharacter = readCharacter(pushbackReader);
      while (!NEW_LINE.equals(String.valueOf(nextCharacter))) {
        nextCharacter = readCharacter(pushbackReader);
      }
      return null;

    } else if (ASTERISK_SYMBOL.equals(String.valueOf(nextCharacter))) {
      while (true) {
        nextCharacter = readCharacter(pushbackReader);

        if (ASTERISK_SYMBOL.equals(String.valueOf(nextCharacter))) {
          nextCharacter = readCharacter(pushbackReader);
          if (BAR_SYMBOL.equals(String.valueOf(nextCharacter))) {
            break;
          }
        }
      }
      return null;
    } else {
      unreadChar(pushbackReader, nextCharacter);
      String id = String.valueOf(character);
      Type type = Type.getTypeById(id);
      return new Token(type, id, linha, coluna);
    }
  }

  private Boolean isInvalidFloatNumber(String character, PushbackReader pushbackReader) throws IOException {
    if (Type.PONTO.getId().equals(character)) {
      char nextCharacter = readCharacter(pushbackReader);
      if (Type.PONTO.getId().equals(String.valueOf(nextCharacter))) {
        return true;
      }
      unreadChar(pushbackReader, nextCharacter);
      return false;
    }
    return false;
  }
}