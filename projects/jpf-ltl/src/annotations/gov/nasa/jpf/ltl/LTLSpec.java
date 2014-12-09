package gov.nasa.jpf.ltl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used to annotate the LTL formula as a <code>String</code>
 * 
 * @author Phuc Nguyen Dinh - luckymanphuc@gmail.com
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LTLSpec {
  /**
   * The LTL formula string
   */
  String value();
}
