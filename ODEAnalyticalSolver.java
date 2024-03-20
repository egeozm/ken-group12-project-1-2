import javax.swing.*;
import java.awt.*;

public class ODEAnalyticalSolver {

    public static long stopwatch(Runnable method) {
        long start_time = System.nanoTime();
        method.run();
        long end_time = System.nanoTime();
        long time = end_time - start_time;
        return time;
    }

    public static void test(int j) {
        for (int i = 0; i < j; i++) {
            // whatever
        }
    }

    public static double f(double t, double y, double k) {
        return -k * y;
    }

    public static double analyticalSolution(double t, double y0, double k) {
        return y0 * Math.exp(-k * t);
    }

    public static void main(String[] args) {
        System.out.println("program executed");

        System.out.println("That is the execution time: "+stopwatch(() -> {
            test(10000);
        }));

        System.out.println(Double.toString(f(4, 4, 4)));
        System.out.println(Double.toString(analyticalSolution(6, 7, 0.2)));

        SwingUtilities.invokeLater(() -> {
            LogLogPlot ex = new LogLogPlot();
            ex.setVisible(true);
        });
    }
}

class LogLogPlot extends JFrame {

    private double[] xData = {0.1, 0.2, 0.3, 0.4, 0.5};//Data of your choice
    private double[] yData = {0.10, 0.20, 0.30, 0.60, 0.100};// date of your choice


    public LogLogPlot() {
        //basic properties of the window
        setTitle("LogLogPlot Example");//title
        setSize(800, 600);//size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//closing operation
        setLocationRelativeTo(null);//makes it appear at the centere of the display

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // calculate the bounds of the data
                double minX = Double.MAX_VALUE;
                double maxX = Double.MIN_VALUE;
                double minY = Double.MAX_VALUE;
                double maxY = Double.MIN_VALUE;
                for (double x : xData) {
                    minX = Math.min(minX, x);//looks for the smallest x value
                    maxX = Math.max(maxX, x);//looks for the biggest x value
                }

                for (double y : yData) {
                    minY = Math.min(minY, y);//looks for the smallest y value
                    maxY = Math.max(maxY, y);//looks for the biggest y value
                }

                // Draw the data points
                g2d.setColor(Color.BLUE);//change color here
                for (int i = 0; i < xData.length; i++) {//iterates through all data points
                    double x = xData[i];
                    double y = yData[i];

                    // Calculate the logarithmic transformation for x and y
                    double logX = Math.log10(x);
                    double logY = Math.log10(y);

                    // Map the logarithmic coordinates to screen coordinates
                    int xPixel = (int) ((logX - Math.log10(minX)) / (Math.log10(maxX) - Math.log10(minX)) * getWidth());
                    int yPixel = getHeight() - (int) ((logY - Math.log10(minY)) / (Math.log10(maxY) - Math.log10(minY)) * getHeight());

                    // Draw the data point
                    g2d.fillOval(xPixel - 5, yPixel - 5, 10, 10);//change appereance of data point here
                }
            }
        };
        add(chartPanel);
    }
}