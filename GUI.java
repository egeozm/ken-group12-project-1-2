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

    private JComboBox<String> solverSelector, outputSelector;
    private JTextField initialConditionsField, stepSizeField, integrationTimeField, functionField, initialTimeField,
            initialValueField, endTimeField;
    private JButton startSimulationButton, resetSimulationButton, randomSimulationButton;
    private JPanel mainPanel, inputPanel, buttonPanel, outputPanel, plotPanel;

    public GUI() {
        super("ODE Solver");
        initializeComponents();
        setUpLayout();
        attachListeners();
        finalizeSetup();
    }

    private void initializeComponents() {
        // Solver selection
        String[] solvers = { "Euler Solver", "ODE Analytical Solver", "RK2Solver" };
        solverSelector = new JComboBox<>(solvers);

        // Output selection
        String[] outputs = { "Time Evolution", "Phase Space" };
        outputSelector = new JComboBox<>(outputs);

        // Plotting panel
        plotPanel = new JPanel();
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
                initialConditionsField.setText(String.valueOf(random.nextInt(100)));
                stepSizeField.setText(String.valueOf(random.nextInt(100)));
                integrationTimeField.setText(String.valueOf(random.nextInt(100) + 1)); // +1 to avoid zero

                if ("Euler Solver".equals(solverSelector.getSelectedItem())) {
                    // Euler fields: generate double values from 0.0 to 10.0
                    functionField.setText(String.format("%.2f", random.nextInt() * 100));
                    initialTimeField.setText(String.format("%.2f", random.nextInt() * 100));
                    initialValueField.setText(String.format("%.2f", random.nextInt() * 100));
                    endTimeField.setText(String.format("%.2f", random.nextInt() * 100));
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

    }

    private void updateInterfaceForSolver(String solver) {
        inputPanel.removeAll(); // Clear the current interface

        inputPanel.add(new JLabel("Solver:"));
        inputPanel.add(solverSelector);

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
        } else {
            // Add components for other solvers here
            inputPanel.add(new JLabel("Initial Conditions:"));
            inputPanel.add(initialConditionsField);
            inputPanel.add(new JLabel("Step Size:"));
            inputPanel.add(stepSizeField);
            inputPanel.add(new JLabel("Integration Time:"));
            inputPanel.add(integrationTimeField);
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
            String selectedSolver = (String) solverSelector.getSelectedItem();
            double initialCondition = Double.parseDouble(initialConditionsField.getText());
            double stepSize = Double.parseDouble(stepSizeField.getText()); // Get step size as double
            int integrationTime = Integer.parseInt(integrationTimeField.getText());
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
                double [] results= odeanal.analyticalSolution(integrationTime, initialCondition, k);
                result=results[results.length-1];
            } else {
                // Add other solvers' implementation here
                if ("RK2Solver".equals(selectedSolver)) {
                    RK2Solver rk = new RK2Solver();
                    result = rk.solveODE(initialCondition, initialCondition, result, stepSize, ABORT);
                } else {

                }
                if ("ODE Native ".equals(selectedSolver)) {
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

    private JTextField createNumericTextField(int columns) {
        JTextField textField = new JTextField(columns);
        applyNumericFilter(textField);
        return textField;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);

    }
}