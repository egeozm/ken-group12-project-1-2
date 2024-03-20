import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Function {
    private Character[] variableOrder;
    private Double[] variableCoefficients;
    private Character[] variableOperands;
    public Function(Character[] variableOrder, Double[] variableCoefficients, Character[] variableOperands){
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
            if(i < leftOperIdx && (variableOperands[i] !='(' || variableOperands[i] !=')')){
                leftVarIdx++;
                rightVarIdx++;
            }
            else if(variableOperands[i] !='(' || variableOperands[i] !=')'){
                //if(parenthesisLeftCount == parenthesisRightCount){
                //    Character[] newVariableOrder = Arrays.copyOfRange()
                //}
                leftVarIdx++;
                rightVarIdx++;
            }
            else if(variableOperands[i] == '('){
                parenthesisLeftCount++;
            }
            else if(variableOperands[i] == ')'){
                parenthesisRightCount++;
            }
        }
        return null;
    }
    public double funcVal(HashMap<Character, Double> variables) throws Exception {
        double ans = variableOrder[0]*variableCoefficients[0];
        if(new HashSet<Character>(Arrays.asList(variableOrder)).size()>variables.size())
            throw new Exception("Insufficient number of variables");
        else{
            int varIdx = 1;
            int operIdx = 0;
            double lastVal = 0;
            while (varIdx < variableOrder.length){
                switch(variableOperands[operIdx]){
                    case '+':
                        lastVal=variables.get(variableOrder[varIdx]);
                    case '-':
                        lastVal=variables.get(variableOrder[varIdx]);
                    case '*':
                        lastVal=(lastVal-1)*variables.get(variableOrder[varIdx]);
                    case '/':
                        lastVal=(lastVal-1)/variables.get(variableOrder[varIdx]);
                }
                lastVal*=variableCoefficients[varIdx]*variableCoefficients[varIdx];
                ans+=lastVal;
                varIdx++;
                operIdx++;
            }
        }
        return ans;
    }
}
