package ru.spbau.pavlyutchenko.task1;

public class TmpSuit {
    public static void main(String[] args) {
        TestLevel testLevel = TestLevel.LOW;

        if (args.length > 0) {
            testLevel = TestLevel.valueOf(TestLevel.class, args[0]);
        }

        TmpClassTemplate.run(testLevel);
    }
}
