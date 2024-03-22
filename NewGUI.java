import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;

public class NewGUI extends JFrame {
    private JLabel solverLabel, initialXLabel, initialYLabel, finalXLabel, stepSizeLabel, odeChoiceLabel, resultLabel, functionLabel;
    private JTextField initialXField, initialYField, finalXField, stepSizeField, functionField;
    private JComboBox<String> odeChoiceComboBox, solverComboBox;
    private JButton solveButton;
    private JTextArea resultArea;

    public NewGUI() {
        setTitle("ODE Solver");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        solverLabel = new JLabel("Select Solver:");
        solverLabel.setBounds(20, 20, 100, 25);
        add(solverLabel);

        String[] solverChoices = {"RK2 Solver", "Euler Solver"};
        solverComboBox = new JComboBox<String>(solverChoices);
        solverComboBox.setBounds(120, 20, 150, 25);
        add(solverComboBox);

        solverComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedSolver = (String) solverComboBox.getSelectedItem();
                if (selectedSolver.equals("RK2 Solver")) {
                    showRK2SolverFields();
                } else if (selectedSolver.equals("Euler Solver")) {
                    showEulerSolverFields();
                }
            }
        });

        initialXLabel = new JLabel("Initial t:");
        initialXLabel.setBounds(20, 50, 80, 25);
        add(initialXLabel);

        initialXField = new JTextField();
        initialXField.setBounds(120, 50, 100, 25);
        add(initialXField);

        initialYLabel = new JLabel("Initial Y:");
        initialYLabel.setBounds(20, 80, 80, 25);
        add(initialYLabel);

        initialYField = new JTextField();
        initialYField.setBounds(120, 80, 100, 25);
        add(initialYField);

        finalXLabel = new JLabel("Final t:");
        finalXLabel.setBounds(20, 110, 80, 25);
        add(finalXLabel);

        finalXField = new JTextField();
        finalXField.setBounds(120, 110, 100, 25);
        add(finalXField);

        stepSizeLabel = new JLabel("Step Size:");
        stepSizeLabel.setBounds(20, 140, 80, 25);
        add(stepSizeLabel);

        stepSizeField = new JTextField();
        stepSizeField.setBounds(120, 140, 100, 25);
        add(stepSizeField);

        odeChoiceLabel = new JLabel("ODE Choice:");
        odeChoiceLabel.setBounds(20, 170, 80, 25);
        add(odeChoiceLabel);

        String[] choices = {"1", "2", "3", "4", "5", "6"};
        odeChoiceComboBox = new JComboBox<String>(choices);
        odeChoiceComboBox.setBounds(120, 170, 100, 25);
        add(odeChoiceComboBox);

        functionLabel = new JLabel("Function:");
        functionLabel.setBounds(20, 200, 80, 25);
        add(functionLabel);
        functionLabel.setVisible(false);

        functionField = new JTextField();
        functionField.setBounds(120, 200, 300, 25);
        add(functionField);
        functionField.setVisible(false);

        solveButton = new JButton("Solve");
        solveButton.setBounds(20, 230, 80, 25);
        add(solveButton);

        resultLabel = new JLabel("Result:");
        resultLabel.setBounds(20, 260, 80, 25);
        add(resultLabel);

        resultArea = new JTextArea();
        resultArea.setBounds(120, 260, 300, 25);
        add(resultArea);

        solveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                solveODE();
            }
        });
    }

    private void showRK2SolverFields() {
        initialYLabel.setVisible(true);
        initialYField.setVisible(true);
        finalXLabel.setVisible(true);
        finalXField.setVisible(true);
        stepSizeLabel.setVisible(true);
        stepSizeField.setVisible(true);
        odeChoiceLabel.setVisible(true);
        odeChoiceComboBox.setVisible(true);
        functionLabel.setVisible(false);
        functionField.setVisible(false);
    }

    private void showEulerSolverFields() {
        finalXLabel.setVisible(true);
        finalXField.setVisible(true);
        initialYLabel.setVisible(true);
        initialYField.setVisible(true);
        stepSizeLabel.setVisible(true);
        stepSizeField.setVisible(true);
        odeChoiceLabel.setVisible(false);
        odeChoiceComboBox.setVisible(false);
        functionLabel.setVisible(true);
        functionField.setVisible(true);
    }
    

    private void solveODE() {
        String selectedSolver = (String) solverComboBox.getSelectedItem();
        if (selectedSolver.equals("RK2 Solver")) {
            solveRK2();
        } else if (selectedSolver.equals("Euler Solver")) {
            solveEuler();
        }
    }

    private void solveRK2() {
        double initialX = Double.parseDouble(initialXField.getText());
        double initialY = Double.parseDouble(initialYField.getText());
        double finalX = Double.parseDouble(finalXField.getText());
        double stepSize = Double.parseDouble(stepSizeField.getText());
        int ODEChoice = odeChoiceComboBox.getSelectedIndex() + 1;

        double result = RK2Solver.solveODE(initialX, initialY, finalX, stepSize, ODEChoice);
        resultArea.setText("Value of y at x = " + finalX + ": " + result);
    }

    private void solveEuler() {
        double initialX = Double.parseDouble(initialXField.getText());
        double initialY = Double.parseDouble(initialYField.getText());
        double finalX = Double.parseDouble(finalXField.getText());
        double stepSize = Double.parseDouble(stepSizeField.getText());
        String function = functionField.getText();

        HashMap<String, Double> varVals = new HashMap<>();
        varVals.put("t", initialX);
        varVals.put("t1", finalX);
        varVals.put("y", initialY);
        varVals.put("h", stepSize);

        try {
            double result = ODESolver.EulerSolver(varVals, new FunctionParser(function));
            resultArea.setText("Value of y at x = " + finalX + ": " + result);
        } catch (Exception ex) {
            resultArea.setText("Error: " + ex.getMessage());
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new NewGUI().setVisible(true);
            }
        });
    }
}
