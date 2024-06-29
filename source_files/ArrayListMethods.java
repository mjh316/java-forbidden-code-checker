public class ArrayListMethods {
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();

        // toArray
        list.add("Hello");
        list.add("World");
        Object[] arr = list.toArray();

        // clone
        ArrayList<String> list2 = (ArrayList<String>) list.clone();
    }
}
