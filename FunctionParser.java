import java.util.ArrayList;

public class FunctionParser {
    private ArrayList<String> functionArr;
    private String operands = "+-*/^()";
    private String nums = "1234567890.";
    public FunctionParser(String function){
        functionArr.add(String.valueOf(function.charAt(0)));
        for(int i = 0; i < function.length(); i++){
        }
    }
}
