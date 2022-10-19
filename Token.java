public class Token 
{
    public Type type;
    public String palavra;
    public String escopo;
    public Integer linha;
    public String tipagem;
    public Integer coluna;

    public Token(Type type, String palavra, Integer linha, Integer coluna) 
    {
        this.type = type;
        this.palavra = palavra;
        this.linha = linha;
        this.coluna = coluna;
    }

    public Type getType() 
    {
        return type;
    }

    public void setType(Type type) 
    {
        this.type = type;
    }

    public String getPalavra() 
    {
        return palavra;
    }

    public void setPalavra(String palavra) 
    {
        this.palavra = palavra;
    }

    public Integer getLinha() 
    {
        return linha;
    }

    public void setLinha(Integer linha) 
    {
        this.linha = linha;
    }

    public Integer getColuna() 
    {
        return coluna;
    }

    public void setColuna(Integer coluna) 
    {
        this.coluna = coluna;
    }

    public String getTipagem() 
    {
        return tipagem;
    }

    public void setTipagem(String tipagem) 
    {
        this.tipagem = tipagem;
    }

    @Override
    public String toString() 
    {
        return String.format("linha: %s, Tipo: %s, Palavra: %s", linha, type, palavra);
    }
}