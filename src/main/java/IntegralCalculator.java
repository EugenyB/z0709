import java.util.function.DoubleUnaryOperator;
import java.util.stream.IntStream;

public class IntegralCalculator {
    private double a;
    private double b;
    private int n;
    private DoubleUnaryOperator f;

    public IntegralCalculator(double a, double b, int n, DoubleUnaryOperator f) {
        this.a = a;
        this.b = b;
        this.n = n;
        this.f = f;
    }

    public double calculate() {
        double h = (b-a)/n;
//        double s = 0;
//        for (int i = 0; i < n; i++) {
//            double x = a + i*h;
//            s += f.applyAsDouble(x)*h;
//        }
//        return s;
        return IntStream.range(0, n)
                .mapToDouble(i -> a + i * h)
                .map(f)
                .map(y -> y * h)
                .sum();
    }
}
