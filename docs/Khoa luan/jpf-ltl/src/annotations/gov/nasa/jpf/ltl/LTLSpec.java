package gov.nasa.jpf.ltl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used to annotate the LTL formula as a String
 * 
 * @author Phuc Nguyen Dinh
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LTLSpec {
  /**
   * The LTL formulae string
   */
  String value();
}
