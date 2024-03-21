import java.util.HashMap;

public class ODESolver {
    public static double EulerSolver(HashMap<String, Double> varVals, Function func) throws Exception {
        while (varVals.get("x0") < varVals.get("x")) {
            varVals.put("y0",varVals.get("y0")+ varVals.get("h") * (func.funcVal(varVals)));
            varVals.put("x0",varVals.get("x0")+ varVals.get("h"));
        }
        return varVals.get("y0");
    }
}
