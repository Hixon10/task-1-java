package ru.spbau.pavlyutchenko.task1;

/**
 * The class contains levels of test
 * @author pavlyutchenko
 */
public enum TestLevel {
    LOW(0),
    MEDIUM(1),
    CRITICAL(2);

    TestLevel(int level) {
        this.level = level;
    }

    public final int level;
}
