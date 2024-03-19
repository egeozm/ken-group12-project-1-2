
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class GUI extends JFrame {

    private JComboBox<String> solverSelector;
    private JTextField initialConditionsField;
    private JTextField stepSizeField;
    private JTextField integrationTimeField;
    private JButton startSimulationButton;
    private JPanel mainPanel;
    private JButton pauseSimulationButton;
    private JButton resetSimulationButton;
    

    public GUI() {
        super("Crazy Putting Simulator");

        // Solver selection
        String[] solvers = { "Euler Solver", "ODE Analytical Solver", "ODE Native Solver" };
        solverSelector = new JComboBox<>(solvers);

        // Parameter inputs
        initialConditionsField = new JTextField(20);
        stepSizeField = new JTextField(5);
        integrationTimeField = new JTextField(5);

        // Simulation control
        startSimulationButton = new JButton("Start Simulation");
        pauseSimulationButton = new JButton("Pause Simulation");
        resetSimulationButton = new JButton("Reset Simulation");
        
        startSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Placeholder for starting the simulation
                System.out.println("Simulation started with: " + solverSelector.getSelectedItem());
                // You'll need to implement the actual simulation starting logic here
            }
        });

        // Main panel layout
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 2));
        mainPanel.add(new JLabel("Solver:"));
        mainPanel.add(solverSelector);
        mainPanel.add(new JLabel("Initial Conditions:"));
        mainPanel.add(initialConditionsField);
        mainPanel.add(new JLabel("Step Size:"));
        mainPanel.add(stepSizeField);
        mainPanel.add(new JLabel("Integration Time:"));
        mainPanel.add(integrationTimeField);
        mainPanel.add(new JLabel("")); // Spacer
        mainPanel.add(startSimulationButton);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
        this.setVisible(true);
    }

}
