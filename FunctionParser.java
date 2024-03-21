import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FunctionParser {
    public ArrayList<String> getFunctionArr() {
        return functionArr;
    }

    private ArrayList<String> functionArr;
    private static String allOperands = "+-/*^()";
    private static String operands = "+-()";
    private static String operands2nd = "/*^";
    private static String nums = "1234567890.";
    private int k = 0;
    public FunctionParser(String functionStr) {
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
            for(int i = idx-1; i >= 0; i--){
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
    public static Double evalFunction(HashMap<String, Double> vals, ArrayList<String> localFunctionArr){
        ArrayList<String> arr = (ArrayList<String>) localFunctionArr.clone();
        for (Map.Entry<String, Double> entry : vals.entrySet()) {
            Collections.replaceAll(arr, entry.getKey(), Double.toString(entry.getValue()));
        }
        int firstIdx = findFirstValue(vals, arr);
        Double sum = null;
        for(int i = 0; i < arr.size(); i++){
            if(arr.get(i).equals("^"))
                System.out.println();
            if(arr.get(i).equals("(")) {
                ArrayList<String> newArr = new ArrayList<>(arr.subList(i, arr.size()));
                int buf = findParenthesis(newArr);
                i+=findParenthesis(newArr);
                newArr = new ArrayList<>(arr.subList(i-buf+1, i));
                if(sum == null)
                    sum = evalFunction(vals, newArr);
                else
                    sum = operandsAction(sum, evalFunction(vals, newArr), arr.get(i - buf - 1));
            }
            else if(!allOperands.contains(arr.get(i))){
                if(sum == null)
                    sum = Double.parseDouble(arr.get(i));
                else
                    sum = operandsAction(sum, Double.parseDouble(arr.get(i)), arr.get(i-1));
            }
        }
        return sum;
        /*int firstIdx = findFirstValue(vals, localFunctionArr);
        Double sum = 0.0;
        try {
            sum = Double.parseDouble(localFunctionArr.get(firstIdx));
        } catch (NumberFormatException e) {
            sum = vals.get(localFunctionArr.get(firstIdx));
        }
        int parenthesisCount = 0;
        double currentVal = 0;
        boolean flagWasBefore = false;
        parenthesisCount = 0;
        boolean flag = true;
        for(int i = firstIdx+1; i < localFunctionArr.size()-1; i++){
            if(localFunctionArr.get(i).equals(")")) {
                parenthesisCount--;
                if(parenthesisCount==0 && flag)
                    return sum;
                else if(parenthesisCount==0 && !flag)
                    flag = true;
            }
            if(localFunctionArr.get(i).equals("("))
                parenthesisCount++;
            if(!flag)
                continue;
            if(!allOperands.contains(localFunctionArr.get(i+1))) {
                try {
                    currentVal = Double.parseDouble(localFunctionArr.get(i + 1));
                } catch (NumberFormatException e) {
                    currentVal = vals.get(localFunctionArr.get(i + 1));
                }
            }
            if(!localFunctionArr.get(i+1).equals("(")) {
                if (localFunctionArr.get(i).equals("+"))
                    sum += currentVal;
                else if (localFunctionArr.get(i).equals("-"))
                    sum -= currentVal;
                else if (localFunctionArr.get(i).equals("*"))
                    sum *= currentVal;
                else if (localFunctionArr.get(i).equals("/"))
                    sum /= currentVal;
                else if (localFunctionArr.get(i).equals("^"))
                    sum = Math.pow(sum, currentVal);
            }
            else{
                parenthesisCount++;
                if (localFunctionArr.get(i).equals("+")) {
                    sum += evalFunction(vals, new ArrayList<>(localFunctionArr.subList(i + 2, localFunctionArr.size())));
                    flag = false;
                }
                else if (localFunctionArr.get(i).equals("-")){
                    sum -= evalFunction(vals, new ArrayList<>(localFunctionArr.subList(i+2, localFunctionArr.size())));
                    flag = false;
                }
                else if (localFunctionArr.get(i).equals("*")){
                    sum *= evalFunction(vals, new ArrayList<>(localFunctionArr.subList(i+2, localFunctionArr.size())));
                    flag = false;
                }
                else if (localFunctionArr.get(i).equals("/")){
                    sum /= evalFunction(vals, new ArrayList<>(localFunctionArr.subList(i+2, localFunctionArr.size())));
                    flag = false;
                }
                else if (localFunctionArr.get(i).equals("^")){
                    Math.pow(sum, evalFunction(vals, new ArrayList<>(localFunctionArr.subList(i+2, localFunctionArr.size()))));
                    flag = false;
                }
            }
        }
        return sum;*/
    }
    private static int findParenthesis(ArrayList<String> a){
        int parenthesisCount = 0;
        for(int i = 0; i < a.size(); i++){
            if(a.get(i).equals("("))
                parenthesisCount++;
            else if(a.get(i).equals(")"))
                parenthesisCount--;
            if(parenthesisCount == 0)
                return i;
        }
        return 0;
    }
    private static double operandsAction(double first, double second, String operand){
        if (operand.equals("+"))
            return first+second;
        else if(operand.equals("-"))
            return first-second;
        else if(operand.equals("*"))
            return first*second;
        else if(operand.equals("/"))
            return first/second;
        else
            return Math.pow(first, second);
    }
    private static int findFirstValue(HashMap<String, Double> vals, ArrayList<String> localFunctionArr){
        double sum = 0;
        for(int i = 0; i < localFunctionArr.size()-1; i++){
            try {
                sum = Double.parseDouble(localFunctionArr.get(i));
            } catch (NumberFormatException e) {
                try {
                    sum = vals.get(localFunctionArr.get(i));
                }
                catch (NullPointerException s){
                    continue;
                }
            }
            if(sum >= 0)
                return i;
        }
        return 0;
    }
}
