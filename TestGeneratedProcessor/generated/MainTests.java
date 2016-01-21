package ru.spbau.pavlyutchenko.task1;
import ru.spbau.pavlyutchenko.task1.TestLevel;
public class MainTests {
    public static void main(String[] args) {
        TestLevel testLevel = TestLevel.LOW;
        if (args.length > 0) {
            testLevel = TestLevel.valueOf(TestLevel.class, args[0]);
        }
        ATest.run(testLevel);
        BTest.run(testLevel);
    }
}