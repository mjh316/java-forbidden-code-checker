public class Protected {
    protected void print() {
        System.out.println("Hello, world!");
    }

    public static void main(String[] args) {
        Protected p = new Protected();
        p.print();
    }
}
