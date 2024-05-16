public interface DifferentialEquation {
    double[] computeDerivatives(double t, double[] state);

    public class EulerMethod {
        public static double[] solve(DifferentialEquation eq, double[] state, double dt) {
            double[] derivatives = eq.computeDerivatives(0, state);
            double[] newState = new double[state.length];
            for (int i = 0; i < state.length; i++) {
                newState[i] = state[i] + dt * derivatives[i];
            }
            return newState;
        }
    }

    public class RK4Method {
        public static double[] solve(DifferentialEquation eq, double[] state, double dt) {
            double[] k1 = eq.computeDerivatives(0, state);
            double[] k2 = eq.computeDerivatives(0, addVectors(state, multiplyVector(k1, dt / 2)));
            double[] k3 = eq.computeDerivatives(0, addVectors(state, multiplyVector(k2, dt / 2)));
            double[] k4 = eq.computeDerivatives(0, addVectors(state, multiplyVector(k3, dt)));

            double[] newState = new double[state.length];
            for (int i = 0; i < state.length; i++) {
                newState[i] = state[i] + (dt / 6) * (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]);
            }
            return newState;
        }

        private static double[] addVectors(double[] v1, double[] v2) {
            double[] result = new double[v1.length];
            for (int i = 0; i < v1.length; i++) {
                result[i] = v1[i] + v2[i];
            }
            return result;
        }

        private static double[] multiplyVector(double[] v, double scalar) {
            double[] result = new double[v.length];
            for (int i = 0; i < v.length; i++) {
                result[i] = v[i] * scalar;
            }
            return result;
        }
    }
}

