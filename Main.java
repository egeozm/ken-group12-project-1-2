import javax.swing.SwingUtilities;
import java.util.HashMap;

public class Main {
    public static void main(String arg[]) throws Exception {
        ODESolver s = new ODESolver();

        HashMap<String, Double> vals = new HashMap<String, Double>();
        vals.put("t", 0.0); vals.put("t1", 1.0); vals.put("y", 3.0); vals.put("h", 0.2);
        //pars a = new pars("1.1*(x0*y0)+(x0-y0)^(x0*y0)/10");
        FunctionParser a = new FunctionParser("t*y^2");
        //FunctionParser a = new FunctionParser("x0/10");
        System.out.println(ODESolver.EulerSolver(vals, a));
        //FunctionParser.evalFunction(vals, a.getFunctionArr());

        
    }
}
