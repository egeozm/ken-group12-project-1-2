import javax.swing.*;
import java.awt.*;
import javax.swing.text.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

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

    private JComboBox<String> solverSelector;
    private JTextField initialConditionsField;
    private JTextField stepSizeField;
    private JTextField integrationTimeField;
    private JButton startSimulationButton;
    private JPanel mainPanel;
    private JButton resetSimulationButton;
    private JButton randomSimulationButton;

    public GUI() {
        super("ODE Solver");
        initializeComponents();
        setUpLayout();
        attachListeners();
        finalizeSetup();
    }

    private void initializeComponents() {
        // Solver selection
        String[] solvers = { "Euler Solver", "ODE Analytical Solver", "ODE Native Solver" };
        solverSelector = new JComboBox<>(solvers);

        // Parameter inputs
        initialConditionsField = new JTextField(20);
        applyNumericFilter(initialConditionsField);

        stepSizeField = new JTextField(5);
        applyNumericFilter(stepSizeField);

        integrationTimeField = new JTextField(5);
        applyNumericFilter(integrationTimeField);

        // Simulation control buttons
        startSimulationButton = new JButton("Start Simulation");
        resetSimulationButton = new JButton("Reset Simulation");
        randomSimulationButton = new JButton("Generate Random Values");
    }

    private void setUpLayout() {
        // Main panel layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(6, 2));
        mainPanel.add(new JLabel(" Solver:"));
        mainPanel.add(solverSelector);
        mainPanel.add(new JLabel(" Initial Conditions:"));
        mainPanel.add(initialConditionsField);
        mainPanel.add(new JLabel(" Step Size:"));
        mainPanel.add(stepSizeField);
        mainPanel.add(new JLabel(" Integration Time:"));
        mainPanel.add(integrationTimeField);
        mainPanel.add(startSimulationButton);
        mainPanel.add(resetSimulationButton);
        mainPanel.add(randomSimulationButton);

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
            }
        });
        randomSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String randoma = Integer.toString(random.nextInt(100));
                String randomb = Integer.toString(random.nextInt(100));
                String randomc = Integer.toString(random.nextInt(100));

                initialConditionsField.setText(randoma);
                stepSizeField.setText(randomb);
                integrationTimeField.setText(randomc);

            }
        });
    }

    private void finalizeSetup() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        this.setVisible(true);
    }

    private void applyNumericFilter(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
    }

    private void startSimulation() {
        try {
            String selectedSolver = (String) solverSelector.getSelectedItem();
            double initialCondition = Double.parseDouble(initialConditionsField.getText());
            double stepSize = Double.parseDouble(stepSizeField.getText()); // Get step size as double
            double integrationTime = Double.parseDouble(integrationTimeField.getText());
            double k = 0.1; // Example constant for the differential equation

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
                ODEAnalyticalSolver odeanal = new ODEAnalyticalSolver();
                result = odeanal.analyticalSolution(integrationTime, initialCondition, k);
            } else {
                // Add other solvers' implementation here
                if ("ODE Native Solver Function".equals(selectedSolver)) { 
                    // Function nativeSolver = new Function();
                    // result = nativeSolver.funcVal(integrationTime, initialCondition,k);
                } else {

                }
                if ("ODE Native Solver".equals(selectedSolver)) {
                    // result = ODENativeSolver.analyticalSolution(integrationTime,
                    // initialCondition, k);

                } else {
                    JOptionPane.showMessageDialog(this, "Solver not implemented yet.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Result: " + result, "Simulation Result",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for initial conditions, step size, and integration time.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUI();
            }
        });
    }
}