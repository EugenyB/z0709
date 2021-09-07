public class Main {

    double sum;
    int finished;

    public static void main(String[] args) {
        Main main = new Main();
        main.run();
    }

    private void run() {
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
        synchronized (this) {
            while (finished < nThreads) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }
        }
        //double s = new IntegralCalculator(a, b, n, Math::sin).calculate();
        long finish = System.currentTimeMillis();
        System.out.println("sum = " + sum);
        System.out.println(finish - start);
    }

    synchronized public void send(double v) {
        sum += v;
        finished++;
        notify();
    }
}
