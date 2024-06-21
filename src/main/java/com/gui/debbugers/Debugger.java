package com.gui.debbugers;

/**
 * The Debugger class provides utility methods for debugging, such as printing matrices.
 */
public class Debugger {

    /**
     * Prints a 2D matrix of doubles to the console.
     *
     * @param a The 2D matrix of doubles to be printed.
     */
    public static void printMatrix(double[][] a) {
        for (double[] doubles : a) {
            for (double aDouble : doubles) {
                System.out.print(aDouble + " ");
            }
            System.out.println();
        }
    }
}
