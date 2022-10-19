import java.util.stream.Stream;

public enum Type 
{
    
  CLASS("class"),
  PUBLIC("public"),
  STATIC("static"),
  VOID("void"),
  MAIN("main"),
  STRING("String"),
  EXTENDS("extends"),
  ATRIBUICAO("="),
  ESCREVA("System.out.println"),
  INTEIRO("int"),
  BOOLEAN("boolean"),
  IF("if"),
  ELSE("else"),
  WHILE("while"),
  LENGTH("length"),
  THIS("this"),
  NEW("new"),
  NEGATE("!"),
  VIRGULA(","),
  PONTO_E_VIRGULA(";"),
  PONTO("."),
  MAIS("+"),
  MENOS("-"),
  MULTIPLICACAO("*"),
  AND("&&"),
  MENOR("<"),
  ABRE_PARENTESIS("("),
  FECHA_PARENTESIS(")"),
  ABRE_COLCHETES("["),
  FECHA_COLCHETES("]"),
  ABRE_CHAVES("{"),
  FECHA_CHAVES("}"),
  NUMERO(null),
  ERRO(null),
  IDENTIFICADOR(null),
  DIVISAO("/"),
  CHAR("char"),
  NULL("null"),
  RETURN("return"),
  BOOLEAN_TRUE("true"),
  BOOLEAN_FALSE("false"),
  IGUALDADE("=="),
  SYSTEM("System"),
  DIFERENCA("!=");

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