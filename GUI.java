import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;


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
        stepSizeField = new JTextField(5);
        integrationTimeField = new JTextField(5);

        // Simulation control buttons
        startSimulationButton = new JButton("Start Simulation");
        resetSimulationButton = new JButton("Reset Simulation");
        randomSimulationButton= new JButton("Generate Random Values");
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
        mainPanel.add( randomSimulationButton);

    }

    private void attachListeners() {
        Random random = new Random();
        startSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();//start

            }});
        resetSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {//clear all textfields
                initialConditionsField.setText("");
                stepSizeField.setText("");
                integrationTimeField.setText("");
            }});
        randomSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               String randoma = Integer.toString(random.nextInt(100));
               String randomb = Integer.toString(random.nextInt(100));
               String randomc = Integer.toString(random.nextInt(100));

                initialConditionsField.setText(randoma);
                stepSizeField.setText(randomb);
                integrationTimeField.setText(randomc);

            }});
    }

    private void finalizeSetup() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        this.setVisible(true);
    }

    private void startSimulation() {
        try {
            String selectedSolver = (String) solverSelector.getSelectedItem();
            double initialCondition = Double.parseDouble(initialConditionsField.getText());
            double integrationTime = Double.parseDouble(integrationTimeField.getText());
            double k = 0.1; // Example constant for the differential equation

            double result = 0.0;
            if ("ODE Analytical Solver".equals(selectedSolver)) {
                ODEAnalyticalSolver odeanal=new ODEAnalyticalSolver();
                result = odeanal.analyticalSolution(integrationTime, initialCondition, k);
            } else {
                // Add other solvers' implementation here
                if ("Euler Solver".equals(selectedSolver)) { //ODE Solver
                    // result = EulerSolver.analyticalSolution(integrationTime, initialCondition, k);
                } else {

                }
                if ("ODE Native Solver".equals(selectedSolver)) {
                    // result = ODENativeSolver.analyticalSolution(integrationTime, initialCondition, k);

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
                    "Please enter valid numbers for initial conditions and integration time.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}