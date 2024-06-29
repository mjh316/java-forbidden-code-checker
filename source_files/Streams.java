public class Streams {
    public static void main(String[] args) {
        // test streams
        List<String> list = Arrays.asList("a", "b", "c");
        list.stream().forEach(System.out::println);

        // test parallel streams
        list.parallelStream().forEach(System.out::println);

        // test map
        list.stream().map(String::toUpperCase).forEach(System.out::println);

        // test filter
        list.stream().filter(s -> s.equals("a")).forEach(System.out::println);

        // test reduce
        String result = list.stream().reduce("", (s1, s2) -> s1 + s2);

        System.out.println(result);

        // test collect
        List<String> collected = list.stream().collect(Collectors.toList());

        for (String s : collected) {
            System.out.println(s);
        }

        // test flatMap
        List<List<String>> nestedList = Arrays.asList(Arrays.asList("a", "b"), Arrays.asList("c", "d"));
        List<String> flatList = nestedList.stream().flatMap(Collection::stream).collect(Collectors.toList());

        for (String s : flatList) {
            System.out.println(s);
        }

        // test findFirst
        Optional<String> first = list.stream().findFirst();

        System.out.println(first.get());

        // test findAny
        Optional<String> any = list.stream().findAny();

        System.out.println(any.get());

        // test anyMatch
        boolean anyMatch = list.stream().anyMatch(s -> s.equals("a"));

        System.out.println(anyMatch);

        // test allMatch
        boolean allMatch = list.stream().allMatch(s -> s.equals("a"));

        System.out.println(allMatch);

        // test noneMatch
        boolean noneMatch = list.stream().noneMatch(s -> s.equals("a"));

        System.out.println(noneMatch);

        // test peek
        list.stream().peek(System.out::println).forEach(System.out::println);

        // test distinct
        list.stream().distinct().forEach(System.out::println);

        // test sorted
        list.stream().sorted().forEach(System.out::println);

        // test limit
        list.stream().limit(2).forEach(System.out::println);

        // test skip
        list.stream().skip(2).forEach(System.out::println);

        // test min
        Optional<String> min = list.stream().min(String::compareTo);
    }
}
