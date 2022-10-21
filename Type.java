import java.util.stream.Stream;

public enum Type 
{
    
  PRINT("System.out.println"),
  PUBLIC("public"),
  STATIC("static"),
  VOID("void"),
  CLASS("class"),
  STRING("String"),
  EXTENDS("extends"),
  ATRIBUICAO("="),
  INTEIRO("int"),
  NEGATE("!"),
  BOOLEAN("boolean"),
  IF("if"),
  ELSE("else"),
  MAIN("main"),
  THIS("this"),
  NEW("new"),
  FECHA_COLCHETES("]"),
  VIRGULA(","),
  FIM_DE_COMANDO(";"),
  PONTO("."),
  MAIS("+"),
  MENOS("-"),
  LARGURA("length"),
  AND("&&"),
  MENOR("<"),
  WHILE("while"),
  ABRE_PARENTESIS("("),
  FECHA_PARENTESIS(")"),
  ABRE_COLCHETES("["),
  MULTIPLICACAO("*"),
  ABRE_CHAVES("{"),
  FECHA_CHAVES("}"),
  DIVISAO("/"),
  CHAR("char"),
  NULO("null"),
  RETURN("return"),
  TRUE_BOOLEAN("true"),
  FALSE_BOOLEAN("false"),
  IGUAL("=="),
  SYSTEM("System"),
  DIFERENTE("!="),
  NUMERO(null),
  IDENTIFICADOR(null),
  ERRO(null);
  
  private String id;

  Type(String id)
  {
    this.id = id;
  }

  public static Type getTypeById(String id) 
  {
    return Stream.of(Type.values())
        .filter(type -> id.equals(type.id))
        .findFirst()
      .orElse(Type.IDENTIFICADOR);
  }

  public String getId() 
  {
    return id;
  }
}