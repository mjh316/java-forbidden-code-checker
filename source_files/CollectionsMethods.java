import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionsMethods {
    public static void main(String[] args) {
        // Collections.copy
        List<String> list1 = new ArrayList<String>(List.of("a", "b", "c"));
        List<String> list2 = List.of("d", "e", "f");
        Collections.copy(list1, list2);

        for (String s : list1) {
            System.out.println(s);
        }

        // Collections.sort
        List<Integer> list3 = new ArrayList<Integer>(List.of(3, 2, 1));
        Collections.sort(list3);

        for (int i : list3) {
            System.out.println(i);
        }

    }
}
