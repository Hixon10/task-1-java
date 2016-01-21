package ru.spbau.pavlyutchenko.task1;

public class Main {

    public static void main(String[] args) throws Exception {
        com.sun.tools.javac.Main.main(new String[]{
                "-proc:only",
                "-processor",
                "ru.spbau.pavlyutchenko.task1.TestSpeedProcessor",
                "TestGeneratedProcessor/src/ru/spbau/pavlyutchenko/task1/A.java"});
    }
}
