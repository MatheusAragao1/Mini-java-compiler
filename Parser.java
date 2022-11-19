import java.util.List;

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
    
    public Boolean analize(List<Token> tokenList) 
    {
        setNextToken(tokenList);
        if(Type.CLASS.equals(actualToken.getType())) 
        {
            setNextToken(tokenList);
            if(Type.IDENTIFICADOR.equals(actualToken.getType())) 
            {
                putIdentifierInSymbolTable("nome_classe");
                setNextToken(tokenList);
                if (Type.ABRE_CHAVES.equals(actualToken.getType()))
                {
                    setNextToken(tokenList);
                    // adicionar if analise ok return true
                    newError("Restante do algoritmo ainda não implementado");
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
                .getTabela()
                .put(actualToken.getPalavra(), actualToken);        
    }
}