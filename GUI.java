import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import javax.swing.text.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Random;

class PlotPanel extends JPanel {
    private double[] xData;
    private double[] yData;
    private String plotType = "Time Evolution"; // default plot type

    public void setPlotData(double[][] Data, String plotType) {
        this.xData = Data[0];
        this.yData = Data[1];
        this.plotType = plotType;
        repaint(); // repaint the panel whenever new data is set
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (xData == null || xData.length > 0) {
            // No data to plot
            return;
        }
        if (xData.length != yData.length) {
            // Data is not paired correctly, can't plot
            return;
        }
        int[] xPixels = new int[xData.length];
        int[] yPixels = new int[xData.length];
        for (int i = 0; i < xData.length; i++) {
            xPixels[i] = (int) (((double) xData[i] / getMax(xData)) * getWidth());
            yPixels[i] = getHeight() - (int) (((double) yData[i] / getMax(yData)) * getHeight());
        }

        // Draw lines between points
        g.setColor(Color.BLUE);
        for (int i = 0; i < xData.length - 1; i++) {
            g.drawLine(xPixels[i], yPixels[i], xPixels[i + 1], yPixels[i + 1]);
        }
    }
    private double getMax(double[] array) {
        double max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }
}
// NumericDocumentFilter class definition
class NumericDocumentFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string == null) {
            return;
        }
        if (isNumericOrPotentialNumeric(string)) {
            super.insertString(fb, offset, string, attr);
        }
        // If the string is not numeric or potentially numeric, do nothing (silently
        // ignore the input)
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (text == null) {
            return;
        }
        if (isNumericOrPotentialNumeric(text)) {
            super.replace(fb, offset, length, text, attrs);
        }
        // If the text is not numeric or potentially numeric, do nothing (silently
        // ignore the input)
    }

    private boolean isNumericOrPotentialNumeric(String text) {
        // Check if the text is numeric, or a minus sign for negative numbers, or
        // includes a decimal point
        return text.matches("-?\\d*(\\.\\d*)?");
    }
}

public class GUI extends JFrame {

    private JComboBox<String> solverSelector, outputSelector;
    private JComboBox<Integer> choiceSelector;
    private JTextField initialConditionsField, stepSizeField, integrationTimeField, functionField, initialTimeField,
            initialValueField, endTimeField, initialXField, initialYField, finalXField;
    private JButton startSimulationButton, resetSimulationButton, randomSimulationButton;
    private JPanel mainPanel, inputPanel, buttonPanel, outputPanel;
    private String eulerFunc;
    private static double h;
    private static double t0;
    private static double y0;


    private static double[][] eulerFuncVals;
    public static double[][] getEulerFuncVals() {
        return eulerFuncVals;
    }

    public static double getH() {
        return h;
    }

    public static double getT0() {
        return t0;
    }

    public static double getY0() {
        return y0;
    }

    public static double getT1() {
        return t1;
    }

    private static double t1;
    private PlotPanel plotPanel;

    public GUI() {
        super("ODE Solver");
        initializeComponents();
        setUpLayout();
        attachListeners();
        finalizeSetup();
    }

    private void initializeComponents() {
        // Solver selection
        String[] solvers = { "Euler Solver", "ODE Analytical Solver", "RK2 Solver" };
        solverSelector = new JComboBox<>(solvers);

        // Output selection
        String[] outputs = { "Time Evolution", "Phase Space" };
        outputSelector = new JComboBox<>(outputs);
        choiceSelector = new JComboBox<>(new Integer[] { 1, 2, 3, 4, 5, 6 }); // For RK2Solver choices
        choiceSelector.setVisible(false); // Initially hidden

        // Plotting panel
        plotPanel = new PlotPanel();
        plotPanel.setPreferredSize(new Dimension(400, 200)); // Set a preferred size for the plot area
        plotPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Just to visualize the panel's borders

        // Parameter inputs
        initialConditionsField = new JTextField(10);
        applyNumericFilter(initialConditionsField);

        stepSizeField = new JTextField(5);
        applyNumericFilter(stepSizeField);

        integrationTimeField = new JTextField(5);
        applyNumericFilter(integrationTimeField);

        functionField = new JTextField(10);
        stepSizeField = createNumericTextField(5);
        initialTimeField = createNumericTextField(5);
        initialValueField = createNumericTextField(5);
        endTimeField = createNumericTextField(5);
        initialXField = createNumericTextField(10);
        initialYField = createNumericTextField(10);
        finalXField = createNumericTextField(10);

        // Simulation control buttons
        startSimulationButton = new JButton("Start Simulation");
        resetSimulationButton = new JButton("Reset Simulation");
        randomSimulationButton = new JButton("Generate Random Values");

    }

    private void setUpLayout() {
        // Main panel layout
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);

        inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Solver:"));
        inputPanel.add(solverSelector);
        inputPanel.add(new JLabel("Initial Conditions:"));
        inputPanel.add(initialConditionsField);
        inputPanel.add(new JLabel("Step Size:"));
        inputPanel.add(stepSizeField);
        inputPanel.add(new JLabel("Integration Time:"));
        inputPanel.add(integrationTimeField);

        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        updateInterfaceForSolver((String) solverSelector.getSelectedItem());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(inputPanel, gbc);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(startSimulationButton);
        buttonPanel.add(resetSimulationButton);
        buttonPanel.add(randomSimulationButton);

        gbc.gridy = 1;
        mainPanel.add(buttonPanel, gbc);

        outputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outputPanel.add(new JLabel("Output:"));
        outputPanel.add(outputSelector);

        gbc.gridy = 2;
        mainPanel.add(outputPanel, gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add(plotPanel, gbc);

        add(mainPanel);

        // Adding choiceSelector to the input panel or wherever appropriate
        inputPanel.add(choiceSelector);

    }

    private void attachListeners() {
        Random random = new Random();
        startSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();// start

            }
        });
        resetSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {// clear all textfields
                initialConditionsField.setText("");
                stepSizeField.setText("");
                integrationTimeField.setText("");
                functionField.setText(""); // Clear function field
                initialTimeField.setText(""); // Clear initial time field
                initialValueField.setText(""); // Clear initial value field
                endTimeField.setText(""); // Clear end time field
                choiceSelector.setSelectedIndex(0); // Reset choice if needed
                initialXField.setText("");
                initialYField.setText("");
                finalXField.setText("");
                choiceSelector.setSelectedIndex(0);

            }
        });
        randomSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initialConditionsField.setText(String.valueOf(random.nextInt(100)));
                stepSizeField.setText(String.valueOf(random.nextInt(100)));
                integrationTimeField.setText(String.valueOf(random.nextInt(100) + 1)); // +1 to avoid zero

                if ("Euler Solver".equals(solverSelector.getSelectedItem())) {
                    // Euler fields: generate double values from 0.0 to 10.0
                    DecimalFormat df = new DecimalFormat("#.##"); // Format to two decimal places

                    // Generate random values within a specified range
                    int initialTime = (int) (0.0 + (10.0 - 0.0) * random.nextDouble()); // Example: from 0.0 to 10.0
                    int initialValue = (int) (1.0 + (10.0 - 1.0) * random.nextDouble()); // Example: from 1.0 to 10.0
                    int endTime = (int) (initialTime + (20.0 - initialTime) * random.nextDouble()); // Ensure endTime is
                    // greater than
                    // initialTime

                    // Set the text fields with formatted random values
                    initialTimeField.setText(df.format(initialTime));
                    initialValueField.setText(df.format(initialValue));
                    endTimeField.setText(df.format(endTime));
                } else if ("RK2 Solver".equals(solverSelector.getSelectedItem())) {
                    initialXField.setText(String.valueOf(random.nextInt(100)));
                    initialYField.setText(String.valueOf(random.nextInt(100)));
                    finalXField.setText(String.valueOf(random.nextInt(100)));
                }
            }
        });

        // Add listener to update plot based on selected output
        outputSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                plotPanel.repaint(); // Trigger repaint to update plot
            }
        });

        solverSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSolver = (String) solverSelector.getSelectedItem();
                updateInterfaceForSolver(selectedSolver);
            }
        });

        // Add a DocumentListener to track changes to the functionField
        functionField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                logChange();
            }

            public void removeUpdate(DocumentEvent e) {
                logChange();
            }

            public void insertUpdate(DocumentEvent e) {
                logChange();
            }

            private void logChange() {
                // This method is called whenever the user types in the functionField
                // Here you can handle the tracking of the input, for example:
            }
        });

    }

    private void updateInterfaceForSolver(String solver) {
        inputPanel.removeAll(); // Clear the current interface

        inputPanel.add(new JLabel("Solver:"));
        Integer[] rk2Choices = { 1, 2, 3, 4, 5, 6 };
        inputPanel.add(solverSelector);
        choiceSelector.removeAllItems();

        if ("Euler Solver".equals(solver)) {
            inputPanel.add(new JLabel("Function f(t, y):"));
            inputPanel.add(functionField);
            inputPanel.add(new JLabel("Step Size (h):"));
            inputPanel.add(stepSizeField);
            inputPanel.add(new JLabel("Initial Time (t0):"));
            inputPanel.add(initialTimeField);
            inputPanel.add(new JLabel("Initial Value (y0):"));
            inputPanel.add(initialValueField);
            inputPanel.add(new JLabel("End Time (t1):"));
            inputPanel.add(endTimeField);

        } else if ("RK2 Solver".equals(solver)) {

            inputPanel.add(new JLabel("Initial X:"));
            inputPanel.add(initialXField);
            inputPanel.add(new JLabel("Initial Y:"));
            inputPanel.add(initialYField);
            inputPanel.add(new JLabel("Final X:"));
            inputPanel.add(finalXField);
            inputPanel.add(new JLabel("Step Size:"));
            inputPanel.add(stepSizeField);
            inputPanel.add(new JLabel("ODE Choice:"));
            inputPanel.add(choiceSelector);
            choiceSelector.setVisible(true);

            // Add the choice selector specific for RK2 Solver
            for (int choice : rk2Choices) {
                choiceSelector.addItem(choice);
            }
            choiceSelector.setVisible(true);

        } else {
            // Add components for other solvers here
            inputPanel.add(new JLabel("Initial Conditions:"));
            inputPanel.add(initialConditionsField);
            inputPanel.add(new JLabel("Step Size:"));
            inputPanel.add(stepSizeField);
            inputPanel.add(new JLabel("Integration Time:"));
            inputPanel.add(integrationTimeField);
            choiceSelector.setVisible(false);

        }

        // Revalidate and repaint to update the UI
        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private void finalizeSetup() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    private void applyNumericFilter(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
    }

    private void startSimulation() {
        try {
            if (functionField != null)
                eulerFunc = functionField.getText();
            if (stepSizeField.getText().length() > 0) {
                h = Double.parseDouble(stepSizeField.getText());
            }
            if (initialTimeField.getText().length() > 0) {
                t0 = Double.parseDouble(initialTimeField.getText());
            }
            if (initialValueField.getText().length() > 0) {
                y0 = Double.parseDouble(initialValueField.getText());
            }
            if (endTimeField.getText().length() > 0) {
                t1 = Double.parseDouble(endTimeField.getText());
            }
            HashMap<String, Double> vals = new HashMap<String, Double>();
            vals.put("t", t0);
            vals.put("t1", t1);
            vals.put("y", y0);
            vals.put("h", h);
            eulerFuncVals = ODESolver.EulerSolverPlotter(vals, new FunctionParser(eulerFunc));
            System.out.println(eulerFuncVals[1][eulerFuncVals[1].length-1]);


            plotPanel.setPlotData(eulerFuncVals, "Time Evolution");


            //plotPanel.setPreferredSize(new Dimension(400, 300));
            //outputPanel.add(plotPanel);
            String selectedSolver = (String) solverSelector.getSelectedItem();

            double stepSize = Double.parseDouble(stepSizeField.getText()); // Get step size as double
            int integrationTime = Integer.parseInt(integrationTimeField.getText());
            double k = 0.1; // Example constant for the differential equation

            String selectedOutput = (String) outputSelector.getSelectedItem();

            List<Double> timeData = new ArrayList<>();
            List<Double> variableData = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                timeData.add((double) i); // Dummy time points
                variableData.add(Math.sin(i * 0.1)); // Dummy variable values (e.g., sine wave)
            }

            plotPanel.setPlotData(timeData, variableData, selectedOutput); // Update the
            // plot panel with new data

            // Check if step size is negative
            if (stepSize <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Step size must be a positive number.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return; // Prevent simulation from starting
            }

            // Check if step size is negative
            if (integrationTime <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Integration time must be a positive number.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return; // Prevent simulation from starting
            }

            double result = 0.0;

            if ("ODE Analytical Solver".equals(selectedSolver)) {
                double initialCondition = Double.parseDouble(initialConditionsField.getText());
                double[] results = ODEAnalyticalSolver.analyticalSolution(integrationTime, initialCondition, k,
                        stepSize);
                result = results[results.length - 1];
            } else {
                // Add other solvers' implementation here
                if ("RK2 Solver".equals(selectedSolver)) {
                    double initialX = Double.parseDouble(initialXField.getText());
                    double initialY = Double.parseDouble(initialYField.getText());
                    double finalX = Double.parseDouble(finalXField.getText());
                    int choice = (int) choiceSelector.getSelectedItem(); // Make sure to cast appropriately

                    result = RK2Solver.solveODE(initialX, initialY, finalX, stepSize, choice);
                } else {
                    FunctionParser a = new FunctionParser(eulerFunc);
                    System.out.println(ODESolver.EulerSolver(vals, a));
                    if ("Euler Solver".equals(selectedSolver)) {

                        result = ODESolver.EulerSolver(vals, a);
                    } else {
                        JOptionPane.showMessageDialog(this, "Solver not implemented yet.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            JOptionPane.showMessageDialog(this, "Result: " + result, "Simulation Result",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace for debugging
            JOptionPane.showMessageDialog(this, "An error occurred during the simulation: " + e.getMessage(),
                    "Simulation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField createNumericTextField(int columns) {
        JTextField textField = new JTextField(columns);
        applyNumericFilter(textField);
        return textField;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);

    }
}