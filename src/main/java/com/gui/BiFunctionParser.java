package com.gui;

import java.util.*;
import java.util.function.BiFunction;

public class BiFunctionParser {

    public static BiFunction<Double, Double, Double> parse(String expression) {
        String[] tokens = tokenize(expression);
        Queue<String> rpnQueue = convertToRPN(tokens);
        return (x, y) -> evaluateRPN(new LinkedList<>(rpnQueue), x, y);
    }

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
                    token.append(c); // This is a unary minus
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
                if (stack.size() < 1) {
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

    private static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isVariable(String token) {
        return token.equals("x") || token.equals("y");
    }

    private static boolean isFunction(String token) {
        return token.equals("sin") || token.equals("cos") || token.equals("exp");
    }

    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("^");
    }

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

    public static void main(String[] args) {
        BiFunction<Double, Double, Double> func = BiFunctionParser.parse("0.4*(0.9-2.72^((x^2+y^2)/8*(-1)))");

        double x = 0;
        double y = 0;
        System.out.println("Result: " + func.apply(x, y));
    }
}
