package ink.anyway.component.message.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Asserts that the annotated string, collection, map or array is not {@code null} or empty.
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
@Documented
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
public @interface NotEmpty {
    String message() default "{ink.anyway.component.message.constraints.NotEmpty.message}";

    Class<?>[] groups() default { };

    /**
     * Defines several {@code @NotEmpty} annotations on the same element.
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        NotEmpty[] value();
    }

}
