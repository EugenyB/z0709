import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    double sum;
    int finished;
    ReentrantLock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    public static void main(String[] args) {
        Main main = new Main();
        main.run3();
    }

    private void run3() {
        double a = 0;
        double b = Math.PI;
        int n = 500_000_000;
        int nThreads = 200;
        double delta = (b - a) / nThreads;
        long start = System.currentTimeMillis();
        ExecutorService es = Executors.newWorkStealingPool();
        List<Callable<Double>> callables = new ArrayList<>();
        for (int i = 0; i < nThreads; i++) {
            CallableCalculator calculator = new CallableCalculator(a + delta * i, a + delta * (i+1), n/nThreads, Math::sin);
            callables.add(calculator);
        }
        try {
            double result = es.invokeAll(callables).stream().map(f -> {
                try {
                    return f.get();
                } catch (Exception e) {
                    throw new IllegalStateException();
                }
            }).mapToDouble(x -> x).sum();
            long finish = System.currentTimeMillis();
            System.out.println(result);
            System.out.println(finish-start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run2() {
        double a = 0;
        double b = Math.PI;
        int n = 500_000_000;
        int nThreads = 2000;
        double delta = (b - a) / nThreads;
        long start = System.currentTimeMillis();
        List<Future<Double>> futures = new ArrayList<>();
        ExecutorService es = Executors.newFixedThreadPool(20);
        for (int i = 0; i < nThreads; i++) {
            CallableCalculator calculator = new CallableCalculator(a + delta * i, a + delta * (i+1), n/nThreads, Math::sin);
            Future<Double> future = es.submit(calculator);
            futures.add(future);
        }
        double result = 0;
        try {
            for (Future<Double> future : futures) {
                result += future.get();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            es.shutdown();
        }
        long finish = System.currentTimeMillis();
        System.out.println(result);
        System.out.println(finish-start);
    }

    private void run1() {
        double a = 0;
        double b = Math.PI;
        int n = 500_000_000;
        int nThreads = 20;
        double delta = (b - a) / nThreads;
        sum = 0;
        finished = 0;
        long start = System.currentTimeMillis();
        https://en.wikipedia.org/wiki/Guarded_suspension
        for (int i = 0; i < nThreads; i++) {
            new Thread(new ThreadedCalculator(a + i*delta, a + (i+1)*delta, n/nThreads, Math::sin, this))
                    .start();
        }

        lock.lock();
        try {
            while (finished < nThreads) {
                condition.await();
            }
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } finally {
            lock.unlock();
        }

        //double s = new IntegralCalculator(a, b, n, Math::sin).calculate();
        long finish = System.currentTimeMillis();
        System.out.println("sum = " + sum);
        System.out.println(finish - start);
    }

    public void send(double v) {
        lock.lock();
        try {
            sum += v;
            finished++;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}
