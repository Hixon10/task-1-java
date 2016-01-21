package ru.spbau.pavlyutchenko.task1;


@TestClass(testUnit = "MainTests")
public class A {
    @TestSpeed
    @Test(testName = "test1", testLevel = TestLevel.MEDIUM)
    public static void foo() {
        int a = 5;

        try {
            a++;
            int b = a;
            Thread.sleep(1000 + b);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    @TestSpeed
    @Test(testName = "test2")
    public static void bar() {
        try {
            Thread.sleep(345);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


