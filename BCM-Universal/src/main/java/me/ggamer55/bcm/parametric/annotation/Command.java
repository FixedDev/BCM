package me.ggamer55.bcm.parametric.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

    String[] names();

    String usage() default "/<command>";

    String desc() default "";

    int min() default 0;

    int max() default -1;

    char[] flags() default {};

    boolean anyFlags() default false;

    String permission() default "";

    String permissionMessage() default "No Permission.";
}
