package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.inject.Stereotype;
import javax.persistence.EntityListeners;

/**
 *
 * @author kounabi
 */
/*Trying to add @EntityListeners(basic.EntityCrudListener.class) in this annotation 
 * so it is not needed by the user to add it in each Entity.
 * Making it a @Stereotype should work but it does not*/
@EntityListeners(core.EntityCrudListener.class)
@Stereotype
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Extpose {
    /*Currently not in use, assuming one persistence unit*/
    public String persistenceUnit() default "";
}
