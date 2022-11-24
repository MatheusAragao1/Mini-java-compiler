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
    
    public Boolean analize(List<Token> tokenList) 
    {
        setNextToken(tokenList);
        if(Type.CLASS.equals(actualToken.getType())) 
        {
            setNextToken(tokenList);
            if(Type.IDENTIFICADOR.equals(actualToken.getType())) 
            {
                putIdentifierInSymbolTable("classe");
                setNextToken(tokenList);
                if (Type.ABRE_CHAVES.equals(actualToken.getType()))
                {
                    setNextToken(tokenList);
                    if (deepMainAnalize(tokenList)) 
                    {
                        if (Type.FECHA_CHAVES.equals(actualToken.getType())) 
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
                if(Type.IDENTIFICADOR.equals(actualToken.getType()))
                {
                    insertIdentifierInSymbolTable("args");
                }
                setNextToken(tokenList);
            }
            else
            {
                newError("Erro na mainClass");
                return false;
            }
        }   
        return true;
    }

    private void insertIdentifierInSymbolTable(String tipagem) 
    {
        actualToken.setTipagem(tipagem);
        symbolTable.getFullTable().put(actualToken.getPalavra(), actualToken);        
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