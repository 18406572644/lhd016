package com.bike.common;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    String module();

    String operationType();

    String description() default "";

    boolean sensitive() default false;
}
