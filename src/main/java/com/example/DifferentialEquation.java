package com.example;

/**
 * Interface for defining a differential equation system.
 */
public interface DifferentialEquation {
    /**
     * Computes the derivatives of the system state at a given time.
     *
     * @param t     The current time.
     * @param state The current state of the system.
     * @return An array representing the derivatives of the state.
     */
    double[] computeDerivatives(double t, double[] state);

    /**
     * Class containing methods for solving differential equations using the Euler method.
     */
    class EulerMethod {
        /**
         * Solves the differential equation using the Euler method.
         *
         * @param eq    The differential equation to solve.
         * @param state The initial state of the system.
         * @param dt    The time step for the integration.
         * @return The new state of the system after one time step.
         */
        public static double[] solve(DifferentialEquation eq, double[] state, double dt) {
            double[] derivatives = eq.computeDerivatives(0, state);
            double[] newState = new double[state.length];
            for (int i = 0; i < state.length; i++) {
                newState[i] = state[i] + dt * derivatives[i];
            }
            return newState;
        }
    }

    /**
     * Class containing methods for solving differential equations using the Runge-Kutta fourth order (RK4) method.
     */
    class RK4Method {
        /**
         * Solves the differential equation using the RK4 method.
         *
         * @param eq    The differential equation to solve.
         * @param state The initial state of the system.
         * @param dt    The time step for the integration.
         * @return The new state of the system after one time step.
         */
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

        /**
         * Adds two vectors element-wise.
         *
         * @param v1 The first vector.
         * @param v2 The second vector.
         * @return A new vector representing the element-wise sum of the input vectors.
         */
        private static double[] addVectors(double[] v1, double[] v2) {
            double[] result = new double[v1.length];
            for (int i = 0; i < v1.length; i++) {
                result[i] = v1[i] + v2[i];
            }
            return result;
        }

        /**
         * Multiplies each element of a vector by a scalar.
         *
         * @param v      The vector.
         * @param scalar The scalar value.
         * @return A new vector where each element is the product of the input vector element and the scalar.
         */
        private static double[] multiplyVector(double[] v, double scalar) {
            double[] result = new double[v.length];
            for (int i = 0; i < v.length; i++) {
                result[i] = v[i] * scalar;
            }
            return result;
        }
    }
}
