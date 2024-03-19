import javax.swing.SwingUtilities;

public class Main {
    public static void main(String arg[]) {
        System.out.println("hello");
        ODESolver s = new ODESolver();
        s.p();

        SwingUtilities.invokeLater(() -> new GUI());
    }
}
