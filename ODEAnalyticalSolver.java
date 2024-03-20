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
    public static void print(String str){
        System.out.println(str);
}
    public static void main(String[] args) {
        System.out.println("program executed");

        System.out.println("That is the execution time: "+stopwatch(() -> {
            test(10000);
        }));

        System.out.println(Double.toString(f(4, 4, 4)));
        System.out.println(Double.toString(analyticalSolution(10, 10, 0.2)));

        SwingUtilities.invokeLater(() -> {
            LogLogPlot ex = new LogLogPlot();
            ex.setVisible(true);
        });
    }
}

class LogLogPlot extends JFrame {

    private double[] xData = {0.1, 0.2, 0.3, 0.4, 0.5,0.6,0.7,0.8,0.9,0.1};//Data of your choice
    private double[] yData = {0.010, 00.10, 0.030, 0.060, 0.0100,0.02,0.040,0.01,0.07,0.050};// date of your choice


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

                    // Calculate the bounds of the data
                    double minX = Double.MAX_VALUE;
                    double maxX = Double.MIN_VALUE;
                    double minY = Double.MAX_VALUE;
                    double maxY = Double.MIN_VALUE;
                    for (double x : xData) {
                        minX = Math.min(minX, x);
                        maxX = Math.max(maxX, x);
                    }

                    for (double y : yData) {
                        minY = Math.min(minY, y);
                        maxY = Math.max(maxY, y);
                    }

                    // Draw the data points (blue line)
                    g2d.setColor(Color.BLUE);
                    for (int i = 0; i < xData.length - 1; i++) {
                        double x1 = xData[i];
                        double y1 = yData[i];
                        double x2 = xData[i + 1];
                        double y2 = yData[i + 1];

                        double logX1 = Math.log10(x1);
                        double logY1 = Math.log10(y1);
                        double logX2 = Math.log10(x2);
                        double logY2 = Math.log10(y2);

                        int xPixel1 = (int) ((logX1 - Math.log10(minX)) / (Math.log10(maxX) - Math.log10(minX)) * getWidth());
                        int yPixel1 = getHeight() - (int) ((logY1 - Math.log10(minY)) / (Math.log10(maxY) - Math.log10(minY)) * getHeight());
                        int xPixel2 = (int) ((logX2 - Math.log10(minX)) / (Math.log10(maxX) - Math.log10(minX)) * getWidth());
                        int yPixel2 = getHeight() - (int) ((logY2 - Math.log10(minY)) / (Math.log10(maxY) - Math.log10(minY)) * getHeight());

                        g2d.drawLine(xPixel1, yPixel1, xPixel2, yPixel2);
                    }
                    g2d.setColor(Color.BLACK);
                    int xAxis = (int) ((-Math.log10(minX) / (Math.log10(maxX) - Math.log10(minX))) * getWidth());
                    int yAxis = (int) ((Math.log10(maxY) / (Math.log10(maxY) - Math.log10(minY))) * getHeight());
                    g2d.drawLine(0, yAxis, getWidth(), yAxis); // X-axis
                    g2d.drawLine(xAxis, 0, xAxis, getHeight()); // Y-axis
                    g2d.setColor(Color.LIGHT_GRAY);
                    int numVerticalLines = 10; // Number of vertical grid lines
                    int numHorizontalLines = 10; // Number of horizontal grid lines
                    for (int i = 0; i < numVerticalLines; i++) {
                        double xValue = minX + (maxX - minX) * i / (numVerticalLines - 1);
                        double logX = Math.log10(xValue);
                        int xPixel = (int) ((logX - Math.log10(minX)) / (Math.log10(maxX) - Math.log10(minX)) * getWidth());
                        g2d.drawLine(xPixel, 0, xPixel, getHeight());
                    }
                    for (int i = 0; i < numHorizontalLines; i++) {
                        double yValue = minY + (maxY - minY) * i / (numHorizontalLines - 1);
                        double logY = Math.log10(yValue);
                        int yPixel = getHeight() - (int) ((logY - Math.log10(minY)) / (Math.log10(maxY) - Math.log10(minY)) * getHeight());
                        g2d.drawLine(0, yPixel, getWidth(), yPixel);
                    }

                    // Draw axis labels
                    g2d.setColor(Color.BLACK);
                    FontMetrics fm = g2d.getFontMetrics();
                    int labelPadding = 5;
                    // X-axis label
                    String xAxisLabel = "X-axis";
                    int xLabelWidth = fm.stringWidth(xAxisLabel);
                    int xLabelHeight = fm.getHeight();
                    g2d.drawString(xAxisLabel, getWidth() - xLabelWidth - labelPadding, getHeight() - xLabelHeight / 2);
                    // Y-axis label
                    String yAxisLabel = "Y-axis";
                    int yLabelWidth = fm.stringWidth(yAxisLabel);
                    g2d.drawString(yAxisLabel, labelPadding, yLabelWidth);

                    // Draw the red curve (fitting a polynomial curve)
                    g2d.setColor(Color.RED);
                    int[] xPixels = new int[xData.length];
                    int[] yPixels = new int[yData.length];
                    for (int i = 0; i < xData.length; i++) {
                        double logX = Math.log10(xData[i]);
                        double logY = Math.log10(yData[i]);
                        int xPixel = (int) ((logX - Math.log10(minX)) / (Math.log10(maxX) - Math.log10(minX)) * getWidth());
                        int yPixel = getHeight() - (int) ((logY - Math.log10(minY)) / (Math.log10(maxY) - Math.log10(minY)) * getHeight());
                        xPixels[i] = xPixel;
                        yPixels[i] = yPixel;
                    }
                    g2d.drawPolyline(xPixels, yPixels, xData.length);

                }
            };
        add(chartPanel);

}}