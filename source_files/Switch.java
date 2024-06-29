public class Switch {
    public static void main(String[] args) {
        switch (args.length) {
            case 0:
                System.out.println("No arguments");
                break;
            case 1:
                System.out.println("One argument");
                break;
            default:
                System.out.println("More than one argument");
                break;
        }
    }
}
