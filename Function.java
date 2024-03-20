import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Function {
    private String[] variableOrder;
    private Double[] variableCoefficients;
    private String[] variableOperands;
    public Function(String[] variableOrder, Double[] variableCoefficients, String[] variableOperands){
        this.variableOrder = variableOrder;
        this.variableCoefficients = variableCoefficients;
        this.variableOperands = variableOperands;
    }
    public Function cutFunction(int leftOperIdx, int rightOperIdx){
        int leftVarIdx = 0;
        int rightVarIdx = 0;
        int parenthesisLeftCount = 0;
        int parenthesisRightCount = 0;
        for(int i = 0; i < variableOperands.length; i++){
            if(i < leftOperIdx && (variableOperands[i] !="(" || variableOperands[i] !=")")){
                leftVarIdx++;
                rightVarIdx++;
            }
            else if(variableOperands[i] !="(" || variableOperands[i] !=")"){
                //if(parenthesisLeftCount == parenthesisRightCount){
                //    Character[] newVariableOrder = Arrays.copyOfRange()
                //}
                leftVarIdx++;
                rightVarIdx++;
            }
            else if(variableOperands[i] == "("){
                parenthesisLeftCount++;
            }
            else if(variableOperands[i] == ")"){
                parenthesisRightCount++;
            }
        }
        return null;
    }
    public double funcVal(HashMap<String, Double> variables) throws Exception {
        double ans = variables.get(variableOrder[0])*variableCoefficients[0];
        if(new HashSet<String>(Arrays.asList(variableOrder)).size()>variables.size())
            throw new Exception("Insufficient number of variables");
        else{
            int varIdx = 1;
            int operIdx = 0;
            double lastVal = variables.get(variableOrder[0]);
            boolean lastWasPower = false;
            while (varIdx < variableOrder.length){
                switch(variableOperands[operIdx]){
                    case "+":
                        lastVal=variables.get(variableOrder[varIdx]);
                        break;
                    case "-":
                        lastVal=variables.get(variableOrder[varIdx]);
                        break;
                    case "*":
                        lastVal=(lastVal)*variables.get(variableOrder[varIdx])-lastVal;
                        break;
                    case "/":
                        lastVal=(lastVal)/variables.get(variableOrder[varIdx])-lastVal;
                        break;
                    case "^":
                        lastVal=Math.pow(lastVal, variableCoefficients[varIdx])-lastVal;
                        lastWasPower = true;
                        break;
                    case "(":
                        lastVal=(lastVal)/variables.get(variableOrder[varIdx])-lastVal;
                        break;
                    case ")":
                        lastVal=(lastVal)/variables.get(variableOrder[varIdx])-lastVal;
                        break;
                }
                if(lastWasPower)
                    lastVal*=variableCoefficients[varIdx-1];
                else
                    lastVal*=variableCoefficients[varIdx];
                lastWasPower = false;
                ans+=lastVal;
                varIdx++;
                operIdx++;
            }
        }
        return ans;
    }
    public Function(String s){
        String operands = "+-*/^()";
        String nums = "1234567890.";
        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<Double> list2 = new ArrayList<>();
        ArrayList<String> list3 = new ArrayList<>();
        String buf = "";
        boolean lastWasCharacter = false;
        for(int i = 0; i < s.length(); i++){
            if(operands.contains(String.valueOf(s.charAt(i)))){
                list1.add(String.valueOf(s.charAt(i)));
                list3.add(buf);
                buf = "";
            }
            else if(nums.contains(String.valueOf(s.charAt(i))) && !lastWasCharacter){
                buf += String.valueOf(s.charAt(i));
            }
            else {
                if(lastWasCharacter == false) {
                    try {
                        if (buf == "")
                            list2.add(1.0);
                        else {
                            list2.add(Double.parseDouble(String.valueOf(s.charAt(i))));
                        }
                        buf = "";
                    } catch (NumberFormatException e) {
                    }
                }
                buf += String.valueOf(s.charAt(i));
                lastWasCharacter = true;
                continue;
            }
            lastWasCharacter = false;
        }
        if(lastWasCharacter)
            list3.add(buf);
        else
            list2.add(Double.parseDouble(String.valueOf((buf))));
        variableOperands = list1.toArray(new String[0]);
        variableCoefficients = list2.toArray(new Double[0]);
        variableOrder = list3.toArray(new String[0]);
    }
}
