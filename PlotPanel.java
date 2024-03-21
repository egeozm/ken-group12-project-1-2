import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;

class PlotPanel extends JPanel {
    private List<List<Point2D.Double>> linesData;

    public PlotPanel() {
        linesData = new ArrayList<>();
        setPreferredSize(new Dimension(220, 320));
    }

    public void setLinesData(List<List<Point2D.Double>> linesData) {
        this.linesData = linesData;
        repaint(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        
        g2d.drawLine(30, getHeight() - 30, 30, 30);
        g2d.drawLine(30, getHeight() - 30, getWidth() - 30, getHeight() - 30);

        // Plotting the data
        Color[] lineColors = {Color.RED, Color.BLUE, Color.GREEN};
        for (int lineIndex = 0; lineIndex < linesData.size(); lineIndex++) {
            List<Point2D.Double> line = linesData.get(lineIndex);
            g2d.setColor(lineColors[lineIndex]);
            for (int i = 0; i < line.size() - 1; i++) {
                Point2D.Double p1 = line.get(i);
                Point2D.Double p2 = line.get(i + 1);
                int x1 = (int) (p1.x * 50) + 30; 
                int y1 = getHeight() - ((int) (p1.y * 5) + 30); 
                int x2 = (int) (p2.x * 50) + 30;
                int y2 = getHeight() - ((int) (p2.y * 5) + 30);
                g2d.draw(new Line2D.Double(x1, y1, x2, y2));
            }
        }
    }
}

class MainFrame extends JFrame {
    private PlotPanel plotPanel;

    public MainFrame() {
        plotPanel = new PlotPanel();
        add(plotPanel);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void plotODESolutions(List<List<Point2D.Double>> linesData) {
        plotPanel.setLinesData(linesData);
    }

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);

        // replace the mock data with the actual computed (X, Y)
        List<List<Point2D.Double>> linesData = new ArrayList<>();
        for (int j = 0; j < 3; j++) { // Three lines
            List<Point2D.Double> lineData = new ArrayList<>();
            for (int i = 0; i < 5; i++) { // Five points per line
                lineData.add(new Point2D.Double(i, Math.pow(i, j + 1)));
            }
            linesData.add(lineData);
        }

        mainFrame.plotODESolutions(linesData);
    }
}
