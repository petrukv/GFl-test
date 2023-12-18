import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class EquationHandlerTest {

    @Test
    void testExecuteEquationSolverValid() {
        Scanner scanner = new Scanner("2*(x+5+x)+5=10\n7");
        EquationHandler.executeEquationSolver(scanner);
    }

    @Test
    void testSearchEquationByRoot() {
        Scanner scanner = new Scanner("7");
        EquationHandler.searchEquationByRoot(scanner);
    }

    @Test
    void testIsValidParenthesesPlacement() {
        assertTrue(EquationHandler.isValidParenthesesPlacement("(2*x)"));
        assertFalse(EquationHandler.isValidParenthesesPlacement("2*(x+5"));
    }

    @Test
    void testIsValidExpression() {
        assertTrue(EquationHandler.isValidExpression("2*x+5"));
        assertFalse(EquationHandler.isValidExpression("2**x+5"));
    }

    @Test
    void testIsMathOperation() {
        assertTrue(EquationHandler.isMathOperation('+'));
        assertFalse(EquationHandler.isMathOperation('x'));
    }

    @Test
    void testEvaluateExpression() {
        double result = EquationHandler.evaluateExpression("2*x+5", 3);
        assertEquals(11, result, 0.01);
    }

    @Test
    void testEvaluateComplexExpression() {
        double result = EquationHandler.evaluateComplexExpression("2*(x+3)+5", 2);
        assertEquals(15, result, 0.01);
    }

    @Test
    void testEvaluateTerm() {
        double result = EquationHandler.evaluateTerm("2*x", 4);
        assertEquals(8, result, 0.01);
    }

    @Test
    void testCalculateExpression() {
        double result = EquationHandler.calculateExpression("x - 2", 4);
        assertEquals(2.0, result, 0.01);
    }
}
