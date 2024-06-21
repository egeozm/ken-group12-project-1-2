package com.gui.parser;

import java.util.*;
import java.util.function.BiFunction;

/**
 * The BiFunctionParser class provides functionality to parse a mathematical expression
 * involving two variables (x and y) and convert it into a BiFunction that can evaluate
 * the expression given values for x and y.
 */
public class BiFunctionParser {

    /**
     * Parses a mathematical expression and returns a BiFunction that evaluates the expression
     * given values for x and y.
     *
     * @param expression The mathematical expression to parse.
     * @return A BiFunction that evaluates the expression.
     */
    public static BiFunction<Double, Double, Double> parse(String expression) {
        String[] tokens = tokenize(expression);
        Queue<String> rpnQueue = convertToRPN(tokens);
        return (x, y) -> evaluateRPN(new LinkedList<>(rpnQueue), x, y);
    }

    /**
     * Tokenizes the input expression into individual tokens.
     *
     * @param expression The expression to tokenize.
     * @return An array of tokens.
     */
    private static String[] tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        boolean previousWasOperator = true;

        for (char c : expression.toCharArray()) {
            if (Character.isWhitespace(c)) {
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                token.append(c);
                previousWasOperator = false;
            } else if (Character.isLetter(c)) {
                if (token.length() > 0 && (Character.isDigit(token.charAt(0)) || token.charAt(0) == '.')) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                token.append(c);
                previousWasOperator = false;
            } else {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                if (c == '-' && previousWasOperator) {
                    token.append(c); // This is an unary minus
                } else {
                    tokens.add(String.valueOf(c));
                }
                previousWasOperator = (c != ')');
            }
        }

        if (token.length() > 0) {
            tokens.add(token.toString());
        }

        return tokens.toArray(new String[0]);
    }

    /**
     * Converts an array of tokens to Reverse Polish Notation (RPN) using the shunting yard algorithm.
     *
     * @param tokens An array of tokens.
     * @return A queue representing the expression in RPN.
     */
    private static Queue<String> convertToRPN(String[] tokens) {
        Queue<String> output = new LinkedList<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token) || isVariable(token)) {
                output.add(token);
            } else if (isFunction(token)) {
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                if (!operators.isEmpty() && operators.peek().equals("(")) {
                    operators.pop();
                }
                if (!operators.isEmpty() && isFunction(operators.peek())) {
                    output.add(operators.pop());
                }
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && isOperator(operators.peek()) && precedence(token) <= precedence(operators.peek())) {
                    output.add(operators.pop());
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }

        return output;
    }

    /**
     * Evaluates an expression in Reverse Polish Notation (RPN) given values for x and y.
     *
     * @param rpnQueue A queue representing the expression in RPN.
     * @param x The value of x.
     * @param y The value of y.
     * @return The result of evaluating the expression.
     */
    private static double evaluateRPN(Queue<String> rpnQueue, double x, double y) {
        Stack<Double> stack = new Stack<>();

        while (!rpnQueue.isEmpty()) {
            String token = rpnQueue.poll();

            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (token.equals("x")) {
                stack.push(x);
            } else if (token.equals("y")) {
                stack.push(y);
            } else if (isOperator(token)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid RPN expression: not enough operands for operator " + token);
                }
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+":
                        stack.push(a + b);
                        break;
                    case "-":
                        stack.push(a - b);
                        break;
                    case "*":
                        stack.push(a * b);
                        break;
                    case "/":
                        stack.push(a / b);
                        break;
                    case "^":
                        stack.push(Math.pow(a, b));
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid operator: " + token);
                }
            } else if (isFunction(token)) {
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("Invalid RPN expression: not enough operands for function " + token);
                }
                double a = stack.pop();
                switch (token) {
                    case "sin":
                        stack.push(Math.sin(a));
                        break;
                    case "cos":
                        stack.push(Math.cos(a));
                        break;
                    case "exp":
                        stack.push(Math.exp(a));
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid function: " + token);
                }
            } else {
                throw new IllegalArgumentException("Invalid token: " + token);
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid RPN expression: remaining stack size is not 1. Stack: " + stack);
        }
        return stack.pop();
    }

    /**
     * Checks if a token is a number.
     *
     * @param token The token to check.
     * @return True if the token is a number, false otherwise.
     */
    private static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if a token is a variable (x or y).
     *
     * @param token The token to check.
     * @return True if the token is a variable, false otherwise.
     */
    private static boolean isVariable(String token) {
        return token.equals("x") || token.equals("y");
    }

    /**
     * Checks if a token is a function (sin, cos, exp).
     *
     * @param token The token to check.
     * @return True if the token is a function, false otherwise.
     */
    private static boolean isFunction(String token) {
        return token.equals("sin") || token.equals("cos") || token.equals("exp");
    }

    /**
     * Checks if a token is an operator (+, -, *, /, ^).
     *
     * @param token The token to check.
     * @return True if the token is an operator, false otherwise.
     */
    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("^");
    }

    /**
     * Determines the precedence of an operator.
     *
     * @param operator The operator.
     * @return The precedence of the operator.
     */
    private static int precedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
        }
        return -1;
    }

    /**
     * Main method to test the BiFunctionParser.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        BiFunction<Double, Double, Double> func = BiFunctionParser.parse("0.4*(0.9-2.72^((x^2+y^2)/8*(-1)))");

        double x = 0;
        double y = 0;
        System.out.println("Result: " + func.apply(x, y));
    }
}
