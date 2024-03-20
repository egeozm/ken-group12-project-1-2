import javax.swing.SwingUtilities;
import java.util.HashMap;

public class Main {
    public static void main(String arg[]) throws Exception {
        ODESolver s = new ODESolver();
        HashMap<String, Double> vals = new HashMap<String, Double>();
        vals.put("x0", 0.0); vals.put("x", 1.0); vals.put("y0", 3.0); vals.put("h", 0.25);
        System.out.println(ODESolver.EulerSolver(vals, new Function("x0*y0^2")));

        
    }
}
