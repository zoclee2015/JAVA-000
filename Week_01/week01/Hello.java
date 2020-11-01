package week01;


/**
 * @author zoc
 */
public class Hello {
    private static final int COUNT = 100;

    static {
        System.out.println("static init : hello class initialized!!");
    }

    public static void main(String[] args) {
        int a = 1;
        int b = 2;
        int c = (a + b) * 5;
        for (int i = 0; i < COUNT; i++) {
            if (c > 0) {
                c--;
            }
        }
    }
}
