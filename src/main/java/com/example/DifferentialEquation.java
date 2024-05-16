package com.example;

public interface DifferentialEquation {
    double computeDerivative(double t, double y);

    public class EulerMethod {
        public static double[] solve(DifferentialEquation eq, double y0, double t0, double tf, int n) {
            double h = (tf - t0) / n;
            double[] ys = new double[n + 1];
            ys[0] = y0;
            double t = t0;

            for (int i = 1; i <= n; i++) {
                ys[i] = ys[i - 1] + h * eq.computeDerivative(t, ys[i - 1]);
                t += h;
            }

            return ys;
        }
    }
    public class RK4Method {
        public static double[] solve(DifferentialEquation eq, double y0, double t0, double tf, int n) {
            double h = (tf - t0) / n;
            double[] ys = new double[n + 1];
            ys[0] = y0;
            double t = t0;

            for (int i = 1; i <= n; i++) {
                double k1 = h * eq.computeDerivative(t, ys[i - 1]);
                double k2 = h * eq.computeDerivative(t + h / 2, ys[i - 1] + k1 / 2);
                double k3 = h * eq.computeDerivative(t + h / 2, ys[i - 1] + k2 / 2);
                double k4 = h * eq.computeDerivative(t + h, ys[i - 1] + k3);
                ys[i] = ys[i - 1] + (k1 + 2 * k2 + 2 * k3 + k4) / 6;
                t += h;
            }

            return ys;
        }
    }
}