public class StringMethods {
    public static void main(String[] args) {
        String s = "Hello, world!";
        // tochararray
        char[] charArray = s.toCharArray();
        for (char c : charArray) {
            System.out.println(c);
        }

        // join
        String[] words = { "Hello", "world" };
        String joined = String.join(" ", words);
        System.out.println(joined);

        // matches
        System.out.println("Hello, world!".matches("Hello, world!"));
        System.out.println("Hello, world!".matches("Hello, [a-z]+!"));
    }
}
