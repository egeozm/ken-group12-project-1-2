import javax.swing.SwingUtilities;

public class Main {
    public static void main(String arg[]) {
        ODESolver s = new ODESolver();
        System.out.println(s.EulerSolver(0.0, 3.0, 0.2, 1.0));



        SwingUtilities.invokeLater(() -> new GUI());
    }
}
