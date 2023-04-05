import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(
        urlPatterns = "/calc"
)
public class CalcServlet extends HttpServlet {
    Map<String, Integer> variables = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String expression = req.getParameter("expression");
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isLetter(c)) {
                String varName = String.valueOf(c);
                String varValue = req.getParameter(varName);
                try {
                    variables.put(varName, Integer.parseInt(varValue));
                } catch (NumberFormatException e) {
                    Integer varRef = variables.get(varValue);
                    if (varRef == null) {
                        return;
                    }
                    variables.put(varName, varRef);
                }
            }
        }
        int result = evaluate(expression);
        resp.getWriter().write(String.valueOf(result));
    }
    private int evaluate(String expression) throws IllegalArgumentException {
        expression = expression.replaceAll("\\s+", "");
        return evaluateExpression(expression);
    }

    private int evaluateExpression(String expression) {
        if (!expression.contains("+") && !expression.contains("-") &&
                !expression.contains("*") && !expression.contains("/")) {
            return evaluateOperand(expression);
        }
        int index = findLastOperatorIndex(expression);
        String leftExpression = expression.substring(0, index);
        String rightExpression = expression.substring(index + 1);
        int leftValue = evaluateExpression(leftExpression);
        int rightValue = evaluateExpression(rightExpression);
        char operator = expression.charAt(index);
        switch (operator) {
            case '+':
                return leftValue + rightValue;
            case '-':
                return leftValue - rightValue;
            case '*':
                return leftValue * rightValue;
            case '/':
                return leftValue / rightValue;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

    private int evaluateOperand(String operand) {
        if (operand.length() == 1 && Character.isLowerCase(operand.charAt(0))) {
            Integer value = variables.get(operand);
            if (value == null) {
                throw new IllegalArgumentException("Undefined variable: " + operand);
            }
            return value;
        }
        try {
            return Integer.parseInt(operand);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid operand: " + operand);
        }
    }

    private int findLastOperatorIndex(String expression) {
        int count = 0;
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (c == ')') {
                count++;
            } else if (c == '(') {
                count--;
            } else if (count == 0 && (c == '+' || c == '-')) {
                return i;
            } else if (count == 0 && (c == '*' || c == '/')) {
                return i;
            }
        }
        if (expression.charAt(0) == '(' && expression.charAt(expression.length() - 1) == ')') {
            int innerIndex = findLastOperatorIndex(expression.substring(1, expression.length() - 1));
            if (innerIndex != -1) {
                return innerIndex + 1;
            }
        }
        return -1;
    }
}