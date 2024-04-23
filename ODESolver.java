import java.util.HashMap;

import static java.lang.Double.NaN;

public class ODESolver {
    public static double EulerSolver(HashMap<String, Double> varVals, FunctionParser f) throws Exception {
        double[] realVals = ODEAnalyticalSolver.analyticalSolution(varVals.get("t1").intValue(), varVals.get("y"), varVals.get("t"), varVals.get("h"));
        while (varVals.get("t") < varVals.get("t1")) {
            varVals.put("y",varVals.get("y")+ varVals.get("h") * (FunctionParser.evalFunction(varVals, f.getFunctionArr())));
            if(FunctionParser.evalFunction(varVals, f.getFunctionArr()).equals(NaN)){
                System.out.println("Equation has no Solution!");
                return 0.0;
            }
            varVals.put("t",varVals.get("t")+ varVals.get("h"));
        }
        return varVals.get("y");
    }
    public static double[][] EulerSolverPlotter(HashMap<String, Double> varVals, FunctionParser f) throws Exception {
        double[] realVals = ODEAnalyticalSolver.analyticalSolution(varVals.get("t1").intValue(), varVals.get("y"), varVals.get("t"), varVals.get("h"));
        double[] yvals;
        double[] xvals;
        double[][] ans=new double[2][];
        if((varVals.get("t1")-varVals.get("t"))%varVals.get("h") == 0) {
            yvals = new double[(int) ((varVals.get("t1") - varVals.get("t")) / varVals.get("h"))];
            xvals = new double[(int) ((varVals.get("t1") - varVals.get("t")) / varVals.get("h"))];
        }
        else {
            yvals = new double[(int) ((varVals.get("t1") - varVals.get("t")) / varVals.get("h")) + 1];
            xvals = new double[(int) ((varVals.get("t1") - varVals.get("t")) / varVals.get("h")) + 1];
        }
        int idx = 0;
        while (varVals.get("t") < varVals.get("t1")) {
            yvals[idx] =varVals.get("y");
            xvals[idx] =varVals.get("t");
            varVals.put("y",varVals.get("y")+ varVals.get("h") * (FunctionParser.evalFunction(varVals, f.getFunctionArr())));
            if(FunctionParser.evalFunction(varVals, f.getFunctionArr()).equals(NaN)){
                System.out.println("Equation has no Solution!");
                return null;
            }
            varVals.put("t",varVals.get("t")+ varVals.get("h"));
            idx++;
        }
        yvals[idx] =varVals.get("y");
        xvals[idx] =varVals.get("t");
        ans[0] = xvals;
        ans[1] = yvals;
        return ans;
    }

    public static void main(String[] args) {

    }
}
