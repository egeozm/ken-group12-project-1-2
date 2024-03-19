import java.util.function.Supplier;



public class ODEAnalyticalSolver {
    public static long stopwatch(Runnable method){
        long start_time = System.nanoTime();
        method.run();
        long end_time = System.nanoTime();
        long time=end_time-start_time;
        return time;
}

public static void test(int j){
        for (int i=0;i<j;i++){
            //whatever
        }
    }//test method


    public static double f(double t, double y, double k) {
        return -k * y;
    }
    public static double analyticalSolution(double t, double y0, double k) {
        return y0 * Math.exp(-k * t);

    }
    public static void main(String[]args){

        System.out.println("program executed");

        System.out.println(stopwatch(() -> {// lambda expression
            test(10000);//method you want to test
        }));
        print(Double.toString(f(4,4,4)));

    }
    public static void print(String str){
        System.out.println(str);
    }
}
