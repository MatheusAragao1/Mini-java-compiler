import java.util.Arrays;
import java.util.List;

public class ScannerConfig {
    public static final List<String> IGNORE_LIST = Arrays.asList(" ", "\n", "\r", "\t", "@");
    public static final List<String> SPECIAL_TOKENS = Arrays.asList(".", ":", ",", ";", "(", ")", "{", "}",
        "<", "[", "]");
}
