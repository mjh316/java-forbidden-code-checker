public class ArraysMethods {
    public static void main(String[] args) {
        // Arrays.fill
        int[] arr = new int[5];
        java.util.Arrays.fill(arr, 5);
        for (int i : arr) {
            System.out.println(i);
        }

        // Arrays.copyOf
        int[] arr2 = { 1, 2, 3, 4, 5 };
        int[] arr3 = java.util.Arrays.copyOf(arr2, 3);
        for (int i : arr3) {
            System.out.println(i);
        }

        // Arrays.copyOfRange
        int[] arr4 = java.util.Arrays.copyOfRange(arr2, 1, 4);
        for (int i : arr4) {
            System.out.println(i);
        }

        // Arrays.sort
        int[] arr5 = { 5, 4, 3, 2, 1 };
        java.util.Arrays.sort(arr5);
        for (int i : arr5) {
            System.out.println(i);
        }
    }
}