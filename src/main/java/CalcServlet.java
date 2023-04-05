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
    private Map<String, Integer> variables;

    @Override
    public void init() throws ServletException {
        super.init();
        variables = new HashMap<>();
    }

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
        int result = evaluate(expression, variables);
        resp.getWriter().write(String.valueOf(result));
    }

    private int evaluate(String expression, Map<String, Integer> variables) throws IllegalArgumentException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            engine.put(entry.getKey(), entry.getValue());
        }
        try {
            return (int) engine.eval(expression);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void destroy() {
        super.destroy();
        variables = null;
    }
}
