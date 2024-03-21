import java.util.HashMap;

import static java.lang.Double.NaN;

public class ODESolver {
    public static double EulerSolver(HashMap<String, Double> varVals, FunctionParser f) throws Exception {
        while (varVals.get("t") < varVals.get("t1")) {
            varVals.put("y",varVals.get("y")+ varVals.get("h") * (FunctionParser.evalFunction(varVals, f.getFunctionArr())));
            if(FunctionParser.evalFunction(varVals, f.getFunctionArr()).equals(NaN)){
                System.out.println("Equation has no Solution!");
                return 0.0;
            }
            System.out.println(varVals.get("h") * FunctionParser.evalFunction(varVals, f.getFunctionArr()));
            varVals.put("t",varVals.get("t")+ varVals.get("h"));
        }
        return varVals.get("y");
    }
}
