package ru.spbau.pavlyutchenko.task1;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import ru.spbau.pavlyutchenko.task1.TestLevel;
public class ATest {
    public static HashMap<Callable, TestLevel> tests = new HashMap<Callable, TestLevel>();
    static {

tests.put(new Callable() {
public Object call() throws Exception {
System.out.print("fooTest");
try {
ru.spbau.pavlyutchenko.task1.A.foo();
System.out.println(" passed");
} catch (Exception e) {
System.out.println(" Error: " + e.getMessage());
}
return null;
}
}, TestLevel.MEDIUM);


tests.put(new Callable() {
public Object call() throws Exception {
System.out.print("barTest");
try {
ru.spbau.pavlyutchenko.task1.A.bar();
System.out.println(" passed");
} catch (Exception e) {
System.out.println(" Error: " + e.getMessage());
}
return null;
}
}, TestLevel.LOW);

    }
    public static void run(TestLevel level) {
        System.out.println("A");
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
