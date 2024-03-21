import java.util.HashMap;

import static java.lang.Double.NaN;

public class ODESolver {
    public static double EulerSolver(HashMap<String, Double> varVals, FunctionParser f) throws Exception {
        while (varVals.get("x0") < varVals.get("x")) {
            varVals.put("y0",varVals.get("y0")+ varVals.get("h") * (FunctionParser.evalFunction(varVals, f.getFunctionArr())));
            if(FunctionParser.evalFunction(varVals, f.getFunctionArr()).equals(NaN)){
                System.out.println("Equation has no Solution!");
                return 0.0;
            }
            System.out.println(varVals.get("h") * FunctionParser.evalFunction(varVals, f.getFunctionArr()));
            varVals.put("x0",varVals.get("x0")+ varVals.get("h"));
        }
        return varVals.get("y0");
    }
}
