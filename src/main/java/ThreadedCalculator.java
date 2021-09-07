import java.util.function.DoubleUnaryOperator;

public class ThreadedCalculator implements Runnable {
    private IntegralCalculator calculator;
    private Main main;

    public ThreadedCalculator(double a, double b, int n, DoubleUnaryOperator f, Main main) {
        calculator = new IntegralCalculator(a, b, n, f);
        this.main = main;
    }

    @Override
    public void run() {
        double v = calculator.calculate();
        main.send(v);
    }
}
