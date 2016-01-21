package ru.spbau.pavlyutchenko.task1;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import ru.spbau.pavlyutchenko.task1.TestLevel;
public class BTest {
    public static HashMap<Callable, TestLevel> tests = new HashMap<Callable, TestLevel>();
    static {

tests.put(new Callable() {
public Object call() throws Exception {
System.out.print("foo2Test");
try {
ru.spbau.pavlyutchenko.task1.B.foo2();
System.out.println(" passed");
} catch (Exception e) {
System.out.println(" Error: " + e.getMessage());
}
return null;
}
}, TestLevel.CRITICAL);


tests.put(new Callable() {
public Object call() throws Exception {
System.out.print("bar2Test");
try {
ru.spbau.pavlyutchenko.task1.B.bar2();
System.out.println(" passed");
} catch (Exception e) {
System.out.println(" Error: " + e.getMessage());
}
return null;
}
}, TestLevel.LOW);

    }
    public static void run(TestLevel level) {
        System.out.println("B");
        for (Map.Entry<Callable, TestLevel> test : tests.entrySet()) {
            if (level.level <= test.getValue().level) {
                try {
                    test.getKey().call();
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }
}
