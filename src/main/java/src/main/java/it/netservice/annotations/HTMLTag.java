package src.main.java.it.netservice.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Daniele Asteggiante
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HTMLTag {
    String name();

    String area() default "body";

    String idWrapper() default "";

    boolean empty() default false;

    String[] attributes() default {};

    boolean autoClose() default false;
}
