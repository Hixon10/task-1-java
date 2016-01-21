package ru.spbau.pavlyutchenko.task1;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author pavlyutchenko
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface TestSpeed {
}
