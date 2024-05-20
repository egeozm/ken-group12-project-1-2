package com.example;

import java.util.*;

public class FunctionParser2 {

    private static final String OPERATORS = "+-*/^";
    private static final Set<String> FUNCTIONS = new HashSet<>(Arrays.asList("sin", "cos"));
    private static final String DIGITS = "0123456789.";

    public static double eval(String functionStr, Map<String, Double> variables) {
        List<String> tokens = tokenize(functionStr);
        List<String> rpn = shuntingYard(tokens);
        return evaluateRPN(rpn, variables);
    }

    private static List<String> tokenize(String functionStr) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < functionStr.length()) {
            char c = functionStr.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
            } else if (Character.isLetter(c)) {
                StringBuilder func = new StringBuilder();
                while (i < functionStr.length() && Character.isLetter(functionStr.charAt(i))) {
                    func.append(functionStr.charAt(i));
                    i++;
                }
                tokens.add(func.toString());
            } else if (DIGITS.indexOf(c) >= 0) {
                StringBuilder number = new StringBuilder();
                while (i < functionStr.length() && DIGITS.indexOf(functionStr.charAt(i)) >= 0) {
                    number.append(functionStr.charAt(i));
                    i++;
                }
                tokens.add(number.toString());
            } else if (OPERATORS.indexOf(c) >= 0 || c == '(' || c == ')') {
                tokens.add(Character.toString(c));
                i++;
            } else {
                throw new IllegalArgumentException("Invalid character: " + c);
            }
        }
        return tokens;
    }

    private static List<String> shuntingYard(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Deque<String> operators = new ArrayDeque<>();

        for (String token : tokens) {
            if (isNumber(token) || isVariable(token)) {
                output.add(token);
            } else if (FUNCTIONS.contains(token)) {
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                if (operators.isEmpty() || !operators.peek().equals("(")) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
                operators.pop(); // Pop the '('
                if (!operators.isEmpty() && FUNCTIONS.contains(operators.peek())) {
                    output.add(operators.pop());
                }
            } else if (OPERATORS.contains(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    output.add(operators.pop());
                }
                operators.push(token);
            } else {
                throw new IllegalArgumentException("Unknown token: " + token);
            }
        }

        while (!operators.isEmpty()) {
            if (operators.peek().equals("(") || operators.peek().equals(")")) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            output.add(operators.pop());
        }

        return output;
    }

    private static double evaluateRPN(List<String> rpn, Map<String, Double> variables) {
        Deque<Double> stack = new ArrayDeque<>();

        for (String token : rpn) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isVariable(token)) {
                if (!variables.containsKey(token)) {
                    throw new IllegalArgumentException("Undefined variable: " + token);
                }
                stack.push(variables.get(token));
            } else if (FUNCTIONS.contains(token)) {
                double arg = stack.pop();
                switch (token) {
                    case "sin":
                        stack.push(Math.sin(arg));
                        break;
                    case "cos":
                        stack.push(Math.cos(arg));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown function: " + token);
                }
            } else if (OPERATORS.contains(token)) {
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
                        throw new IllegalArgumentException("Unknown operator: " + token);
                }
            } else {
                throw new IllegalArgumentException("Unknown token: " + token);
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid RPN expression");
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
        return token.matches("[a-zA-Z]+") && !FUNCTIONS.contains(token);
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
            default:
                return -1;
        }
    }

    public static void main(String[] args) {
        String functionStr = "15*cos(x + y)*cos(y^2)*((2.8)^sin(x-y))/(12+x^2+y^2)";
        Map<String, Double> variables = new HashMap<>();
        variables.put("x", 1.0);
        variables.put("y", 1.0);
        double result = eval(functionStr, variables);
        System.out.println("Result: " + result);
    }
}
