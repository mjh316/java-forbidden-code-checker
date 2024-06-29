import java.util.StringTokenizer;

public class StringClasses {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello, ");
        sb.append("world!");
        System.out.println(sb.toString());

        StringBuffer sbf = new StringBuffer();
        sbf.append("Hello, ");
        sbf.append("world!");
        System.out.println(sbf.toString());

        StringJoiner sj = new StringJoiner(", ");
        sj.add("Hello");
        sj.add("world!");
        System.out.println(sj.toString());

        StringTokenizer st = new StringTokenizer("Hello, world!");
        while (st.hasMoreTokens()) {
            System.out.println(st.nextToken());
        }
    }
}
