import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser
{
    public SymbolTable symbolTable = new SymbolTable();
    public Token actualToken = null;
    public int globalIndex = 0;

    public Boolean newError(String message) 
    {
        System.out.println(message + "\nLinha: " + actualToken.getLinha() + " Coluna: " + actualToken.getColuna());
        return false;
    }

    public static List<Type> mainTypesToVerify = Arrays.asList(Type.PUBLIC, Type.STATIC, Type.VOID, Type.MAIN, Type.ABRE_PARENTESIS, Type.STRING, Type.ABRE_COLCHETES, Type.FECHA_COLCHETES, Type.IDENTIFICADOR, Type.FECHA_PARENTESIS, Type.ABRE_CHAVES );
    
    private Boolean isTypeDeclaration()
    {
        return Type.INTEIRO.equals(actualToken.getType()) || Type.BOOLEAN.equals(actualToken.getType());
    }

    private Boolean isIdentifier()
    {
        return Type.IDENTIFICADOR.equals(actualToken.getType());
    }

    private Boolean isEndOfBlock()
    {
        return Type.FECHA_CHAVES.equals(actualToken.getType());
    }

    private Boolean isValidIdentifierDeclaration(Type typeVar)
    {
        return (Type.NUMERO.equals(actualToken.getType()) && Type.INTEIRO.equals(typeVar)) || ((Type.TRUE_BOOLEAN.equals(actualToken.getType()) || Type.FALSE_BOOLEAN.equals(actualToken.getType())) && Type.BOOLEAN.equals(typeVar));
    }

    private Boolean IsDeclaredIdentifierOrNumber()
    {
        return (Type.IDENTIFICADOR.equals(actualToken.getType()) && identifierIsInSymbolTable(actualToken.getPalavra())) || Type.NUMERO.equals(actualToken.getType());
    }

    private Boolean possibleMathOperations()
    {
        return Type.MAIS.equals(actualToken.getType()) || Type.MENOS.equals(actualToken.getType())
        || Type.MULTIPLICACAO.equals(actualToken.getType()) || Type.DIVISAO.equals(actualToken.getType());
    }

    public Boolean analize(List<Token> tokenList) 
    {
        setNextToken(tokenList);
        if(Type.CLASS.equals(actualToken.getType())) 
        {
            setNextToken(tokenList);
            if(isIdentifier())
            {
                putIdentifierInSymbolTable("classe");
                setNextToken(tokenList);
                if (Type.ABRE_CHAVES.equals(actualToken.getType()))
                {
                    setNextToken(tokenList);
                    if (deepMainAnalize(tokenList)) 
                    {
                        if (isEndOfBlock()) 
                        {
                            return true;
                        }
                        newError("Faltando fechamento das chaves");
                        return false;
                    }
                    return false;
                }
                newError("Faltando abertura de chaves");
                return false;
            }
            newError("Faltando declaração do nome da classe");
            return false;
        }
        newError("Faltando declaração de classe");
        return false;
    }

    private Boolean deepMainAnalize(List<Token> tokenList)
    {
        for (Type type : mainTypesToVerify) {
            Boolean isEqual = type.equals(actualToken.getType());
            if(isEqual)
            {
                if(isIdentifier())
                {
                    insertIdentifierInSymbolTable("args");
                }
                setNextToken(tokenList);
                if(Type.ABRE_CHAVES.equals(actualToken.getType()))
                {
                    setNextToken(tokenList);
                    if(deepScopeAnalize(tokenList))
                    {
                        if (isEndOfBlock()) 
                        {
                            return true;
                        }
                        newError("Faltando fechar chaves");
                        return false;
                    }
                }
            }
            else
            {
                newError("Erro na mainClass");
                return false;
            }
        }   
        return true;
    }

    private Boolean deepScopeAnalize(List<Token> tokenList) 
    {
        if(isTypeDeclaration()) 
        {
            return analizeIdentifier(tokenList);
        } 
        else if (Type.PRINT.equals(actualToken.getType())) {
            setNextToken(tokenList);
            return analizePrint(tokenList);
        } 
        else if (Type.IDENTIFICADOR.equals(actualToken.getType()) || Type.NUMERO.equals(actualToken.getType()) ) 
        {
            return analizeIdentifierOrNumber(tokenList);
        } 
        else if (isEndOfBlock()) 
        {
            setNextToken(tokenList);
            return isEndOfBlock();
        }

        newError("Bloco invalido");
        return false;
    }

    private Boolean analizeIdentifierOrNumber(List<Token> tokenList) 
    {
        String value1 = actualToken.getPalavra();
        if (IsDeclaredIdentifierOrNumber()) {
            setNextToken(tokenList);
            if (possibleMathOperations()) 
            {    
                return analisaOperacao(value1, tokenList);
            }
            return true;
        }
        newError("Faltando valor/identificador");
        return false;
    }

    private Boolean analisaOperacao(String value1, List<Token> tokenList) 
    {
            setNextToken(tokenList);
            if (IsDeclaredIdentifierOrNumber())
            {
                String value2 = actualToken.getPalavra();
                setNextToken(tokenList); 
                if (Type.FIM_DE_COMANDO.equals(actualToken.getType())) 
                {
                    setNextToken(tokenList);
                    return deepScopeAnalize(tokenList);
                }
                else if (possibleMathOperations())
                {
                    analisaOperacao(value2, tokenList);
                }
                return true;
            }
            newError("Faltando valor/identificador");
            return false;
    }

    private Boolean analizePrint(List<Token> tokenList) 
    {
            if (Type.ABRE_PARENTESIS.equals(actualToken.getType())) {
                setNextToken(tokenList);
                if (deepScopeAnalize(tokenList)) 
                {
                    if (Type.FECHA_PARENTESIS.equals(actualToken.getType())) 
                    {
                        setNextToken(tokenList);
                        if (Type.FIM_DE_COMANDO.equals(actualToken.getType())) 
                        {
                            setNextToken(tokenList);
                            return deepScopeAnalize(tokenList);
                        }
                        newError("Faltando ponto virgula");
                        return false;
                    }
                    newError("Faltando fechar parenteses");
                    return false;
                }
                return false;
            }
            newError("Faltando abertura de parenteses");
            return false;

    }

    private Boolean analizeIdentifier(List<Token> tokenList) 
    {
        String palavraVar = actualToken.getPalavra();
        Type typeVar = actualToken.getType();
        setNextToken(tokenList);
        if (isIdentifier()) 
        {
            insertIdentifierInSymbolTable(palavraVar);
            setNextToken(tokenList);
            if (Type.ATRIBUICAO.equals(actualToken.getType())) 
            {
                setNextToken(tokenList);
                if (isValidIdentifierDeclaration(typeVar))
                {
                    setNextToken(tokenList);
                    if (Type.FIM_DE_COMANDO.equals(actualToken.getType())) 
                    {
                        setNextToken(tokenList);
                        return deepScopeAnalize(tokenList);
                    }
                    newError("Faltando ponto virgula");
                    return false;
                }
                newError("Faltando valor ou atribuindo em tipo errado");
                return false;
            }
            newError("Faltando operador de atribuição");
            return false;
        }
        newError("Faltando identificador da variavel");
        return false;
    }

    private void insertIdentifierInSymbolTable(String tipagem) 
    {
        actualToken.setTipagem(tipagem);
        symbolTable.getFullTable().put(actualToken.getPalavra(), actualToken);        
    }

    private Boolean identifierIsInSymbolTable(String palavra) 
    {
        return symbolTable.IsInTable(palavra);        
    }

    private void setNextToken(List<Token> tokenList)
    {
        if (actualToken == null && globalIndex == 0)
        {
            actualToken = tokenList.get(globalIndex);
        }
        else
        {
            globalIndex++;
            actualToken = tokenList.get(globalIndex);
        }
    }

    private void putIdentifierInSymbolTable(String tipagem) 
    {
            actualToken.setTipagem(tipagem);
            symbolTable
                .getFullTable()
                .put(actualToken.getPalavra(), actualToken);        
    }
}