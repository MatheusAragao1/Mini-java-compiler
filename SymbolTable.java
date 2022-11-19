import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable 
{
    private Map<String, Token> table;

    public SymbolTable() 
    {
        this.table = new HashMap<>();
    }

    public void setProperty(String key, String prop, String value) throws NoSuchFieldException, IllegalAccessException 
    {
        Token token = table.get(key);
        Field field = token.getClass().getDeclaredField(prop);
        field.set(token, value);
    }

    public String getProperty(String key, String prop) throws NoSuchFieldException, IllegalAccessException 
    {
        Token token = table.get(key);
        Field field = token.getClass().getDeclaredField(prop);
        return (String) field.get(token);
    }

    public Map<String, Token> getTabela() 
    {
        return table;
    }
}