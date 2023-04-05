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
                variables.put(varName, Integer.parseInt(varValue));
            }
        }
        int result = evaluate(expression);
        resp.getWriter().write(String.valueOf(result));
    }
    private int evaluate(String expression) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            engine.put(entry.getKey(), entry.getValue());
        }
        int result = 0;
        try {
            result = ((Number) engine.eval(expression)).intValue();
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}