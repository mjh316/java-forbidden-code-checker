public class Java11StringMethods {
    public static void main(String[] args) {
        String s = "Hello, world!";
        System.out.println(s.repeat(3));
        System.out.println(s.strip());
        System.out.println(s.stripLeading());
        System.out.println(s.stripTrailing());
        System.out.println(s.isBlank());
        System.out.println(s.lines().count());
    }
}
