import java.sql.*;
import java.util.Scanner;

public class EquationHandler {
    static void executeEquationSolver(Scanner scanner) {
        System.out.println("Введіть рівняння у вигляді -1.3*-5/x=1.2, або 17=2*x+5:");
        String equation = scanner.nextLine();

        if (!isValidParenthesesPlacement(equation) || !isValidExpression(equation)) {
            System.out.println("Некоректне рівняння. Перевірте коректність розміщення дужок та виразу.");
            return;
        }

        System.out.println("Введіть значення x:");
        double x = scanner.nextDouble();

        String[] parts = equation.split("=");

        double leftSideResult = evaluateExpression(parts[0], x);
        double rightSideResult = evaluateExpression(parts[1], x);

        System.out.println("Ліва частина: " + leftSideResult);
        System.out.println("Права частина: " + rightSideResult);

        double difference = Math.abs(leftSideResult - rightSideResult);

        if (difference < Math.pow(10, -9)) {
            System.out.println("Корінь вірний");

            EquationDatabase.saveToDatabase(equation, String.valueOf(x));
        } else {
            System.out.println("Корінь не вірний");
        }
    }

    //пошук рівняння за заданим коренем
    static void searchEquationByRoot(Scanner scanner) {
        System.out.println("Введіть корінь для пошуку рівняння:");
        String rootToSearch = scanner.nextLine();

        try {
            EquationDatabase.findEquationByRoot(rootToSearch);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     //перевірка правильності розміщення дужок
    static boolean isValidParenthesesPlacement(String equation) {
        int openParenthesesCount = 0;
        int closeParenthesesCount = 0;

        for (char ch : equation.toCharArray()) {
            if (ch == '(') {
                openParenthesesCount++;
            } else if (ch == ')') {
                closeParenthesesCount++;
                if (closeParenthesesCount > openParenthesesCount) {
                    return false;
                }
            }
        }

        return openParenthesesCount == closeParenthesesCount;
    }

    //перевірка чи вираз в рівнянні є коректним
    static boolean isValidExpression(String equation) {
        for (int i = 0; i < equation.length() - 1; i++) {
            char currentChar = equation.charAt(i);
            char nextChar = equation.charAt(i + 1);

            if (isMathOperation(currentChar) && isMathOperation(nextChar) && !(currentChar == '*' && nextChar == '-')) {
                return false;
            }
        }

        return true;
    }

    //перевірка чи символ є математичним оператором
    static boolean isMathOperation(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    //обчислює значення виразу для певного значення x
    static double evaluateExpression(String expression, double x) {
        if (expression.contains("*") || expression.contains("/")) {
            return evaluateComplexExpression(expression, x);
        }

        while (expression.contains("(")) {
            int startIndex = expression.lastIndexOf("(");
            int endIndex = expression.indexOf(")", startIndex);

            String subExpression = expression.substring(startIndex + 1, endIndex);
            double subExpressionResult = evaluateExpression(subExpression, x);

            expression = expression.substring(0, startIndex) + subExpressionResult + expression.substring(endIndex + 1);
        }

        String[] terms = expression.split("\\+");
        double result = 0;

        for (String term : terms) {
            result += evaluateTerm(term, x);
        }

        return result;
    }

    //обчислює вираз, який може містити дужки
    static double evaluateComplexExpression(String expression, double x) {
        while (expression.contains("(")) {
            int startIndex = expression.lastIndexOf("(");
            int endIndex = expression.indexOf(")", startIndex);

            String subExpression = expression.substring(startIndex + 1, endIndex);
            double subExpressionResult = evaluateExpression(subExpression, x);

            expression = expression.substring(0, startIndex) + subExpressionResult + expression.substring(endIndex + 1);
        }

        String[] terms = expression.split("\\+");
        double result = 0;

        for (String term : terms) {
            result += evaluateTerm(term, x);
        }

        return result;
    }

    //обчислює значення терміну (ділення, множення або число) для певного значення x
    static double evaluateTerm(String term, double x) {
        if (term.contains("*") || term.contains("/")) {
            String[] factors = term.split("\\*");
            double result = 1;

            for (String factor : factors) {
                if (factor.contains("/")) {
                    String[] divisionParts = factor.split("/");
                    double numerator = Double.valueOf(divisionParts[0].trim());

                    double denominator = Double.valueOf(divisionParts[1].replace("x", String.valueOf(x)).trim());

                    if (denominator != 0) {
                        result *= numerator / denominator;
                    } else {
                        throw new ArithmeticException("Ділення на нуль");
                    }
                } else {
                    result *= Double.valueOf(factor.replace("x", String.valueOf(x)).trim());
                }
            }

            return result;
        }

        try {
            return Double.valueOf(term.replace("x", String.valueOf(x)).trim());
        } catch (NumberFormatException e) {
            return calculateExpression(term, x);
        }
    }

    //обчислює значення виразу, що містить віднімання
    static double calculateExpression(String expression, double x) {
        if (expression.matches("[+-]?\\d*(\\.\\d+)?")) {
            return Double.parseDouble(expression);
        }

        String[] terms = expression.split("-");
        if (terms.length == 2) {
            double firstOperand = Double.valueOf(terms[0].replace("x", String.valueOf(x)).trim());
            double secondOperand = Double.valueOf(terms[1].replace("x", String.valueOf(x)).trim());
            return firstOperand - secondOperand;
        } else {
            throw new NumberFormatException("Некоректний вираз для обчислення, перевірте оператори " + expression);
        }
    }

}
