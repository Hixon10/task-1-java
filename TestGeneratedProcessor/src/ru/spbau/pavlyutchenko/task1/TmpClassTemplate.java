package ru.spbau.pavlyutchenko.task1;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class TmpClassTemplate {
    public static HashMap<Callable, TestLevel> tests = new HashMap<Callable, TestLevel>();


    static {
        tests.put(new Callable() {
            public Object call() throws Exception {
                System.out.print("test name");
                try {
                    A.foo();
                    System.out.println(" passed");
                } catch (Exception e) {
                    System.out.println(" Error: " + e.getMessage());
                }

                return null;
            }
        }, TestLevel.CRITICAL);

    }

    public static void run(TestLevel level) {
        System.out.println("ATest");

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
