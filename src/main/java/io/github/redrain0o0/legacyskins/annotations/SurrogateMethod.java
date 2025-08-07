//? if figurac {
package io.github.redrain0o0.legacyskins.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tells Mixin to get rid of the annotated method.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface SurrogateMethod {
}
//?}