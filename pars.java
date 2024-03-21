import java.util.ArrayList;
import java.util.HashMap;
public class pars {
    private ArrayList<String> functionArr;
    private static String operands = "+-()";
    private static String operands2nd = "/*^";
    private static String nums = "1234567890.";
    public pars(String functionStr) {
        functionArr = stringToFunction(functionStr);
    }
    public static ArrayList<String> stringToFunction(String functionStr){
        ArrayList<String> func = new ArrayList<String>();
        String buf = "";
        for(int i = 0; i < functionStr.length(); i++) {
            if(operands.contains(String.valueOf(functionStr.charAt(i))) || operands2nd.contains(String.valueOf(functionStr.charAt(i)))) {
                if(!buf.equals(""))
                    func.add(buf);
                buf = "";
                func.add(String.valueOf(functionStr.charAt(i)));
            }
            else{
                buf+=String.valueOf(functionStr.charAt(i));
            }
        }
        if(!buf.equals(""))
            func.add(buf);

        int i = 0;
        while (i < func.size()-1) {
            if(operands2nd.contains(func.get(i))) {
                if(func.get(i-1).equals(")"))
                    findNextParenthesis(func, i, true);
                else
                    func.add(i-1, "(");
                i++;
                if(func.get(i+1).equals("("))
                    findNextParenthesis(func, i, false);
                else
                    func.add(i+2, ")");
            }
            i++;
        }
        return func;
    }
    private static void findNextParenthesis(ArrayList<String> a, int idx, boolean direction){
        int parenthesisCounter = 0;
        if(direction == false){
            for(int i = idx+1; i < a.size(); i++){
                if(a.get(i).equals("("))
                    parenthesisCounter++;
                else if(a.get(i).equals(")"))
                    parenthesisCounter--;
                if(parenthesisCounter == 0) {
                    a.add(i, ")");
                    return;
                }
            }
        }
        else{
            for(int i = idx-1; i > 0; i--){
                if(a.get(i).equals("("))
                    parenthesisCounter++;
                else if(a.get(i).equals(")"))
                    parenthesisCounter--;
                if(parenthesisCounter == 0) {
                    a.add(i, "(");
                    return;
                }
            }
        }
    }
}
