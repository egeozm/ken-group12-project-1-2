package com.ode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class ODESolversAccuracyTest {

    public static void main(String[] args) {
        double initialTime = 0.0;
        double endTime = 5.0;

        // exponential decay equation
        System.out.println("Exponential Decay Equation:");
        DifferentialEquation expDecayEquation = (t, state) -> new double[]{-1.0 * state[0]};
        AnalyticalFunction expDecayAnalyticalSolution = ODESolversAccuracyTest::analyticalSolutionExpDecay;
        double[] expDecayInitialState = new double[]{1.0};
        String expDecayOutputFile = "exp_decay_errors.csv";
        testEquation(expDecayEquation, expDecayAnalyticalSolution, expDecayInitialState, initialTime, endTime, expDecayOutputFile);

        // harmonic oscillator equation
        System.out.println("Harmonic Oscillator Equation:");
        DifferentialEquation harmonicOscillatorEquation = (t, state) -> new double[]{state[1], -1.0 * state[0]};
        AnalyticalFunction harmonicOscillatorAnalyticalSolution = ODESolversAccuracyTest::analyticalSolutionHarmonic;
        double[] harmonicOscillatorInitialState = new double[]{1.0, 0.0};
        String harmonicOscillatorOutputFile = "harmonic_oscillator_errors.csv";
        testEquation(harmonicOscillatorEquation, harmonicOscillatorAnalyticalSolution, harmonicOscillatorInitialState, initialTime, endTime, harmonicOscillatorOutputFile);

        // logistic growth equation
        System.out.println("Logistic Growth Equation:");
        DifferentialEquation logisticGrowthEquation = (t, state) -> new double[]{state[0] * (1 - state[0] / 10)};
        AnalyticalFunction logisticGrowthAnalyticalSolution = ODESolversAccuracyTest::analyticalSolutionLogisticGrowth;
        double[] logisticGrowthInitialState = new double[]{1.0};
        String logisticGrowthOutputFile = "logistic_growth_errors.csv";
        testEquation(logisticGrowthEquation, logisticGrowthAnalyticalSolution, logisticGrowthInitialState, initialTime, endTime, logisticGrowthOutputFile);
    }


    private static void testEquation(DifferentialEquation equation, AnalyticalFunction analyticalFunc, double[] y0, double t0, double tEnd, String filename) {
        double[] stepSizes = {0.1, 0.05, 0.01, 0.005, 0.001};

        try (FileWriter writer = new FileWriter(filename)) {
            writer.append("StepSize,EulerMaxError,RK4MaxError\n");

            for (double stepSize : stepSizes) {
                System.out.println("Step Size: " + stepSize);
                System.out.println("------------------------");

                double[][] eulerResults = solveWithEuler(equation, t0, y0, tEnd, stepSize);
                double[] eulerErrors = calculateErrors(eulerResults, t0, tEnd, stepSize, y0, analyticalFunc);

                double[][] rk4Results = solveWithRK4(equation, t0, y0, tEnd, stepSize);
                double[] rk4Errors = calculateErrors(rk4Results, t0, tEnd, stepSize, y0, analyticalFunc);

                double eulerMaxError = Arrays.stream(eulerErrors).max().orElse(0.0);
                double rk4MaxError = Arrays.stream(rk4Errors).max().orElse(0.0);

                writer.append(stepSize + "," + eulerMaxError + "," + rk4MaxError + "\n");

                System.out.println("Euler Method Max Error: " + eulerMaxError);
                System.out.println("RK4 Method Max Error: " + rk4MaxError);
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double[][] solveWithEuler(DifferentialEquation eq, double t0, double[] y0, double tEnd, double dt) {
        int n = (int) ((tEnd - t0) / dt);
        double[][] results = new double[n + 1][y0.length];
        results[0] = y0.clone();

        for (int i = 1; i <= n; i++) {
            results[i] = DifferentialEquation.EulerMethod.solve(eq, results[i - 1], dt);
        }

        return results;
    }

    private static double[][] solveWithRK4(DifferentialEquation eq, double t0, double[] y0, double tEnd, double dt) {
        int n = (int) ((tEnd - t0) / dt);
        double[][] results = new double[n + 1][y0.length];
        results[0] = y0.clone();

        for (int i = 1; i <= n; i++) {
            results[i] = DifferentialEquation.RK4Method.solve(eq, results[i - 1], dt);
        }

        return results;
    }

    private static double[] calculateErrors(double[][] numericalResults, double t0, double tEnd, double stepSize, double[] y0, AnalyticalFunction analyticalFunc) {
        int steps = (int) ((tEnd - t0) / stepSize) + 1;
        double[] errors = new double[steps];

        for (int i = 0; i < steps; i++) {
            double t = t0 + i * stepSize;
            double[] analytical = analyticalFunc.evaluate(t, y0);
            for (int j = 0; j < y0.length; j++) {
                errors[i] += Math.abs(analytical[j] - numericalResults[i][j]);
            }
        }

        return errors;
    }

    private static double[] analyticalSolutionExpDecay(double t, double[] y0) {
        double k = 1.0;
        return new double[]{y0[0] * Math.exp(-k * t)};
    }

    private static double[] analyticalSolutionHarmonic(double t, double[] y0) {
        double A = y0[0];
        double B = y0[1];
        return new double[]{A * Math.cos(t) + B * Math.sin(t), -A * Math.sin(t) + B * Math.cos(t)};
    }

    private static double[] analyticalSolutionLogisticGrowth(double t, double[] y0) {
        double K = 10.0;
        double r = 1.0;
        double y0Initial = y0[0];
        return new double[]{K * y0Initial * Math.exp(r * t) / (K + y0Initial * (Math.exp(r * t) - 1))};
    }

    @FunctionalInterface
    private interface AnalyticalFunction {
        double[] evaluate(double t, double[] y0);
    }
}