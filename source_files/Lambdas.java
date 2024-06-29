public class Lambdas {
    public static void main(String[] args) {
        Runnable r = () -> System.out.println("Hello, world!");
        r.run();
    }
}
